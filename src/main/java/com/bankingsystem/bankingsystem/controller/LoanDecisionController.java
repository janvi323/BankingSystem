package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.Service.LoanCalculationService;
import com.bankingsystem.bankingsystem.Service.LoanService;
import com.bankingsystem.bankingsystem.Service.ai.*;
import com.bankingsystem.bankingsystem.dto.*;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import com.bankingsystem.bankingsystem.repository.LoanDecisionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * LoanDecisionController — REST API for all AI-powered lending features.
 *
 * <p>Uses HttpSession (same as all other controllers) to resolve the logged-in customer.
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

    private final BankOfferEngine         bankOfferEngine;
    private final LoanSimulationService   simulationService;
    private final FinancialHealthScorer   healthScorer;
    private final PreApprovedOfferService preApprovedService;
    private final LoanDecisionRepository  decisionRepository;
    private final CustomerRepository      customerRepository;
    private final LoanService             loanService;
    private final LoanCalculationService  calcService;

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
     * GET /api/banks/compare?amount=500000&tenure=24&purpose=Personal
     * Returns ranked bank offers for the logged-in customer.
     */
    @GetMapping("/banks/compare")
    public ResponseEntity<?> compareBanks(
            @RequestParam double amount,
            @RequestParam int    tenure,
            @RequestParam(defaultValue = "Personal") String purpose,
            HttpSession session) {
        try {
            Customer customer = resolveCustomer(session);
            if (customer == null)
                return ResponseEntity.status(401).body(Map.of("error", "Please login first"));

            BankComparisonResult result = bankOfferEngine.compareOffers(customer, amount, tenure, purpose);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── 2. Fetch AI Decision for a Loan ───────────────────────────────────────

    @GetMapping("/loan-decision/{loanId}")
    public ResponseEntity<?> getLoanDecision(@PathVariable Long loanId, HttpSession session) {
        try {
            Customer customer = resolveCustomer(session);
            if (customer == null)
                return ResponseEntity.status(401).body(Map.of("error", "Please login first"));

            return decisionRepository.findByLoanId(loanId)
                    .map(d -> {
                        double emi = 0;
                        var loanOpt = loanService.getLoanById(loanId);
                        if (loanOpt.isPresent() && loanOpt.get().getEmiAmount() != null)
                            emi = loanOpt.get().getEmiAmount();
                        return ResponseEntity.ok(LoanDecisionResult.from(d, emi));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── 3. What-If Simulation ─────────────────────────────────────────────────

    @PostMapping("/loan-decision/simulate")
    public ResponseEntity<?> simulate(@RequestBody SimulationRequest request, HttpSession session) {
        try {
            Customer customer = resolveCustomer(session);
            if (customer == null)
                return ResponseEntity.status(401).body(Map.of("error", "Please login first"));
            request.setCustomerId(customer.getId());

            SimulationResult result = simulationService.simulate(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── 4. Financial Health Score ─────────────────────────────────────────────

    @GetMapping("/loan-decision/health")
    public ResponseEntity<?> getFinancialHealth(HttpSession session) {
        try {
            Customer customer = resolveCustomer(session);
            if (customer == null)
                return ResponseEntity.status(401).body(Map.of("error", "Please login first"));

            var loans = loanService.getLoansByCustomerId(customer.getId());
            FinancialHealthScorer.HealthResult result = healthScorer.compute(customer, loans);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── 5. Pre-Approved Offers ────────────────────────────────────────────────

    @GetMapping("/pre-approved-offers")
    public ResponseEntity<?> getPreApprovedOffers(HttpSession session) {
        try {
            Customer customer = resolveCustomer(session);
            if (customer == null)
                return ResponseEntity.status(401).body(Map.of("error", "Please login first"));

            PreApprovedOffersResult result = preApprovedService.computeOffers(customer);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── 6. Admin: Fraud Check ─────────────────────────────────────────────────

    @GetMapping("/loan-decision/fraud-check/{loanId}")
    public ResponseEntity<?> getFraudFlag(@PathVariable Long loanId, HttpSession session) {
        try {
            Customer customer = resolveCustomer(session);
            if (customer == null || customer.getRole() != Customer.Role.ADMIN)
                return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));

            return decisionRepository.findByLoanId(loanId)
                    .map(d -> ResponseEntity.ok(Map.of(
                            "loanId",       loanId,
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

    /** Resolves the logged-in customer from session (consistent with all other controllers). */
    private Customer resolveCustomer(HttpSession session) {
        return (Customer) session.getAttribute("loggedInCustomer");
    }
}
