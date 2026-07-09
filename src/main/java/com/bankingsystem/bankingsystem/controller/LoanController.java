package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.Service.LoanService;
import com.bankingsystem.bankingsystem.dto.LoanDecisionResult;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.entity.LoanDecision;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import com.bankingsystem.bankingsystem.repository.LoanDecisionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final CustomerRepository customerRepository;
    private final LoanDecisionRepository loanDecisionRepository;
    private final com.bankingsystem.bankingsystem.Service.LoanCalculationService loanCalculationService;
    private final com.bankingsystem.bankingsystem.Service.EMIService emiService;

    public LoanController(LoanService loanService, CustomerRepository customerRepository,
                         LoanDecisionRepository loanDecisionRepository,
                         com.bankingsystem.bankingsystem.Service.LoanCalculationService loanCalculationService,
                         com.bankingsystem.bankingsystem.Service.EMIService emiService) {
        this.loanService = loanService;
        this.customerRepository = customerRepository;
        this.loanDecisionRepository = loanDecisionRepository;
        this.loanCalculationService = loanCalculationService;
        this.emiService = emiService;
    }

    // Customer: Apply for loan
    @PostMapping("/apply")
    @Transactional
    public ResponseEntity<Map<String, Object>> applyForLoan(@RequestBody Map<String, Object> loanData, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Please login first"));
        if (customer.getRole() != Customer.Role.CUSTOMER)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only customers can apply for loans"));

        try {
            Customer managed = customerRepository.findById(customer.getId())
                    .orElseThrow(() -> new Exception("Customer not found"));

            Double  amount         = Double.valueOf(loanData.get("amount").toString());
            String  purpose        = loanData.get("purpose").toString();
            Integer tenure         = Integer.valueOf(loanData.get("tenure").toString());
            String  employmentType = loanData.containsKey("employmentType") ? loanData.get("employmentType").toString() : "SALARIED";
            Integer empYears       = loanData.containsKey("employmentYears") ? Integer.valueOf(loanData.get("employmentYears").toString()) : 2;
            Double  monthlyIncome  = loanData.containsKey("monthlyIncome") && loanData.get("monthlyIncome") != null
                                     ? Double.valueOf(loanData.get("monthlyIncome").toString()) : null;
            String  selectedBank   = loanData.containsKey("selectedBankName") && loanData.get("selectedBankName") != null
                                     ? loanData.get("selectedBankName").toString() : null;
            if (selectedBank != null && selectedBank.isBlank()) selectedBank = null;

            Loan loan = loanService.applyForLoan(managed, amount, purpose, tenure,
                                                  employmentType, empYears, monthlyIncome, selectedBank);

            // Build response — attach AI decision if available
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("loanId",   loan.getId());
            resp.put("amount",   loan.getAmount());
            resp.put("status",   loan.getStatus().name());
            resp.put("bankName", loan.getSelectedBankName());

            loanDecisionRepository.findByLoanId(loan.getId()).ifPresent(d -> {
                Map<String, Object> dec = new LinkedHashMap<>();
                dec.put("decisionType",         d.getDecisionType() != null ? d.getDecisionType().name() : "MANUAL_REVIEW");
                dec.put("decisionSummary",       buildDecisionSummary(d));
                dec.put("confidencePercent",     d.getConfidencePercent());
                dec.put("financialHealthScore",  d.getFinancialHealthScore());
                dec.put("riskProfile",           d.getRiskProfile() != null ? d.getRiskProfile().name() : "MEDIUM");
                dec.put("rejectionReasons",      d.getRejectionReasonsList());
                dec.put("recommendations",       d.getRecommendationsList());
                dec.put("scoreBreakdown",        d.getScoreBreakdownList());
                dec.put("personalizedRate",      d.getPersonalizedInterestRate());
                dec.put("fraudFlagged",          d.getFraudFlagged());
                resp.put("decision", dec);
            });

            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            System.err.println("Loan application error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    private String buildDecisionSummary(com.bankingsystem.bankingsystem.entity.LoanDecision d) {
        if (d.getDecisionType() == com.bankingsystem.bankingsystem.entity.LoanDecision.DecisionType.AUTO_APPROVED)
            return String.format("Congratulations! AI score: %d/100. Your EMI schedule is being generated.",
                    d.getConfidencePercent() != null ? d.getConfidencePercent() : 0);
        if (d.getDecisionType() == com.bankingsystem.bankingsystem.entity.LoanDecision.DecisionType.MANUAL_REVIEW)
            return String.format("Score: %d/100. Application is under manual review — our team will contact you within 2 business days.",
                    d.getConfidencePercent() != null ? d.getConfidencePercent() : 0);
        return String.format("Score: %d/100. Application could not be approved at this time. See recommendations below.",
                d.getConfidencePercent() != null ? d.getConfidencePercent() : 0);
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
    public ResponseEntity<List<Loan>> getAllLoans(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if (customer.getRole() != Customer.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    // Admin: Approve/Reject loan
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateLoanStatus(@PathVariable Long id,
                                                   @RequestBody Map<String, String> statusData,
                                                   HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login first");
        }
        if (customer.getRole() != Customer.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can approve/reject loans");
        }
        
        try {
            Loan.Status status = Loan.Status.valueOf(statusData.get("status").toUpperCase());
            String comments = statusData.get("comments");
            Loan updatedLoan = loanService.updateLoanStatus(id, status, comments);
            System.out.println("Loan " + id + " status updated to " + status + " by admin: " + customer.getName());
            return ResponseEntity.ok("Loan status updated to: " + updatedLoan.getStatus());
        } catch (Exception e) {
            System.err.println("Error updating loan status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed: " + e.getMessage());
        }
    }

    // Admin: Get total loan count
    @GetMapping("/count")
    public ResponseEntity<Integer> getTotalLoanCount(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(0);
        }
        if (customer.getRole() != Customer.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(0);
        }
        return ResponseEntity.ok(loanService.getTotalLoanCount());
    }

    // Admin: Get pending loan approvals count
    @GetMapping("/pending/count")
    public ResponseEntity<Integer> getPendingLoanCount(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(0);
        }
        if (customer.getRole() != Customer.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(0);
        }
        return ResponseEntity.ok(loanService.getPendingLoanCount());
    }

    // Customer: Get my loan count
    @GetMapping("/my/count")
    public ResponseEntity<Integer> getMyLoanCount(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(0);

        return ResponseEntity.ok(loanService.getCustomerLoanCount(customer.getId()));
    }

    // Customer: Get my pending loan count
    @GetMapping("/my/pending/count")
    public ResponseEntity<Integer> getMyPendingLoanCount(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(0);

        return ResponseEntity.ok(loanService.getCustomerPendingLoanCount(customer.getId()));
    }

    // Customer: Get my approved loan count
    @GetMapping("/my/approved/count")
    public ResponseEntity<Integer> getMyApprovedLoanCount(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(0);

        return ResponseEntity.ok(loanService.getCustomerApprovedLoanCount(customer.getId()));
    }

    // Debug endpoint: Get loan details by ID
    @GetMapping("/{id}/details")
    public ResponseEntity<Loan> getLoanDetails(@PathVariable Long id, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        
        try {
            Loan loan = loanService.getLoanById(id).orElse(null);
            if (loan == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(loan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Admin: Get loans for a specific customer by customer ID
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Loan>> getLoansByCustomerId(@PathVariable Long customerId, HttpSession session) {
        Customer sessionCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (sessionCustomer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        
        // Allow admin to view any customer's loans, or customers to view their own loans
        boolean isAdmin = "ADMIN".equals(sessionCustomer.getRole().toString());
        boolean isOwnLoans = sessionCustomer.getId().equals(customerId);
        
        if (!isAdmin && !isOwnLoans) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            List<Loan> loans = loanService.getLoansByCustomerId(customerId);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // New endpoint for real-time loan calculation preview
    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateLoanDetails(@RequestBody Map<String, Object> loanData, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Please login first"));

        try {
            Double amount = Double.valueOf(loanData.get("amount").toString());
            String purpose = loanData.get("purpose").toString();
            Integer tenure = Integer.valueOf(loanData.get("tenure").toString());

            // Use the injected calculation service
            double interestRate = loanCalculationService.calculateInterestRate(purpose, amount, tenure);
            double emiAmount = loanCalculationService.calculateEMI(amount, interestRate, tenure);
            double totalAmount = loanCalculationService.calculateTotalAmount(emiAmount, tenure);

            Map<String, Object> result = Map.of(
                "interestRate", Math.round(interestRate * 100.0) / 100.0,
                "emiAmount", Math.round(emiAmount * 100.0) / 100.0,
                "totalAmount", Math.round(totalAmount * 100.0) / 100.0,
                "totalInterest", Math.round((totalAmount - amount) * 100.0) / 100.0
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Calculation failed: " + e.getMessage()));
        }
    }

    // Admin: Generate EMIs for all approved loans that don't have EMIs yet
    @PostMapping("/generate-emis")
    public ResponseEntity<Map<String, Object>> generateEMIsForApprovedLoans(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                               .body(Map.of("error", "Please login first"));
        }

        if (customer.getRole() != Customer.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                               .body(Map.of("error", "Only admins can generate EMIs"));
        }

        try {
            int generatedCount = emiService.generateMissingEMIsForApprovedLoans();

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "EMIs generated successfully for " + generatedCount + " approved loans",
                "count", generatedCount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(Map.of("error", "Error generating EMIs: " + e.getMessage()));
        }
    }
}
