package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.EMI;
import com.bankingsystem.bankingsystem.Service.EMIService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

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
}
