package com.bankingsystem.bankingsystem.dto;

/**
 * SimulationResult — shows the impact of a what-if scenario on loan eligibility.
 */
public class SimulationResult {

    private String scenarioDescription;     // "If credit score increases by 50 points..."
    private String currentDecision;         // AUTO_REJECTED
    private String simulatedDecision;       // AUTO_APPROVED
    private int    currentApprovalProb;     // 38%
    private int    simulatedApprovalProb;   // 79%
    private double currentInterestRate;
    private double simulatedInterestRate;
    private double currentEmi;
    private double simulatedEmi;
    private int    currentFinancialHealth;
    private int    simulatedFinancialHealth;
    private String impactSummary;           // "This change improves your approval chance by 41%"
    private boolean becomeEligible;         // Would the customer become eligible?

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public String getScenarioDescription()                             { return scenarioDescription; }
    public void setScenarioDescription(String s)                       { this.scenarioDescription = s; }
    public String getCurrentDecision()                                  { return currentDecision; }
    public void setCurrentDecision(String s)                           { this.currentDecision = s; }
    public String getSimulatedDecision()                                { return simulatedDecision; }
    public void setSimulatedDecision(String s)                         { this.simulatedDecision = s; }
    public int getCurrentApprovalProb()                                 { return currentApprovalProb; }
    public void setCurrentApprovalProb(int v)                          { this.currentApprovalProb = v; }
    public int getSimulatedApprovalProb()                               { return simulatedApprovalProb; }
    public void setSimulatedApprovalProb(int v)                        { this.simulatedApprovalProb = v; }
    public double getCurrentInterestRate()                              { return currentInterestRate; }
    public void setCurrentInterestRate(double v)                        { this.currentInterestRate = v; }
    public double getSimulatedInterestRate()                            { return simulatedInterestRate; }
    public void setSimulatedInterestRate(double v)                      { this.simulatedInterestRate = v; }
    public double getCurrentEmi()                                       { return currentEmi; }
    public void setCurrentEmi(double v)                                 { this.currentEmi = v; }
    public double getSimulatedEmi()                                     { return simulatedEmi; }
    public void setSimulatedEmi(double v)                               { this.simulatedEmi = v; }
    public int getCurrentFinancialHealth()                              { return currentFinancialHealth; }
    public void setCurrentFinancialHealth(int v)                        { this.currentFinancialHealth = v; }
    public int getSimulatedFinancialHealth()                            { return simulatedFinancialHealth; }
    public void setSimulatedFinancialHealth(int v)                      { this.simulatedFinancialHealth = v; }
    public String getImpactSummary()                                    { return impactSummary; }
    public void setImpactSummary(String s)                              { this.impactSummary = s; }
    public boolean isBecomeEligible()                                   { return becomeEligible; }
    public void setBecomeEligible(boolean b)                            { this.becomeEligible = b; }
}
