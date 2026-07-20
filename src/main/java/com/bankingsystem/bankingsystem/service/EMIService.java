package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.entity.EMI;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.EMIRepository;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class EMIService {

    private final EMIRepository emiRepository;
    private final LoanRepository loanRepository;
    private final JdbcTemplate jdbc;

    public EMIService(EMIRepository emiRepository, LoanRepository loanRepository, JdbcTemplate jdbc) {
        this.emiRepository = emiRepository;
        this.loanRepository = loanRepository;
        this.jdbc = jdbc;
    }

    /** Reset Postgres IDENTITY sequence to MAX(id)+1 — works on Neon/PostgreSQL. */
    private void resetEmiSequence() {
        try {
            jdbc.execute(
                "SELECT pg_catalog.setval(" +
                "  pg_get_serial_sequence('emi','id'), " +
                "  COALESCE((SELECT MAX(id) FROM emi), 0) + 1, " +
                "  false" +
                ")"
            );
        } catch (Exception e) {
            System.err.println("EMI sequence reset (non-fatal): " + e.getMessage());
        }
    }

    // Generate EMIs for a loan when it's approved
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void generateEMIsForLoan(Loan loan) {
        List<EMI> existing = emiRepository.findByLoanId(loan.getId());
        if (!existing.isEmpty()) {
            System.out.println("EMIs already exist for loan ID: " + loan.getId());
            return;
        }

        // Fix out-of-sync Postgres sequence before inserting
        resetEmiSequence();

        LocalDate firstEMIDate = loan.getApprovalDate() != null
            ? loan.getApprovalDate().toLocalDate().plusMonths(1)
            : LocalDate.now().plusMonths(1);

        for (int i = 1; i <= loan.getTenure(); i++) {
            EMI emi = new EMI();
            emi.setLoan(loan);
            emi.setEmiNumber(i);
            emi.setDueDate(firstEMIDate.plusMonths(i - 1));
            emi.setAmount(loan.getEmiAmount() != null ? loan.getEmiAmount() : 0.0);
            emi.setStatus(EMI.Status.PENDING);
            emiRepository.saveAndFlush(emi);
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
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = YearMonth.from(today).atEndOfMonth();
        return emiRepository.findEMIsDueThisMonth(customerId, startOfMonth, endOfMonth);
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
        double totalPenalty = overdueEMIs.stream().mapToDouble(EMI::getComputedLateFee).sum();

        long paidCount = allEMIs.stream().filter(e -> e.getStatus() == EMI.Status.PAID).count();

        return new EMIStats(
            allEMIs.size(),
            pendingEMIs.size(),
            overdueEMIs.size(),
            (int) paidCount,
            totalPending,
            totalOverdue,
            totalPenalty
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
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void generateEMIsForHistoricalLoan(Loan loan) {
        resetEmiSequence();
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
        private final double totalPenaltyAmount;

        public EMIStats(int totalEMIs, int pendingEMIs, int overdueEMIs, int paidEMIs,
                       double totalPendingAmount, double totalOverdueAmount, double totalPenaltyAmount) {
            this.totalEMIs = totalEMIs;
            this.pendingEMIs = pendingEMIs;
            this.overdueEMIs = overdueEMIs;
            this.paidEMIs = paidEMIs;
            this.totalPendingAmount = totalPendingAmount;
            this.totalOverdueAmount = totalOverdueAmount;
            this.totalPenaltyAmount = totalPenaltyAmount;
        }

        // Getters
        public int getTotalEMIs() { return totalEMIs; }
        public int getPendingEMIs() { return pendingEMIs; }
        public int getOverdueEMIs() { return overdueEMIs; }
        public int getPaidEMIs() { return paidEMIs; }
        public double getTotalPendingAmount() { return totalPendingAmount; }
        public double getTotalOverdueAmount() { return totalOverdueAmount; }
        public double getTotalPenaltyAmount() { return totalPenaltyAmount; }
    }

    // Inner class for loan waterfall statistics
    public static class WaterfallStats {
        private final int totalEMIs;
        private final int paidEMIs;
        private final int pendingEMIs;
        private final int overdueEMIs;
        private final double totalPaidAmount;
        private final double totalPendingAmount;
        private final double totalPenaltyAmount;
        private final double completionPercentage;

        public WaterfallStats(int totalEMIs, int paidEMIs, int pendingEMIs, int overdueEMIs,
                             double totalPaidAmount, double totalPendingAmount,
                             double totalPenaltyAmount, double completionPercentage) {
            this.totalEMIs = totalEMIs;
            this.paidEMIs = paidEMIs;
            this.pendingEMIs = pendingEMIs;
            this.overdueEMIs = overdueEMIs;
            this.totalPaidAmount = totalPaidAmount;
            this.totalPendingAmount = totalPendingAmount;
            this.totalPenaltyAmount = totalPenaltyAmount;
            this.completionPercentage = completionPercentage;
        }

        public int getTotalEMIs() { return totalEMIs; }
        public int getPaidEMIs() { return paidEMIs; }
        public int getPendingEMIs() { return pendingEMIs; }
        public int getOverdueEMIs() { return overdueEMIs; }
        public double getTotalPaidAmount() { return totalPaidAmount; }
        public double getTotalPendingAmount() { return totalPendingAmount; }
        public double getTotalPenaltyAmount() { return totalPenaltyAmount; }
        public double getCompletionPercentage() { return completionPercentage; }
    }

    /** Generate missing EMIs for a specific customer's approved loans */
    @Transactional
    public int generateMissingEmisForCustomer(Long customerId) {
        List<Loan> approved = loanRepository.findByStatus(Loan.Status.APPROVED);
        int count = 0;
        for (Loan loan : approved) {
            if (loan.getCustomer() != null && loan.getCustomer().getId().equals(customerId)) {
                List<EMI> existing = emiRepository.findByLoanId(loan.getId());
                if (existing.isEmpty()) {
                    try {
                        generateEMIsForHistoricalLoan(loan);
                        count++;
                    } catch (Exception e) {
                        System.err.println("EMI gen error loan " + loan.getId() + ": " + e.getMessage());
                    }
                }
            }
        }
        return count;
    }

    /** Returns approved loans for a customer that have no EMIs yet */
    public List<Loan> getApprovedLoansWithoutEmis(Long customerId) {
        List<Loan> approved = loanRepository.findByStatus(Loan.Status.APPROVED);
        return approved.stream()
            .filter(l -> l.getCustomer() != null && l.getCustomer().getId().equals(customerId))
            .filter(l -> emiRepository.findByLoanId(l.getId()).isEmpty())
            .collect(java.util.stream.Collectors.toList());
    }

    // Get all EMIs for a specific loan in waterfall order
    public List<EMI> getEMIsByLoanId(Long loanId) {
        return emiRepository.findByLoanIdOrderByEmiNumberAsc(loanId);
    }

    // Pay partial amount toward an EMI
    @Transactional
    public EMI payPartialEMI(Long emiId, Double paymentAmount, String paymentMethod) throws Exception {
        Optional<EMI> emiOpt = emiRepository.findById(emiId);
        if (emiOpt.isEmpty()) throw new Exception("EMI not found");
        EMI emi = emiOpt.get();
        if (emi.getStatus() == EMI.Status.PAID) throw new Exception("EMI already paid");
        if (paymentAmount == null || paymentAmount < 100) throw new Exception("Minimum payment is ₹100");

        double currentPartial = emi.getPartialAmountPaid() != null ? emi.getPartialAmountPaid() : 0.0;
        double newPartial = currentPartial + paymentAmount;
        double totalDue = emi.getAmount() + emi.getComputedLateFee();

        if (newPartial >= totalDue) {
            // Full payment achieved
            emi.setPartialAmountPaid(0.0);
            emi.setLateFee(emi.getComputedLateFee());
            emi.setStatus(EMI.Status.PAID);
            emi.setPaymentDate(LocalDateTime.now());
            emi.setPaymentMethod(paymentMethod);
        } else {
            emi.setPartialAmountPaid(newPartial);
        }

        return emiRepository.save(emi);
    }

    // Pay all remaining EMIs for a loan (foreclosure)
    @Transactional
    public int payFullLoan(Long loanId, String paymentMethod) throws Exception {
        List<EMI> emis = emiRepository.findByLoanIdOrderByEmiNumberAsc(loanId);
        int paidCount = 0;
        for (EMI emi : emis) {
            if (emi.getStatus() != EMI.Status.PAID) {
                emi.setLateFee(emi.getComputedLateFee());
                emi.setPartialAmountPaid(0.0);
                emi.setStatus(EMI.Status.PAID);
                emi.setPaymentDate(LocalDateTime.now());
                emi.setPaymentMethod(paymentMethod + " (Foreclosure)");
                emiRepository.save(emi);
                paidCount++;
            }
        }
        if (paidCount == 0) throw new Exception("No pending EMIs found for this loan");
        return paidCount;
    }

    // Get waterfall stats for a specific loan
    public WaterfallStats getLoanWaterfallStats(Long loanId) {
        List<EMI> emis = emiRepository.findByLoanIdOrderByEmiNumberAsc(loanId);
        int total = emis.size();
        int paid = 0, pending = 0, overdue = 0;
        double totalPaidAmt = 0, totalPendingAmt = 0, totalPenalties = 0;
        LocalDate today = LocalDate.now();

        for (EMI emi : emis) {
            if (emi.getStatus() == EMI.Status.PAID) {
                paid++;
                totalPaidAmt += emi.getAmount() + (emi.getLateFee() != null ? emi.getLateFee() : 0.0);
            } else {
                double penalty = emi.getComputedLateFee();
                totalPenalties += penalty;
                if (emi.getDueDate().isBefore(today)) {
                    overdue++;
                    totalPendingAmt += emi.getAmount() + penalty;
                } else {
                    pending++;
                    totalPendingAmt += emi.getAmount();
                }
            }
        }

        double completion = total > 0 ? Math.round((paid * 100.0 / total) * 10.0) / 10.0 : 0.0;
        return new WaterfallStats(total, paid, pending, overdue, totalPaidAmt, totalPendingAmt, totalPenalties, completion);
    }

    // Get all approved loans for a customer that have EMIs (for loan selector tabs)
    public List<Loan> getActiveLoansWithEmis(Long customerId) {
        List<Loan> approved = loanRepository.findByCustomerIdAndStatus(customerId, Loan.Status.APPROVED);
        return approved.stream()
            .filter(l -> !emiRepository.findByLoanId(l.getId()).isEmpty())
            .collect(java.util.stream.Collectors.toList());
    }
}
