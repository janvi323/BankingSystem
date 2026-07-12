package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.dto.BankComparisonResult;
import com.bankingsystem.bankingsystem.dto.BankOffer;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.Service.LoanCalculationService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * BankOfferEngine — computes personalized loan offers from 6 simulated bank profiles.
 *
 * <p>For each bank, the engine calculates:
 * <ul>
 *   <li>approvalProbability — how likely this bank is to approve, given the customer's profile</li>
 *   <li>personalizedRate — base rate adjusted by the customer's risk score</li>
 *   <li>EMI and total payable</li>
 *   <li>AI-generated reason text explaining the bank's position</li>
 * </ul>
 *
 * <p>Banks are ranked by a composite score that balances approval probability
 * and interest rate. The top-ranked bank is flagged as recommended.
 */
@Service
public class BankOfferEngine {

    private final LoanCalculationService calcService;

    public BankOfferEngine(LoanCalculationService calcService) {
        this.calcService = calcService;
    }

    // ── Bank Profile Definitions ──────────────────────────────────────────────

    /** Immutable bank profile configuration. */
    public record BankProfile(
            String code,
            String displayName,
            String logo,
            int    minCreditScore,
            double maxDTI,
            int    preferredCreditAgeMonths,
            double baseInterestRate,
            double riskPremiumFactor,   // added per risk point below threshold
            double approvalBias,        // flat bonus/penalty to approval prob (-10 to +15)
            String specialty
    ) {}

    private static final List<BankProfile> BANKS = List.of(
        new BankProfile("ALPHA_BANK",    "Alpha Bank",    "🏦", 700, 0.45, 12, 8.4,  0.03, 5,  "Balanced — best rate/approval combo"),
        new BankProfile("TRUST_FINANCE", "Trust Finance", "🤝", 650, 0.55, 6,  9.1,  0.02, 12, "Inclusive — highest approval rate"),
        new BankProfile("URBAN_CREDIT",  "Urban Credit",  "🏙️", 730, 0.40, 24, 7.9,  0.04, -5, "Premium — lowest rate, strictest criteria"),
        new BankProfile("HORIZON_BANK",  "Horizon Bank",  "🌅", 680, 0.50, 24, 10.2, 0.025,2,  "Prefers long credit history"),
        new BankProfile("NOVA_FINANCE",  "Nova Finance",  "⭐", 620, 0.60, 6,  11.5, 0.015,15, "Accessible — accepts lower credit scores"),
        new BankProfile("METRO_CREDIT",  "Metro Credit",  "🚇", 710, 0.45, 12, 9.8,  0.03, 3,  "Prefers salaried, stable employment")
    );

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Generates a ranked comparison of all bank offers for a customer.
     *
     * @param customer  the logged-in customer (profile data used for scoring)
     * @param amount    requested loan principal in INR
     * @param tenure    loan tenure in months
     * @param purpose   loan purpose string
     * @return sorted list of offers + AI recommendation text
     */
    public BankComparisonResult compareOffers(Customer customer, double amount, int tenure, String purpose) {
        List<BankOffer> offers = new ArrayList<>();

        for (BankProfile bank : BANKS) {
            BankOffer offer = buildOffer(bank, customer, amount, tenure);
            offers.add(offer);
        }

        // Sort by composite ranking score (lower = better)
        offers.sort(Comparator.comparingInt(BankOffer::getRankScore));

        // Mark top offer as recommended
        if (!offers.isEmpty()) {
            offers.get(0).setRecommended(true);
        }

        // Assign rank badges
        assignBadges(offers);

        BankComparisonResult result = new BankComparisonResult();
        result.setOffers(offers);
        result.setLoanAmount(amount);
        result.setTenure(tenure);
        if (!offers.isEmpty()) {
            result.setRecommendedBankCode(offers.get(0).getBankCode());
            result.setAiRecommendationText(buildRecommendationText(offers.get(0), customer));
        }
        return result;
    }

    /** Returns a specific bank's offer — used when customer has already selected a bank. */
    public Optional<BankOffer> getOfferForBank(String bankCode, Customer customer, double amount, int tenure) {
        return BANKS.stream()
                .filter(b -> b.code().equals(bankCode))
                .map(b -> buildOffer(b, customer, amount, tenure))
                .findFirst();
    }

    /** Returns the BankProfile for a given code. */
    public Optional<BankProfile> getBankProfile(String bankCode) {
        return BANKS.stream().filter(b -> b.code().equals(bankCode)).findFirst();
    }

    public List<BankProfile> getAllBanks() { return BANKS; }

