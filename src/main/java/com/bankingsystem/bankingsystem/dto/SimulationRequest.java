package com.bankingsystem.bankingsystem.dto;

/**
 * SimulationRequest — what-if scenario parameters sent by the customer.
 * All delta fields are optional; null means "keep current value".
 */
public class SimulationRequest {

    private Long   customerId;
    private double loanAmount;
    private int    tenure;
    private String purpose;
    private String selectedBankCode;

    // What-if deltas
    private Integer creditScoreDelta;      // e.g. +50 means "what if my score is 50 higher?"
    private Double  debtReductionAmount;   // e.g. 200000 means "what if I reduce debt by ₹2L?"
    private Double  newMonthlyIncome;      // e.g. 80000 means "what if my income is ₹80K/month?"
    private Double  newLoanAmount;         // e.g. 300000 means "what if I request ₹3L instead?"
    private Integer newTenure;             // alternative tenure

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public Long getCustomerId()                         { return customerId; }
    public void setCustomerId(Long customerId)         { this.customerId = customerId; }
    public double getLoanAmount()                       { return loanAmount; }
    public void setLoanAmount(double loanAmount)       { this.loanAmount = loanAmount; }
    public int getTenure()                              { return tenure; }
    public void setTenure(int tenure)                  { this.tenure = tenure; }
    public String getPurpose()                          { return purpose; }
    public void setPurpose(String purpose)             { this.purpose = purpose; }
    public String getSelectedBankCode()                 { return selectedBankCode; }
    public void setSelectedBankCode(String s)           { this.selectedBankCode = s; }
    public Integer getCreditScoreDelta()                { return creditScoreDelta; }
    public void setCreditScoreDelta(Integer c)          { this.creditScoreDelta = c; }
    public Double getDebtReductionAmount()              { return debtReductionAmount; }
    public void setDebtReductionAmount(Double d)        { this.debtReductionAmount = d; }
    public Double getNewMonthlyIncome()                 { return newMonthlyIncome; }
    public void setNewMonthlyIncome(Double n)           { this.newMonthlyIncome = n; }
    public Double getNewLoanAmount()                    { return newLoanAmount; }
    public void setNewLoanAmount(Double n)              { this.newLoanAmount = n; }
    public Integer getNewTenure()                       { return newTenure; }
    public void setNewTenure(Integer n)                 { this.newTenure = n; }
}
