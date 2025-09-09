package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.Service.LoanService;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // Apply for a new loan
    @PostMapping("/apply")
    public ResponseEntity<String> applyForLoan(@RequestBody Map<String, Object> loanData, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login first");
        }

        try {
            Loan loan = new Loan();
            loan.setCustomerId(customer.getId());
            loan.setAmount(Double.valueOf(loanData.get("amount").toString()));
            loan.setPurpose(loanData.get("purpose").toString());
            loan.setStatus("PENDING");
            loan.setAppliedDate(LocalDateTime.now());

            loanService.saveLoan(loan);
            return ResponseEntity.ok("Loan application submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to apply for loan: " + e.getMessage());
        }
    }

    // Get loans for current customer
    @GetMapping("/my-loans")
    public ResponseEntity<List<Loan>> getMyLoans(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<Loan> loans = loanService.getLoansByCustomerId(customer.getId());
        return ResponseEntity.ok(loans);
    }

    // Get all loans (Admin only)
    @GetMapping("/all")
    public ResponseEntity<List<Loan>> getAllLoans(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null || !"ADMIN".equals(customer.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<Loan> loans = loanService.getAllLoans();
        return ResponseEntity.ok(loans);
    }

    // Approve or reject loan (Admin only)
    @PutMapping("/{loanId}/status")
    public ResponseEntity<String> updateLoanStatus(@PathVariable Long loanId,
                                                   @RequestBody Map<String, String> statusData,
                                                   HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null || !"ADMIN".equals(customer.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }

        String newStatus = statusData.get("status");
        if (!"APPROVED".equals(newStatus) && !"REJECTED".equals(newStatus)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Status must be APPROVED or REJECTED");
        }

        try {
            Optional<Loan> loanOpt = loanService.getLoanById(loanId);
            if (loanOpt.isPresent()) {
                Loan loan = loanOpt.get();
                loan.setStatus(newStatus);
                if ("APPROVED".equals(newStatus)) {
                    loan.setApprovedDate(LocalDateTime.now());
                }
                loanService.saveLoan(loan);
                return ResponseEntity.ok("Loan status updated to " + newStatus);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update loan status");
        }
    }
}
