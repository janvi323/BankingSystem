package com.bankingsystem.bankingsystem.dto;

public class CreditScoreRequestDto {
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Double income;
    private Double debtToIncomeRatio;
    private Integer paymentHistoryScore;
    private Double creditUtilizationRatio;
    private Integer creditAgeMonths;
    private Integer numberOfAccounts;

    // Constructors
    public CreditScoreRequestDto() {}

    public CreditScoreRequestDto(Long customerId, String customerName, String customerEmail, Double income) {
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