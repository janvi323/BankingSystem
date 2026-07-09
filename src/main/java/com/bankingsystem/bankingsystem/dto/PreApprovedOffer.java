package com.bankingsystem.bankingsystem.dto;

/**
 * PreApprovedOffer — one pre-approved loan category offer for a customer.
 */
public class PreApprovedOffer {

    private String loanCategory;      // Personal, Car, Education, Home
    private String icon;              // emoji
    private boolean eligible;
    private double maxAmount;         // Maximum pre-approved amount
    private double minInterestRate;   // Best rate available for this category
    private String reason;            // Why eligible or what's blocking
    private String applyUrl;          // pre-filled apply URL

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public String getLoanCategory()                           { return loanCategory; }
    public void setLoanCategory(String loanCategory)         { this.loanCategory = loanCategory; }
    public String getIcon()                                   { return icon; }
    public void setIcon(String icon)                          { this.icon = icon; }
    public boolean isEligible()                               { return eligible; }
    public void setEligible(boolean eligible)                 { this.eligible = eligible; }
    public double getMaxAmount()                              { return maxAmount; }
    public void setMaxAmount(double maxAmount)                { this.maxAmount = maxAmount; }
    public double getMinInterestRate()                        { return minInterestRate; }
    public void setMinInterestRate(double minInterestRate)    { this.minInterestRate = minInterestRate; }
    public String getReason()                                 { return reason; }
    public void setReason(String reason)                      { this.reason = reason; }
    public String getApplyUrl()                               { return applyUrl; }
    public void setApplyUrl(String applyUrl)                  { this.applyUrl = applyUrl; }
}
