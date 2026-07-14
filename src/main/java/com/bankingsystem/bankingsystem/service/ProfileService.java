package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.Service.LoanCalculationService;
import com.bankingsystem.bankingsystem.Service.ai.*;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * ProfileService — manages all profile read/write operations.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Compute profile completion percentage</li>
 *   <li>Recalculate financial snapshot after any financial field update</li>
 *   <li>Maintain audit log of financial changes</li>
 *   <li>Provide Financial Snapshot for the profile page</li>
 * </ul>
 */
@Service
public class ProfileService {

    private final CustomerRepository      customerRepo;
    private final LoanRepository          loanRepo;
    private final FinancialHealthScorer   healthScorer;
    private final BankOfferEngine         bankOfferEngine;
    private final PreApprovedOfferService preApprovedService;
    private final LoanCalculationService  calcService;
    private final BCryptPasswordEncoder   passwordEncoder = new BCryptPasswordEncoder();

    public ProfileService(CustomerRepository customerRepo,
                          LoanRepository loanRepo,
                          FinancialHealthScorer healthScorer,
                          BankOfferEngine bankOfferEngine,
                          PreApprovedOfferService preApprovedService,
                          LoanCalculationService calcService) {
        this.customerRepo      = customerRepo;
        this.loanRepo          = loanRepo;
        this.healthScorer      = healthScorer;
        this.bankOfferEngine   = bankOfferEngine;
        this.preApprovedService = preApprovedService;
        this.calcService       = calcService;
    }

    // ── Profile Completion ────────────────────────────────────────────────────

    public record CompletionResult(int percent, List<String> missingFields) {}

    public CompletionResult getCompletion(Customer c) {
        List<String> missing = new ArrayList<>();

        // Personal (20 pts)
        if (isBlank(c.getPhone()))          missing.add("Phone number");
        if (c.getDateOfBirth() == null)     missing.add("Date of birth");
        if (isBlank(c.getCity()))           missing.add("City");
        if (isBlank(c.getMaritalStatus()))  missing.add("Marital status");

        // Employment (25 pts)
        if (isBlank(c.getEmploymentType())) missing.add("Employment type");
        if (isBlank(c.getEmployerName()))   missing.add("Employer name");
        if (isBlank(c.getIndustry()))       missing.add("Industry");
        if (isBlank(c.getJobTitle()))       missing.add("Job title");
        if (c.getWorkExperienceYears() == null)         missing.add("Work experience");
        if (c.getEmploymentStabilityYears() == null)    missing.add("Employment stability years");

        // Financial (35 pts)
        if (c.effectiveMonthlyIncome() <= 0) missing.add("Monthly income");
        if (c.getEmi() == null)              missing.add("Existing EMIs");
        if (c.getSavings() == null)          missing.add("Savings information");
        if (c.getMonthlyExpenses() == null)  missing.add("Monthly expenses");
        if (c.getCreditUtilizationRatio() == null) missing.add("Credit utilization");
        if (c.getCreditScore() == null)      missing.add("Credit score");
        if (c.getPaymentHistoryScore() == null) missing.add("Payment history score");

        // Preferences (20 pts)
        if (isBlank(c.getPreferredLoanTypes())) missing.add("Preferred loan types");
        if (c.getPreferredTenure() == null)     missing.add("Preferred tenure");
        if (isBlank(c.getRiskAppetite()))       missing.add("Risk appetite");

        int total = 20; // personal, employment, financial, preferences sections
        int filled = total - missing.size();
        int percent = Math.max(10, Math.min(100, (int) Math.round((double) filled / total * 100)));
        return new CompletionResult(percent, missing);
    }

    // ── Financial Snapshot ────────────────────────────────────────────────────

    public record FinancialSnapshot(
            int    healthScore,
            String grade,
            String riskProfile,
            int    approvalProbability,
            double maxEligibleLoan,
            double recommendedEMILimit,
            String summary
    ) {}

    public FinancialSnapshot getSnapshot(Customer c) {
        List<Loan> loans = loanRepo.findByCustomerId(c.getId());
        FinancialHealthScorer.HealthResult health = healthScorer.compute(c, loans);

        // Approval probability based on health score
        int approvalProb = Math.min(98, Math.max(10, health.totalScore()));

        // Risk profile
        String risk = health.totalScore() >= 80 ? "LOW"
                    : health.totalScore() >= 60 ? "MEDIUM"
                    : health.totalScore() >= 40 ? "HIGH"
                    : "VERY_HIGH";

        // Max eligible loan: 60x monthly income (conservative), capped by credit score
        double monthlyIncome = c.effectiveMonthlyIncome();
        double creditMult    = c.getCreditScore() != null
                ? Math.max(20, Math.min(60, (c.getCreditScore() - 500) / 10.0))
                : 30;
        double maxLoan = monthlyIncome * creditMult;

        // Recommended EMI limit: 40% of monthly income minus existing EMIs
        double existingEmi = c.getEmi() != null ? c.getEmi() : 0;
        double recEMI = Math.max(0, monthlyIncome * 0.40 - existingEmi);

        return new FinancialSnapshot(
            health.totalScore(),
            health.grade(),
            risk,
            approvalProb,
            Math.round(maxLoan),
            Math.round(recEMI),
            health.summary()
        );
    }

