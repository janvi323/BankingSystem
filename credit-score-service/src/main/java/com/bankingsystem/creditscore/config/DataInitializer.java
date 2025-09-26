package com.bankingsystem.creditscore.config;

import com.bankingsystem.creditscore.dto.CreditScoreRequest;
import com.bankingsystem.creditscore.service.CreditScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Data initializer to create sample customer profiles with contrasting credit scores
 * This demonstrates how debt profiles affect credit score calculations
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CreditScoreService creditScoreService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🎯 INITIALIZING SAMPLE CUSTOMER PROFILES");
        System.out.println("=".repeat(80));

        // Create Profile 1: High Debt, Poor Credit (Expected Low Score)
        createHighDebtProfile();
        
        // Create Profile 2: Low Debt, Excellent Credit (Expected High Score)
        createLowDebtProfile();
        
        System.out.println("=".repeat(80));
        System.out.println("✅ Sample profiles created successfully!");
        System.out.println("🔍 Access http://localhost:8083/api/credit-scores/customer/{customerId} to view scores");
        System.out.println("=".repeat(80) + "\n");
    }

    private void createHighDebtProfile() {
        try {
            System.out.println("\n📉 Creating HIGH DEBT profile (Expected: Low Credit Score)");
            
            CreditScoreRequest highDebtRequest = new CreditScoreRequest();
            highDebtRequest.setCustomerId(1001L);
            highDebtRequest.setCustomerName("Sarah Wilson");
            highDebtRequest.setCustomerEmail("sarah.wilson@example.com");
            
            // Poor financial profile
            highDebtRequest.setIncome(35000.0); // Low income
            highDebtRequest.setDebtToIncomeRatio(0.85); // Very high debt-to-income ratio (85%)
            highDebtRequest.setPaymentHistoryScore(45); // Poor payment history
            highDebtRequest.setCreditUtilizationRatio(0.95); // Very high credit utilization (95%)
            highDebtRequest.setCreditAgeMonths(18); // Short credit history
            highDebtRequest.setNumberOfAccounts(8); // Many accounts (potentially overextended)
            
            var response = creditScoreService.calculateCreditScore(highDebtRequest);
            
            System.out.println("   Customer: " + response.getCustomerName());
            System.out.println("   Income: ₹" + String.format("%,.2f", response.getIncome()));
            System.out.println("   Debt-to-Income Ratio: " + String.format("%.1f%%", response.getDebtToIncomeRatio() * 100));
            System.out.println("   Payment History Score: " + response.getPaymentHistoryScore() + "/100");
            System.out.println("   Credit Utilization: " + String.format("%.1f%%", response.getCreditUtilizationRatio() * 100));
            System.out.println("   Credit Age: " + response.getCreditAgeMonths() + " months");
            System.out.println("   Number of Accounts: " + response.getNumberOfAccounts());
            System.out.println("   🎯 CALCULATED CREDIT SCORE: " + response.getCreditScore() + " (" + response.getScoreGrade() + ")");
            
        } catch (Exception e) {
            System.out.println("   ❌ Error creating high debt profile: " + e.getMessage());
        }
    }

    private void createLowDebtProfile() {
        try {
            System.out.println("\n📈 Creating LOW DEBT profile (Expected: High Credit Score)");
            
            CreditScoreRequest lowDebtRequest = new CreditScoreRequest();
            lowDebtRequest.setCustomerId(1002L);
            lowDebtRequest.setCustomerName("Michael Johnson");
            lowDebtRequest.setCustomerEmail("michael.johnson@example.com");
            
            // Excellent financial profile
            lowDebtRequest.setIncome(95000.0); // High income
            lowDebtRequest.setDebtToIncomeRatio(0.15); // Very low debt-to-income ratio (15%)
            lowDebtRequest.setPaymentHistoryScore(98); // Excellent payment history
            lowDebtRequest.setCreditUtilizationRatio(0.05); // Very low credit utilization (5%)
            lowDebtRequest.setCreditAgeMonths(84); // Long credit history (7 years)
            lowDebtRequest.setNumberOfAccounts(4); // Moderate number of accounts
            
            var response = creditScoreService.calculateCreditScore(lowDebtRequest);
            
            System.out.println("   Customer: " + response.getCustomerName());
            System.out.println("   Income: ₹" + String.format("%,.2f", response.getIncome()));
            System.out.println("   Debt-to-Income Ratio: " + String.format("%.1f%%", response.getDebtToIncomeRatio() * 100));
            System.out.println("   Payment History Score: " + response.getPaymentHistoryScore() + "/100");
            System.out.println("   Credit Utilization: " + String.format("%.1f%%", response.getCreditUtilizationRatio() * 100));
            System.out.println("   Credit Age: " + response.getCreditAgeMonths() + " months");
            System.out.println("   Number of Accounts: " + response.getNumberOfAccounts());
            System.out.println("   🎯 CALCULATED CREDIT SCORE: " + response.getCreditScore() + " (" + response.getScoreGrade() + ")");
            
        } catch (Exception e) {
            System.out.println("   ❌ Error creating low debt profile: " + e.getMessage());
        }
    }
}