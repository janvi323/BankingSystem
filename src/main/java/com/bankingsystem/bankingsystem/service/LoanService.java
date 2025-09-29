package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import com.bankingsystem.bankingsystem.service.LoanCalculationService;
import com.bankingsystem.bankingsystem.service.EMIService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final CreditScoreClientService creditScoreClientService;
    private final LoanCalculationService loanCalculationService;
    private final EMIService emiService;

    public LoanService(LoanRepository loanRepository, CreditScoreClientService creditScoreClientService,
                      LoanCalculationService loanCalculationService, EMIService emiService) {
        this.loanRepository = loanRepository;
        this.creditScoreClientService = creditScoreClientService;
        this.loanCalculationService = loanCalculationService;
        this.emiService = emiService;
    }

    // Apply for a loan with automatic interest rate and EMI calculation
    @Transactional
    public Loan applyForLoan(Customer customer, Double amount, String purpose, Integer tenure) throws Exception {
        if (amount <= 0) {
            throw new Exception("Loan amount must be greater than 0");
        }

        if (tenure <= 0) {
            throw new Exception("Loan tenure must be greater than 0");
        }

        // Calculate interest rate based on loan parameters
        double interestRate = loanCalculationService.calculateInterestRate(purpose, amount, tenure);

        // Calculate EMI based on principal, interest rate, and tenure
        double emiAmount = loanCalculationService.calculateEMI(amount, interestRate, tenure);

        // Calculate total amount to be paid
        double totalAmount = loanCalculationService.calculateTotalAmount(emiAmount, tenure);

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setAmount(amount);
        loan.setPurpose(purpose);
        loan.setTenure(tenure);
        loan.setInterestRate(interestRate);
        loan.setEmiAmount(emiAmount);
        loan.setTotalAmount(totalAmount);
        loan.setStatus(Loan.Status.PENDING);
        loan.setApplicationDate(LocalDateTime.now());

        Loan savedLoan = loanRepository.save(loan);
        System.out.println("Loan saved with ID: " + savedLoan.getId() +
                         ", Interest Rate: " + interestRate + "%" +
                         ", EMI: ₹" + emiAmount +
                         ", Total Amount: ₹" + totalAmount);
        return savedLoan;
    }

    // Get loans by customer ID
    public List<Loan> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    // Get all loans (for admin)
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // Get loans by status
    public List<Loan> getLoansByStatus(Loan.Status status) {
        return loanRepository.findByStatus(status);
    }

    // Approve/Reject loan (admin only) - Updated to generate EMIs when approved
    public Loan updateLoanStatus(Long loanId, Loan.Status status, String adminComments) throws Exception {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);
        if (loanOpt.isEmpty()) {
            throw new Exception("Loan not found");
        }

        Loan loan = loanOpt.get();
        Loan.Status previousStatus = loan.getStatus();
        loan.setStatus(status);
        loan.setAdminComments(adminComments);

        if (status == Loan.Status.APPROVED || status == Loan.Status.REJECTED) {
            loan.setApprovalDate(LocalDateTime.now());
        }

        Loan savedLoan = loanRepository.save(loan);

        // Generate EMIs when loan is approved
        if (status == Loan.Status.APPROVED && previousStatus != Loan.Status.APPROVED) {
            try {
                emiService.generateEMIsForLoan(savedLoan);
                System.out.println("EMIs generated for approved loan ID: " + loanId);
            } catch (Exception e) {
                System.err.println("Error generating EMIs for loan " + loanId + ": " + e.getMessage());
            }
        }

        // Update credit score if status changed to APPROVED or REJECTED
        if ((status == Loan.Status.APPROVED || status == Loan.Status.REJECTED) && 
            previousStatus != status) {
            try {
                Long customerId = loan.getCustomer().getId();
                String statusString = status.toString();
                Double loanAmount = loan.getAmount();
                
                creditScoreClientService.updateCreditScoreForLoanStatus(customerId, statusString, loanAmount);
                System.out.println("Credit score updated for customer " + customerId + 
                                 " due to loan " + statusString.toLowerCase() + " (Amount: ₹" + loanAmount + ")");
            } catch (Exception e) {
                // Log the error but don't fail the loan status update
                System.err.println("Warning: Failed to update credit score for customer " + 
                                 loan.getCustomer().getId() + ": " + e.getMessage());
            }
        }

        return savedLoan;
    }

    // Get loan by ID
    public Optional<Loan> getLoanById(Long loanId) {
        return loanRepository.findById(loanId);
    }

    // Statistics methods for dashboard

    // Get total loan count (for admin)
    public int getTotalLoanCount() {
        return (int) loanRepository.count();
    }

    // Get pending loan count (for admin)
    public int getPendingLoanCount() {
        return loanRepository.findByStatus(Loan.Status.PENDING).size();
    }

    // Get customer's total loan count
    public int getCustomerLoanCount(Long customerId) {
        return loanRepository.findByCustomerId(customerId).size();
    }

    // Get customer's pending loan count
    public int getCustomerPendingLoanCount(Long customerId) {
        return loanRepository.findByCustomerIdAndStatus(customerId, Loan.Status.PENDING).size();
    }

    // Get customer's approved loan count
    public int getCustomerApprovedLoanCount(Long customerId) {
        return loanRepository.findByCustomerIdAndStatus(customerId, Loan.Status.APPROVED).size();
    }
}
