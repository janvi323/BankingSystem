package com.bankingsystem.creditscore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_scores")
public class CreditScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false, unique = true)
    private Long customerId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Min(300)
    @Max(850)
    @Column(name = "credit_score", nullable = false)
    private Integer creditScore;

    @Column(name = "score_grade", nullable = false)
    private String scoreGrade;

    @Column(name = "income", nullable = false)
    private Double income;

    @Column(name = "debt_to_income_ratio")
    private Double debtToIncomeRatio;

    @Column(name = "payment_history_score")
    private Integer paymentHistoryScore;

    @Column(name = "credit_utilization_ratio")
    private Double creditUtilizationRatio;

    @Column(name = "credit_age_months")
    private Integer creditAgeMonths;

    @Column(name = "number_of_accounts")
    private Integer numberOfAccounts;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public CreditScore() {
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public CreditScore(Long customerId, String customerName, String customerEmail, Double income) {
        this();
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.income = income;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
        this.scoreGrade = calculateGrade(creditScore);
        this.lastUpdated = LocalDateTime.now();
    }

    public String getScoreGrade() {
        return scoreGrade;
    }

    public void setScoreGrade(String scoreGrade) {
        this.scoreGrade = scoreGrade;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getDebtToIncomeRatio() {
        return debtToIncomeRatio;
    }

    public void setDebtToIncomeRatio(Double debtToIncomeRatio) {
        this.debtToIncomeRatio = debtToIncomeRatio;
    }

    public Integer getPaymentHistoryScore() {
        return paymentHistoryScore;
    }

    public void setPaymentHistoryScore(Integer paymentHistoryScore) {
        this.paymentHistoryScore = paymentHistoryScore;
    }

    public Double getCreditUtilizationRatio() {
        return creditUtilizationRatio;
    }

    public void setCreditUtilizationRatio(Double creditUtilizationRatio) {
        this.creditUtilizationRatio = creditUtilizationRatio;
    }

    public Integer getCreditAgeMonths() {
        return creditAgeMonths;
    }

    public void setCreditAgeMonths(Integer creditAgeMonths) {
        this.creditAgeMonths = creditAgeMonths;
    }

    public Integer getNumberOfAccounts() {
        return numberOfAccounts;
    }

    public void setNumberOfAccounts(Integer numberOfAccounts) {
        this.numberOfAccounts = numberOfAccounts;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper method to calculate grade based on credit score
    private String calculateGrade(Integer score) {
        if (score == null) return "N/A";
        if (score >= 800) return "Excellent";
        if (score >= 740) return "Very Good";
        if (score >= 670) return "Good";
        if (score >= 580) return "Fair";
        return "Poor";
    }

    @Override
    public String toString() {
        return "CreditScore{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", creditScore=" + creditScore +
                ", scoreGrade='" + scoreGrade + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}