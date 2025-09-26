package com.bankingsystem.creditscore.dto;

import jakarta.validation.constraints.*;

public class CreditScoreRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;

    @NotNull(message = "Income is required")
    @Positive(message = "Income must be positive")
    private Double income;

    @DecimalMin(value = "0.0", message = "Debt to income ratio must be non-negative")
    @DecimalMax(value = "1.0", message = "Debt to income ratio cannot exceed 1.0")
    private Double debtToIncomeRatio;

    @Min(value = 0, message = "Payment history score must be non-negative")
    @Max(value = 100, message = "Payment history score cannot exceed 100")
    private Integer paymentHistoryScore;

    @DecimalMin(value = "0.0", message = "Credit utilization ratio must be non-negative")
    @DecimalMax(value = "1.0", message = "Credit utilization ratio cannot exceed 1.0")
    private Double creditUtilizationRatio;

    @Min(value = 0, message = "Credit age must be non-negative")
    private Integer creditAgeMonths;

    @Min(value = 0, message = "Number of accounts must be non-negative")
    private Integer numberOfAccounts;

    // Constructors
    public CreditScoreRequest() {}

    public CreditScoreRequest(Long customerId, String customerName, String customerEmail, Double income) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.income = income;
    }

    // Getters and Setters
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
}