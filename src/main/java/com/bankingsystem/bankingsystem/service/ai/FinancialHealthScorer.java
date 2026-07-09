package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * FinancialHealthScorer — computes a proprietary 0-100 Financial Health Score.
 *
 * <p>6 sub-dimensions:
 * <pre>
 *  Dimension              Weight
 *  ──────────────────── ──────
 *  Savings Ratio          25%
 *  Debt Ratio             25%
 *  Payment Discipline     20%
 *  Credit Utilization     15%
 *  Income Stability       10%
 *  Existing Liabilities    5%
 *  ──────────────────── ──────
 *  Total                 100%
 * </pre>
 */
@Service
public class FinancialHealthScorer {

    /** Sub-dimension result used in the breakdown display. */
    public record Dimension(String name, int score, int maxScore, String status, String tip) {}

    /** Full result returned to the UI. */
    public record HealthResult(int totalScore, String grade, List<Dimension> dimensions, String summary) {}

    // ── Public API ────────────────────────────────────────────────────────────

    /** Returns only the integer score (used by LoanDecisionEngine). */
    public int score(Customer customer, List<Loan> loans) {
        return compute(customer, loans).totalScore();
    }

    /** Returns the full result with breakdown (used by REST API / dashboard). */
    public HealthResult compute(Customer customer, List<Loan> loans) {
        List<Dimension> dims = new ArrayList<>();

        int savingsScore     = scoreSavings(customer, dims);
        int debtScore        = scoreDebt(customer, dims);
        int paymentScore     = scorePayment(customer, dims);
        int utilizationScore = scoreUtilization(customer, dims);
        int stabilityScore   = scoreStability(customer, dims);
        int liabilityScore   = scoreLiabilities(loans, dims);

        // Weighted total (weights × max score)
        int total = (int) Math.round(
                savingsScore     * 0.25 +
                debtScore        * 0.25 +
                paymentScore     * 0.20 +
                utilizationScore * 0.15 +
                stabilityScore   * 0.10 +
                liabilityScore   * 0.05
        );
        total = Math.max(0, Math.min(100, total));

        String grade   = gradeOf(total);
        String summary = summaryOf(total, dims);

        return new HealthResult(total, grade, dims, summary);
    }

    // ── Dimension 1: Savings Ratio (25%) ─────────────────────────────────────
    private int scoreSavings(Customer customer, List<Dimension> dims) {
        Double income = customer.getIncome();
        Double emi    = customer.getEmi();

        if (income == null || income <= 0) {
            dims.add(new Dimension("Savings Ratio", 0, 100, "Unknown",
                    "Provide income details to compute savings score"));
            return 50; // neutral
        }

        double monthlyIncome = income / 12.0;
        double monthlyEmi    = emi != null ? emi : 0;
        double savingsRatio  = (monthlyIncome - monthlyEmi) / monthlyIncome;

        int score;
        String status, tip;
        if      (savingsRatio >= 0.40) { score = 100; status = "Excellent"; tip = "Great savings margin"; }
        else if (savingsRatio >= 0.25) { score = 75;  status = "Good";      tip = "Target 40% savings ratio"; }
        else if (savingsRatio >= 0.10) { score = 50;  status = "Fair";      tip = "Reduce EMI obligations to save more"; }
        else if (savingsRatio > 0)     { score = 25;  status = "Poor";      tip = "Income barely covers EMIs — reduce debt"; }
        else                           { score = 0;   status = "Critical";  tip = "EMI exceeds income — urgent debt reduction needed"; }

        dims.add(new Dimension("Savings Ratio", score, 100, status, tip));
        return score;
    }

    // ── Dimension 2: Debt Ratio (25%) ────────────────────────────────────────
    private int scoreDebt(Customer customer, List<Dimension> dims) {
        Double dti = customer.getDebtToIncomeRatio();
        if (dti == null) {
            dims.add(new Dimension("Debt Ratio", 60, 100, "Unknown", "Provide DTI data for accurate scoring"));
            return 60;
        }

        int score;
        String status, tip;
        if      (dti <= 0.20) { score = 100; status = "Excellent"; tip = "Very low debt burden"; }
        else if (dti <= 0.35) { score = 80;  status = "Good";      tip = "Debt well within safe limits"; }
        else if (dti <= 0.45) { score = 60;  status = "Fair";      tip = "Acceptable, aim below 35%"; }
        else if (dti <= 0.55) { score = 35;  status = "High";      tip = "Reduce monthly debt by ₹5,000+"; }
        else                   { score = 10;  status = "Critical";  tip = "Urgent: debt exceeds 55% of income"; }

        dims.add(new Dimension("Debt Ratio", score, 100, status, tip));
        return score;
    }