    // ── Core Calculation ──────────────────────────────────────────────────────

    private BankOffer buildOffer(BankProfile bank, Customer customer, double amount, int tenure) {
        int    approvalProb = calculateApprovalProbability(bank, customer, amount);
        double rate         = calculatePersonalizedRate(bank, customer);
        double emi          = calcService.calculateEMI(amount, rate, tenure);
        double totalPayable = calcService.calculateTotalAmount(emi, tenure);
        String aiReason     = buildAiReason(bank, customer, approvalProb, rate);
        int    rankScore    = calculateRankScore(approvalProb, rate);

        BankOffer offer = new BankOffer();
        offer.setBankCode(bank.code());
        offer.setBankName(bank.displayName());
        offer.setBankLogo(bank.logo());
        offer.setInterestRate(Math.round(rate * 10.0) / 10.0);
        offer.setApprovalProbability(approvalProb);
        offer.setEmiAmount(emi);
        offer.setTotalPayable(Math.round(totalPayable * 100.0) / 100.0);
        offer.setAiReason(aiReason);
        offer.setRecommended(false);
        offer.setRankScore(rankScore);
        return offer;
    }

    /**
     * Approval probability formula (0-100%).
     * Weighted across: credit score vs threshold, DTI, payment history,
     * credit age, and employment.
     */
    private int calculateApprovalProbability(BankProfile bank, Customer customer, double amount) {
        double score = bank.approvalBias(); // start with bank bias

        // ── Factor 1: Credit Score (max 35 pts) ──────────────────────────
        int creditScore = customer.getCreditScore() != null ? customer.getCreditScore() : 600;
        if (creditScore >= bank.minCreditScore()) {
            double excess = creditScore - bank.minCreditScore();
            score += Math.min(35, 15 + excess / 8.0);
        } else {
            double deficit = bank.minCreditScore() - creditScore;
            score -= Math.min(40, deficit / 3.0); // heavy penalty for below threshold
        }

        // ── Factor 2: DTI (max 25 pts) ───────────────────────────────────
        double dti = customer.getDebtToIncomeRatio() != null ? customer.getDebtToIncomeRatio() : 0.4;
        if (dti <= bank.maxDTI()) {
            score += 25.0 * (1.0 - dti / bank.maxDTI());
        } else {
            score -= 20.0 * (dti - bank.maxDTI()) / bank.maxDTI();
        }

        // ── Factor 3: Payment History (max 20 pts) ───────────────────────
        int payHist = customer.getPaymentHistoryScore() != null ? customer.getPaymentHistoryScore() : 70;
        score += payHist / 100.0 * 20.0;

        // ── Factor 4: Credit Age (max 10 pts) ────────────────────────────
        int creditAge = customer.getCreditAgeMonths() != null ? customer.getCreditAgeMonths() : 12;
        double ageRatio = Math.min(1.0, (double) creditAge / bank.preferredCreditAgeMonths());
        score += ageRatio * 10.0;

        // ── Factor 5: Income sufficiency (max 10 pts) ────────────────────
        Double income = customer.getIncome();
        if (income != null && income > 0) {
            double monthlyIncome = income / 12.0;
            double emiEstimate   = calcService.calculateEMI(amount, bank.baseInterestRate(), 60);
            double emiRatio      = emiEstimate / monthlyIncome;
            if (emiRatio <= 0.3)        score += 10;
            else if (emiRatio <= 0.5)   score += 5;
            else                         score -= 10;
        }

        // ── Factor 6: Employment type & stability (max 8 pts) ────────────
        String empType  = customer.getEmploymentType();
        Integer empYrs  = customer.getEmploymentYears();
        if (empType != null) {
            switch (empType.toUpperCase()) {
                case "SALARIED"     -> score += 8;
                case "SELF_EMPLOYED", "BUSINESS" -> score += 3;
                case "UNEMPLOYED"   -> score -= 20;
            }
        }
        if (empYrs != null) {
            // Stability bonus: up to 7 pts for 5+ years
            score += Math.min(7, empYrs * 1.4);
        }
        // Metro Credit specifically favors salaried with 2+ years
        if ("METRO_CREDIT".equals(bank.code()) && "SALARIED".equalsIgnoreCase(empType) && empYrs != null && empYrs >= 2) {
            score += 5;
        }

        return (int) Math.max(0, Math.min(100, Math.round(score)));
    }


