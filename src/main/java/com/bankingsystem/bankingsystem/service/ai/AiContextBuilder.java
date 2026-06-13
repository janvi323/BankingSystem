package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.Service.EMIService;
import com.bankingsystem.bankingsystem.Service.LoanService;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.EMI;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import com.bankingsystem.bankingsystem.repository.EMIRepository;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class AiContextBuilder {

    private static final Locale INDIA = Locale.forLanguageTag("en-IN");
    private static final int ADMIN_LOAN_SAMPLE_LIMIT = 10;

    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final EMIRepository emiRepository;
    private final LoanService loanService;
    private final EMIService emiService;

    public AiContextBuilder(CustomerRepository customerRepository,
                            LoanRepository loanRepository,
                            EMIRepository emiRepository,
                            LoanService loanService,
                            EMIService emiService) {
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
        this.emiRepository = emiRepository;
        this.loanService = loanService;
        this.emiService = emiService;
    }

    public String buildContext(AiRole role, Customer customer) {
        return switch (role) {
            case ADMIN -> buildAdminContext(customer);
            case CUSTOMER -> buildCustomerContext(customer);
            case ANONYMOUS -> buildAnonymousContext();
        };
    }

    private String buildAnonymousContext() {
        return """
                Authentication: Not logged in
                Available actions: Browse platform info, register, or log in via email/password or Google OAuth
                Personal data (credit score, EMIs, loans): Requires login
                """;
    }

    private String buildCustomerContext(Customer customer) {
        if (customer == null) {
            return buildAnonymousContext();
        }

        StringBuilder ctx = new StringBuilder();
        ctx.append("Authentication: Logged in as CUSTOMER\n");
        ctx.append("Customer ID: ").append(customer.getId()).append("\n");
        ctx.append("Name: ").append(nullSafe(customer.getName())).append("\n");
        appendProfileSummary(ctx, customer);
        appendCustomerLoans(ctx, customer.getId());
        appendCustomerEmiSummary(ctx, customer.getId());
        return ctx.toString();
    }

    private String buildAdminContext(Customer admin) {
        StringBuilder ctx = new StringBuilder();
        ctx.append("Authentication: Logged in as ADMIN\n");
        if (admin != null) {
            ctx.append("Admin: ").append(nullSafe(admin.getName())).append(" (ID: ").append(admin.getId()).append(")\n");
        }

        List<Customer> allCustomers = customerRepository.findAll();
        long customerRoleCount = allCustomers.stream()
                .filter(c -> c.getRole() == Customer.Role.CUSTOMER)
                .count();

        List<Loan> allLoans = loanRepository.findAll();
        long pendingLoans = allLoans.stream().filter(l -> l.getStatus() == Loan.Status.PENDING).count();
        long approvedLoans = allLoans.stream().filter(l -> l.getStatus() == Loan.Status.APPROVED).count();
        long rejectedLoans = allLoans.stream().filter(l -> l.getStatus() == Loan.Status.REJECTED).count();

        double avgCreditScore = allCustomers.stream()
                .filter(c -> c.getRole() == Customer.Role.CUSTOMER && c.getCreditScore() != null)
                .mapToInt(Customer::getCreditScore)
                .average()
                .orElse(0);

        long platformOverdueEmis = countPlatformOverdueEmis();

        ctx.append("\n--- Portfolio Metrics ---\n");
        ctx.append("Total registered users: ").append(allCustomers.size()).append("\n");
        ctx.append("Retail customers: ").append(customerRoleCount).append("\n");
        ctx.append("Total loans: ").append(allLoans.size()).append("\n");
        ctx.append("Pending applications: ").append(pendingLoans).append("\n");
        ctx.append("Approved loans: ").append(approvedLoans).append("\n");
        ctx.append("Rejected loans: ").append(rejectedLoans).append("\n");
        ctx.append("Average customer credit score: ").append(String.format("%.0f", avgCreditScore)).append("\n");
        ctx.append("Platform overdue EMIs: ").append(platformOverdueEmis).append("\n");

        ctx.append("\n--- Pending Loan Applications (sample) ---\n");
        List<Loan> pendingSample = allLoans.stream()
                .filter(l -> l.getStatus() == Loan.Status.PENDING)
                .sorted(Comparator.comparing(Loan::getApplicationDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(ADMIN_LOAN_SAMPLE_LIMIT)
                .toList();

        if (pendingSample.isEmpty()) {
            ctx.append("No pending applications.\n");
        } else {
            for (Loan loan : pendingSample) {
                Customer applicant = loan.getCustomer();
                ctx.append(String.format("- Loan #%d | %s | %s | %s | %d months | Applied: %s%n",
                        loan.getId(),
                        applicant != null ? nullSafe(applicant.getName()) : "Unknown",
                        money(loan.getAmount()),
                        nullSafe(loan.getPurpose()),
                        loan.getTenure() != null ? loan.getTenure() : 0,
                        loan.getApplicationDate()));
            }
        }

        ctx.append("\n--- Recent Approved Loans (sample) ---\n");
        List<Loan> approvedSample = allLoans.stream()
                .filter(l -> l.getStatus() == Loan.Status.APPROVED)
                .sorted(Comparator.comparing(Loan::getApprovalDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();

        if (approvedSample.isEmpty()) {
            ctx.append("No approved loans.\n");
        } else {
            for (Loan loan : approvedSample) {
                ctx.append(String.format("- Loan #%d | %s | EMI %s/month%n",
                        loan.getId(),
                        money(loan.getAmount()),
                        money(loan.getEmiAmount() != null ? loan.getEmiAmount() : 0)));
            }
        }

        ctx.append("\nAdmin capabilities: Review pending loans at /admin-loans, manage customers at /customers, approve/reject via admin APIs.\n");
        return ctx.toString();
    }

    private long countPlatformOverdueEmis() {
        return emiRepository.findByStatus(EMI.Status.PENDING).stream()
                .filter(emi -> emi.getDueDate() != null && emi.getDueDate().isBefore(LocalDate.now()))
                .count();
    }

    private void appendProfileSummary(StringBuilder ctx, Customer customer) {
        ctx.append("\n--- Profile ---\n");
        ctx.append("Credit score: ").append(customer.getCreditScore() != null ? customer.getCreditScore() : "Not available");
        if (customer.getCreditScore() != null) {
            ctx.append(" (").append(creditGrade(customer.getCreditScore())).append(")");
        }
        ctx.append("\n");

        if (customer.getIncome() != null) {
            ctx.append("Annual income: ").append(money(customer.getIncome())).append("\n");
        }
        if (customer.getDebtToIncomeRatio() != null) {
            ctx.append("Debt-to-income ratio: ").append(String.format("%.2f", customer.getDebtToIncomeRatio())).append("\n");
        }
        if (customer.getPaymentHistoryScore() != null) {
            ctx.append("Payment history score: ").append(customer.getPaymentHistoryScore()).append("/100\n");
        }
        if (customer.getCreditUtilizationRatio() != null) {
            ctx.append("Credit utilization: ").append(String.format("%.0f%%", customer.getCreditUtilizationRatio() * 100)).append("\n");
        }
    }

    private void appendCustomerLoans(StringBuilder ctx, Long customerId) {
        List<Loan> loans = loanService.getLoansByCustomerId(customerId);
        ctx.append("\n--- Loans (").append(loans.size()).append(") ---\n");

        if (loans.isEmpty()) {
            ctx.append("No loan applications on file.\n");
            return;
        }

        for (Loan loan : loans) {
            ctx.append(String.format("- Loan #%d | %s | %s | %d months | Status: %s",
                    loan.getId(),
                    money(loan.getAmount()),
                    nullSafe(loan.getPurpose()),
                    loan.getTenure() != null ? loan.getTenure() : 0,
                    loan.getStatus()));
            if (loan.getEmiAmount() != null && loan.getStatus() == Loan.Status.APPROVED) {
                ctx.append(" | EMI: ").append(money(loan.getEmiAmount())).append("/month");
            }
            ctx.append("\n");
        }
    }

    private void appendCustomerEmiSummary(StringBuilder ctx, Long customerId) {
        EMIService.EMIStats stats = emiService.getEMIStats(customerId);
        List<EMI> pending = emiService.getPendingEMIsByCustomerId(customerId);
        List<EMI> overdue = emiService.getOverdueEMIsByCustomerId(customerId);

        ctx.append("\n--- EMI Summary ---\n");
        ctx.append("Total EMIs: ").append(stats.getTotalEMIs()).append("\n");
        ctx.append("Paid: ").append(stats.getPaidEMIs()).append("\n");
        ctx.append("Pending: ").append(stats.getPendingEMIs()).append("\n");
        ctx.append("Overdue: ").append(stats.getOverdueEMIs()).append("\n");
        ctx.append("Pending amount: ").append(money(stats.getTotalPendingAmount())).append("\n");

        pending.stream()
                .filter(emi -> emi.getDueDate() != null)
                .min(Comparator.comparing(EMI::getDueDate))
                .ifPresent(next -> ctx.append("Next EMI due: ")
                        .append(next.getDueDate())
                        .append(" — ")
                        .append(money(next.getAmount()))
                        .append("\n"));

        if (!overdue.isEmpty()) {
            ctx.append("Overdue EMIs: ")
                    .append(overdue.stream()
                            .limit(3)
                            .map(emi -> "#" + emi.getEmiNumber() + " due " + emi.getDueDate())
                            .collect(Collectors.joining(", ")))
                    .append("\n");
        }
    }

    private String creditGrade(int score) {
        if (score >= 750) return "Excellent";
        if (score >= 650) return "Good";
        if (score >= 550) return "Fair";
        return "Needs improvement";
    }

    private String money(double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(INDIA);
        format.setMaximumFractionDigits(0);
        return format.format(value);
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "N/A" : value;
    }
}
