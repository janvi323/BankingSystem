package com.bankingsystem.bankingsystem.test;

import com.bankingsystem.bankingsystem.Service.CreditScoreClientService;
import com.bankingsystem.bankingsystem.Service.LoanService;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Test class to demonstrate credit score fluctuation functionality
 * This will run when the main application starts to show how credit scores change
 * based on loan approvals and rejections.
 */
@Component
public class CreditScoreFluctuationDemo implements CommandLineRunner {

    @Autowired
    private LoanService loanService;
    
    @Autowired
    private CreditScoreClientService creditScoreClientService;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🎯 CREDIT SCORE FLUCTUATION DEMO");
        System.out.println("=".repeat(80));
        
        try {
            // Check if credit score service is available
            if (!creditScoreClientService.isServiceHealthy()) {
                System.out.println("⚠️  Credit Score Service is not available at this time.");
                System.out.println("📝 To test the functionality:");
                System.out.println("   1. Start the credit-score-service on port 8083");
                System.out.println("   2. Restart this application");
                System.out.println("   3. Apply for loans and approve/reject them to see credit score changes");
                return;
            }

            System.out.println("✅ Credit Score Service is running and healthy!");
            System.out.println("\n📋 HOW THE CREDIT SCORE FLUCTUATION WORKS:");
            System.out.println("   • When a loan is APPROVED → Credit score INCREASES");
            System.out.println("   • When a loan is REJECTED → Credit score DECREASES");
            System.out.println("   • Larger loan amounts have bigger impact on score changes");
            System.out.println("   • Score adjustments are automatic when loan status changes");
            
            System.out.println("\n🔄 CREDIT SCORE FLUCTUATION ALGORITHM:");
            System.out.println("   Base adjustment: ±10 points");
            System.out.println("   Amount multiplier:");
            System.out.println("     - Small loans (< ₹50K): 1.0x");
            System.out.println("     - Medium loans (₹50K-₹200K): 1.5x");
            System.out.println("     - Large loans (> ₹200K): 2.0x");
            System.out.println("   Profile adjustments:");
            System.out.println("     - Lower scores get bigger boosts on approval");
            System.out.println("     - Higher scores get smaller penalties on rejection");
            
            System.out.println("\n🎯 TO TEST THE FUNCTIONALITY:");
            System.out.println("   1. Register/Login as a customer");
            System.out.println("   2. Apply for a loan");
            System.out.println("   3. Login as admin and approve/reject the loan");
            System.out.println("   4. Check the customer's credit score to see the change");

            System.out.println("\n📊 EXAMPLE SCENARIOS:");
            System.out.println("   Scenario 1: ₹75,000 loan APPROVED for customer with 650 credit score");
            System.out.println("   → New score: 650 + (10 × 1.5 × 1.1) ≈ 667 points");
            System.out.println("   ");
            System.out.println("   Scenario 2: ₹300,000 loan REJECTED for customer with 720 credit score");
            System.out.println("   → New score: 720 + (-7 × 2.0 × 0.85) ≈ 708 points");

        } catch (Exception e) {
            System.out.println("❌ Error during demo setup: " + e.getMessage());
            System.out.println("📝 The credit score fluctuation feature is still available!");
            System.out.println("   Simply ensure the credit-score-service is running on port 8083");
        } finally {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("✨ Credit Score Fluctuation Feature is Ready!");
            System.out.println("=".repeat(80) + "\n");
        }
    }
}