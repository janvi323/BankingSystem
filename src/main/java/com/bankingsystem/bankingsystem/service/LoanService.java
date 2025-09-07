package com.bankingsystem.bankingsystem.service;

import com.bankingsystem.bankingsystem.client.CreditScoreClient;
import com.bankingsystem.bankingsystem.model.Customer;
import com.bankingsystem.bankingsystem.model.Loan;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private CreditScoreClient creditScoreClient;

    @Transactional
    public Loan applyLoan(Long customerId, BigDecimal amount, Integer termMonths) {
        Customer customer = customerRepo.findById(customerId).orElseThrow();
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setAmount(amount);
        loan.setTermMonths(termMonths);
        loan.setStatus("PENDING");

        int score = creditScoreClient.getScore(customerId);

        if (score < 500) loan.setStatus("REJECTED");
        else if (score < 650) loan.setStatus("PENDING");
        else loan.setStatus("APPROVED");

        return loanRepo.save(loan);
    }

    public Loan getLoanById(Long id) {
        return loanRepo.findById(id).orElseThrow();
    }
}
