package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CreditScoreClientService creditScoreClientService;

    public CustomerService(CustomerRepository customerRepository, CreditScoreClientService creditScoreClientService) {
        this.customerRepository = customerRepository;
        this.creditScoreClientService = creditScoreClientService;
    }

    // 1️⃣ Register New Customer
    public Customer registerNewCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // 2️⃣ Find Customer By Email (For Login Purpose)
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    // 3️⃣ Find Customer By ID
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    // 4️⃣ Get All Customers
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // 5️⃣ Update Customer (Optional)
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // 6️⃣ Delete Customer
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

    // 7️⃣ Get Customer Count (For Dashboard Statistics)
    public int getCustomerCount() {
        return (int) customerRepository.count();
    }

    // 8️⃣ Synchronize Credit Score for a Customer
    public Customer synchronizeCreditScore(Long customerId) {
        Optional<Customer> customerOpt = getCustomerById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            
            // Only synchronize for customers with financial data
            if (customer.getRole() == Customer.Role.CUSTOMER && customer.getIncome() != null) {
                try {
                    var creditScoreDto = creditScoreClientService.calculateCreditScore(
                        customer,
                        customer.getIncome(),
                        customer.getDebtToIncomeRatio(),
                        customer.getPaymentHistoryScore(),
                        customer.getCreditUtilizationRatio(),
                        customer.getCreditAgeMonths(),
                        customer.getNumberOfAccounts()
                    );
                    
                    customer.setCreditScore(creditScoreDto.getCreditScore());
                    return updateCustomer(customer);
                    
                } catch (Exception e) {
                    System.err.println("Failed to synchronize credit score for customer " + customerId + ": " + e.getMessage());
                    return customer;
                }
            }
        }
        return customerOpt.orElse(null);
    }

    // 9️⃣ Synchronize Credit Scores for All Customers
    public void synchronizeAllCreditScores() {
        List<Customer> customers = getAllCustomers();
        int synchronized = 0;
        int failed = 0;
        
        for (Customer customer : customers) {
            if (customer.getRole() == Customer.Role.CUSTOMER && customer.getIncome() != null) {
                try {
                    synchronizeCreditScore(customer.getId());
                    synchronized++;
                } catch (Exception e) {
                    failed++;
                    System.err.println("Failed to synchronize credit score for customer " + customer.getId() + ": " + e.getMessage());
                }
            }
        }
        
        System.out.println("Credit Score Synchronization Complete: " + synchronized + " successful, " + failed + " failed");
    }
}
