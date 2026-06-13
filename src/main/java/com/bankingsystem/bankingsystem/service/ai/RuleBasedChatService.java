package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.Service.LoanCalculationService;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.EMI;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.EMIRepository;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RuleBasedChatService {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");
    private static final Locale INDIA = Locale.forLanguageTag("en-IN");

    private final LoanRepository loanRepository;
    private final EMIRepository emiRepository;
    private final LoanCalculationService loanCalculationService;

    public RuleBasedChatService(LoanRepository loanRepository,
                                EMIRepository emiRepository,
                                LoanCalculationService loanCalculationService) {
        this.loanRepository = loanRepository;
        this.emiRepository = emiRepository;
        this.loanCalculationService = loanCalculationService;
    }

    public RuleBasedResult respond(AiRole role, Customer customer, String message) {
        String lower = message.toLowerCase();

        if (role == AiRole.ADMIN) {
            return respondAdmin(lower);
        }

        if (containsAny(lower, "credit score", "cibil", "score")) {
            return RuleBasedResult.of(answerCreditScore(customer), "CREDIT_SCORE");
        }

        if (containsAny(lower, "emi", "installment", "instalment")) {
            return RuleBasedResult.of(answerEmi(customer, lower), "EMI");
        }

        if (containsAny(lower, "apply", "loan", "dashboard", "where")) {
            return RuleBasedResult.of(answerLoanNavigation(), "NAVIGATION");
        }

        if (containsAny(lower, "hello", "hi", "hey", "hue")) {
            return RuleBasedResult.of(welcomeMessage(role), "WELCOME");
        }

        return RuleBasedResult.of(fallbackMessage(role), "GENERAL");
    }

    private RuleBasedResult respondAdmin(String lower) {
        if (containsAny(lower, "hello", "hi", "hey", "hue")) {
            return RuleBasedResult.of(
                    "Hello! I am Hue Analyst, your Banking Intelligence assistant. "
                            + "Ask about pending loan applications, portfolio metrics, overdue EMIs, or credit trends.",
                    "WELCOME");
        }

        if (containsAny(lower, "pending", "application", "approve", "queue")) {
            return RuleBasedResult.of(
                    "Review pending loan applications on the Admin Loans page (/admin-loans). "
                            + "You can approve or reject applications and generate EMIs upon approval.",
                    "ADMIN_LOANS");
        }

        if (containsAny(lower, "customer", "portfolio", "metric", "analytics", "insight")) {
            return RuleBasedResult.of(
                    "Open the Customers page (/customers) for customer management, "
                            + "or ask me a specific question about portfolio metrics when the AI service is connected.",
                    "ADMIN_ANALYTICS");
        }

        if (containsAny(lower, "overdue", "emi", "payment")) {
            return RuleBasedResult.of(
                    "Monitor overdue EMIs across the platform from admin dashboards. "
                            + "Customers with overdue payments may need follow-up for credit risk management.",
                    "ADMIN_EMI");
        }

        return RuleBasedResult.of(
                "I can help with pending loan reviews, portfolio analytics, overdue EMI monitoring, and credit trends. "
                        + "Connect AI_API_KEY on Render for full intelligence responses.",
                "ADMIN_GENERAL");
    }

    private String welcomeMessage(AiRole role) {
        if (role == AiRole.ANONYMOUS) {
            return "Hi, I am Hue! Log in to access your credit score, EMIs, and loan details. "
                    + "I can also explain how DebtHues works.";
        }
        return "Hi, I am Hue, your Financial Coach! How can I help with your credit score, EMIs, or loan application today?";
    }

    private String fallbackMessage(AiRole role) {
        if (role == AiRole.ANONYMOUS) {
            return "I can explain DebtHues features and guide you to log in. "
                    + "Try: How do I register? or What features does DebtHues offer?";
        }
        return "I can help with your credit score, EMI details, EMI estimates, and loan applications. "
                + "Try asking: What is my credit score?";
    }

    private String answerCreditScore(Customer customer) {
        if (customer == null) {
            return "Please log in first, then I can fetch your live credit score from your profile.";
        }

        Integer score = customer.getCreditScore();
        if (score == null) {
            return "I could not find a credit score for your profile yet. "
                    + "Complete your financial details or refresh the credit score section on the dashboard.";
        }

        return "Your current credit score is " + score + " (" + creditGrade(score) + "). "
                + "You can also view it on the dashboard credit score card.";
    }

    private String answerEmi(Customer customer, String lowerMessage) {
        Optional<EmiEstimate> estimate = parseEmiEstimate(lowerMessage);
        if (estimate.isPresent()) {
            EmiEstimate e = estimate.get();
            double rate = loanCalculationService.calculateInterestRate("personal", e.amount(), e.tenureMonths());
            double emi = loanCalculationService.calculateEMI(e.amount(), rate, e.tenureMonths());
            return "For " + money(e.amount()) + " over " + e.tenureMonths() + " months, your estimated EMI is "
                    + money(emi) + " per month at about " + String.format("%.2f", rate) + "% annual interest.";
        }

        if (customer == null) {
            return "Please log in first, then I can show your live EMI. "
                    + "For an estimate, ask: what will be my EMI for 100000 for 12 months?";
        }

        List<Loan> approvedLoans = loanRepository.findByCustomerIdAndStatus(customer.getId(), Loan.Status.APPROVED);
        Optional<Loan> latestApproved = approvedLoans.stream()
                .filter(loan -> loan.getEmiAmount() != null)
                .max(Comparator.comparing(Loan::getApprovalDate, Comparator.nullsLast(Comparator.naturalOrder())));

        if (latestApproved.isPresent()) {
            Loan loan = latestApproved.get();
            List<EMI> pending = emiRepository.findPendingEMIsByCustomerId(customer.getId());
            Optional<EMI> nextEmi = pending.stream()
                    .filter(emi -> emi.getDueDate() != null)
                    .min(Comparator.comparing(EMI::getDueDate));

            String dueText = nextEmi
                    .map(emi -> " Your next EMI is due on " + emi.getDueDate() + ".")
                    .orElse(" I do not see a pending EMI due date right now.");
            return "Your current loan EMI is " + money(loan.getEmiAmount()) + " per month." + dueText;
        }

        if (customer.getEmi() != null && customer.getEmi() > 0) {
            return "Your profile EMI is " + money(customer.getEmi()) + " per month. "
                    + "For a new loan estimate, ask: what will be my EMI for 100000 for 12 months?";
        }

        return "I do not see an active approved loan EMI yet. "
                + "For a new loan estimate, ask: what will be my EMI for 100000 for 12 months?";
    }

    private String answerLoanNavigation() {
        return "To apply for a loan, go to Dashboard, open Quick Actions, and choose Apply for Loan. "
                + "You can also open the Loans page and submit a new loan request there.";
    }

    private Optional<EmiEstimate> parseEmiEstimate(String text) {
        Matcher matcher = NUMBER_PATTERN.matcher(text.replace(",", ""));
        Double amount = null;
        Integer tenure = null;

        while (matcher.find()) {
            double value = Double.parseDouble(matcher.group(1));
            int end = matcher.end();
            String after = text.substring(end, Math.min(text.length(), end + 20));

            if (amount == null && value >= 1000) {
                amount = value;
            } else if (after.contains("year")) {
                tenure = (int) Math.round(value * 12);
            } else if (after.contains("month") || after.contains("tenure") || (tenure == null && value > 0 && value <= 360)) {
                tenure = (int) Math.round(value);
            }
        }

        if (amount != null && tenure != null && tenure > 0) {
            return Optional.of(new EmiEstimate(amount, tenure));
        }
        return Optional.empty();
    }

    private boolean containsAny(String text, String... needles) {
        for (String needle : needles) {
            if (text.contains(needle)) {
                return true;
            }
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

    public record RuleBasedResult(String response, String messageType) {
        public static RuleBasedResult of(String response, String messageType) {
            return new RuleBasedResult(response, messageType);
        }
    }

    private record EmiEstimate(double amount, int tenureMonths) {
    }
}
