package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.Service.CustomerService;
import com.bankingsystem.bankingsystem.Service.LoanCalculationService;
import com.bankingsystem.bankingsystem.Service.LoanService;
import com.bankingsystem.bankingsystem.Service.ai.*;
import com.bankingsystem.bankingsystem.dto.*;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.LoanDecision;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import com.bankingsystem.bankingsystem.repository.LoanDecisionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * LoanDecisionController — REST API for all AI-powered lending features.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>GET  /api/banks/compare          — Multi-Bank Offer Comparison</li>
 *   <li>GET  /api/loan-decision/{loanId} — Fetch AI decision for a loan</li>
 *   <li>POST /api/loan-decision/simulate — What-if scenario simulation</li>
 *   <li>GET  /api/loan-decision/health   — Financial Health Score</li>
 *   <li>GET  /api/pre-approved-offers    — Pre-Approved Offers</li>
 * </ul>
 */
@RestController
@RequestMapping("/api")
public class LoanDecisionController {

    private final BankOfferEngine          bankOfferEngine;
    private final LoanSimulationService    simulationService;
    private final FinancialHealthScorer    healthScorer;
    private final PreApprovedOfferService  preApprovedService;
    private final LoanDecisionRepository   decisionRepository;
    private final CustomerRepository       customerRepository;
    private final LoanService              loanService;
    private final LoanCalculationService   calcService;

    public LoanDecisionController(BankOfferEngine bankOfferEngine,
                                   LoanSimulationService simulationService,
                                   FinancialHealthScorer healthScorer,
                                   PreApprovedOfferService preApprovedService,
                                   LoanDecisionRepository decisionRepository,
                                   CustomerRepository customerRepository,
                                   LoanService loanService,
                                   LoanCalculationService calcService) {
        this.bankOfferEngine    = bankOfferEngine;
        this.simulationService  = simulationService;
        this.healthScorer       = healthScorer;
        this.preApprovedService = preApprovedService;
        this.decisionRepository = decisionRepository;
        this.customerRepository = customerRepository;
        this.loanService        = loanService;
        this.calcService        = calcService;
    }

    // ── 1. Multi-Bank Offer Comparison ────────────────────────────────────────

    /**
     * GET /api/banks/compare?amount=500000&tenure=24&purpose=personal
     *
     * Returns ranked bank offers for the logged-in customer.
     */
    @GetMapping("/banks/compare")
    public ResponseEntity<?> compareBanks(
            @RequestParam double amount,
            @RequestParam int    tenure,
            @RequestParam(defaultValue = "personal") String purpose,
            Authentication auth) {
        try {
            Customer customer = resolveCustomer(auth);
            if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

            BankComparisonResult result = bankOfferEngine.compareOffers(customer, amount, tenure, purpose);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── 2. Fetch AI Decision for a Loan ───────────────────────────────────────

    /**
     * GET /api/loan-decision/{loanId}
     *
     * Returns the stored AI decision record for a specific loan.
     */
    @GetMapping("/loan-decision/{loanId}")
    public ResponseEntity<?> getLoanDecision(@PathVariable Long loanId, Authentication auth) {
        try {
            Customer customer = resolveCustomer(auth);
            if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

            return decisionRepository.findByLoanId(loanId)
                    .map(d -> {
                        double emi = 0;
                        var loanOpt = loanService.getLoanById(loanId);
                        if (loanOpt.isPresent()) emi = loanOpt.get().getEmiAmount() != null
                                ? loanOpt.get().getEmiAmount() : 0;
                        return ResponseEntity.ok(LoanDecisionResult.from(d, emi));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── 3. What-If Simulation ─────────────────────────────────────────────────

    /**
     * POST /api/loan-decision/simulate
     * Body: SimulationRequest JSON
     */
    @PostMapping("/loan-decision/simulate")
    public ResponseEntity<?> simulate(@RequestBody SimulationRequest request, Authentication auth) {
        try {
            Customer customer = resolveCustomer(auth);
            if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            request.setCustomerId(customer.getId());

            SimulationResult result = simulationService.simulate(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── 4. Financial Health Score ─────────────────────────────────────────────

    /**
     * GET /api/loan-decision/health
     *
     * Returns the financial health score and breakdown for the logged-in customer.
     */
    @GetMapping("/loan-decision/health")
    public ResponseEntity<?> getFinancialHealth(Authentication auth) {
        try {
            Customer customer = resolveCustomer(auth);
            if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

            var loans = loanService.getLoansByCustomerId(customer.getId());
            FinancialHealthScorer.HealthResult result = healthScorer.compute(customer, loans);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── 5. Pre-Approved Offers ────────────────────────────────────────────────

    /**
     * GET /api/pre-approved-offers
     *
     * Returns pre-approved loan offers across 4 categories for the logged-in customer.
     */
    @GetMapping("/pre-approved-offers")
    public ResponseEntity<?> getPreApprovedOffers(Authentication auth) {
        try {
            Customer customer = resolveCustomer(auth);
            if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

            PreApprovedOffersResult result = preApprovedService.computeOffers(customer);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── 6. Admin: Fraud Overview ──────────────────────────────────────────────

    /**
     * GET /api/loan-decision/fraud-check/{loanId}
     * Admin only — returns fraud flag for a loan's decision record.
     */
    @GetMapping("/loan-decision/fraud-check/{loanId}")
    public ResponseEntity<?> getFraudFlag(@PathVariable Long loanId, Authentication auth) {
        try {
            Customer customer = resolveCustomer(auth);
            if (customer == null || customer.getRole() != Customer.Role.ADMIN) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
            }
            return decisionRepository.findByLoanId(loanId)
                    .map(d -> ResponseEntity.ok(Map.of(
                            "loanId",      loanId,
                            "fraudFlagged", d.getFraudFlagged(),
                            "riskProfile",  d.getRiskProfile() != null ? d.getRiskProfile().name() : "UNKNOWN",
                            "confidence",   d.getConfidencePercent()
                    )))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Customer resolveCustomer(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        return customerRepository.findByEmail(auth.getName()).orElse(null);
    }
}
