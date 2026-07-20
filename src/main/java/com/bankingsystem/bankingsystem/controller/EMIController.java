package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.EMI;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.Service.EMIService;
import com.bankingsystem.bankingsystem.dto.EmiDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emi")
public class EMIController {

    private final EMIService emiService;

    public EMIController(EMIService emiService) {
        this.emiService = emiService;
    }

    // Get all EMIs for the logged-in customer
    @GetMapping("/my-emis")
    public ResponseEntity<List<EMI>> getMyEMIs(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<EMI> emis = emiService.getEMIsByCustomerId(customer.getId());
        return ResponseEntity.ok(emis);
    }

    // Get pending EMIs for the logged-in customer
    @GetMapping("/pending")
    public ResponseEntity<List<EMI>> getPendingEMIs(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<EMI> pendingEMIs = emiService.getPendingEMIsByCustomerId(customer.getId());
        return ResponseEntity.ok(pendingEMIs);
    }

    // Get overdue EMIs for the logged-in customer
    @GetMapping("/overdue")
    public ResponseEntity<List<EMI>> getOverdueEMIs(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<EMI> overdueEMIs = emiService.getOverdueEMIsByCustomerId(customer.getId());
        return ResponseEntity.ok(overdueEMIs);
    }

    // Get EMIs due this month for the logged-in customer
    @GetMapping("/due-this-month")
    public ResponseEntity<List<EMI>> getEMIsDueThisMonth(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<EMI> emisDueThisMonth = emiService.getEMIsDueThisMonth(customer.getId());
        return ResponseEntity.ok(emisDueThisMonth);
    }

    // Get EMI statistics for the logged-in customer
    @GetMapping("/stats")
    public ResponseEntity<EMIService.EMIStats> getEMIStats(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        EMIService.EMIStats stats = emiService.getEMIStats(customer.getId());
        return ResponseEntity.ok(stats);
    }

    // Pay an EMI (fake payment for demo)
    @PostMapping("/pay/{emiId}")
    public ResponseEntity<String> payEMI(@PathVariable Long emiId,
                                        @RequestBody Map<String, String> paymentData,
                                        HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login first");
        }

