package com.bankingsystem.bankingsystem.dto;

import com.bankingsystem.bankingsystem.entity.LoanDecision;
import java.time.LocalDateTime;
import java.util.List;

/**
 * LoanDecisionResult — the complete AI evaluation response returned to the frontend.
 * Carries everything needed to render the Decision Panel in apply-loan.jsp.
 */
public class LoanDecisionResult {

    private Long loanId;
    private String decisionType;          // AUTO_APPROVED / MANUAL_REVIEW / AUTO_REJECTED
    private Double decisionScore;         // 0–100
    private Integer confidencePercent;
    private Integer financialHealthScore; // 0–100
    private String riskProfile;           // LOW / MEDIUM / HIGH / VERY_HIGH
    private Boolean fraudFlagged;
    private Double personalizedInterestRate;
    private Double emiAmount;
    private String selectedBankName;

    private List<String> rejectionReasons;
    private List<String> recommendations;
    private List<String> scoreBreakdown;

    private String decisionSummary;       // Human-readable one-liner
    private LocalDateTime decidedAt;

    // ── Factory method ────────────────────────────────────────────────────────
    public static LoanDecisionResult from(LoanDecision d, Double emiAmount) {
        LoanDecisionResult r = new LoanDecisionResult();
        r.loanId                  = d.getLoan().getId();
        r.decisionType            = d.getDecisionType().name();
        r.decisionScore           = d.getDecisionScore();
        r.confidencePercent       = d.getConfidencePercent();
        r.financialHealthScore    = d.getFinancialHealthScore();
        r.riskProfile             = d.getRiskProfile() != null ? d.getRiskProfile().name() : "MEDIUM";
        r.fraudFlagged            = d.getFraudFlagged();
        r.personalizedInterestRate = d.getPersonalizedInterestRate();
        r.emiAmount               = emiAmount;
        r.selectedBankName        = d.getSelectedBankName();
        r.rejectionReasons        = d.getRejectionReasonsList();
        r.recommendations         = d.getRecommendationsList();
        r.scoreBreakdown          = d.getScoreBreakdownList();
        r.decidedAt               = d.getDecidedAt();
        r.decisionSummary         = buildSummary(d);
        return r;
    }

    private static String buildSummary(LoanDecision d) {
        return switch (d.getDecisionType()) {
            case AUTO_APPROVED  -> "✅ Congratulations! Your loan has been automatically approved with " +
                                   d.getConfidencePercent() + "% confidence.";
            case MANUAL_REVIEW  -> "⏳ Your application is under review. Our team will evaluate it shortly.";
            case AUTO_REJECTED  -> "❌ Your application was not approved at this time. " +
                                   "See recommendations below to improve eligibility.";
        };
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public Long getLoanId()                             { return loanId; }
    public void setLoanId(Long loanId)                 { this.loanId = loanId; }
    public String getDecisionType()                     { return decisionType; }
    public void setDecisionType(String decisionType)   { this.decisionType = decisionType; }
    public Double getDecisionScore()                    { return decisionScore; }
    public void setDecisionScore(Double decisionScore) { this.decisionScore = decisionScore; }
    public Integer getConfidencePercent()               { return confidencePercent; }
    public void setConfidencePercent(Integer c)         { this.confidencePercent = c; }
    public Integer getFinancialHealthScore()             { return financialHealthScore; }
    public void setFinancialHealthScore(Integer s)      { this.financialHealthScore = s; }
    public String getRiskProfile()                      { return riskProfile; }
    public void setRiskProfile(String riskProfile)     { this.riskProfile = riskProfile; }
    public Boolean getFraudFlagged()                    { return fraudFlagged; }
    public void setFraudFlagged(Boolean fraudFlagged)  { this.fraudFlagged = fraudFlagged; }
    public Double getPersonalizedInterestRate()          { return personalizedInterestRate; }
    public void setPersonalizedInterestRate(Double r)   { this.personalizedInterestRate = r; }
    public Double getEmiAmount()                         { return emiAmount; }
    public void setEmiAmount(Double emiAmount)           { this.emiAmount = emiAmount; }
    public String getSelectedBankName()                  { return selectedBankName; }
    public void setSelectedBankName(String s)            { this.selectedBankName = s; }
    public List<String> getRejectionReasons()            { return rejectionReasons; }
    public void setRejectionReasons(List<String> r)     { this.rejectionReasons = r; }
    public List<String> getRecommendations()             { return recommendations; }
    public void setRecommendations(List<String> r)      { this.recommendations = r; }
    public List<String> getScoreBreakdown()              { return scoreBreakdown; }
    public void setScoreBreakdown(List<String> s)        { this.scoreBreakdown = s; }
    public String getDecisionSummary()                   { return decisionSummary; }
    public void setDecisionSummary(String s)             { this.decisionSummary = s; }
    public LocalDateTime getDecidedAt()                  { return decidedAt; }
    public void setDecidedAt(LocalDateTime d)            { this.decidedAt = d; }
}
