package com.bankingsystem.bankingsystem.entity;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Customer {

    public enum Role { ADMIN, CUSTOMER }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String address;
    
   
    private String password;

    @Convert(converter = RoleConverter.class)
    private Role role;

    // Financial Information for Credit Score Calculation
    private Double income; // Annual income
    private Double debtToIncomeRatio; // Ratio of debt to income (0.0 to 1.0)
    private Integer paymentHistoryScore; // Payment history score (0-100)
    private Double creditUtilizationRatio; // Credit utilization ratio (0.0 to 1.0)
    private Integer creditAgeMonths; // Age of credit history in months
    private Integer numberOfAccounts; // Number of credit accounts
    private Double emi; // Monthly loan installment (EMI)

    // Credit score from microservice (not defaulted anymore)
    private Integer creditScore;
    public Double getEmi() {
        return emi;
    }

    public void setEmi(Double emi) {
        this.emi = emi;
    }

    // Default constructor
    public Customer() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    // Financial Information Getters and Setters
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
