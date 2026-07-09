package com.bankingsystem.bankingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Loan {

    public enum Status { PENDING, APPROVED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private Double amount;
    private String purpose;
    private Integer tenure;

    // Automatic calculation fields
    private Double interestRate;  // Annual interest rate in percentage
    private Double emiAmount;     // Monthly EMI amount
    private Double totalAmount;   // Total amount to be paid including interest

    // ── Bank Selection (from Multi-Bank Comparison) ────────────────────────
    private String selectedBankName;  // e.g. "Alpha Bank"
    private Double selectedBankRate;  // Personalized rate from selected bank

    // ── Employment Info (for AI Decision Engine) ───────────────────────────
    private String employmentType;         // SALARIED, SELF_EMPLOYED, BUSINESS, UNEMPLOYED
    private Integer employmentStabilityYears; // Years at current employer
    private Double  monthlyIncome;         // Monthly gross income in INR

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private LocalDateTime applicationDate = LocalDateTime.now();
    private LocalDateTime approvalDate;
    private String adminComments;

    // Default constructor
    public Loan() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Integer getTenure() {
        return tenure;
    }

    public void setTenure(Integer tenure) {
        this.tenure = tenure;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getAdminComments() {
        return adminComments;
    }

    public void setAdminComments(String adminComments) {
        this.adminComments = adminComments;
    }

    // New getters and setters for interest rate and EMI
    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Double getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(Double emiAmount) {
        this.emiAmount = emiAmount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    // ── Bank Selection ─────────────────────────────────────────────────────
    public String getSelectedBankName() { return selectedBankName; }
    public void setSelectedBankName(String selectedBankName) { this.selectedBankName = selectedBankName; }

    public Double getSelectedBankRate() { return selectedBankRate; }
    public void setSelectedBankRate(Double selectedBankRate) { this.selectedBankRate = selectedBankRate; }

    // ── Employment Info ────────────────────────────────────────────────────
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public Integer getEmploymentStabilityYears() { return employmentStabilityYears; }
    public void setEmploymentStabilityYears(Integer employmentStabilityYears) { this.employmentStabilityYears = employmentStabilityYears; }

    public Double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }
}
