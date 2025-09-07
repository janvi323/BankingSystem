package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.model.Customer;
import com.bankingsystem.bankingsystem.model.Loan;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import com.bankingsystem.bankingsystem.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
public class ViewController {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private LoanRepository loanRepo;

    @Autowired
    private LoanService loanService;

    @GetMapping("/apply")
    public String showApplyPage(Model model) {
        model.addAttribute("customers", customerRepo.findAll());
        return "apply-loan";
    }

    @PostMapping("/apply")
    public String applyLoan(
            @RequestParam Long customerId,
            @RequestParam BigDecimal amount,
            @RequestParam Integer termMonths
    ) {
        loanService.applyLoan(customerId, amount, termMonths);
        return "redirect:/loans";
    }

    @GetMapping("/loans")
    public String listLoans(Model model) {
        model.addAttribute("loans", loanRepo.findAll());
        return "list-loans";
    }
}
