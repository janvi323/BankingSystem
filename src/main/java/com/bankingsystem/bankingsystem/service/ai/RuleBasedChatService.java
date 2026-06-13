package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.Service.LoanCalculationService;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.EMI;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import com.bankingsystem.bankingsystem.repository.EMIRepository;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * RuleBasedChatService — deterministic, DB-backed fallback when the LLM is unavailable.
 *
 * <p>Design principles:
 * <ul>
 *   <li>Admin responses always fetch live data from repositories — never placeholder text</li>
 *   <li>Customer responses use the resolved customer entity + live loan/EMI data</li>
 *   <li>Anonymous responses explain platform features and guide to login</li>
 *   <li>All monetary values use the Indian locale (₹)</li>
 * </ul>
 */
@Service
public class RuleBasedChatService {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+(?:[,.]\\d+)*)");
    private static final Locale INDIA = Locale.forLanguageTag("en-IN");

    private final LoanRepository loanRepository;
    private final EMIRepository emiRepository;
    private final CustomerRepository customerRepository;
    private final LoanCalculationService loanCalculationService;

    public RuleBasedChatService(LoanRepository loanRepository,
                                EMIRepository emiRepository,
                                CustomerRepository customerRepository,
                                LoanCalculationService loanCalculationService) {
        this.loanRepository          = loanRepository;
        this.emiRepository           = emiRepository;
        this.customerRepository      = customerRepository;
        this.loanCalculationService  = loanCalculationService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Entry point
    // ─────────────────────────────────────────────────────────────────────────

    public RuleBasedResult respond(AiRole role, Customer customer, String message) {
        String lower = message.toLowerCase();

        return switch (role) {
            case ADMIN     -> respondAdmin(lower);
            case CUSTOMER  -> respondCustomer(customer, lower);
            case ANONYMOUS -> respondAnonymous(lower);
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Admin responses — live DB-backed data
    // ─────────────────────────────────────────────────────────────────────────

    private RuleBasedResult respondAdmin(String lower) {
        // Greeting
        if (containsAny(lower, "hello", "hi", "hey", "hue")) {
            long pending = loanRepository.findByStatus(Loan.Status.PENDING).size();
            return RuleBasedResult.of(
                    "Hello! I am Hue Analyst, your Banking Intelligence assistant. " +
                    "Currently there are **" + pending + " pending loan application(s)** awaiting review. " +
                    "Ask me about pending loans, portfolio metrics, overdue EMIs, or credit trends.",
                    "WELCOME");
        }

        // Pending loan count / applications
        if (containsAny(lower, "pending", "application", "queue", "review")) {
            return RuleBasedResult.of(buildAdminPendingLoansResponse(), "ADMIN_LOANS");
        }

        // Approval / rejection stats
        if (containsAny(lower, "approved", "rejected", "approval rate", "rejection")) {
            return RuleBasedResult.of(buildAdminApprovalStatsResponse(), "ADMIN_APPROVAL");
        }

        // Portfolio / overall metrics
        if (containsAny(lower, "portfolio", "total loan", "all loan", "metric", "overview", "summary")) {
            return RuleBasedResult.of(buildAdminPortfolioSummary(), "ADMIN_PORTFOLIO");
        }

        // Credit score analytics
        if (containsAny(lower, "credit score", "credit", "cibil", "average score")) {
            return RuleBasedResult.of(buildAdminCreditScoreResponse(), "ADMIN_CREDIT");
        }

        // Overdue / EMI health
        if (containsAny(lower, "overdue", "emi", "payment", "delinquent")) {
            return RuleBasedResult.of(buildAdminOverdueResponse(), "ADMIN_EMI");
        }

        // Customer count
        if (containsAny(lower, "customer", "user", "registered", "how many")) {
            return RuleBasedResult.of(buildAdminCustomerCountResponse(), "ADMIN_CUSTOMERS");
        }

        // Default admin fallback
        return RuleBasedResult.of(buildAdminFallback(), "ADMIN_GENERAL");
    }

    private String buildAdminPendingLoansResponse() {
        List<Loan> pending = loanRepository.findByStatus(Loan.Status.PENDING).stream()
                .sorted(Comparator.comparing(Loan::getApplicationDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        if (pending.isEmpty()) {
            return "✅ No pending loan applications at this time. The queue is clear.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📋 **").append(pending.size()).append(" pending loan application(s)**:\n\n");

        pending.stream().limit(5).forEach(loan -> {
            Customer c = loan.getCustomer();
            sb.append("• Loan #").append(loan.getId())
              .append(" — ").append(c != null ? c.getName() : "Unknown")
              .append(" — ").append(money(loan.getAmount()))
              .append(" (").append(loan.getPurpose()).append(")")
              .append(" — Applied: ").append(loan.getApplicationDate() != null
                      ? loan.getApplicationDate().toLocalDate() : "N/A")
              .append("\n");
        });

        if (pending.size() > 5) {
            sb.append("...and ").append(pending.size() - 5).append(" more. ");
        }
        sb.append("\nReview all pending applications at **/admin-loans**.");
        return sb.toString();
    }

    private String buildAdminApprovalStatsResponse() {
        List<Loan> all      = loanRepository.findAll();
        long approved       = all.stream().filter(l -> l.getStatus() == Loan.Status.APPROVED).count();
        long rejected       = all.stream().filter(l -> l.getStatus() == Loan.Status.REJECTED).count();
        long pending        = all.stream().filter(l -> l.getStatus() == Loan.Status.PENDING).count();
        long decided        = approved + rejected;
        double approvalRate = decided == 0 ? 0 : (double) approved / decided * 100;

        double totalApprovedValue = all.stream()
                .filter(l -> l.getStatus() == Loan.Status.APPROVED && l.getAmount() != null)
                .mapToDouble(Loan::getAmount).sum();

        return String.format(
                "📊 **Loan Decision Summary**:\n" +
                "• Total applications: %d\n" +
                "• Approved: %d | Rejected: %d | Pending: %d\n" +
                "• Approval rate (decided): %.1f%%\n" +
                "• Total approved loan value: %s\n\n" +
                "Review and process pending applications at **/admin-loans**.",
                all.size(), approved, rejected, pending, approvalRate, money(totalApprovedValue));
    }

    private String buildAdminPortfolioSummary() {
        List<Loan>     allLoans    = loanRepository.findAll();
        List<Customer> allCustomers = customerRepository.findAll();

        long customers  = allCustomers.stream().filter(c -> c.getRole() == Customer.Role.CUSTOMER).count();
        long approved   = allLoans.stream().filter(l -> l.getStatus() == Loan.Status.APPROVED).count();
        long pending    = allLoans.stream().filter(l -> l.getStatus() == Loan.Status.PENDING).count();

        double totalBook = allLoans.stream()
                .filter(l -> l.getStatus() == Loan.Status.APPROVED && l.getAmount() != null)
                .mapToDouble(Loan::getAmount).sum();

        long overdueEmis = emiRepository.findByStatus(EMI.Status.PENDING).stream()
                .filter(e -> e.getDueDate() != null && e.getDueDate().isBefore(LocalDate.now()))
                .count();

        double avgScore = allCustomers.stream()
                .filter(c -> c.getRole() == Customer.Role.CUSTOMER && c.getCreditScore() != null)
                .mapToInt(Customer::getCreditScore)
                .average().orElse(0);

        return String.format(
                "📈 **Portfolio Overview** (as of %s):\n" +
                "• Retail customers: %d\n" +
                "• Total loan applications: %d | Approved: %d | Pending: %d\n" +
                "• Total approved loan book: %s\n" +
                "• Platform overdue EMIs: %d\n" +
                "• Average customer credit score: %.0f\n\n" +
                "Access detailed admin tools at **/admin-loans** (loans) and **/customers** (customers).",
                LocalDate.now(), customers, allLoans.size(), approved, pending,
                money(totalBook), overdueEmis, avgScore);
    }

    private String buildAdminCreditScoreResponse() {
        List<Customer> customers = customerRepository.findAll().stream()
                .filter(c -> c.getRole() == Customer.Role.CUSTOMER && c.getCreditScore() != null)
                .toList();

        if (customers.isEmpty()) {
            return "No customers with credit scores on record yet.";
        }

        double avg      = customers.stream().mapToInt(Customer::getCreditScore).average().orElse(0);
        long excellent  = customers.stream().filter(c -> c.getCreditScore() >= 750).count();
        long good       = customers.stream().filter(c -> c.getCreditScore() >= 650 && c.getCreditScore() < 750).count();
        long fair       = customers.stream().filter(c -> c.getCreditScore() >= 550 && c.getCreditScore() < 650).count();
        long poor       = customers.stream().filter(c -> c.getCreditScore() < 550).count();

        return String.format(
                "💳 **Portfolio Credit Score Distribution** (%d customers with scores):\n" +
                "• Average score: %.0f\n" +
                "• Excellent (750+): %d customers\n" +
                "• Good (650–749): %d customers\n" +
                "• Fair (550–649): %d customers\n" +
                "• Needs improvement (<550): %d customers\n\n" +
                "High-risk customers (score <550) represent %.1f%% of the scored portfolio.",
                customers.size(), avg, excellent, good, fair, poor,
                customers.isEmpty() ? 0 : (double) poor / customers.size() * 100);
    }

    private String buildAdminOverdueResponse() {
        List<EMI> allOverdue = emiRepository.findByStatus(EMI.Status.PENDING).stream()
                .filter(e -> e.getDueDate() != null && e.getDueDate().isBefore(LocalDate.now()))
                .toList();

        long count = allOverdue.size();
        if (count == 0) {
            return "✅ No overdue EMIs across the platform. All EMI payments are current.";
        }

        double totalOverdueAmount = allOverdue.stream()
                .filter(e -> e.getAmount() != null).mapToDouble(EMI::getAmount).sum();

        // Top 3 customers by overdue count
        String topOverdue = allOverdue.stream()
                .filter(e -> e.getLoan() != null && e.getLoan().getCustomer() != null)
                .collect(Collectors.groupingBy(e -> e.getLoan().getCustomer(), Collectors.counting()))
                .entrySet().stream()
                .sorted(java.util.Map.Entry.<Customer, Long>comparingByValue().reversed())
                .limit(3)
                .map(e -> e.getKey().getName() + " (" + e.getValue() + " EMIs)")
                .collect(Collectors.joining(", "));

        return String.format(
                "⚠️ **Platform EMI Overdue Report**:\n" +
                "• Total overdue EMIs: %d\n" +
                "• Total overdue amount: %s\n" +
                "• Top affected customers: %s\n\n" +
                "Follow up with overdue customers through the **/customers** admin page.",
                count, money(totalOverdueAmount), topOverdue.isBlank() ? "N/A" : topOverdue);
    }

    private String buildAdminCustomerCountResponse() {
        List<Customer> all  = customerRepository.findAll();
        long customers      = all.stream().filter(c -> c.getRole() == Customer.Role.CUSTOMER).count();
        long admins         = all.stream().filter(c -> c.getRole() == Customer.Role.ADMIN).count();

        return String.format(
                "👥 **Registered Users**:\n" +
                "• Total accounts: %d\n" +
                "• Retail customers: %d\n" +
                "• Admin accounts: %d\n\n" +
                "Manage customers at **/customers**.",
                all.size(), customers, admins);
    }

    private String buildAdminFallback() {
        List<Loan> pending = loanRepository.findByStatus(Loan.Status.PENDING);
        long overdue = emiRepository.findByStatus(EMI.Status.PENDING).stream()
                .filter(e -> e.getDueDate() != null && e.getDueDate().isBefore(LocalDate.now()))
                .count();

        return "I can help with pending loan reviews, portfolio analytics, overdue EMI monitoring, " +
               "customer stats, and credit trends.\n\n" +
               "**Quick stats**: " + pending.size() + " pending loan(s) | " + overdue + " overdue EMI(s).\n\n" +
               "Enable AI_API_KEY on Render for full intelligent analytics responses.";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Customer responses
    // ─────────────────────────────────────────────────────────────────────────

    private RuleBasedResult respondCustomer(Customer customer, String lower) {
        // EMI estimate (must check before generic EMI — it parses numbers)
        Optional<EmiEstimate> emiEstimate = parseEmiEstimate(lower);
        if (emiEstimate.isPresent()) {
            return RuleBasedResult.of(answerEmiEstimate(emiEstimate.get()), "EMI_ESTIMATE");
        }

        if (containsAny(lower, "credit score", "cibil", "score")) {
            return RuleBasedResult.of(answerCreditScore(customer), "CREDIT_SCORE");
        }

        if (containsAny(lower, "emi", "installment", "instalment")) {
            return RuleBasedResult.of(answerEmi(customer), "EMI");
        }

        if (containsAny(lower, "loan", "apply", "application", "borrow")) {
            return RuleBasedResult.of(answerLoan(customer), "LOAN");
        }

        if (containsAny(lower, "improve", "increase", "boost", "better")) {
            return RuleBasedResult.of(answerCreditImprovement(customer), "CREDIT_IMPROVEMENT");
        }

        if (containsAny(lower, "hello", "hi", "hey", "hue", "help")) {
            return RuleBasedResult.of(buildCustomerWelcome(customer), "WELCOME");
        }

        return RuleBasedResult.of(buildCustomerFallback(customer), "GENERAL");
    }

    private String buildCustomerWelcome(Customer customer) {
        if (customer == null) {
            return "Hi! I am Hue, your Financial Coach. Please log in to access your personal banking information.";
        }
        String firstName = customer.getName() != null
                ? customer.getName().split("\\s+")[0] : "there";
        List<Loan> loans = loanRepository.findByCustomerId(customer.getId());
        long overdue     = emiRepository.findByStatus(EMI.Status.PENDING).stream()
                .filter(e -> e.getDueDate() != null
                        && e.getDueDate().isBefore(LocalDate.now())
                        && e.getLoan() != null
                        && e.getLoan().getCustomer() != null
                        && e.getLoan().getCustomer().getId().equals(customer.getId()))
                .count();

        StringBuilder msg = new StringBuilder("Hi ").append(firstName).append("! I'm Hue, your Financial Coach. 👋\n");
        if (overdue > 0) {
            msg.append("⚠️ You have ").append(overdue).append(" overdue EMI(s) — paying promptly helps your credit score.\n");
        }
        msg.append("You have ").append(loans.size()).append(" loan application(s) on record. ");
        msg.append("Ask me about your credit score, EMI schedule, or loan status!");
        return msg.toString();
    }

    private String buildCustomerFallback(Customer customer) {
        if (customer == null) {
            return "I can explain DebtHues features and help you get started. " +
                   "Try: 'What features does DebtHues offer?' or log in to see your personal data.";
        }
        return "I can help with your credit score, EMI schedule, loan status, and financial tips. " +
               "Try: 'What is my credit score?' or 'What is my next EMI due date?'";
    }

    private String answerCreditScore(Customer customer) {
        if (customer == null) {
            return "Please log in first — I can then fetch your live credit score and give you personalized tips.";
        }

        Integer score = customer.getCreditScore();
        if (score == null) {
            return "Your credit score hasn't been calculated yet. " +
                   "Go to **Dashboard → Credit Score** to calculate it using your financial details. " +
                   "You'll need your income, debt-to-income ratio, and payment history.";
        }

        String grade = creditGrade(score);
        StringBuilder msg = new StringBuilder();
        msg.append("Your current credit score is **").append(score).append("** (").append(grade).append(").\n\n");
        msg.append(creditScoreContext(score));
        return msg.toString();
    }

    private String creditScoreContext(int score) {
        if (score >= 750) {
            return "🌟 Excellent! You qualify for the best interest rates on loans. " +
                   "Keep up your on-time payment streak to maintain this score.";
        } else if (score >= 650) {
            return "👍 Good score. You qualify for most standard loan products. " +
                   "Improving your payment history and reducing utilization could push you to Excellent.";
        } else if (score >= 550) {
            return "📊 Fair score. You may still qualify for some loans but at higher rates. " +
                   "Focus on paying EMIs on time and reducing your debt-to-income ratio.";
        } else {
            return "⚠️ Your score needs improvement. Prioritize paying overdue EMIs first, " +
                   "then maintain a streak of on-time payments for 6+ months to see improvement.";
        }
    }

    private String answerCreditImprovement(Customer customer) {
        if (customer == null) {
            return "Log in to get personalized credit score improvement tips based on your profile.";
        }

        Integer score = customer.getCreditScore();
        if (score == null) {
            return "Calculate your credit score first (Dashboard → Credit Score), " +
                   "then I can give you personalized improvement tips.";
        }

        StringBuilder tips = new StringBuilder("Here are personalized tips to improve your credit score:\n\n");

        if (customer.getPaymentHistoryScore() != null && customer.getPaymentHistoryScore() < 80) {
            tips.append("1️⃣ **Pay EMIs on time** — Payment history is the biggest factor. " +
                        "Your score is ").append(customer.getPaymentHistoryScore()).append("/100. " +
                        "Set up reminders or auto-pay for your EMI due dates.\n");
        }

        if (customer.getCreditUtilizationRatio() != null && customer.getCreditUtilizationRatio() > 0.30) {
            tips.append("2️⃣ **Reduce credit utilization** — Currently at ")
                .append(String.format("%.0f%%", customer.getCreditUtilizationRatio() * 100))
                .append(". Aim for below 30% by paying down balances.\n");
        }

        if (customer.getDebtToIncomeRatio() != null && customer.getDebtToIncomeRatio() > 0.40) {
            tips.append("3️⃣ **Lower your debt-to-income ratio** — Avoid taking new loans until you " +
                        "reduce existing debt. Consider debt consolidation.\n");
        }

        if (customer.getCreditAgeMonths() != null && customer.getCreditAgeMonths() < 24) {
            tips.append("4️⃣ **Build credit age** — Your history is ")
                .append(customer.getCreditAgeMonths())
                .append(" months old. Keep existing accounts open and active.\n");
        }

        tips.append("\n✅ **Next step**: Focus on tip #1 and check your score again in 30 days.");
        return tips.toString();
    }

    private String answerEmi(Customer customer) {
        if (customer == null) {
            return "Please log in first to see your EMI schedule. " +
                   "For an estimate, ask: 'What will be my EMI for ₹1,00,000 for 12 months?'";
        }

        List<Loan> approvedLoans = loanRepository.findByCustomerIdAndStatus(
                customer.getId(), Loan.Status.APPROVED);

        if (approvedLoans.isEmpty()) {
            if (customer.getEmi() != null && customer.getEmi() > 0) {
                return "Your profile EMI is " + money(customer.getEmi()) + " per month. " +
                       "For a new estimate, ask: 'What will my EMI be for ₹1,00,000 for 12 months?'";
            }
            return "No active approved loans found. " +
                   "Apply for a loan at **Dashboard → Quick Actions → Apply for Loan**. " +
                   "For an estimate, ask: 'What will my EMI be for ₹1,00,000 for 12 months?'";
        }

        // Find the most recent approved loan with EMI amount
        Optional<Loan> latestLoan = approvedLoans.stream()
                .filter(l -> l.getEmiAmount() != null)
                .max(Comparator.comparing(Loan::getApprovalDate,
                        Comparator.nullsLast(Comparator.naturalOrder())));

        if (latestLoan.isEmpty()) {
            return "I found approved loan(s) but couldn't determine the EMI amount. " +
                   "Please check your loan details on the **/loans** page.";
        }

        Loan loan = latestLoan.get();
        List<EMI> pendingEmis = emiRepository.findPendingEMIsByCustomerId(customer.getId());

        Optional<EMI> nextEmi = pendingEmis.stream()
                .filter(e -> e.getDueDate() != null)
                .min(Comparator.comparing(EMI::getDueDate));

        StringBuilder msg = new StringBuilder();
        msg.append("Your current loan EMI is **").append(money(loan.getEmiAmount())).append("** per month");
        if (loan.getInterestRate() != null) {
            msg.append(String.format(" at %.2f%% annual interest", loan.getInterestRate()));
        }
        msg.append(".\n");

        nextEmi.ifPresentOrElse(emi -> {
            long days = LocalDate.now().until(emi.getDueDate()).getDays();
            msg.append("📅 Next EMI due: **").append(emi.getDueDate()).append("**");
            if (days >= 0) {
                msg.append(" — in ").append(days).append(" day(s)");
            } else {
                msg.append(" — **OVERDUE by ").append(-days).append(" day(s)**");
            }
            msg.append(".\n");
        }, () -> msg.append("No upcoming pending EMI found.\n"));

        return msg.toString();
    }

    private String answerEmiEstimate(EmiEstimate estimate) {
        double rate = loanCalculationService.calculateInterestRate(
                estimate.purpose() != null ? estimate.purpose() : "personal",
                estimate.amount(), estimate.tenureMonths());
        double emi   = loanCalculationService.calculateEMI(estimate.amount(), rate, estimate.tenureMonths());
        double total = loanCalculationService.calculateTotalAmount(emi, estimate.tenureMonths());
        double interest = total - estimate.amount();

        return String.format(
                "💰 **EMI Estimate**:\n" +
                "• Loan amount: %s\n" +
                "• Tenure: %d months\n" +
                "• Interest rate: %.2f%% per annum\n" +
                "• **Monthly EMI: %s**\n" +
                "• Total payable: %s\n" +
                "• Total interest: %s\n\n" +
                "To apply, go to **Dashboard → Quick Actions → Apply for Loan**.",
                money(estimate.amount()), estimate.tenureMonths(),
                rate, money(emi), money(total), money(interest));
    }

    private String answerLoan(Customer customer) {
        if (customer == null) {
            return "Please log in to view your loans or apply for a new one.";
        }

        List<Loan> loans = loanRepository.findByCustomerId(customer.getId());
        if (loans.isEmpty()) {
            return "You have no loan applications yet. " +
                   "To apply, go to **Dashboard → Quick Actions → Apply for Loan** or visit **/apply-loan**. " +
                   "We offer Home, Car, Education, Business, Personal, and Medical loans.";
        }

        long approved = loans.stream().filter(l -> l.getStatus() == Loan.Status.APPROVED).count();
        long pending  = loans.stream().filter(l -> l.getStatus() == Loan.Status.PENDING).count();
        long rejected = loans.stream().filter(l -> l.getStatus() == Loan.Status.REJECTED).count();

        return String.format(
                "You have **%d loan application(s)**: %d approved, %d pending, %d rejected.\n\n" +
                "View full details at **/loans**. " +
                "To apply for a new loan, visit **Dashboard → Quick Actions → Apply for Loan**.",
                loans.size(), approved, pending, rejected);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Anonymous responses
    // ─────────────────────────────────────────────────────────────────────────

    private RuleBasedResult respondAnonymous(String lower) {
        if (containsAny(lower, "hello", "hi", "hey", "hue")) {
            return RuleBasedResult.of(
                    "Hi! I am Hue, the DebtHues banking assistant. 👋\n" +
                    "Log in to access your credit score, EMIs, and loan details.\n" +
                    "Or ask me what DebtHues can do for you!",
                    "WELCOME");
        }
        if (containsAny(lower, "register", "sign up", "create account", "new account")) {
            return RuleBasedResult.of(
                    "To register on DebtHues, visit **/register** and fill in your details. " +
                    "You can also sign in instantly with your Google account via Google OAuth — no password needed!",
                    "REGISTRATION");
        }
        if (containsAny(lower, "login", "log in", "sign in", "google")) {
            return RuleBasedResult.of(
                    "Log in at **/login** using your email and password, " +
                    "or click **Continue with Google** for instant OAuth login.",
                    "LOGIN");
        }
        if (containsAny(lower, "feature", "offer", "what can", "what does", "about")) {
            return RuleBasedResult.of(
                    "DebtHues offers:\n" +
                    "• 💳 Credit score calculation with detailed breakdown\n" +
                    "• 💰 Loan applications (Home, Car, Education, Business, Personal, Medical)\n" +
                    "• 📅 EMI payment tracking with due date reminders\n" +
                    "• 📊 Personal financial dashboard\n" +
                    "• 🔐 Secure Google OAuth login\n\n" +
                    "Register at **/register** or log in at **/login** to get started!",
                    "FEATURES");
        }
        if (containsAny(lower, "credit score", "cibil", "score")) {
            return RuleBasedResult.of(
                    "Your credit score reflects your creditworthiness (300–900 range). " +
                    "DebtHues calculates it using: income, debt-to-income ratio, payment history, " +
                    "credit utilization, credit age, and number of accounts.\n\n" +
                    "Log in at **/login** to calculate and view your personal credit score.",
                    "CREDIT_INFO");
        }
        if (containsAny(lower, "loan", "apply", "borrow", "emi")) {
            return RuleBasedResult.of(
                    "DebtHues offers loans for Home Purchase, Car, Education, Business, Personal needs, " +
                    "Debt Consolidation, and Medical Expenses — with interest rates from 9% to 14%.\n\n" +
                    "Log in at **/login** to apply for a loan and get instant EMI calculations.",
                    "LOAN_INFO");
        }
        return RuleBasedResult.of(
                "I can explain DebtHues features and guide you to get started. " +
                "Try: 'What features does DebtHues offer?' or 'How do I register?'\n\n" +
                "Log in at **/login** to access your personal banking dashboard.",
                "GENERAL");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EMI estimate parser
    // ─────────────────────────────────────────────────────────────────────────

    private Optional<EmiEstimate> parseEmiEstimate(String text) {
        // Remove commas from numbers (e.g. 1,00,000 → 100000)
        String cleaned = text.replace(",", "");
        Matcher matcher = NUMBER_PATTERN.matcher(cleaned);

        Double amount = null;
        Integer tenure = null;
        String purpose = null;

        // Extract loan purpose if mentioned
        String[] purposes = {"home", "car", "education", "business", "personal", "medical"};
        for (String p : purposes) {
            if (cleaned.contains(p)) {
                purpose = p;
                break;
            }
        }

        while (matcher.find()) {
            double value = Double.parseDouble(matcher.group(1).replace(",", ""));
            int end      = matcher.end();
            String after = cleaned.substring(end, Math.min(cleaned.length(), end + 25)).toLowerCase();

            if (amount == null && value >= 1000) {
                amount = value;
            } else if (after.contains("year")) {
                tenure = (int) Math.round(value * 12);
            } else if (after.contains("month") || after.contains("tenure") ||
                       (tenure == null && value > 0 && value <= 360)) {
                tenure = (int) Math.round(value);
            }
        }

        if (amount != null && tenure != null && tenure > 0) {
            return Optional.of(new EmiEstimate(amount, tenure, purpose));
        }
        return Optional.empty();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utility methods
    // ─────────────────────────────────────────────────────────────────────────

    private boolean containsAny(String text, String... needles) {
        for (String needle : needles) {
            if (text.contains(needle)) return true;
        }
        return false;
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

    // ─────────────────────────────────────────────────────────────────────────
    // Inner records
    // ─────────────────────────────────────────────────────────────────────────

    public record RuleBasedResult(String response, String messageType) {
        public static RuleBasedResult of(String response, String messageType) {
            return new RuleBasedResult(response, messageType);
        }
    }

    private record EmiEstimate(double amount, int tenureMonths, String purpose) {}
}
