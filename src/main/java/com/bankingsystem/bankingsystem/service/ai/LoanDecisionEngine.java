package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.entity.LoanDecision;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * LoanDecisionEngine — the 10-factor weighted AI scoring model.
 *
 * <p>Scoring model (total 100 points):
 * <pre>
 *  Factor                  Weight   Max Pts
 *  ─────────────────────── ──────   ───────
 *  Credit Score             30%      30
 *  Debt-to-Income Ratio     20%      20
 *  Payment History          15%      15
 *  EMI Affordability        10%      10
 *  Income Sufficiency       10%      10
 *  Employment Stability      5%       5
 *  Credit Utilization        5%       5
 *  Credit Age                2%       2
 *  Number of Accounts        1%       1
 *  Existing Active Loans     2%       2
 *  ─────────────────────── ──────   ───────
 *  Total                   100%     100
 * </pre>
 *
 * <p>Decision thresholds:
 * <ul>
 *   <li>Score ≥ 72 → AUTO_APPROVED</li>
 *   <li>Score 50-71 → MANUAL_REVIEW</li>
 *   <li>Score < 50 → AUTO_REJECTED</li>
 * </ul>
 */
@Service
public class LoanDecisionEngine {

    // Thresholds
    private static final double AUTO_APPROVE_THRESHOLD = 72.0;
    private static final double MANUAL_REVIEW_THRESHOLD = 50.0;

    private final FinancialHealthScorer healthScorer;
    private final DynamicInterestRateEngine rateEngine;
    private final LoanRepository loanRepository;

