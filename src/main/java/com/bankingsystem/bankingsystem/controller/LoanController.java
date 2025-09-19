package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.Service.LoanService;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final CustomerRepository customerRepository;

    public LoanController(LoanService loanService, CustomerRepository customerRepository) {
        this.loanService = loanService;
        this.customerRepository = customerRepository;
    }

    // Customer: Apply for loan
    @PostMapping("/apply")
    @Transactional
    public ResponseEntity<String> applyForLoan(@RequestBody Map<String, Object> loanData, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login first");
        if (customer.getRole() != Customer.Role.CUSTOMER)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only customers can apply for loans");

        try {
            Customer managedCustomer = customerRepository.findById(customer.getId())
                    .orElseThrow(() -> new Exception("Customer not found"));

            Double amount = Double.valueOf(loanData.get("amount").toString());
            String purpose = loanData.get("purpose").toString();
            Integer tenure = Integer.valueOf(loanData.get("tenure").toString());

            Loan loan = loanService.applyForLoan(managedCustomer, amount, purpose, tenure);
            System.out.println("Loan application processed - ID: " + loan.getId() + ", Customer: " + managedCustomer.getName());
            return ResponseEntity.ok("Loan applied successfully. ID: " + loan.getId());
        } catch (Exception e) {
            System.err.println("Loan application error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Loan application failed: " + e.getMessage());
        }
    }

    // Customer: View own loans
    @GetMapping("/my-loans")
    public ResponseEntity<List<Loan>> getMyLoans(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        List<Loan> loans = loanService.getLoansByCustomerId(customer.getId());
        return ResponseEntity.ok(loans);
    }

    // Admin: View all loans
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    // Admin: Approve/Reject loan
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateLoanStatus(@PathVariable Long id,
                                                   @RequestBody Map<String, String> statusData) {
        try {
            Loan.Status status = Loan.Status.valueOf(statusData.get("status").toUpperCase());
            String comments = statusData.get("comments");
            Loan updatedLoan = loanService.updateLoanStatus(id, status, comments);
            return ResponseEntity.ok("Loan status updated to: " + updatedLoan.getStatus());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed: " + e.getMessage());
        }
    }
}
