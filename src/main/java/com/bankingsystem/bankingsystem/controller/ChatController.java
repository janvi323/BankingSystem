package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.Service.LoanCalculationService;
import com.bankingsystem.bankingsystem.dto.ChatRequest;
import com.bankingsystem.bankingsystem.dto.ChatResponse;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.EMI;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import com.bankingsystem.bankingsystem.repository.EMIRepository;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");
    private static final Locale INDIA = Locale.forLanguageTag("en-IN");

    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final EMIRepository emiRepository;
    private final LoanCalculationService loanCalculationService;

    public ChatController(CustomerRepository customerRepository,
                          LoanRepository loanRepository,
                          EMIRepository emiRepository,
                          LoanCalculationService loanCalculationService) {
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
        this.emiRepository = emiRepository;
        this.loanCalculationService = loanCalculationService;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatResponse> sendMessage(@Valid @RequestBody ChatRequest request, HttpSession session) {
        Customer customer = getCurrentCustomer(session).orElse(null);
        String message = request.getMessage() == null ? "" : request.getMessage().trim();
        String lower = message.toLowerCase();

        if (containsAny(lower, "credit score", "cibil", "score")) {
            return ResponseEntity.ok(ChatResponse.ok(answerCreditScore(customer), "CREDIT_SCORE"));
        }

        if (containsAny(lower, "emi", "installment", "instalment")) {
            return ResponseEntity.ok(ChatResponse.ok(answerEmi(customer, lower), "EMI"));
        }

        if (containsAny(lower, "apply", "loan", "dashboard", "where")) {
            return ResponseEntity.ok(ChatResponse.ok(answerLoanNavigation(), "NAVIGATION"));
        }

        if (containsAny(lower, "hello", "hi", "hey", "hue")) {
            return ResponseEntity.ok(ChatResponse.ok("Hi, I am Hue! How can I help you with your credit score, EMI, loan application, or dashboard today?", "WELCOME"));
        }

        return ResponseEntity.ok(ChatResponse.ok("I can help you with your live credit score, EMI details, EMI estimates, and where to apply for a loan on the dashboard. Try asking: What is my credit score?", "GENERAL"));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "message", "Hue is ready"));
    }

    private Optional<Customer> getCurrentCustomer(HttpSession session) {
        Object loggedIn = session == null ? null : session.getAttribute("loggedInCustomer");
        if (loggedIn instanceof Customer customer && customer.getId() != null) {
            return customerRepository.findById(customer.getId()).or(() -> Optional.of(customer));
        }
        return Optional.empty();
    }

    private String answerCreditScore(Customer customer) {
        if (customer == null) {
            return "Please log in first, then I can fetch your live credit score from your profile.";
        }

        Integer score = customer.getCreditScore();
        if (score == null) {
            return "I could not find a credit score for your profile yet. Complete your financial details or refresh the credit score section on the dashboard.";
        }

        return "Your current credit score is " + score + " (" + creditGrade(score) + "). You can also view it on the dashboard credit score card.";
    }

    private String answerEmi(Customer customer, String lowerMessage) {
        Optional<EmiEstimate> estimate = parseEmiEstimate(lowerMessage);
        if (estimate.isPresent()) {
            EmiEstimate e = estimate.get();
            double rate = loanCalculationService.calculateInterestRate("personal", e.amount(), e.tenureMonths());
            double emi = loanCalculationService.calculateEMI(e.amount(), rate, e.tenureMonths());
            return "For " + money(e.amount()) + " over " + e.tenureMonths() + " months, your estimated EMI is " + money(emi) + " per month at about " + String.format("%.2f", rate) + "% annual interest.";
        }

        if (customer == null) {
            return "Please log in first, then I can show your live EMI. For an estimate, ask like: what will be my EMI for 100000 for 12 months?";
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
            return "Your profile EMI is " + money(customer.getEmi()) + " per month. For a new loan estimate, ask: what will be my EMI for 100000 for 12 months?";
        }

        return "I do not see an active approved loan EMI yet. For a new loan estimate, ask: what will be my EMI for 100000 for 12 months?";
    }

    private String answerLoanNavigation() {
        return "To apply for a loan, go to Dashboard, open the Quick Actions area, and choose Apply for Loan. You can also open the Loans page and submit a new loan request there.";
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

    private record EmiEstimate(double amount, int tenureMonths) {
    }
}