    public LoanDecisionEngine(FinancialHealthScorer healthScorer,
                              DynamicInterestRateEngine rateEngine,
                              LoanRepository loanRepository) {
        this.healthScorer   = healthScorer;
        this.rateEngine     = rateEngine;
        this.loanRepository = loanRepository;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Evaluates a loan application and returns a fully-populated LoanDecision.
     * The returned entity is NOT yet persisted — caller must save it.
     */
    public LoanDecision evaluate(Customer customer, Loan loan) {
        ScoreCard card = buildScoreCard(customer, loan);

        LoanDecision decision = new LoanDecision();
        decision.setLoan(loan);
        decision.setDecisionScore(card.totalScore);
        decision.setConfidencePercent((int) Math.round(card.totalScore));
        decision.setFinancialHealthScore(healthScorer.score(customer,
                loanRepository.findByCustomerId(customer.getId())));
        decision.setRiskProfile(deriveRiskProfile(card.totalScore));
        decision.setDecisionType(deriveDecisionType(card.totalScore));
        decision.setPersonalizedInterestRate(
                rateEngine.calculate(customer, loan.getPurpose(),
                        loan.getAmount(), loan.getTenure(), loan.getSelectedBankName()));
        decision.setSelectedBankName(loan.getSelectedBankName());
        decision.setRejectionReasons(String.join("|", card.reasons));
        decision.setRecommendations(String.join("|", card.recommendations));
        decision.setScoreBreakdown(String.join("|", card.breakdown));
        decision.setFraudFlagged(false); // FraudDetectionService sets this separately

        return decision;
    }

    // ── Score Card Builder ────────────────────────────────────────────────────

    private ScoreCard buildScoreCard(Customer customer, Loan loan) {
        ScoreCard card = new ScoreCard();

        scoreCredit(card, customer);
        scoreDTI(card, customer);
        scorePaymentHistory(card, customer);
        scoreEmiAffordability(card, customer, loan);
        scoreIncomeSufficiency(card, customer, loan);
        scoreEmploymentStability(card, loan);
        scoreCreditUtilization(card, customer);
        scoreCreditAge(card, customer);
        scoreNumberOfAccounts(card, customer);
        scoreExistingLoans(card, customer);

        card.totalScore = Math.min(100, Math.max(0, card.totalScore));
        return card;
    }

    // ── Factor 1: Credit Score (30 pts) ──────────────────────────────────────
    private void scoreCredit(ScoreCard card, Customer customer) {
        int score = customer.getCreditScore() != null ? customer.getCreditScore() : 0;
        double pts;
        if      (score >= 750) { pts = 30; }
        else if (score >= 700) { pts = 24; }
        else if (score >= 650) { pts = 18; }
        else if (score >= 600) { pts = 10; }
        else if (score >= 550) { pts = 5;  }
        else                   { pts = 0;  }

        card.totalScore += pts;
        card.breakdown.add(String.format("Credit Score (%d): %.0f/30", score, pts));

        if (pts < 18) {
            card.reasons.add(String.format(
                "Credit score %d is below the recommended threshold of 650", score));
            int needed = Math.max(0, 650 - score);
            if (needed > 0) {
                card.recommendations.add(String.format(
                    "Improve credit score by at least %d points (from %d to %d) through on-time payments",
                    needed, score, score + needed));
            }
        }
    }

    // ── Factor 2: Debt-to-Income Ratio (20 pts) ──────────────────────────────
    private void scoreDTI(ScoreCard card, Customer customer) {
        double dti = customer.getDebtToIncomeRatio() != null ? customer.getDebtToIncomeRatio() : 0.5;
        double pts;
        if      (dti <= 0.25) { pts = 20; }
        else if (dti <= 0.35) { pts = 17; }
        else if (dti <= 0.45) { pts = 12; }
        else if (dti <= 0.55) { pts = 6;  }
        else if (dti <= 0.65) { pts = 2;  }
        else                   { pts = 0;  }

        card.totalScore += pts;
        card.breakdown.add(String.format("Debt-to-Income Ratio (%.0f%%): %.0f/20",
                dti * 100, pts));

        if (pts < 10) {
            card.reasons.add(String.format(
                "Debt-to-income ratio %.0f%% exceeds the safe limit of 45%%", dti * 100));
            card.recommendations.add(
                "Reduce existing monthly debt obligations to bring DTI below 40%");
        }
    }

    // ── Factor 3: Payment History (15 pts) ───────────────────────────────────
    private void scorePaymentHistory(ScoreCard card, Customer customer) {
        int hist = customer.getPaymentHistoryScore() != null ? customer.getPaymentHistoryScore() : 70;
        double pts;
        if      (hist >= 85) { pts = 15; }
        else if (hist >= 70) { pts = 11; }
        else if (hist >= 55) { pts = 7;  }
        else if (hist >= 40) { pts = 3;  }
        else                  { pts = 0;  }

        card.totalScore += pts;
        card.breakdown.add(String.format("Payment History (%d/100): %.0f/15", hist, pts));

        if (pts < 7) {
            card.reasons.add(String.format(
                "Payment history score %d/100 indicates irregular repayment behaviour", hist));
            card.recommendations.add(
                "Maintain 100% on-time payments for the next 6 months to improve repayment history");
        }
    }

    // ── Factor 4: EMI Affordability (10 pts) ─────────────────────────────────
    private void scoreEmiAffordability(ScoreCard card, Customer customer, Loan loan) {
        Double monthlyIncome = loan.getMonthlyIncome();
        if (monthlyIncome == null && customer.getIncome() != null) {
            monthlyIncome = customer.getIncome() / 12.0;
        }
        if (monthlyIncome == null || monthlyIncome <= 0) {
            card.breakdown.add("EMI Affordability: N/A (income not provided) 0/10");
            card.recommendations.add("Provide monthly income details to improve assessment accuracy");
            return;
        }

        double emi = loan.getEmiAmount() != null ? loan.getEmiAmount() : 0;
        double existingEmi = customer.getEmi() != null ? customer.getEmi() : 0;
        double totalEmi = emi + existingEmi;
        double emiRatio = totalEmi / monthlyIncome;

        double pts;
        if      (emiRatio <= 0.30) { pts = 10; }
        else if (emiRatio <= 0.40) { pts = 7;  }
        else if (emiRatio <= 0.50) { pts = 4;  }
        else if (emiRatio <= 0.60) { pts = 1;  }
        else                        { pts = 0;  }

        card.totalScore += pts;
        card.breakdown.add(String.format("EMI Affordability (%.0f%% of income): %.0f/10",
                emiRatio * 100, pts));

        if (pts < 5) {
            card.reasons.add(String.format(
                "Total EMI obligation (₹%.0f/month) is %.0f%% of income — exceeds safe 50%% limit",
                totalEmi, emiRatio * 100));
            double safeEmi = monthlyIncome * 0.40 - existingEmi;
            if (safeEmi > 0) {
                card.recommendations.add(String.format(
                    "Maximum new EMI at safe level should be ₹%.0f/month — consider reducing loan amount or extending tenure",
                    safeEmi));
            }
        }
    }

    // ── Factor 5: Income Sufficiency (10 pts) ────────────────────────────────
    private void scoreIncomeSufficiency(ScoreCard card, Customer customer, Loan loan) {
        Double income = customer.getIncome();
        if (income == null || income <= 0) {
            card.breakdown.add("Income Sufficiency: N/A 0/10");
            return;
        }
        double amount = loan.getAmount() != null ? loan.getAmount() : 0;
        int    tenure = loan.getTenure() != null ? loan.getTenure() : 12;
        double monthlyIncome = income / 12.0;
        // Stress test: loan amount should not exceed 5× annual income for personal loans
        double incomeMultiple = amount / income;
        double pts;
        if      (incomeMultiple <= 2)  { pts = 10; }
        else if (incomeMultiple <= 3)  { pts = 7;  }
        else if (incomeMultiple <= 5)  { pts = 4;  }
        else if (incomeMultiple <= 8)  { pts = 1;  }
        else                            { pts = 0;  }

        card.totalScore += pts;
        card.breakdown.add(String.format("Income Sufficiency (%.1f× annual): %.0f/10",
                incomeMultiple, pts));

        if (pts < 4) {
            card.reasons.add(String.format(
                "Loan amount ₹%.0f is %.1f× your annual income — exceeds recommended 5× limit",
                amount, incomeMultiple));
            double maxRecommended = income * 5;
            card.recommendations.add(String.format(
                "Consider reducing loan amount to ₹%.0f (5× your annual income of ₹%.0f)",
                maxRecommended, income));
        }
    }

    // ── Factor 6: Employment Stability (5 pts) ───────────────────────────────
    private void scoreEmploymentStability(ScoreCard card, Loan loan) {
        int years = loan.getEmploymentStabilityYears() != null ? loan.getEmploymentStabilityYears() : 1;
        String empType = loan.getEmploymentType() != null ? loan.getEmploymentType() : "SALARIED";

        double pts = 0;
        if      (years >= 3)  { pts = 5; }
        else if (years >= 1)  { pts = 3; }
        else                   { pts = 0; }

        // Bonus for salaried employment
        if ("SALARIED".equalsIgnoreCase(empType) && pts > 0) pts = Math.min(5, pts + 0.5);
        // Penalty for unemployed
        if ("UNEMPLOYED".equalsIgnoreCase(empType)) pts = 0;

        card.totalScore += pts;
        card.breakdown.add(String.format("Employment Stability (%s, %dyr): %.0f/5",
                empType, years, pts));

        if (pts < 3) {
            card.reasons.add(String.format(
                "Employment stability of %d year(s) as %s is below preferred minimum of 3 years",
                years, empType));
            card.recommendations.add(
                "Maintain current employment for at least 3 years before reapplying for better rates");
        }
    }

    // ── Factor 7: Credit Utilization (5 pts) ─────────────────────────────────
    private void scoreCreditUtilization(ScoreCard card, Customer customer) {
        double util = customer.getCreditUtilizationRatio() != null
                ? customer.getCreditUtilizationRatio() : 0.5;
        double pts;
        if      (util < 0.30) { pts = 5; }
        else if (util < 0.50) { pts = 3; }
        else if (util < 0.70) { pts = 1; }
        else                   { pts = 0; }

        card.totalScore += pts;
        card.breakdown.add(String.format("Credit Utilization (%.0f%%): %.0f/5",
                util * 100, pts));

        if (pts < 2) {
            card.reasons.add(String.format(
                "Credit utilization %.0f%% is high — ideal is below 30%%", util * 100));
            card.recommendations.add(
                "Pay down credit card balances to reduce utilization below 30%");
        }
    }

    // ── Factor 8: Credit Age (2 pts) ─────────────────────────────────────────
    private void scoreCreditAge(ScoreCard card, Customer customer) {
        int age = customer.getCreditAgeMonths() != null ? customer.getCreditAgeMonths() : 0;
        double pts;
        if      (age >= 36) { pts = 2; }
        else if (age >= 12) { pts = 1; }
        else                 { pts = 0; }

        card.totalScore += pts;
        card.breakdown.add(String.format("Credit Age (%d months): %.0f/2", age, pts));

        if (pts == 0) {
            card.recommendations.add(
                "Credit history is very new — build credit history over 12+ months before applying");
        }
    }

    // ── Factor 9: Number of Accounts (1 pt) ──────────────────────────────────
    private void scoreNumberOfAccounts(ScoreCard card, Customer customer) {
        int accounts = customer.getNumberOfAccounts() != null ? customer.getNumberOfAccounts() : 0;
        double pts = (accounts >= 2 && accounts <= 8) ? 1 : 0;

        card.totalScore += pts;
        card.breakdown.add(String.format("Credit Accounts (%d): %.0f/1", accounts, pts));
    }

    // ── Factor 10: Existing Active Loans (2 pts) ──────────────────────────────
    private void scoreExistingLoans(ScoreCard card, Customer customer) {
        long activeLoans = loanRepository.findByCustomerId(customer.getId()).stream()
                .filter(l -> l.getStatus() == Loan.Status.APPROVED)
                .count();
        double pts;
        if      (activeLoans == 0) { pts = 2; }
        else if (activeLoans == 1) { pts = 1; }
        else                        { pts = 0; }

        card.totalScore += pts;
        card.breakdown.add(String.format("Active Loans (%d): %.0f/2", activeLoans, pts));

        if (activeLoans >= 2) {
            card.reasons.add(String.format(
                "%d active loans increase repayment risk — lenders prefer customers with fewer obligations",
                activeLoans));
            card.recommendations.add(
                "Close or pay off at least one existing loan before applying for a new one");
        }
    }

    // ── Derived Fields ────────────────────────────────────────────────────────

    private LoanDecision.DecisionType deriveDecisionType(double score) {
        if (score >= AUTO_APPROVE_THRESHOLD)  return LoanDecision.DecisionType.AUTO_APPROVED;
        if (score >= MANUAL_REVIEW_THRESHOLD) return LoanDecision.DecisionType.MANUAL_REVIEW;
        return LoanDecision.DecisionType.AUTO_REJECTED;
    }

    private LoanDecision.RiskProfile deriveRiskProfile(double score) {
        if (score >= 80) return LoanDecision.RiskProfile.LOW;
        if (score >= 65) return LoanDecision.RiskProfile.MEDIUM;
        if (score >= 45) return LoanDecision.RiskProfile.HIGH;
        return LoanDecision.RiskProfile.VERY_HIGH;
    }

    // ── Internal ScoreCard ────────────────────────────────────────────────────

    private static class ScoreCard {
        double       totalScore    = 0;
        List<String> reasons       = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        List<String> breakdown     = new ArrayList<>();
    }
}