    /**
     * Personalized rate = base rate + risk premium based on customer's deficit
     * from the bank's ideal profile.
     */
    private double calculatePersonalizedRate(BankProfile bank, Customer customer) {
        double rate = bank.baseInterestRate();

        int creditScore = customer.getCreditScore() != null ? customer.getCreditScore() : 600;
        if (creditScore < bank.minCreditScore()) {
            int deficit = bank.minCreditScore() - creditScore;
            rate += deficit * bank.riskPremiumFactor();
        } else if (creditScore >= 750) {
            rate -= 0.5;
        } else if (creditScore >= 720) {
            rate -= 0.25;
        }

        double dti = customer.getDebtToIncomeRatio() != null ? customer.getDebtToIncomeRatio() : 0.4;
        if (dti > bank.maxDTI()) {
            rate += (dti - bank.maxDTI()) * 3.0;
        }

        // Clamp: ±2% from base
        rate = Math.max(bank.baseInterestRate() - 0.5, Math.min(bank.baseInterestRate() + 4.0, rate));
        return Math.round(rate * 10.0) / 10.0;
    }

    /**
     * Composite rank score (lower = better).
     * Balances approval probability and interest rate.
     * approvalWeight = 60%, rateWeight = 40%
     */
    private int calculateRankScore(int approvalProb, double rate) {
        // Normalize: high approval → low score; low rate → low score
        int approvalPenalty = (100 - approvalProb);       // 0 best
        int ratePenalty     = (int) ((rate - 7.0) * 5);   // ~0 for 7%, ~30 for 13%
        return (int) (approvalPenalty * 0.6 + ratePenalty * 0.4);
    }

    // ── AI Reason Text ────────────────────────────────────────────────────────

    private String buildAiReason(BankProfile bank, Customer customer, int approvalProb, double rate) {
        int creditScore = customer.getCreditScore() != null ? customer.getCreditScore() : 600;
        int creditAge   = customer.getCreditAgeMonths() != null ? customer.getCreditAgeMonths() : 12;

        StringBuilder sb = new StringBuilder();

        if (approvalProb >= 85) {
            sb.append("Strong match. ");
        } else if (approvalProb >= 65) {
            sb.append("Good match. ");
        } else if (approvalProb >= 45) {
            sb.append("Partial match. ");
        } else {
            sb.append("Challenging. ");
        }

        if (creditScore < bank.minCreditScore()) {
            int gap = bank.minCreditScore() - creditScore;
            sb.append("Your score (").append(creditScore).append(") is ")
              .append(gap).append(" points below this bank's minimum (")
              .append(bank.minCreditScore()).append("). ");
        } else {
            sb.append("Your credit score meets their threshold. ");
        }

        if ("HORIZON_BANK".equals(bank.code()) && creditAge < bank.preferredCreditAgeMonths()) {
            int monthsNeeded = bank.preferredCreditAgeMonths() - creditAge;
            sb.append("Horizon Bank prefers ≥24 months credit history — you need ")
              .append(monthsNeeded).append(" more months. ");
        }

        if ("URBAN_CREDIT".equals(bank.code()) && creditScore >= 730) {
            sb.append("Qualifies for their premium low-rate tier. ");
        }

        if ("TRUST_FINANCE".equals(bank.code())) {
            sb.append("Highest approval probability option. ");
        }

        if ("NOVA_FINANCE".equals(bank.code())) {
            sb.append("Accessible for lower scores but at a higher rate. ");
        }

        if (approvalProb >= 50) {
            sb.append("Improving score by 30 points would reduce rate by ~0.5%.");
        } else {
            sb.append("Improving score by 50+ points would significantly improve chances.");
        }

        return sb.toString();
    }

    private String buildRecommendationText(BankOffer top, Customer customer) {
        return String.format(
            "🤖 %s offers the best balance of approval probability (%d%%) " +
            "and competitive rate (%.1f%%). %s",
            top.getBankName(),
            top.getApprovalProbability(),
            top.getInterestRate(),
            top.getAiReason()
        );
    }

    private void assignBadges(List<BankOffer> sorted) {
        if (sorted.isEmpty()) return;

        // Best rate badge
        sorted.stream()
              .min(Comparator.comparingDouble(BankOffer::getInterestRate))
              .ifPresent(o -> o.setRiskBadge("💰 Best Rate"));

        // Highest approval badge (if different from best rate)
        sorted.stream()
              .max(Comparator.comparingInt(BankOffer::getApprovalProbability))
              .ifPresent(o -> {
                  if (o.getRiskBadge() == null) o.setRiskBadge("✅ Highest Approval");
              });

        // First offer gets recommended badge if no others assigned
        if (sorted.get(0).getRiskBadge() == null) {
            sorted.get(0).setRiskBadge("⭐ AI Recommended");
        }
    }
}
