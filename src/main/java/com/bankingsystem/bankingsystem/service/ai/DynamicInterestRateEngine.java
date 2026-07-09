package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.Service.ai.BankOfferEngine.BankProfile;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * DynamicInterestRateEngine — replaces static purpose-based rates with
 * personalized rates driven by credit score, financial risk, and selected bank.
 *
 * <p>Rate calculation order:
 * <ol>
 *   <li>Start with base rate from selected bank (if provided) or purpose base rate</li>
 *   <li>Apply credit score adjustment</li>
 *   <li>Apply DTI risk adjustment</li>
 *   <li>Apply loan amount discount</li>
 *   <li>Apply tenure adjustment</li>
 *   <li>Clamp to [floor, ceiling] range</li>
 * </ol>
 */
@Service
public class DynamicInterestRateEngine {

    private final BankOfferEngine bankOfferEngine;

    public DynamicInterestRateEngine(BankOfferEngine bankOfferEngine) {
        this.bankOfferEngine = bankOfferEngine;
    }

    /**
     * Calculates a personalized interest rate.
     *
     * @param customer      the borrower profile
     * @param purpose       loan purpose string
     * @param amount        requested principal in INR
     * @param tenureMonths  loan tenure in months
     * @param selectedBankCode  optional bank code from Multi-Bank Comparison
     * @return personalized annual interest rate (%)
     */
    public double calculate(Customer customer, String purpose,
                            double amount, int tenureMonths,
                            String selectedBankCode) {

        // Step 1: Base rate
        double base;
        if (selectedBankCode != null && !selectedBankCode.isBlank()) {
            Optional<BankProfile> bankOpt = bankOfferEngine.getBankProfile(selectedBankCode);
            base = bankOpt.map(BankProfile::baseInterestRate).orElse(purposeBase(purpose));
        } else {
            base = purposeBase(purpose);
        }

        // Step 2: Credit score adjustment
        int creditScore = customer.getCreditScore() != null ? customer.getCreditScore() : 650;
        base += creditScoreAdjustment(creditScore);

        // Step 3: DTI risk adjustment
        double dti = customer.getDebtToIncomeRatio() != null ? customer.getDebtToIncomeRatio() : 0.4;
        base += dtiAdjustment(dti);

        // Step 4: Loan amount discount (larger = cheaper)
        base += amountAdjustment(amount);

        // Step 5: Tenure adjustment
        base += tenureAdjustment(tenureMonths);

        // Step 6: Clamp [7.5%, 22%]
        base = Math.max(7.5, Math.min(22.0, base));

        return Math.round(base * 10.0) / 10.0;
    }

    // ── Sub-calculations ──────────────────────────────────────────────────────

    private double creditScoreAdjustment(int score) {
        if      (score >= 800) return -2.0;
        else if (score >= 750) return -1.0;
        else if (score >= 700) return  0.0;
        else if (score >= 650) return  1.0;
        else if (score >= 600) return  2.5;
        else if (score >= 550) return  4.0;
        else                   return  6.0;
    }

    private double dtiAdjustment(double dti) {
        if      (dti <= 0.25) return -0.5;
        else if (dti <= 0.35) return  0.0;
        else if (dti <= 0.45) return  0.5;
        else if (dti <= 0.55) return  1.5;
        else                  return  3.0;
    }

    private double amountAdjustment(double amount) {
        if      (amount >= 2_000_000) return -1.5;
        else if (amount >= 1_000_000) return -1.0;
        else if (amount >= 500_000)   return -0.5;
        else if (amount >= 100_000)   return  0.0;
        else                          return  0.5;
    }

    private double tenureAdjustment(int months) {
        if      (months <= 12)  return  0.0;
        else if (months <= 36)  return  0.5;
        else if (months <= 60)  return  1.0;
        else if (months <= 120) return  1.5;
        else                    return  2.0;
    }

    private double purposeBase(String purpose) {
        if (purpose == null) return 12.0;
        return switch (purpose.toLowerCase()) {
            case "home purchase"     -> 9.5;
            case "home improvement"  -> 11.0;
            case "car purchase"      -> 10.5;
            case "education"         -> 9.0;
            case "business"          -> 14.0;
            case "personal"          -> 12.5;
            case "debt consolidation"-> 13.0;
            case "medical expenses"  -> 11.5;
            default                  -> 12.0;
        };
    }
}
