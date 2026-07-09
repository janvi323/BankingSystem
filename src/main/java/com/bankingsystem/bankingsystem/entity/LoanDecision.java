package com.bankingsystem.bankingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * LoanDecision — persists the AI engine's evaluation result for every loan application.
 *
 * <p>Decision types:
 * <ul>
 *   <li>AUTO_APPROVED  — score ≥ 72, loan auto-approved</li>
 *   <li>MANUAL_REVIEW  — score 50-71, held for admin</li>
 *   <li>AUTO_REJECTED  — score < 50, loan auto-rejected with explanations</li>
 * </ul>
 *
 * <p>rejectionReasons, recommendations, and scoreBreakdown are stored as
 * pipe-separated strings (e.g. "Credit score below threshold|DTI too high")
 * to avoid a JSON library dependency.
 */
@Entity
@Table(name = "loan_decision")
public class LoanDecision {

    public enum DecisionType { AUTO_APPROVED, MANUAL_REVIEW, AUTO_REJECTED }
    public enum RiskProfile   { LOW, MEDIUM, HIGH, VERY_HIGH }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false, unique = true)
    private Loan loan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecisionType decisionType;

    /** Weighted score 0-100 from the 10-factor model. */
    private Double decisionScore;

    /** Same as decisionScore cast to int — displayed as confidence %. */
    private Integer confidencePercent;

    /** Proprietary Financial Health Score 0-100 from 6 sub-dimensions. */
    private Integer financialHealthScore;

    @Enumerated(EnumType.STRING)
    private RiskProfile riskProfile;

    /** True when FraudDetectionService flagged suspicious patterns. */
    private Boolean fraudFlagged = false;

    /** Personalized interest rate from DynamicInterestRateEngine (%). */
    private Double personalizedInterestRate;

    /** Selected bank name from the Multi-Bank Comparison. */
    private String selectedBankName;

    /**
     * Pipe-separated list of human-readable rejection reasons.
     * E.g. "Credit score 620 is below the minimum threshold of 650|
     *        DTI ratio 0.58 exceeds safe limit of 0.45"
     */
    @Column(columnDefinition = "TEXT")
    private String rejectionReasons;

    /**
     * Pipe-separated list of actionable improvement recommendations.
     * E.g. "Reduce existing EMIs by ₹3,000/month|
     *        Improve credit score by 30 points before reapplying"
     */
    @Column(columnDefinition = "TEXT")
    private String recommendations;

    /**
     * Pipe-separated score breakdown per factor.
     * E.g. "Credit Score: 18/30|DTI: 15/20|Payment History: 11/15"
     */
    @Column(columnDefinition = "TEXT")
    private String scoreBreakdown;

    private LocalDateTime decidedAt = LocalDateTime.now();

    // ── Constructors ──────────────────────────────────────────────────────────

    public LoanDecision() {}

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Loan getLoan() { return loan; }
    public void setLoan(Loan loan) { this.loan = loan; }

    public DecisionType getDecisionType() { return decisionType; }
    public void setDecisionType(DecisionType decisionType) { this.decisionType = decisionType; }

    public Double getDecisionScore() { return decisionScore; }
    public void setDecisionScore(Double decisionScore) { this.decisionScore = decisionScore; }

    public Integer getConfidencePercent() { return confidencePercent; }
    public void setConfidencePercent(Integer confidencePercent) { this.confidencePercent = confidencePercent; }

    public Integer getFinancialHealthScore() { return financialHealthScore; }
    public void setFinancialHealthScore(Integer financialHealthScore) { this.financialHealthScore = financialHealthScore; }

    public RiskProfile getRiskProfile() { return riskProfile; }
    public void setRiskProfile(RiskProfile riskProfile) { this.riskProfile = riskProfile; }

    public Boolean getFraudFlagged() { return fraudFlagged; }
    public void setFraudFlagged(Boolean fraudFlagged) { this.fraudFlagged = fraudFlagged; }

    public Double getPersonalizedInterestRate() { return personalizedInterestRate; }
    public void setPersonalizedInterestRate(Double personalizedInterestRate) { this.personalizedInterestRate = personalizedInterestRate; }

    public String getSelectedBankName() { return selectedBankName; }
    public void setSelectedBankName(String selectedBankName) { this.selectedBankName = selectedBankName; }

    public String getRejectionReasons() { return rejectionReasons; }
    public void setRejectionReasons(String rejectionReasons) { this.rejectionReasons = rejectionReasons; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }

    public String getScoreBreakdown() { return scoreBreakdown; }
    public void setScoreBreakdown(String scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; }

    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns rejection reasons as a List (split on pipe). */
    public java.util.List<String> getRejectionReasonsList() {
        if (rejectionReasons == null || rejectionReasons.isBlank()) return java.util.List.of();
        return java.util.Arrays.asList(rejectionReasons.split("\\|"));
    }

    /** Returns recommendations as a List (split on pipe). */
    public java.util.List<String> getRecommendationsList() {
        if (recommendations == null || recommendations.isBlank()) return java.util.List.of();
        return java.util.Arrays.asList(recommendations.split("\\|"));
    }

    /** Returns score breakdown entries as a List (split on pipe). */
    public java.util.List<String> getScoreBreakdownList() {
        if (scoreBreakdown == null || scoreBreakdown.isBlank()) return java.util.List.of();
        return java.util.Arrays.asList(scoreBreakdown.split("\\|"));
    }
}
