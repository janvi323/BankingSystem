package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    // Save/Update Loan
    public Loan saveLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    // Get Loan By ID
    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    // Get All Loans
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // Get Loans by Customer ID
    public List<Loan> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    // Update Loan
    public Loan updateLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    // Delete Loan
    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }
}
