package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.model.Loan;
import com.bankingsystem.bankingsystem.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/apply")
    public Loan applyLoan(
            @RequestParam Long customerId,
            @RequestParam BigDecimal amount,
            @RequestParam Integer termMonths
    ) {
        return loanService.applyLoan(customerId, amount, termMonths);
    }

    @GetMapping("/{id}")
    public Loan getLoan(@PathVariable Long id) {
        return loanService.getLoanById(id);
    }
}
