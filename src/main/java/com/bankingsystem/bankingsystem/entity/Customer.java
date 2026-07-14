package com.bankingsystem.bankingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Customer {

    public enum Role { ADMIN, CUSTOMER }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Core Identity ─────────────────────────────────────────────────────────
    private String name;
    private String email;
    private String phone;
    private String address;
    private String password;

    @Convert(converter = RoleConverter.class)
    private Role role;

    // ── Personal Details ──────────────────────────────────────────────────────
    private LocalDate  dateOfBirth;
    private String     city;
    private String     maritalStatus;   // SINGLE, MARRIED, DIVORCED, WIDOWED

    // ── Employment Details ────────────────────────────────────────────────────
    private String  employmentType;          // SALARIED, SELF_EMPLOYED, BUSINESS, UNEMPLOYED, RETIRED
    private String  employerName;
    private String  industry;
    private String  jobTitle;
    private Integer workExperienceYears;
    private Integer employmentStabilityYears;

    // ── Financial Details (core credit scoring inputs) ────────────────────────
    private Double  income;                  // Annual income
    private Double  monthlyIncome;           // Monthly income (= income/12 when set directly)
    private Double  debtToIncomeRatio;       // 0.0 – 1.0
    private Integer paymentHistoryScore;     // 0–100
    private Double  creditUtilizationRatio;  // 0.0 – 1.0
    private Integer creditAgeMonths;
    private Integer numberOfAccounts;
    private Double  emi;                     // Total existing monthly EMIs
    private Integer existingLoans;           // Count of active loans
    private Double  savings;                 // Total savings/liquid assets
    private Double  monthlyExpenses;         // Fixed monthly expenses

    // ── Credit Score ──────────────────────────────────────────────────────────
    private Integer creditScore;

    // ── Loan Preferences ─────────────────────────────────────────────────────
    @Column(columnDefinition = "TEXT")
    private String  preferredLoanTypes;      // Pipe-separated: PERSONAL|HOME|CAR
    private Integer preferredTenure;         // Preferred tenure in months
    private String  riskAppetite;            // LOW, MEDIUM, HIGH

    // ── Security & Audit ─────────────────────────────────────────────────────
    private Boolean       googleConnected       = false;
    private LocalDateTime profileCompletedAt;
    private LocalDateTime lastFinancialUpdateAt;
    private LocalDateTime lastLoginAt;
    @Column(columnDefinition = "TEXT")
    private String        financialAuditLog;   // Pipe-separated audit entries

    // ── Constructors ──────────────────────────────────────────────────────────
    public Customer() {}

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    // Personal
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    // Employment
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getEmployerName() { return employerName; }
    public void setEmployerName(String employerName) { this.employerName = employerName; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public Integer getWorkExperienceYears() { return workExperienceYears; }
    public void setWorkExperienceYears(Integer workExperienceYears) { this.workExperienceYears = workExperienceYears; }

    public Integer getEmploymentStabilityYears() { return employmentStabilityYears; }
    public void setEmploymentStabilityYears(Integer employmentStabilityYears) { this.employmentStabilityYears = employmentStabilityYears; }

    // Financial
    public Double getIncome() { return income; }
    public void setIncome(Double income) { this.income = income; }

    public Double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public Double getDebtToIncomeRatio() { return debtToIncomeRatio; }
    public void setDebtToIncomeRatio(Double debtToIncomeRatio) { this.debtToIncomeRatio = debtToIncomeRatio; }

    public Integer getPaymentHistoryScore() { return paymentHistoryScore; }
    public void setPaymentHistoryScore(Integer paymentHistoryScore) { this.paymentHistoryScore = paymentHistoryScore; }

    public Double getCreditUtilizationRatio() { return creditUtilizationRatio; }
    public void setCreditUtilizationRatio(Double creditUtilizationRatio) { this.creditUtilizationRatio = creditUtilizationRatio; }

    public Integer getCreditAgeMonths() { return creditAgeMonths; }
    public void setCreditAgeMonths(Integer creditAgeMonths) { this.creditAgeMonths = creditAgeMonths; }

    public Integer getNumberOfAccounts() { return numberOfAccounts; }
    public void setNumberOfAccounts(Integer numberOfAccounts) { this.numberOfAccounts = numberOfAccounts; }

    public Double getEmi() { return emi; }
    public void setEmi(Double emi) { this.emi = emi; }

    public Integer getExistingLoans() { return existingLoans; }
    public void setExistingLoans(Integer existingLoans) { this.existingLoans = existingLoans; }

    public Double getSavings() { return savings; }
    public void setSavings(Double savings) { this.savings = savings; }

    public Double getMonthlyExpenses() { return monthlyExpenses; }
    public void setMonthlyExpenses(Double monthlyExpenses) { this.monthlyExpenses = monthlyExpenses; }

    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }

    // Preferences
    public String getPreferredLoanTypes() { return preferredLoanTypes; }
    public void setPreferredLoanTypes(String preferredLoanTypes) { this.preferredLoanTypes = preferredLoanTypes; }

    public Integer getPreferredTenure() { return preferredTenure; }
    public void setPreferredTenure(Integer preferredTenure) { this.preferredTenure = preferredTenure; }

    public String getRiskAppetite() { return riskAppetite; }
    public void setRiskAppetite(String riskAppetite) { this.riskAppetite = riskAppetite; }

    // Security & Audit
    public Boolean getGoogleConnected() { return googleConnected; }
    public void setGoogleConnected(Boolean googleConnected) { this.googleConnected = googleConnected; }

    public LocalDateTime getProfileCompletedAt() { return profileCompletedAt; }
    public void setProfileCompletedAt(LocalDateTime profileCompletedAt) { this.profileCompletedAt = profileCompletedAt; }

    public LocalDateTime getLastFinancialUpdateAt() { return lastFinancialUpdateAt; }
    public void setLastFinancialUpdateAt(LocalDateTime lastFinancialUpdateAt) { this.lastFinancialUpdateAt = lastFinancialUpdateAt; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public String getFinancialAuditLog() { return financialAuditLog; }
    public void setFinancialAuditLog(String financialAuditLog) { this.financialAuditLog = financialAuditLog; }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns effective annual income (from income field, or monthlyIncome*12). */
    public double effectiveAnnualIncome() {
        if (income != null && income > 0) return income;
        if (monthlyIncome != null && monthlyIncome > 0) return monthlyIncome * 12.0;
        return 0;
    }

    /** Returns effective monthly income. */
    public double effectiveMonthlyIncome() {
        if (monthlyIncome != null && monthlyIncome > 0) return monthlyIncome;
        if (income != null && income > 0) return income / 12.0;
        return 0;
    }

    /** Appends an audit entry (pipe-separated, max last 10 entries). */
    public void appendAudit(String entry) {
        String ts = java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")
                .format(LocalDateTime.now());
        String newEntry = ts + ": " + entry;
        if (financialAuditLog == null || financialAuditLog.isBlank()) {
            financialAuditLog = newEntry;
        } else {
            String[] parts = financialAuditLog.split("\\|");
            int start = Math.max(0, parts.length - 9);
            StringBuilder sb = new StringBuilder();
            for (int i = start; i < parts.length; i++) {
                if (sb.length() > 0) sb.append("|");
                sb.append(parts[i]);
            }
            sb.append("|").append(newEntry);
            financialAuditLog = sb.toString();
        }
    }

    /** Returns preferred loan types as a List. */
    public java.util.List<String> getPreferredLoanTypesList() {
        if (preferredLoanTypes == null || preferredLoanTypes.isBlank()) return java.util.List.of();
        return java.util.Arrays.asList(preferredLoanTypes.split("\\|"));
    }

    /** Returns audit log entries as a List (newest first). */
    public java.util.List<String> getAuditEntries() {
        if (financialAuditLog == null || financialAuditLog.isBlank()) return java.util.List.of();
        String[] parts = financialAuditLog.split("\\|");
        java.util.List<String> list = java.util.Arrays.asList(parts);
        java.util.Collections.reverse(list);
        return list;
    }

    /** Alias kept for backward compatibility. */
    public Integer getEmploymentYears() { return employmentStabilityYears; }
    public void setEmploymentYears(Integer v) { this.employmentStabilityYears = v; }
}