    // ── Dimension 3: Payment Discipline (20%) ────────────────────────────────
    private int scorePayment(Customer customer, List<Dimension> dims) {
        Integer hist = customer.getPaymentHistoryScore();
        if (hist == null) {
            dims.add(new Dimension("Payment Discipline", 70, 100, "Unknown", "No payment history data"));
            return 70;
        }

        String status = hist >= 85 ? "Excellent" : hist >= 70 ? "Good" : hist >= 55 ? "Fair" : "Poor";
        String tip    = hist >= 85 ? "Keep up perfect payments"
                      : hist >= 70 ? "Avoid even 1 late payment"
                      : hist >= 55 ? "Set up auto-pay to avoid misses"
                      : "Immediately start paying all dues on time";

        dims.add(new Dimension("Payment Discipline", hist, 100, status, tip));
        return hist;
    }

    // ── Dimension 4: Credit Utilization (15%) ────────────────────────────────
    private int scoreUtilization(Customer customer, List<Dimension> dims) {
        Double util = customer.getCreditUtilizationRatio();
        if (util == null) {
            dims.add(new Dimension("Credit Utilization", 60, 100, "Unknown", "No utilization data"));
            return 60;
        }

        int score = (int) Math.round(Math.max(0, 100 - util * 120));
        String status = util < 0.30 ? "Excellent" : util < 0.50 ? "Good" : util < 0.70 ? "Fair" : "Poor";
        String tip    = util < 0.30 ? "Ideal utilization — keep it below 30%"
                      : "Pay down cards to reduce utilization below 30%";

        dims.add(new Dimension("Credit Utilization", score, 100, status, tip));
        return score;
    }

    // ── Dimension 5: Income Stability (10%) ──────────────────────────────────
    private int scoreStability(Customer customer, List<Dimension> dims) {
        // Proxy: use credit age as a proxy for financial longevity
        Integer creditAge = customer.getCreditAgeMonths();
        int score;
        String status, tip;
        if      (creditAge == null)  { score = 50; status = "Unknown"; tip = "Provide employment history"; }
        else if (creditAge >= 60)    { score = 100; status = "Excellent"; tip = "Long financial track record"; }
        else if (creditAge >= 36)    { score = 80;  status = "Good"; tip = "Solid history building"; }
        else if (creditAge >= 12)    { score = 55;  status = "Fair"; tip = "Continue building credit history"; }
        else                          { score = 25;  status = "New";  tip = "Credit history too new — wait 12 months"; }

        dims.add(new Dimension("Income Stability", score, 100, status, tip));
        return score;
    }

    // ── Dimension 6: Existing Liabilities (5%) ───────────────────────────────
    private int scoreLiabilities(List<Loan> loans, List<Dimension> dims) {
        long active = loans == null ? 0 : loans.stream()
                .filter(l -> l.getStatus() == Loan.Status.APPROVED)
                .count();

        int score;
        String status, tip;
        if      (active == 0) { score = 100; status = "Excellent"; tip = "No existing active loans"; }
        else if (active == 1) { score = 65;  status = "Moderate"; tip = "1 active loan — manageable"; }
        else if (active == 2) { score = 35;  status = "High"; tip = "Close one loan before applying for another"; }
        else                   { score = 0;   status = "Critical"; tip = "Too many active loans — reduce before new application"; }

        dims.add(new Dimension("Existing Liabilities", score, 100, status, tip));
        return score;
    }

    // ── Grade & Summary ───────────────────────────────────────────────────────

    private String gradeOf(int score) {
        if (score >= 85) return "A+ (Excellent)";
        if (score >= 70) return "A  (Good)";
        if (score >= 55) return "B  (Fair)";
        if (score >= 40) return "C  (Needs Improvement)";
        return "D  (Poor)";
    }

    private String summaryOf(int score, List<Dimension> dims) {
        if (score >= 85) return "Your financial health is excellent. You are well-positioned for loan approval at competitive rates.";
        if (score >= 70) return "Your financial health is good. Minor improvements in debt or utilization could further enhance your profile.";
        if (score >= 55) return "Your financial health needs attention in a few areas. Focus on reducing DTI and improving payment discipline.";
        if (score >= 40) return "Your financial health requires significant improvement. Prioritize debt reduction and on-time payments.";
        return "Your financial health is poor. We strongly recommend consulting a financial advisor before applying for new credit.";
    }
}
