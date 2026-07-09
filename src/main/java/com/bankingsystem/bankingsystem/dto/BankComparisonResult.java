package com.bankingsystem.bankingsystem.dto;

import java.util.List;

/**
 * BankComparisonResult — the full multi-bank comparison response.
 * Contains all bank offers + AI recommendation text.
 */
public class BankComparisonResult {

    private List<BankOffer> offers;
    private String recommendedBankCode;
    private String aiRecommendationText;  // E.g. "Alpha Bank offers the best balance..."
    private double loanAmount;
    private int    tenure;

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public List<BankOffer> getOffers()                                    { return offers; }
    public void setOffers(List<BankOffer> offers)                         { this.offers = offers; }
    public String getRecommendedBankCode()                                 { return recommendedBankCode; }
    public void setRecommendedBankCode(String recommendedBankCode)         { this.recommendedBankCode = recommendedBankCode; }
    public String getAiRecommendationText()                                { return aiRecommendationText; }
    public void setAiRecommendationText(String aiRecommendationText)       { this.aiRecommendationText = aiRecommendationText; }
    public double getLoanAmount()                                           { return loanAmount; }
    public void setLoanAmount(double loanAmount)                           { this.loanAmount = loanAmount; }
    public int getTenure()                                                  { return tenure; }
    public void setTenure(int tenure)                                      { this.tenure = tenure; }
}
