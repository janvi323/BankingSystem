package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.dto.PreApprovedOffer;
import com.bankingsystem.bankingsystem.dto.PreApprovedOffersResult;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import com.bankingsystem.bankingsystem.Service.LoanCalculationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * PreApprovedOfferService — calculates what loan amounts a customer is
 * pre-approved for across 4 categories without submitting an application.
 *
 * <p>Categories: Personal Loan, Car Loan, Education Loan, Home Loan
 *
 * <p>Eligibility formula per category:
 * <pre>
 *   maxAmount = min(
 *     monthlyIncome × 60 × repaymentCapacity,
 *     categoryAbsoluteMax,
 *     creditScoreMultiplier × annualIncome
 *   )
 * </pre>
 */
@Service
public class PreApprovedOfferService {

    private final LoanRepository loanRepository;
    private final LoanCalculationService calcService;

    public PreApprovedOfferService(LoanRepository loanRepository,
                                   LoanCalculationService calcService) {
        this.loanRepository = loanRepository;
        this.calcService    = calcService;
    }

    // ── Category Configs ──────────────────────────────────────────────────────

    private record LoanCategory(
            String name, String icon, String purpose,
            int    minCreditScore,
            double absoluteMax,       // absolute ceiling in INR
            double incomeMultiplier,  // max = annualIncome × this
            double baseRate,
            String applyPurposeParam
    ) {}

    private static final List<LoanCategory> CATEGORIES = List.of(
        new LoanCategory("Personal Loan",   "💳", "personal",      650,  1_500_000,  4.0, 12.5, "personal"),
        new LoanCategory("Car Loan",        "🚗", "car purchase",  680,  3_000_000,  6.0, 10.5, "car+purchase"),
        new LoanCategory("Education Loan",  "🎓", "education",     620,  5_000_000,  8.0,  9.0, "education"),
        new LoanCategory("Home Loan",       "🏠", "home purchase", 700, 10_000_000, 10.0,  9.5, "home+purchase")
    );

    // ── Public API ────────────────────────────────────────────────────────────

    public PreApprovedOffersResult computeOffers(Customer customer) {
        List<PreApprovedOffer> offers = new ArrayList<>();
        List<Loan> existingLoans     = loanRepository.findByCustomerId(customer.getId());

        for (LoanCategory cat : CATEGORIES) {
            offers.add(computeOffer(cat, customer, existingLoans));
        }

        return new PreApprovedOffersResult(offers);
    }

    // ── Per-Category Calculation ──────────────────────────────────────────────

    private PreApprovedOffer computeOffer(LoanCategory cat, Customer customer,
                                           List<Loan> existingLoans) {
        PreApprovedOffer offer = new PreApprovedOffer();
        offer.setLoanCategory(cat.name());
        offer.setIcon(cat.icon());
        offer.setMinInterestRate(cat.baseRate());
        offer.setApplyUrl("/loans/apply?purpose=" + cat.applyPurposeParam());

        int creditScore = customer.getCreditScore() != null ? customer.getCreditScore() : 0;

        // ── Credit score gate ─────────────────────────────────────────────
        if (creditScore < cat.minCreditScore()) {
            offer.setEligible(false);
            offer.setMaxAmount(0);
            offer.setReason(String.format(
                "Minimum credit score required: %d. Your score: %d. " +
                "Improve by %d points to unlock this offer.",
                cat.minCreditScore(), creditScore, cat.minCreditScore() - creditScore));
            return offer;
        }

        // ── Income check ──────────────────────────────────────────────────
        Double income = customer.getIncome();
        if (income == null || income <= 0) {
            offer.setEligible(false);
            offer.setMaxAmount(0);
            offer.setReason("Income information required to calculate pre-approved amount.");
            return offer;
        }

        double monthlyIncome     = income / 12.0;
        double existingMonthlyEmi = customer.getEmi() != null ? customer.getEmi() : 0;
        double dti               = customer.getDebtToIncomeRatio() != null
                                   ? customer.getDebtToIncomeRatio() : 0.3;

        // Available repayment capacity (40% of income after existing EMIs)
        double availableMonthly  = Math.max(0, monthlyIncome * 0.40 - existingMonthlyEmi);

        if (availableMonthly <= 0) {
            offer.setEligible(false);
            offer.setMaxAmount(0);
            offer.setReason("Existing EMI obligations use up all available repayment capacity. Reduce EMIs first.");
            return offer;
        }

        // Max EMI capacity → max principal (at 60-month tenure for calculation)
        double maxByCapacity    = calcService.calculateEMI(1_000_000, cat.baseRate(), 60);
        // Reverse: availableMonthly EMI → principal at 60 months
        double maxByIncome      = (availableMonthly / maxByCapacity) * 1_000_000;
        double maxByMultiplier  = income * cat.incomeMultiplier();
        double creditBonus      = creditScore >= 750 ? 1.1 : creditScore >= 700 ? 1.0 : 0.85;

        double maxAmount = Math.min(
                Math.min(maxByIncome * creditBonus, maxByMultiplier),
                cat.absoluteMax()
        );

        // Round to nearest ₹10,000
        maxAmount = Math.floor(maxAmount / 10_000) * 10_000;

        if (maxAmount < 50_000) {
            offer.setEligible(false);
            offer.setMaxAmount(0);
            offer.setReason("Based on your income and existing EMIs, the eligible amount is too low to offer.");
            return offer;
        }

        offer.setEligible(true);
        offer.setMaxAmount(maxAmount);
        offer.setReason(String.format(
            "Based on your income and credit score %d, you qualify for up to ₹%.0f.", creditScore, maxAmount));
        return offer;
    }
}
