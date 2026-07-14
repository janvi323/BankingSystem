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
     *     &employmentType=SALARIED&employmentYears=3&monthlyIncome=60000
     *
     * Returns ranked bank offers personalised to the logged-in customer's profile.
     * Optional query params override DB values for fresher data from the form.
     */
    @GetMapping("/banks/compare")
    public ResponseEntity<?> compareBanks(
            @RequestParam double  amount,
            @RequestParam int     tenure,
            @RequestParam(defaultValue = "Personal") String purpose,
            @RequestParam(required = false) String  employmentType,
            @RequestParam(required = false) Integer employmentYears,
            @RequestParam(required = false) Double  monthlyIncome,
            HttpSession session) {
        try {
            Customer customer = resolveCustomer(session);
            if (customer == null)
                return ResponseEntity.status(401).body(Map.of("error", "Please login first"));

            // Apply form overrides so AI uses freshest user-supplied data
            Customer enriched = applyFormOverrides(customer, employmentType, employmentYears, monthlyIncome);

            BankComparisonResult result = bankOfferEngine.compareOffers(enriched, amount, tenure, purpose);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Applies form-supplied overrides to a transient copy of the customer for AI scoring. */
    private Customer applyFormOverrides(Customer base, String empType, Integer empYears, Double monthlyIncome) {
        // Use a lightweight in-memory override; DO NOT save to DB
        Customer c = new Customer();
        c.setId(base.getId());
        c.setName(base.getName());
        c.setEmail(base.getEmail());
        c.setCreditScore(base.getCreditScore());
        c.setDebtToIncomeRatio(base.getDebtToIncomeRatio());
        c.setPaymentHistoryScore(base.getPaymentHistoryScore());
        c.setCreditAgeMonths(base.getCreditAgeMonths());
        c.setCreditUtilizationRatio(base.getCreditUtilizationRatio());
        c.setNumberOfAccounts(base.getNumberOfAccounts());
        c.setEmi(base.getEmi());
        c.setRole(base.getRole());

        // Apply overrides from form
        if (monthlyIncome != null && monthlyIncome > 0) {
            c.setIncome(monthlyIncome * 12.0); // store as annual internally
        } else {
            c.setIncome(base.getIncome());
        }

        // Employment type/years affect AI bias in BankOfferEngine
        c.setEmploymentType(empType != null ? empType : base.getEmploymentType());
        c.setEmploymentYears(empYears != null ? empYears : base.getEmploymentYears());
        return c;
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
