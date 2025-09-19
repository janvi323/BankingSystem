package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    // Apply for a loan
    @Transactional
    public Loan applyForLoan(Customer customer, Double amount, String purpose, Integer tenure) throws Exception {
        if (amount <= 0) {
            throw new Exception("Loan amount must be greater than 0");
        }

        if (tenure <= 0) {
            throw new Exception("Loan tenure must be greater than 0");
        }

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setAmount(amount);
        loan.setPurpose(purpose);
        loan.setTenure(tenure);
        loan.setStatus(Loan.Status.PENDING);
        loan.setApplicationDate(LocalDateTime.now());

        Loan savedLoan = loanRepository.save(loan);
        System.out.println("Loan saved with ID: " + savedLoan.getId());
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

    // Approve/Reject loan (admin only)
    public Loan updateLoanStatus(Long loanId, Loan.Status status, String adminComments) throws Exception {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);
        if (loanOpt.isEmpty()) {
            throw new Exception("Loan not found");
        }

        Loan loan = loanOpt.get();
        loan.setStatus(status);
        loan.setAdminComments(adminComments);

        if (status == Loan.Status.APPROVED || status == Loan.Status.REJECTED) {
            loan.setApprovalDate(LocalDateTime.now());
        }

        return loanRepository.save(loan);
    }

    // Get loan by ID
    public Optional<Loan> getLoanById(Long loanId) {
        return loanRepository.findById(loanId);
    }
}