    // ── Update Operations ─────────────────────────────────────────────────────

    @Transactional
    public Customer updatePersonal(Customer customer, Map<String, String> data) {
        if (data.containsKey("name"))          customer.setName(data.get("name"));
        if (data.containsKey("phone"))         customer.setPhone(data.get("phone"));
        if (data.containsKey("city"))          customer.setCity(data.get("city"));
        if (data.containsKey("maritalStatus")) customer.setMaritalStatus(data.get("maritalStatus"));
        if (data.containsKey("address"))       customer.setAddress(data.get("address"));
        if (data.containsKey("dateOfBirth") && !data.get("dateOfBirth").isBlank()) {
            try {
                customer.setDateOfBirth(java.time.LocalDate.parse(data.get("dateOfBirth")));
            } catch (Exception ignored) {}
        }
        return customerRepo.save(customer);
    }

    @Transactional
    public Customer updateEmployment(Customer customer, Map<String, String> data) {
        String oldType = customer.getEmploymentType();
        if (data.containsKey("employmentType"))          customer.setEmploymentType(data.get("employmentType"));
        if (data.containsKey("employerName"))            customer.setEmployerName(data.get("employerName"));
        if (data.containsKey("industry"))                customer.setIndustry(data.get("industry"));
        if (data.containsKey("jobTitle"))                customer.setJobTitle(data.get("jobTitle"));
        if (data.containsKey("workExperienceYears"))     customer.setWorkExperienceYears(parseInt(data.get("workExperienceYears")));
        if (data.containsKey("employmentStabilityYears")) customer.setEmploymentStabilityYears(parseInt(data.get("employmentStabilityYears")));

        customer.appendAudit("Employment updated: " + oldType + " → " + customer.getEmploymentType());
        customer.setLastFinancialUpdateAt(LocalDateTime.now());
        return customerRepo.save(customer);
    }

    @Transactional
    public Customer updateFinancial(Customer customer, Map<String, String> data) {
        double oldIncome = customer.effectiveMonthlyIncome();

        if (data.containsKey("monthlyIncome")) {
            Double mi = parseDouble(data.get("monthlyIncome"));
            if (mi != null) { customer.setMonthlyIncome(mi); customer.setIncome(mi * 12); }
        }
        if (data.containsKey("emi"))                     customer.setEmi(parseDouble(data.get("emi")));
        if (data.containsKey("existingLoans"))            customer.setExistingLoans(parseInt(data.get("existingLoans")));
        if (data.containsKey("savings"))                  customer.setSavings(parseDouble(data.get("savings")));
        if (data.containsKey("monthlyExpenses"))          customer.setMonthlyExpenses(parseDouble(data.get("monthlyExpenses")));
        if (data.containsKey("creditUtilizationRatio"))   customer.setCreditUtilizationRatio(parseDouble(data.get("creditUtilizationRatio")));
        if (data.containsKey("paymentHistoryScore"))      customer.setPaymentHistoryScore(parseInt(data.get("paymentHistoryScore")));
        if (data.containsKey("creditScore"))              customer.setCreditScore(parseInt(data.get("creditScore")));
        if (data.containsKey("creditAgeMonths"))          customer.setCreditAgeMonths(parseInt(data.get("creditAgeMonths")));

        // Recalculate DTI
        if (customer.effectiveMonthlyIncome() > 0 && customer.getEmi() != null) {
            double dti = customer.getEmi() / customer.effectiveMonthlyIncome();
            customer.setDebtToIncomeRatio(Math.min(1.0, dti));
        }

        double newIncome = customer.effectiveMonthlyIncome();
        customer.appendAudit(String.format("Financial updated: income ₹%.0f→₹%.0f/mo, EMI ₹%.0f",
            oldIncome, newIncome, customer.getEmi() != null ? customer.getEmi() : 0));
        customer.setLastFinancialUpdateAt(LocalDateTime.now());
        return customerRepo.save(customer);
    }

    @Transactional
    public Customer updatePreferences(Customer customer, Map<String, String> data) {
        if (data.containsKey("preferredLoanTypes"))  customer.setPreferredLoanTypes(data.get("preferredLoanTypes"));
        if (data.containsKey("preferredTenure"))     customer.setPreferredTenure(parseInt(data.get("preferredTenure")));
        if (data.containsKey("riskAppetite"))        customer.setRiskAppetite(data.get("riskAppetite"));
        return customerRepo.save(customer);
    }

    @Transactional
    public boolean changePassword(Customer customer, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, customer.getPassword())) return false;
        customer.setPassword(passwordEncoder.encode(newPassword));
        customer.appendAudit("Password changed");
        customerRepo.save(customer);
        return true;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean isBlank(String s) { return s == null || s.isBlank(); }

    private Integer parseInt(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return null; }
    }

    private Double parseDouble(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return null; }
    }
}
