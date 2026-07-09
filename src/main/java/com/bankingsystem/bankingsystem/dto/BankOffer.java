package com.bankingsystem.bankingsystem.dto;

/**
 * BankOffer — a single bank's offer for a customer's loan request.
 * Returned as part of {@link BankComparisonResult}.
 */
public class BankOffer {

    private String bankCode;
    private String bankName;
    private String bankLogo;         // emoji used as icon in UI
    private double interestRate;     // personalized rate (%)
    private int    approvalProbability; // 0-100 %
    private double emiAmount;        // calculated monthly EMI
    private double totalPayable;     // principal + interest over tenure
    private String aiReason;         // Why this bank is/isn't ideal
    private boolean recommended;     // AI top pick flag
    private String riskBadge;        // "Best Rate" / "Highest Approval" / "Safe Bet"
    private int    rankScore;        // composite ranking score (lower = better)

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public String getBankCode()                             { return bankCode; }
    public void setBankCode(String bankCode)               { this.bankCode = bankCode; }
    public String getBankName()                             { return bankName; }
    public void setBankName(String bankName)               { this.bankName = bankName; }
    public String getBankLogo()                             { return bankLogo; }
    public void setBankLogo(String bankLogo)               { this.bankLogo = bankLogo; }
    public double getInterestRate()                         { return interestRate; }
    public void setInterestRate(double interestRate)       { this.interestRate = interestRate; }
    public int getApprovalProbability()                     { return approvalProbability; }
    public void setApprovalProbability(int approvalProbability) { this.approvalProbability = approvalProbability; }
    public double getEmiAmount()                            { return emiAmount; }
    public void setEmiAmount(double emiAmount)             { this.emiAmount = emiAmount; }
    public double getTotalPayable()                         { return totalPayable; }
    public void setTotalPayable(double totalPayable)       { this.totalPayable = totalPayable; }
    public String getAiReason()                             { return aiReason; }
    public void setAiReason(String aiReason)               { this.aiReason = aiReason; }
    public boolean isRecommended()                          { return recommended; }
    public void setRecommended(boolean recommended)         { this.recommended = recommended; }
    public String getRiskBadge()                            { return riskBadge; }
    public void setRiskBadge(String riskBadge)             { this.riskBadge = riskBadge; }
    public int getRankScore()                               { return rankScore; }
    public void setRankScore(int rankScore)                 { this.rankScore = rankScore; }
}
