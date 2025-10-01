package com.bankingsystem.bankingsystem.Service;

import org.springframework.stereotype.Service;

@Service
public class LoanCalculationService {

    /**
     * Calculate interest rate based on loan purpose, amount, and tenure
     * Interest rates are determined by loan type and risk assessment
     */
    public double calculateInterestRate(String purpose, double amount, int tenureMonths) {
        double baseRate = getBaseRateByPurpose(purpose);

        // Adjust rate based on loan amount (higher amounts get slightly better rates)
        double amountAdjustment = getAmountAdjustment(amount);

        // Adjust rate based on tenure (longer tenure = higher rate)
        double tenureAdjustment = getTenureAdjustment(tenureMonths);

        double finalRate = baseRate + amountAdjustment + tenureAdjustment;

        // Ensure minimum rate of 8% and maximum rate of 20%
        return Math.max(8.0, Math.min(20.0, finalRate));
    }

    /**
     * Calculate EMI using the standard EMI formula
     * EMI = P * r * (1 + r)^n / ((1 + r)^n - 1)
     * where P = Principal, r = Monthly interest rate, n = Number of months
     */
    public double calculateEMI(double principal, double annualRate, int tenureMonths) {
        if (tenureMonths == 0 || annualRate == 0) {
            return principal / Math.max(1, tenureMonths);
        }

        double monthlyRate = annualRate / (12 * 100); // Convert annual % to monthly decimal

        double emi = principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths) /
                    (Math.pow(1 + monthlyRate, tenureMonths) - 1);

        return Math.round(emi * 100.0) / 100.0; // Round to 2 decimal places
    }

    /**
     * Calculate total amount to be paid over the loan tenure
     */
    public double calculateTotalAmount(double emi, int tenureMonths) {
        return Math.round(emi * tenureMonths * 100.0) / 100.0;
    }

    /**
     * Get base interest rate by loan purpose
     */
    private double getBaseRateByPurpose(String purpose) {
        switch (purpose.toLowerCase()) {
            case "home purchase":
                return 9.5; // Home loans typically have lower rates
            case "home improvement":
                return 11.0;
            case "car purchase":
                return 10.5;
            case "education":
                return 9.0; // Education loans often have preferential rates
            case "business":
                return 14.0; // Business loans have higher risk
            case "personal":
                return 12.5;
            case "debt consolidation":
                return 13.0;
            case "medical expenses":
                return 11.5;
            case "other":
            default:
                return 12.0;
        }
    }

    /**
     * Adjust rate based on loan amount
     */
    private double getAmountAdjustment(double amount) {
        if (amount >= 1000000) { // 10 Lakh and above
            return -1.0; // 1% discount for large loans
        } else if (amount >= 500000) { // 5 Lakh to 10 Lakh
            return -0.5; // 0.5% discount
        } else if (amount >= 100000) { // 1 Lakh to 5 Lakh
            return 0.0; // No adjustment
        } else {
            return 0.5; // Small loans have slightly higher rates
        }
    }

    /**
     * Adjust rate based on tenure
     */
    private double getTenureAdjustment(int tenureMonths) {
        if (tenureMonths <= 12) { // Up to 1 year
            return 0.0;
        } else if (tenureMonths <= 36) { // 1-3 years
            return 0.5;
        } else if (tenureMonths <= 60) { // 3-5 years
            return 1.0;
        } else if (tenureMonths <= 120) { // 5-10 years
            return 1.5;
        } else { // More than 10 years
            return 2.0;
        }
    }
}
