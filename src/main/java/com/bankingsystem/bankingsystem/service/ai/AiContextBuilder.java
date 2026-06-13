package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.Service.CreditScoreClientService;
import com.bankingsystem.bankingsystem.Service.EMIService;
import com.bankingsystem.bankingsystem.Service.LoanService;
import com.bankingsystem.bankingsystem.dto.CreditScoreDto;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.EMI;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import com.bankingsystem.bankingsystem.repository.EMIRepository;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AiContextBuilder — assembles the authoritative data context block injected into
 * every AI prompt. Role determines what data is included:
 *
 * <ul>
 *   <li><b>CUSTOMER</b>: Only that customer's own data (loans, EMIs, credit score)</li>
 *   <li><b>ADMIN</b>: Portfolio-wide analytics + pending loan queue sample</li>
 *   <li><b>ANONYMOUS</b>: Platform feature overview only — no personal data</li>
 * </ul>
 *
 * <p>Credit scores are fetched live from the credit-score microservice with a
 * graceful fallback to the DB-cached value when the service is unavailable.
 */
@Component
public class AiContextBuilder {

    private static final Logger log = LoggerFactory.getLogger(AiContextBuilder.class);
    private static final Locale INDIA = Locale.forLanguageTag("en-IN");
    private static final int ADMIN_LOAN_SAMPLE_LIMIT = 10;
    private static final int ADMIN_OVERDUE_SAMPLE_LIMIT = 5;

    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final EMIRepository emiRepository;
    private final LoanService loanService;
    private final EMIService emiService;
    private final CreditScoreClientService creditScoreClientService;

