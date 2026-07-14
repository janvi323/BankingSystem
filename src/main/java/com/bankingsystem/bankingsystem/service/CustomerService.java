package com.bankingsystem.bankingsystem.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CreditScoreClientService creditScoreClientService;

    public record CreditScoreSyncSummary(int refreshed, int skipped, int microserviceSynced, int localOnly, int failed) {}

    public CustomerService(CustomerRepository customerRepository, CreditScoreClientService creditScoreClientService) {
        this.customerRepository = customerRepository;
        this.creditScoreClientService = creditScoreClientService;
    }

    public Customer registerNewCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public boolean deleteCustomer(Long id) {
        try {
            if (customerRepository.existsById(id)) {
                customerRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting customer: " + e.getMessage(), e);
        }
    }

    public int getCustomerCount() {
        return (int) customerRepository.count();
    }

    public Customer synchronizeCreditScore(Long customerId) {
        Optional<Customer> customerOpt = getCustomerById(customerId);
        if (customerOpt.isEmpty()) {
            return null;
        }

        Customer customer = customerOpt.get();
        if (customer.getRole() != Customer.Role.CUSTOMER || customer.effectiveAnnualIncome() <= 0) {
            return customer;
        }

        prepareCreditScoreInputs(customer);
        int localScore = calculateLocalCreditScore(customer);
        Integer previousScore = customer.getCreditScore();
        customer.setCreditScore(localScore);
        customer.appendAudit("Credit score refreshed: " +
            (previousScore != null ? previousScore : "N/A") + " -> " + localScore + " (local)");
        customer = updateCustomer(customer);

        try {
            var creditScoreDto = creditScoreClientService.calculateCreditScore(
                customer,
                customer.effectiveAnnualIncome(),
                customer.getDebtToIncomeRatio(),
                customer.getPaymentHistoryScore(),
                customer.getCreditUtilizationRatio(),
                customer.getCreditAgeMonths(),
                customer.getNumberOfAccounts()
            );

            Integer serviceScore = creditScoreDto.getCreditScore();
            if (serviceScore != null) {
                customer.setCreditScore(serviceScore);
                customer.appendAudit("Credit score refreshed from service: " + localScore + " -> " + serviceScore);
                customer = updateCustomer(customer);
            }
        } catch (Exception e) {
            System.err.println("Credit score service unavailable for customer " + customerId
                + "; kept local refreshed score " + localScore + ". " + e.getMessage());
        }

        return customer;
    }

    public void synchronizeAllCreditScores() {
        CreditScoreSyncSummary summary = refreshAllCreditScores();
        System.out.println("Credit Score Synchronization Complete: " + summary.refreshed()
            + " refreshed, " + summary.skipped() + " skipped, " + summary.microserviceSynced()
            + " service synced, " + summary.localOnly() + " local-only, " + summary.failed() + " failed");
    }

    public CreditScoreSyncSummary refreshAllCreditScores() {
        List<Customer> customers = getAllCustomers();
        int refreshed = 0;
        int skipped = 0;
        int microserviceSynced = 0;
        int localOnly = 0;
        int failed = 0;

        for (Customer customer : customers) {
            if (customer.getRole() != Customer.Role.CUSTOMER) {
                continue;
            }
            if (customer.effectiveAnnualIncome() <= 0) {
                skipped++;
                continue;
            }

            try {
                Customer refreshedCustomer = synchronizeCreditScore(customer.getId());
                refreshed++;
                if (refreshedCustomer != null && refreshedCustomer.getFinancialAuditLog() != null
                        && refreshedCustomer.getFinancialAuditLog().contains("refreshed from service")) {
                    microserviceSynced++;
                } else {
                    localOnly++;
                }
            } catch (Exception e) {
                failed++;
                System.err.println("Failed to refresh credit score for customer " + customer.getId() + ": " + e.getMessage());
            }
        }

        return new CreditScoreSyncSummary(refreshed, skipped, microserviceSynced, localOnly, failed);
    }

    public int calculateLocalCreditScore(Customer customer) {
        double annualIncome = customer.effectiveAnnualIncome();
        int baseScore = 300;
        int incomeScore = Math.min(200, (int) (annualIncome / 1000));
        int paymentScore = customer.getPaymentHistoryScore() != null
            ? Math.min(150, (int) (customer.getPaymentHistoryScore() * 1.5))
            : 0;
        int debtScore = customer.getDebtToIncomeRatio() != null
            ? Math.max(0, 100 - (int) (clampRatio(customer.getDebtToIncomeRatio()) * 200))
            : 0;
        int utilizationScore = customer.getCreditUtilizationRatio() != null
            ? Math.max(0, 100 - (int) (clampRatio(customer.getCreditUtilizationRatio()) * 200))
            : 0;
        int ageScore = customer.getCreditAgeMonths() != null
            ? Math.min(100, Math.max(0, customer.getCreditAgeMonths()) / 2)
            : 0;
        int accountsScore = customer.getNumberOfAccounts() != null
            ? Math.min(50, Math.max(0, customer.getNumberOfAccounts()) * 5)
            : 0;

        int totalScore = baseScore + incomeScore + paymentScore + debtScore
            + utilizationScore + ageScore + accountsScore;
        return Math.min(850, Math.max(300, totalScore));
    }

    private void prepareCreditScoreInputs(Customer customer) {
        normalizeIncome(customer);

        double monthlyIncome = customer.effectiveMonthlyIncome();
        if (customer.getEmi() == null) {
            customer.setEmi(0.0);
        }
        if (customer.getDebtToIncomeRatio() == null && monthlyIncome > 0) {
            customer.setDebtToIncomeRatio(Math.min(1.0, customer.getEmi() / monthlyIncome));
        } else if (customer.getDebtToIncomeRatio() != null) {
            customer.setDebtToIncomeRatio(clampRatio(customer.getDebtToIncomeRatio()));
        }

        double dti = customer.getDebtToIncomeRatio() != null ? customer.getDebtToIncomeRatio() : 0.0;
        if (customer.getPaymentHistoryScore() == null) {
            customer.setPaymentHistoryScore(Math.max(50, 100 - (int) (dti * 100)));
        }
        if (customer.getCreditUtilizationRatio() == null) {
            customer.setCreditUtilizationRatio(Math.min(0.3, dti));
        } else {
            customer.setCreditUtilizationRatio(clampRatio(customer.getCreditUtilizationRatio()));
        }
        if (customer.getCreditAgeMonths() == null) {
            customer.setCreditAgeMonths(customer.getExistingLoans() != null && customer.getExistingLoans() > 0 ? 24 : 60);
        }
        if (customer.getNumberOfAccounts() == null) {
            customer.setNumberOfAccounts(customer.getExistingLoans() != null && customer.getExistingLoans() > 0 ? 2 : 3);
        }
    }

    private void normalizeIncome(Customer customer) {
        if (customer.getMonthlyIncome() != null && customer.getMonthlyIncome() > 0) {
            customer.setIncome(customer.getMonthlyIncome() * 12.0);
            return;
        }

        Double income = customer.getIncome();
        if (income == null || income <= 0) {
            return;
        }

        Double emi = customer.getEmi();
        Double dti = customer.getDebtToIncomeRatio();
        if (emi != null && emi > 0 && dti != null && dti > 0) {
            double inferredMonthlyIncome = emi / dti;
            customer.setMonthlyIncome(inferredMonthlyIncome);
            customer.setIncome(inferredMonthlyIncome * 12.0);
            return;
        }

        boolean looksLikeDebtFreeRegistration = (emi != null && emi == 0.0)
            && (dti != null && dti == 0.0)
            && (customer.getPaymentHistoryScore() != null && customer.getPaymentHistoryScore() >= 90)
            && (customer.getCreditUtilizationRatio() != null && customer.getCreditUtilizationRatio() <= 0.10);

        if (looksLikeDebtFreeRegistration) {
            customer.setMonthlyIncome(income);
            customer.setIncome(income * 12.0);
        } else {
            customer.setMonthlyIncome(income / 12.0);
        }
    }

    private double clampRatio(double value) {
        return Math.min(1.0, Math.max(0.0, value));
    }
}
