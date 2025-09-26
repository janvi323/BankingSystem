package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.entity.Customer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * Migration component to update existing customers with financial data
 * and calculate their credit scores using the microservice
 */
@Component
public class CustomerDataMigration implements CommandLineRunner {

    private final CustomerService customerService;
    private final Random random = new Random();

    public CustomerDataMigration(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("🔄 Starting Customer Data Migration");
        System.out.println("=".repeat(60));

        migrateExistingCustomers();

        System.out.println("=".repeat(60));
        System.out.println("✅ Customer Data Migration Complete");
        System.out.println("=".repeat(60));
    }

    private void migrateExistingCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        int migrated = 0;
        int skipped = 0;

        for (Customer customer : customers) {
            if (customer.getRole() == Customer.Role.CUSTOMER) {
                // Check if customer already has financial data
                if (customer.getIncome() == null) {
                    System.out.println("📝 Migrating customer: " + customer.getName() + " (ID: " + customer.getId() + ")");
                    
                    // Generate reasonable default financial data based on different profiles
                    populateFinancialData(customer);
                    
                    // Save the updated customer
                    Customer updatedCustomer = customerService.updateCustomer(customer);
                    
                    // Synchronize credit score with microservice
                    customerService.synchronizeCreditScore(updatedCustomer.getId());
                    
                    migrated++;
                    System.out.println("✅ Migrated: " + customer.getName() + " - Credit Score: " + customer.getCreditScore());
                } else {
                    // Customer already has financial data, just synchronize credit score
                    customerService.synchronizeCreditScore(customer.getId());
                    skipped++;
                    System.out.println("🔄 Synchronized existing: " + customer.getName());
                }
            }
        }

        System.out.println("📊 Migration Summary:");
        System.out.println("   - Customers migrated: " + migrated);
        System.out.println("   - Customers synchronized: " + skipped);
    }

    private void populateFinancialData(Customer customer) {
        // Generate varied financial profiles to create realistic diversity
        int profileType = random.nextInt(5); // 5 different profile types

        switch (profileType) {
            case 0: // Excellent Credit Profile
                customer.setIncome(80000.0 + random.nextDouble() * 40000); // 80K-120K
                customer.setDebtToIncomeRatio(0.10 + random.nextDouble() * 0.15); // 10-25%
                customer.setPaymentHistoryScore(90 + random.nextInt(6)); // 90-95
                customer.setCreditUtilizationRatio(0.05 + random.nextDouble() * 0.15); // 5-20%
                customer.setCreditAgeMonths(60 + random.nextInt(61)); // 5-10 years
                customer.setNumberOfAccounts(5 + random.nextInt(6)); // 5-10 accounts
                break;

            case 1: // Very Good Credit Profile
                customer.setIncome(60000.0 + random.nextDouble() * 30000); // 60K-90K
                customer.setDebtToIncomeRatio(0.20 + random.nextDouble() * 0.15); // 20-35%
                customer.setPaymentHistoryScore(80 + random.nextInt(10)); // 80-89
                customer.setCreditUtilizationRatio(0.15 + random.nextDouble() * 0.20); // 15-35%
                customer.setCreditAgeMonths(36 + random.nextInt(49)); // 3-7 years
                customer.setNumberOfAccounts(3 + random.nextInt(5)); // 3-7 accounts
                break;

            case 2: // Good Credit Profile
                customer.setIncome(45000.0 + random.nextDouble() * 25000); // 45K-70K
                customer.setDebtToIncomeRatio(0.30 + random.nextDouble() * 0.20); // 30-50%
                customer.setPaymentHistoryScore(70 + random.nextInt(10)); // 70-79
                customer.setCreditUtilizationRatio(0.25 + random.nextDouble() * 0.25); // 25-50%
                customer.setCreditAgeMonths(24 + random.nextInt(37)); // 2-5 years
                customer.setNumberOfAccounts(2 + random.nextInt(4)); // 2-5 accounts
                break;

            case 3: // Fair Credit Profile
                customer.setIncome(30000.0 + random.nextDouble() * 20000); // 30K-50K
                customer.setDebtToIncomeRatio(0.45 + random.nextDouble() * 0.25); // 45-70%
                customer.setPaymentHistoryScore(55 + random.nextInt(15)); // 55-69
                customer.setCreditUtilizationRatio(0.40 + random.nextDouble() * 0.30); // 40-70%
                customer.setCreditAgeMonths(12 + random.nextInt(25)); // 1-3 years
                customer.setNumberOfAccounts(1 + random.nextInt(3)); // 1-3 accounts
                break;

            case 4: // Poor Credit Profile
                customer.setIncome(20000.0 + random.nextDouble() * 15000); // 20K-35K
                customer.setDebtToIncomeRatio(0.65 + random.nextDouble() * 0.25); // 65-90%
                customer.setPaymentHistoryScore(30 + random.nextInt(20)); // 30-49
                customer.setCreditUtilizationRatio(0.70 + random.nextDouble() * 0.25); // 70-95%
                customer.setCreditAgeMonths(6 + random.nextInt(19)); // 6 months-2 years
                customer.setNumberOfAccounts(1 + random.nextInt(2)); // 1-2 accounts
                break;
        }

        System.out.println("   Generated profile type " + profileType + " for " + customer.getName());
    }
}