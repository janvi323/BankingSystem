package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.entity.EMI;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.EMIRepository;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EMIService {

    private final EMIRepository emiRepository;
    private final LoanRepository loanRepository;

    public EMIService(EMIRepository emiRepository, LoanRepository loanRepository) {
        this.emiRepository = emiRepository;
        this.loanRepository = loanRepository;
    }

    // Generate EMIs for a loan when it's approved
    @Transactional
    public void generateEMIsForLoan(Loan loan) {
        // Check if EMIs already exist for this loan
        List<EMI> existingEMIs = emiRepository.findByLoanId(loan.getId());
        if (!existingEMIs.isEmpty()) {
            System.out.println("EMIs already exist for loan ID: " + loan.getId());
            return;
        }

        LocalDate firstEMIDate = loan.getApprovalDate() != null
            ? loan.getApprovalDate().toLocalDate().plusMonths(1)
            : LocalDate.now().plusMonths(1);

        for (int i = 1; i <= loan.getTenure(); i++) {
            EMI emi = new EMI();
            emi.setLoan(loan);
            emi.setEmiNumber(i);
            emi.setDueDate(firstEMIDate.plusMonths(i - 1));
            emi.setAmount(loan.getEmiAmount());
            emi.setStatus(EMI.Status.PENDING);

            emiRepository.save(emi);
        }

        System.out.println("Generated " + loan.getTenure() + " EMIs for loan ID: " + loan.getId());
    }

    // Get all EMIs for a customer
    public List<EMI> getEMIsByCustomerId(Long customerId) {
        return emiRepository.findByCustomerId(customerId);
    }

    // Get pending EMIs for a customer
    public List<EMI> getPendingEMIsByCustomerId(Long customerId) {
        return emiRepository.findPendingEMIsByCustomerId(customerId);
    }

    // Get overdue EMIs for a customer
    public List<EMI> getOverdueEMIsByCustomerId(Long customerId) {
        return emiRepository.findOverdueEMIsByCustomerId(customerId, LocalDate.now());
    }

    // Get EMIs due this month for a customer
    public List<EMI> getEMIsDueThisMonth(Long customerId) {
        return emiRepository.findEMIsDueThisMonth(customerId, LocalDate.now());
    }

    // Process EMI payment (fake payment for demo)
    @Transactional
    public EMI payEMI(Long emiId, String paymentMethod) throws Exception {
        Optional<EMI> emiOpt = emiRepository.findById(emiId);
        if (emiOpt.isEmpty()) {
            throw new Exception("EMI not found");
        }

        EMI emi = emiOpt.get();

        if (emi.getStatus() == EMI.Status.PAID) {
            throw new Exception("EMI already paid");
        }

        // Process fake payment
        emi.setStatus(EMI.Status.PAID);
        emi.setPaymentDate(LocalDateTime.now());
        emi.setPaymentMethod(paymentMethod);

        EMI savedEMI = emiRepository.save(emi);

        System.out.println("EMI payment processed - ID: " + emiId +
                         ", Amount: ₹" + emi.getAmount() +
                         ", Method: " + paymentMethod);

        return savedEMI;
    }

    // Get EMI statistics for a customer
    public EMIStats getEMIStats(Long customerId) {
        List<EMI> allEMIs = getEMIsByCustomerId(customerId);
        List<EMI> pendingEMIs = getPendingEMIsByCustomerId(customerId);
        List<EMI> overdueEMIs = getOverdueEMIsByCustomerId(customerId);

        double totalPending = pendingEMIs.stream().mapToDouble(EMI::getAmount).sum();
        double totalOverdue = overdueEMIs.stream().mapToDouble(EMI::getAmount).sum();

        long paidCount = allEMIs.stream().filter(e -> e.getStatus() == EMI.Status.PAID).count();

        return new EMIStats(
            allEMIs.size(),
            pendingEMIs.size(),
            overdueEMIs.size(),
            (int) paidCount,
            totalPending,
            totalOverdue
        );
    }

    // Generate EMIs for all approved loans that don't have EMIs yet
    @Transactional
    public void generateEMIsForAllApprovedLoans() {
        List<Loan> approvedLoans = loanRepository.findByStatus(Loan.Status.APPROVED);

        for (Loan loan : approvedLoans) {
            try {
                generateEMIsForLoan(loan);
            } catch (Exception e) {
                System.err.println("Error generating EMIs for loan " + loan.getId() + ": " + e.getMessage());
            }
        }
    }

    // Generate EMIs specifically for approved loans that don't have EMIs (for historical data)
    @Transactional
    public int generateMissingEMIsForApprovedLoans() {
        List<Loan> approvedLoans = loanRepository.findByStatus(Loan.Status.APPROVED);
        int generatedCount = 0;

        for (Loan loan : approvedLoans) {
            try {
                // Check if this loan already has EMIs
                List<EMI> existingEMIs = emiRepository.findByLoanId(loan.getId());
                if (existingEMIs.isEmpty()) {
                    generateEMIsForHistoricalLoan(loan);
                    generatedCount++;
                    System.out.println("Generated EMIs for historical loan ID: " + loan.getId());
                }
            } catch (Exception e) {
                System.err.println("Error generating EMIs for historical loan " + loan.getId() + ": " + e.getMessage());
            }
        }

        return generatedCount;
    }

    // Generate EMIs for a historical approved loan with realistic past due dates
    @Transactional
    public void generateEMIsForHistoricalLoan(Loan loan) {
        // For historical loans, we'll start EMIs from when they were approved
        // or use a reasonable default if approval date is missing
        LocalDate startDate;
        if (loan.getApprovalDate() != null) {
            startDate = loan.getApprovalDate().toLocalDate().plusMonths(1);
        } else {
            // If no approval date, assume it was approved 6 months ago for demo purposes
            startDate = LocalDate.now().minusMonths(Math.min(6, loan.getTenure() - 1));
        }

        // If the loan doesn't have EMI amount calculated, calculate it now
        if (loan.getEmiAmount() == null || loan.getEmiAmount() == 0) {
            // Use default interest rate for historical loans if not set
            double interestRate = loan.getInterestRate() != null ? loan.getInterestRate().doubleValue() : 12.0;
            com.bankingsystem.bankingsystem.Service.LoanCalculationService calcService =
                new com.bankingsystem.bankingsystem.Service.LoanCalculationService();

            double emiAmount = calcService.calculateEMI(loan.getAmount(), interestRate, loan.getTenure());
            loan.setEmiAmount(emiAmount);
            loan.setInterestRate(interestRate);
            loan.setTotalAmount(calcService.calculateTotalAmount(emiAmount, loan.getTenure()));
            loanRepository.save(loan);
        }

        // Generate EMIs with realistic dates
        for (int i = 1; i <= loan.getTenure(); i++) {
            EMI emi = new EMI();
            emi.setLoan(loan);
            emi.setEmiNumber(i);
            emi.setDueDate(startDate.plusMonths(i - 1));
            emi.setAmount(loan.getEmiAmount());

            // For historical loans, mark some EMIs as paid based on due dates
            LocalDate today = LocalDate.now();
            if (emi.getDueDate().isBefore(today.minusMonths(1))) {
                // EMIs that were due more than 1 month ago are marked as paid
                emi.setStatus(EMI.Status.PAID);
                emi.setPaymentDate(emi.getDueDate().atStartOfDay().plusDays(
                    (long) (Math.random() * 10))); // Random payment within 10 days of due date
                emi.setPaymentMethod("Historical Payment");
            } else {
                // Recent and future EMIs remain pending
                emi.setStatus(EMI.Status.PENDING);
            }

            emiRepository.save(emi);
        }

        System.out.println("Generated " + loan.getTenure() + " EMIs for historical loan ID: " + loan.getId() +
                         " with EMI amount: ₹" + loan.getEmiAmount());
    }

    // Inner class for EMI statistics
    public static class EMIStats {
        private final int totalEMIs;
        private final int pendingEMIs;
        private final int overdueEMIs;
        private final int paidEMIs;
        private final double totalPendingAmount;
        private final double totalOverdueAmount;

        public EMIStats(int totalEMIs, int pendingEMIs, int overdueEMIs, int paidEMIs,
                       double totalPendingAmount, double totalOverdueAmount) {
            this.totalEMIs = totalEMIs;
            this.pendingEMIs = pendingEMIs;
            this.overdueEMIs = overdueEMIs;
            this.paidEMIs = paidEMIs;
            this.totalPendingAmount = totalPendingAmount;
            this.totalOverdueAmount = totalOverdueAmount;
        }

        // Getters
        public int getTotalEMIs() { return totalEMIs; }
        public int getPendingEMIs() { return pendingEMIs; }
        public int getOverdueEMIs() { return overdueEMIs; }
        public int getPaidEMIs() { return paidEMIs; }
        public double getTotalPendingAmount() { return totalPendingAmount; }
        public double getTotalOverdueAmount() { return totalOverdueAmount; }
    }
}