    public AiContextBuilder(CustomerRepository customerRepository,
                            LoanRepository loanRepository,
                            EMIRepository emiRepository,
                            LoanService loanService,
                            EMIService emiService,
                            CreditScoreClientService creditScoreClientService) {
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
        this.emiRepository = emiRepository;
        this.loanService = loanService;
        this.emiService = emiService;
        this.creditScoreClientService = creditScoreClientService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Entry point
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public String buildContext(AiRole role, Customer customer) {
        return switch (role) {
            case ADMIN    -> buildAdminContext(customer);
            case CUSTOMER -> buildCustomerContext(customer);
            case ANONYMOUS -> buildAnonymousContext();
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Anonymous context — platform overview only
    // ─────────────────────────────────────────────────────────────────────────

    private String buildAnonymousContext() {
        return """
                Authentication: Not logged in
                Available actions: Browse platform information, register an account, or log in via email/password or Google OAuth
                Personal data (credit score, EMIs, loans): Requires login
                Today's date: %s
                """.formatted(LocalDate.now());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Customer context — personal data only
    // ─────────────────────────────────────────────────────────────────────────

    private String buildCustomerContext(Customer customer) {
        if (customer == null) {
            return buildAnonymousContext();
        }

        StringBuilder ctx = new StringBuilder();
        ctx.append("Authentication: Logged in as CUSTOMER\n");
        ctx.append("Customer ID: ").append(customer.getId()).append("\n");
        ctx.append("Name: ").append(nullSafe(customer.getName())).append("\n");
        ctx.append("Today's date: ").append(LocalDate.now()).append("\n");

        appendCustomerCreditProfile(ctx, customer);
        appendCustomerLoans(ctx, customer.getId());
        appendCustomerEmiSummary(ctx, customer.getId());

        return ctx.toString();
    }

    /**
     * Builds the credit profile section for a customer.
     * Attempts to fetch live data from the credit-score microservice; falls back
     * gracefully to the DB-cached score if the service is unreachable.
     */
    private void appendCustomerCreditProfile(StringBuilder ctx, Customer customer) {
        ctx.append("\n--- Credit Profile ---\n");

        // Try live credit score from microservice
        CreditScoreDto liveScore = fetchLiveCreditScore(customer.getId());

        if (liveScore != null) {
            int score = liveScore.getCreditScore() != null ? liveScore.getCreditScore() : 0;
            ctx.append("Credit score: ").append(score)
               .append(" (").append(creditGrade(score)).append(")\n");
            ctx.append("Score grade: ").append(nullSafe(liveScore.getScoreGrade())).append("\n");

            if (liveScore.getIncome() != null) {
                ctx.append("Annual income: ").append(money(liveScore.getIncome())).append("\n");
            }
            if (liveScore.getDebtToIncomeRatio() != null) {
                ctx.append("Debt-to-income ratio: ")
                   .append(String.format("%.2f", liveScore.getDebtToIncomeRatio())).append("\n");
            }
            if (liveScore.getPaymentHistoryScore() != null) {
                ctx.append("Payment history score: ")
                   .append(liveScore.getPaymentHistoryScore()).append("/100\n");
            }
            if (liveScore.getCreditUtilizationRatio() != null) {
                ctx.append("Credit utilization: ")
                   .append(String.format("%.0f%%", liveScore.getCreditUtilizationRatio() * 100)).append("\n");
            }
            if (liveScore.getCreditAgeMonths() != null) {
                ctx.append("Credit history age: ")
                   .append(liveScore.getCreditAgeMonths()).append(" months\n");
            }
            if (liveScore.getNumberOfAccounts() != null) {
                ctx.append("Number of credit accounts: ")
                   .append(liveScore.getNumberOfAccounts()).append("\n");
            }
            if (liveScore.getLastUpdated() != null) {
                ctx.append("Score last updated: ").append(liveScore.getLastUpdated().toLocalDate()).append("\n");
            }

        } else {
            // Fallback: use DB-cached credit score
            Integer cachedScore = customer.getCreditScore();
            if (cachedScore != null) {
                ctx.append("Credit score: ").append(cachedScore)
                   .append(" (").append(creditGrade(cachedScore)).append(") [cached]\n");
            } else {
                ctx.append("Credit score: Not yet calculated — customer should complete financial profile\n");
            }

            // Include any available DB-stored financial fields
            if (customer.getIncome() != null) {
                ctx.append("Annual income: ").append(money(customer.getIncome())).append("\n");
            }
            if (customer.getDebtToIncomeRatio() != null) {
                ctx.append("Debt-to-income ratio: ")
                   .append(String.format("%.2f", customer.getDebtToIncomeRatio())).append("\n");
            }
            if (customer.getPaymentHistoryScore() != null) {
                ctx.append("Payment history score: ")
                   .append(customer.getPaymentHistoryScore()).append("/100\n");
            }
            if (customer.getCreditUtilizationRatio() != null) {
                ctx.append("Credit utilization: ")
                   .append(String.format("%.0f%%", customer.getCreditUtilizationRatio() * 100)).append("\n");
            }
        }
    }

    private void appendCustomerLoans(StringBuilder ctx, Long customerId) {
        List<Loan> loans = loanService.getLoansByCustomerId(customerId);
        ctx.append("\n--- Loans (").append(loans.size()).append(") ---\n");

        if (loans.isEmpty()) {
            ctx.append("No loan applications on file. Customer can apply via Dashboard > Quick Actions > Apply for Loan.\n");
            return;
        }

        long approvedCount = loans.stream().filter(l -> l.getStatus() == Loan.Status.APPROVED).count();
        long pendingCount  = loans.stream().filter(l -> l.getStatus() == Loan.Status.PENDING).count();
        long rejectedCount = loans.stream().filter(l -> l.getStatus() == Loan.Status.REJECTED).count();
        ctx.append("Summary: ").append(approvedCount).append(" approved, ")
           .append(pendingCount).append(" pending, ")
           .append(rejectedCount).append(" rejected\n");

        for (Loan loan : loans) {
            ctx.append(String.format("- Loan #%d | %s | %s | %d months | Status: %s",
                    loan.getId(),
                    money(loan.getAmount()),
                    nullSafe(loan.getPurpose()),
                    loan.getTenure() != null ? loan.getTenure() : 0,
                    loan.getStatus()));

            if (loan.getInterestRate() != null) {
                ctx.append(String.format(" | Rate: %.2f%%", loan.getInterestRate()));
            }
            if (loan.getEmiAmount() != null && loan.getStatus() == Loan.Status.APPROVED) {
                ctx.append(" | EMI: ").append(money(loan.getEmiAmount())).append("/month");
            }
            if (loan.getTotalAmount() != null && loan.getStatus() == Loan.Status.APPROVED) {
                ctx.append(" | Total payable: ").append(money(loan.getTotalAmount()));
            }
            if (loan.getApplicationDate() != null) {
                ctx.append(" | Applied: ").append(loan.getApplicationDate().toLocalDate());
            }
            ctx.append("\n");
        }
    }

    private void appendCustomerEmiSummary(StringBuilder ctx, Long customerId) {
        EMIService.EMIStats stats  = emiService.getEMIStats(customerId);
        List<EMI> pending          = emiService.getPendingEMIsByCustomerId(customerId);
        List<EMI> overdue          = emiService.getOverdueEMIsByCustomerId(customerId);

        ctx.append("\n--- EMI Summary ---\n");
        ctx.append("Total EMIs: ").append(stats.getTotalEMIs()).append("\n");
        ctx.append("Paid:       ").append(stats.getPaidEMIs()).append("\n");
        ctx.append("Pending:    ").append(stats.getPendingEMIs()).append("\n");
        ctx.append("Overdue:    ").append(stats.getOverdueEMIs()).append("\n");
        ctx.append("Total pending amount: ").append(money(stats.getTotalPendingAmount())).append("\n");

        // Next EMI due
        pending.stream()
               .filter(emi -> emi.getDueDate() != null)
               .min(Comparator.comparing(EMI::getDueDate))
               .ifPresent(next -> {
                   long daysUntil = LocalDate.now().until(next.getDueDate()).getDays();
                   ctx.append("Next EMI due: ").append(next.getDueDate())
                      .append(" — ").append(money(next.getAmount()));
                   if (daysUntil >= 0) {
                       ctx.append(" (in ").append(daysUntil).append(" days)");
                   } else {
                       ctx.append(" (OVERDUE by ").append(-daysUntil).append(" days)");
                   }
                   ctx.append("\n");
               });

        // Upcoming next 3 EMIs
        List<EMI> upcoming = pending.stream()
                .filter(e -> e.getDueDate() != null && !e.getDueDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(EMI::getDueDate))
                .limit(3)
                .toList();
        if (!upcoming.isEmpty()) {
            ctx.append("Upcoming EMIs: ");
            ctx.append(upcoming.stream()
                    .map(e -> "#" + e.getEmiNumber() + " due " + e.getDueDate() + " (" + money(e.getAmount()) + ")")
                    .collect(Collectors.joining(", ")));
            ctx.append("\n");
        }

        // Overdue detail
        if (!overdue.isEmpty()) {
            ctx.append("Overdue EMIs: ")
               .append(overdue.stream()
                       .limit(3)
                       .map(emi -> "#" + emi.getEmiNumber() + " due " + emi.getDueDate()
                               + " (" + emi.getDaysOverdue() + " days overdue)")
                       .collect(Collectors.joining(", ")))
               .append("\n");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Admin context — portfolio-wide analytics
    // ─────────────────────────────────────────────────────────────────────────

    private String buildAdminContext(Customer admin) {
        StringBuilder ctx = new StringBuilder();
        ctx.append("Authentication: Logged in as ADMIN\n");
        if (admin != null) {
            ctx.append("Admin: ").append(nullSafe(admin.getName()))
               .append(" (ID: ").append(admin.getId()).append(")\n");
        }
        ctx.append("Today's date: ").append(LocalDate.now()).append("\n");

        appendPortfolioMetrics(ctx);
        appendPendingLoanQueue(ctx);
        appendRecentApprovedLoans(ctx);
        appendTopOverdueCustomers(ctx);
        appendCreditScoreDistribution(ctx);

        ctx.append("\n--- Admin Navigation ---\n");
        ctx.append("Pending loans review: /admin-loans\n");
        ctx.append("Customer management: /customers\n");
        ctx.append("Dashboard: /dashboard\n");

        return ctx.toString();
    }

    private void appendPortfolioMetrics(StringBuilder ctx) {
        List<Customer> allCustomers = customerRepository.findAll();
        List<Loan>     allLoans     = loanRepository.findAll();

        long customerRoleCount = allCustomers.stream()
                .filter(c -> c.getRole() == Customer.Role.CUSTOMER).count();
        long adminCount        = allCustomers.stream()
                .filter(c -> c.getRole() == Customer.Role.ADMIN).count();

        long pending  = allLoans.stream().filter(l -> l.getStatus() == Loan.Status.PENDING).count();
        long approved = allLoans.stream().filter(l -> l.getStatus() == Loan.Status.APPROVED).count();
        long rejected = allLoans.stream().filter(l -> l.getStatus() == Loan.Status.REJECTED).count();

        double approvalRate = allLoans.isEmpty() ? 0 :
                (double) approved / allLoans.size() * 100;

        double totalApprovedValue = allLoans.stream()
                .filter(l -> l.getStatus() == Loan.Status.APPROVED && l.getAmount() != null)
                .mapToDouble(Loan::getAmount).sum();

        double avgCreditScore = allCustomers.stream()
                .filter(c -> c.getRole() == Customer.Role.CUSTOMER && c.getCreditScore() != null)
                .mapToInt(Customer::getCreditScore)
                .average().orElse(0);

        long platformOverdueEmis = emiRepository.findByStatus(EMI.Status.PENDING).stream()
                .filter(e -> e.getDueDate() != null && e.getDueDate().isBefore(LocalDate.now()))
                .count();

        double totalOverdueAmount = emiRepository.findByStatus(EMI.Status.PENDING).stream()
                .filter(e -> e.getDueDate() != null && e.getDueDate().isBefore(LocalDate.now())
                        && e.getAmount() != null)
                .mapToDouble(EMI::getAmount).sum();

        ctx.append("\n--- Portfolio Metrics ---\n");
        ctx.append("Total registered users: ").append(allCustomers.size()).append("\n");
        ctx.append("Retail customers: ").append(customerRoleCount).append("\n");
        ctx.append("Admin accounts: ").append(adminCount).append("\n");
        ctx.append("Total loan applications: ").append(allLoans.size()).append("\n");
        ctx.append("Pending applications: ").append(pending).append("\n");
        ctx.append("Approved loans: ").append(approved).append("\n");
        ctx.append("Rejected loans: ").append(rejected).append("\n");
        ctx.append(String.format("Approval rate: %.1f%%\n", approvalRate));
        ctx.append("Total approved loan value: ").append(money(totalApprovedValue)).append("\n");
        ctx.append("Average customer credit score: ").append(String.format("%.0f", avgCreditScore)).append("\n");
        ctx.append("Platform overdue EMIs: ").append(platformOverdueEmis).append("\n");
        ctx.append("Total overdue EMI value: ").append(money(totalOverdueAmount)).append("\n");
    }

    private void appendPendingLoanQueue(StringBuilder ctx) {
        List<Loan> pendingSample = loanRepository.findByStatus(Loan.Status.PENDING).stream()
                .sorted(Comparator.comparing(Loan::getApplicationDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(ADMIN_LOAN_SAMPLE_LIMIT)
                .toList();

        ctx.append("\n--- Pending Loan Applications (latest ").append(ADMIN_LOAN_SAMPLE_LIMIT).append(") ---\n");
        if (pendingSample.isEmpty()) {
            ctx.append("No pending applications.\n");
        } else {
            for (Loan loan : pendingSample) {
                Customer applicant = loan.getCustomer();
                String applicantName = applicant != null ? nullSafe(applicant.getName()) : "Unknown";
                Integer creditScore  = applicant != null ? applicant.getCreditScore() : null;
                ctx.append(String.format("- Loan #%d | %s | %s | %s | %d months | Rate: %.2f%% | Applied: %s | Credit: %s%n",
                        loan.getId(),
                        applicantName,
                        money(loan.getAmount()),
                        nullSafe(loan.getPurpose()),
                        loan.getTenure() != null ? loan.getTenure() : 0,
                        loan.getInterestRate() != null ? loan.getInterestRate() : 0.0,
                        loan.getApplicationDate() != null ? loan.getApplicationDate().toLocalDate() : "N/A",
                        creditScore != null ? creditScore + " (" + creditGrade(creditScore) + ")" : "N/A"));
            }
        }
    }

    private void appendRecentApprovedLoans(StringBuilder ctx) {
        List<Loan> approvedSample = loanRepository.findByStatus(Loan.Status.APPROVED).stream()
                .sorted(Comparator.comparing(Loan::getApprovalDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();

        ctx.append("\n--- Recently Approved Loans (latest 5) ---\n");
        if (approvedSample.isEmpty()) {
            ctx.append("No approved loans.\n");
        } else {
            for (Loan loan : approvedSample) {
                Customer customer = loan.getCustomer();
                ctx.append(String.format("- Loan #%d | %s | %s | EMI %s/month | Approved: %s%n",
                        loan.getId(),
                        customer != null ? nullSafe(customer.getName()) : "Unknown",
                        money(loan.getAmount()),
                        money(loan.getEmiAmount() != null ? loan.getEmiAmount() : 0),
                        loan.getApprovalDate() != null ? loan.getApprovalDate().toLocalDate() : "N/A"));
            }
        }
    }

    private void appendTopOverdueCustomers(StringBuilder ctx) {
        // Collect pending EMIs that are overdue, group by customer
        List<EMI> allOverdue = emiRepository.findByStatus(EMI.Status.PENDING).stream()
                .filter(e -> e.getDueDate() != null && e.getDueDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(EMI::getDueDate)) // oldest overdue first
                .toList();

        ctx.append("\n--- Top Overdue EMI Customers (sample) ---\n");
        if (allOverdue.isEmpty()) {
            ctx.append("No overdue EMIs across the platform.\n");
            return;
        }

        // Deduplicate by customer, take top N
        allOverdue.stream()
                .filter(e -> e.getLoan() != null && e.getLoan().getCustomer() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getLoan().getCustomer().getId(),
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(java.util.Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(ADMIN_OVERDUE_SAMPLE_LIMIT)
                .forEach(entry -> {
                    Long customerId = entry.getKey();
                    customerRepository.findById(customerId).ifPresent(c -> {
                        double overdueAmt = allOverdue.stream()
                                .filter(e -> e.getLoan().getCustomer().getId().equals(customerId)
                                        && e.getAmount() != null)
                                .mapToDouble(EMI::getAmount).sum();
                        ctx.append(String.format("  - %s (ID %d): %d overdue EMI(s), total %s%n",
                                nullSafe(c.getName()), customerId, entry.getValue(), money(overdueAmt)));
                    });
                });
    }

    private void appendCreditScoreDistribution(StringBuilder ctx) {
        List<Customer> customers = customerRepository.findAll().stream()
                .filter(c -> c.getRole() == Customer.Role.CUSTOMER && c.getCreditScore() != null)
                .toList();

        if (customers.isEmpty()) {
            return;
        }

        long excellent = customers.stream().filter(c -> c.getCreditScore() >= 750).count();
        long good      = customers.stream().filter(c -> c.getCreditScore() >= 650 && c.getCreditScore() < 750).count();
        long fair      = customers.stream().filter(c -> c.getCreditScore() >= 550 && c.getCreditScore() < 650).count();
        long poor      = customers.stream().filter(c -> c.getCreditScore() < 550).count();

        ctx.append("\n--- Credit Score Distribution ---\n");
        ctx.append("Excellent (750+): ").append(excellent).append(" customers\n");
        ctx.append("Good (650–749): ").append(good).append(" customers\n");
        ctx.append("Fair (550–649): ").append(fair).append(" customers\n");
        ctx.append("Needs improvement (<550): ").append(poor).append(" customers\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Fetches a live credit score from the credit-score microservice.
     * Returns null on any failure — callers must handle the null case by
     * falling back to the DB-cached value.
     */
    private CreditScoreDto fetchLiveCreditScore(Long customerId) {
        try {
            Optional<CreditScoreDto> result = creditScoreClientService.getCreditScoreByCustomerId(customerId);
            return result.orElse(null);
        } catch (Exception ex) {
            log.warn("Credit score microservice unavailable for customer {}: {}. Using DB-cached value.",
                    customerId, ex.getMessage());
            return null;
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