        try {
            String paymentMethod = paymentData.getOrDefault("paymentMethod", "Online Banking");
            EMI paidEMI = emiService.payEMI(emiId, paymentMethod);

            return ResponseEntity.ok("EMI payment successful! Amount: ₹" +
                                   String.format("%.2f", paidEMI.getAmount()) +
                                   " paid via " + paymentMethod);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                               .body("EMI payment failed: " + e.getMessage());
        }
    }

    // Generate EMIs for all approved loans (admin function)
    @PostMapping("/generate-all")
    public ResponseEntity<String> generateAllEMIs(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login first");
        }

        if (customer.getRole() != Customer.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                               .body("Only admins can generate EMIs for all loans");
        }

        try {
            emiService.generateEMIsForAllApprovedLoans();
            return ResponseEntity.ok("EMIs generated successfully for all approved loans");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error generating EMIs: " + e.getMessage());
        }
    }

    // Admin: Generate EMIs for existing approved loans that don't have EMIs
    @PostMapping("/generate-missing")
    public ResponseEntity<String> generateMissingEMIs(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login first");
        }

        if (customer.getRole() != Customer.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                               .body("Only admins can generate missing EMIs");
        }

        try {
            int generatedCount = emiService.generateMissingEMIsForApprovedLoans();
            return ResponseEntity.ok("Generated EMIs for " + generatedCount + " approved loans that were missing EMIs");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error generating missing EMIs: " + e.getMessage());
        }
    }

    // ── Safe DTO endpoint – avoids LazyInitializationException ──────────
    @GetMapping("/my-emis-safe")
    @Transactional(readOnly = true)
    public ResponseEntity<List<EmiDto>> getMyEmisSafe(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        List<EMI> emis = emiService.getEMIsByCustomerId(customer.getId());
        List<EmiDto> dtos = emis.stream().map(e -> {
            EmiDto d = new EmiDto();
            d.id            = e.getId();
            d.emiNumber     = e.getEmiNumber();
            d.dueDate       = e.getDueDate() != null ? e.getDueDate().toString() : null;
            d.amount        = e.getAmount() != null ? e.getAmount() : 0.0;
            d.status        = e.getStatus() != null ? e.getStatus().name() : "PENDING";
            d.paymentDate   = e.getPaymentDate() != null ? e.getPaymentDate().toLocalDate().toString() : null;
            d.paymentMethod = e.getPaymentMethod();
            d.paid          = e.isPaid();
            if (e.getLoan() != null) {
                d.loanId           = e.getLoan().getId();
                d.purpose          = e.getLoan().getPurpose();
                d.tenure           = e.getLoan().getTenure();
                d.loanAmount       = e.getLoan().getAmount();
                d.interestRate     = e.getLoan().getInterestRate();
                d.emiAmount        = e.getLoan().getEmiAmount();
                d.selectedBankName = e.getLoan().getSelectedBankName();
                d.loanStatus       = e.getLoan().getStatus() != null ? e.getLoan().getStatus().name() : null;
            }
            return d;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ── Customer: self-generate missing EMIs for approved loans ──────────
    @PostMapping("/generate-my-emis")
    public ResponseEntity<String> generateMyEmis(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login first");
        try {
            int count = emiService.generateMissingEmisForCustomer(customer.getId());
            return ResponseEntity.ok("Generated EMIs for " + count + " approved loan(s).");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Get all EMIs for a specific loan (waterfall view)
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<EMI>> getEMIsByLoan(@PathVariable Long loanId, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<EMI> emis = emiService.getEMIsByLoanId(loanId);
        return ResponseEntity.ok(emis);
    }

    // Get waterfall stats for a specific loan
    @GetMapping("/loan/{loanId}/stats")
    public ResponseEntity<EMIService.WaterfallStats> getLoanWaterfallStats(@PathVariable Long loanId, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        EMIService.WaterfallStats stats = emiService.getLoanWaterfallStats(loanId);
        return ResponseEntity.ok(stats);
    }

    // Pay partial amount toward an EMI
    @PostMapping("/pay-partial/{emiId}")
    public ResponseEntity<String> payPartialEMI(@PathVariable Long emiId,
                                                @RequestBody Map<String, Object> paymentData,
                                                HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login first");
        }
        try {
            Double amount = Double.parseDouble(paymentData.get("amount").toString());
            String paymentMethod = paymentData.getOrDefault("paymentMethod", "Online Banking").toString();
            EMI paidEMI = emiService.payPartialEMI(emiId, amount, paymentMethod);
            if (paidEMI.getStatus() == EMI.Status.PAID) {
                return ResponseEntity.ok("EMI payment successful! Full amount covered.");
            } else {
                return ResponseEntity.ok("Partial payment of ₹" +
                    String.format("%.2f", amount) + " recorded successfully. Remaining: ₹" +
                    String.format("%.2f", paidEMI.getTotalPayable()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed: " + e.getMessage());
        }
    }

    // Pay all remaining EMIs for a loan (foreclosure)
    @PostMapping("/pay-full-loan/{loanId}")
    public ResponseEntity<String> payFullLoan(@PathVariable Long loanId,
                                              @RequestBody Map<String, String> paymentData,
                                              HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login first");
        }
        try {
            String paymentMethod = paymentData.getOrDefault("paymentMethod", "Online Banking");
            int paidCount = emiService.payFullLoan(loanId, paymentMethod);
            return ResponseEntity.ok("Loan foreclosure successful! " + paidCount + " EMIs paid.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Foreclosure failed: " + e.getMessage());
        }
    }

    // Get active loans with EMIs for the loan selector
    @GetMapping("/active-loans")
    public ResponseEntity<List<com.bankingsystem.bankingsystem.entity.Loan>> getActiveLoans(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<com.bankingsystem.bankingsystem.entity.Loan> loans = emiService.getActiveLoansWithEmis(customer.getId());
        return ResponseEntity.ok(loans);
    }
}
