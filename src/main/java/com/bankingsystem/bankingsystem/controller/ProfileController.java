package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.Service.ProfileService;
import com.bankingsystem.bankingsystem.Service.ai.PreApprovedOfferService;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ProfileController — REST endpoints for the Profile & Settings page.
 *
 * <p>All endpoints require the user to be logged in (resolved via HttpSession).
 *
 * <p>Endpoints:
 * <ul>
 *   <li>GET  /api/profile          — full profile data</li>
 *   <li>GET  /api/profile/snapshot — financial snapshot card</li>
 *   <li>GET  /api/profile/completion — completion % + missing fields</li>
 *   <li>GET  /api/profile/offers   — pre-approved loan offers</li>
 *   <li>PUT  /api/profile/personal — update personal details</li>
 *   <li>PUT  /api/profile/employment — update employment details</li>
 *   <li>PUT  /api/profile/financial — update financial details</li>
 *   <li>PUT  /api/profile/preferences — update loan preferences</li>
 *   <li>POST /api/profile/change-password — change password</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService        profileService;
    private final CustomerRepository    customerRepo;
    private final PreApprovedOfferService preApprovedService;

    public ProfileController(ProfileService profileService,
                             CustomerRepository customerRepo,
                             PreApprovedOfferService preApprovedService) {
        this.profileService      = profileService;
        this.customerRepo        = customerRepo;
        this.preApprovedService  = preApprovedService;
    }

    // ── GET full profile ──────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<?> getProfile(HttpSession session) {
        Customer c = resolveCustomer(session);
        if (c == null) return ResponseEntity.status(401).body(error("Not authenticated"));
        return ResponseEntity.ok(buildProfileMap(c));
    }

    // ── GET financial snapshot ────────────────────────────────────────────────

    @GetMapping("/snapshot")
    public ResponseEntity<?> getSnapshot(HttpSession session) {
        Customer c = resolveCustomer(session);
        if (c == null) return ResponseEntity.status(401).body(error("Not authenticated"));
        ProfileService.FinancialSnapshot snap = profileService.getSnapshot(c);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("healthScore",          snap.healthScore());
        m.put("grade",                snap.grade());
        m.put("riskProfile",          snap.riskProfile());
        m.put("approvalProbability",  snap.approvalProbability());
        m.put("maxEligibleLoan",      snap.maxEligibleLoan());
        m.put("recommendedEMILimit",  snap.recommendedEMILimit());
        m.put("summary",              snap.summary());
        return ResponseEntity.ok(m);
    }

    // ── GET completion ────────────────────────────────────────────────────────

    @GetMapping("/completion")
    public ResponseEntity<?> getCompletion(HttpSession session) {
        Customer c = resolveCustomer(session);
        if (c == null) return ResponseEntity.status(401).body(error("Not authenticated"));
        ProfileService.CompletionResult cr = profileService.getCompletion(c);
        return ResponseEntity.ok(Map.of("percent", cr.percent(), "missingFields", cr.missingFields()));
    }

    // ── GET pre-approved offers ───────────────────────────────────────────────

    @GetMapping("/offers")
    public ResponseEntity<?> getOffers(HttpSession session) {
        Customer c = resolveCustomer(session);
        if (c == null) return ResponseEntity.status(401).body(error("Not authenticated"));
        try {
            var offers = preApprovedService.computeOffers(c).getOffers();
            return ResponseEntity.ok(Map.of("offers", offers));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("offers", java.util.List.of(),
                    "note", "Complete your financial profile to see personalised offers"));
        }
    }

    // ── PUT personal details ──────────────────────────────────────────────────

    @PutMapping("/personal")
    public ResponseEntity<?> updatePersonal(@RequestBody Map<String, String> data, HttpSession session) {
        Customer c = resolveCustomer(session);
        if (c == null) return ResponseEntity.status(401).body(error("Not authenticated"));
        try {
            Customer updated = profileService.updatePersonal(c, data);
            syncSession(session, updated);
            return ResponseEntity.ok(Map.of("success", true, "message", "Personal details updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    // ── PUT employment details ────────────────────────────────────────────────

    @PutMapping("/employment")
    public ResponseEntity<?> updateEmployment(@RequestBody Map<String, String> data, HttpSession session) {
        Customer c = resolveCustomer(session);
        if (c == null) return ResponseEntity.status(401).body(error("Not authenticated"));
        try {
            Customer updated = profileService.updateEmployment(c, data);
            syncSession(session, updated);
            ProfileService.FinancialSnapshot snap = profileService.getSnapshot(updated);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Employment details updated. Financial snapshot recalculated.",
                "snapshot", Map.of(
                    "healthScore",  snap.healthScore(),
                    "riskProfile",  snap.riskProfile(),
                    "approvalProbability", snap.approvalProbability()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    // ── PUT financial details ─────────────────────────────────────────────────

    @PutMapping("/financial")
    public ResponseEntity<?> updateFinancial(@RequestBody Map<String, String> data, HttpSession session) {
        Customer c = resolveCustomer(session);
        if (c == null) return ResponseEntity.status(401).body(error("Not authenticated"));
        try {
            Customer updated = profileService.updateFinancial(c, data);
            syncSession(session, updated);
            ProfileService.FinancialSnapshot snap = profileService.getSnapshot(updated);
            ProfileService.CompletionResult cr    = profileService.getCompletion(updated);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Financial details updated. Snapshot recalculated.",
                "snapshot", Map.of(
                    "healthScore",         snap.healthScore(),
                    "grade",               snap.grade(),
                    "riskProfile",         snap.riskProfile(),
                    "approvalProbability", snap.approvalProbability(),
                    "maxEligibleLoan",     snap.maxEligibleLoan(),
                    "recommendedEMILimit", snap.recommendedEMILimit()
                ),
                "completion", Map.of("percent", cr.percent(), "missingFields", cr.missingFields())
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    // ── PUT preferences ───────────────────────────────────────────────────────

    @PutMapping("/preferences")
    public ResponseEntity<?> updatePreferences(@RequestBody Map<String, String> data, HttpSession session) {
        Customer c = resolveCustomer(session);
        if (c == null) return ResponseEntity.status(401).body(error("Not authenticated"));
        try {
            Customer updated = profileService.updatePreferences(c, data);
            syncSession(session, updated);
            return ResponseEntity.ok(Map.of("success", true, "message", "Preferences saved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    // ── POST change password ──────────────────────────────────────────────────

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> data, HttpSession session) {
        Customer c = resolveCustomer(session);
        if (c == null) return ResponseEntity.status(401).body(error("Not authenticated"));
        String current = data.get("currentPassword");
        String newPwd  = data.get("newPassword");
        if (current == null || newPwd == null || newPwd.length() < 8)
            return ResponseEntity.badRequest().body(error("New password must be at least 8 characters"));
        boolean ok = profileService.changePassword(c, current, newPwd);
        if (ok) return ResponseEntity.ok(Map.of("success", true, "message", "Password changed successfully"));
        return ResponseEntity.badRequest().body(error("Current password is incorrect"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Customer resolveCustomer(HttpSession session) {
        Object id = session.getAttribute("customerId");
        if (id instanceof Number number) {
            return customerRepo.findById(number.longValue()).orElse(null);
        }

        Object loggedIn = session.getAttribute("loggedInCustomer");
        if (loggedIn instanceof Customer customer && customer.getId() != null) {
            return customerRepo.findById(customer.getId()).orElse(customer);
        }

        return null;
    }

    private void syncSession(HttpSession session, Customer c) {
        session.setAttribute("customerId", c.getId());
        session.setAttribute("customerName", c.getName());
        session.setAttribute("customerEmail", c.getEmail());
        session.setAttribute("loggedInCustomer", c);
    }

    private Map<String, Object> buildProfileMap(Customer c) {
        Map<String, Object> m = new LinkedHashMap<>();
        // Personal
        m.put("id", c.getId());
        m.put("name", c.getName());
        m.put("email", c.getEmail());
        m.put("phone", c.getPhone());
        m.put("address", c.getAddress());
        m.put("city", c.getCity());
        m.put("dateOfBirth", c.getDateOfBirth());
        m.put("maritalStatus", c.getMaritalStatus());
        // Employment
        m.put("employmentType", c.getEmploymentType());
        m.put("employerName", c.getEmployerName());
        m.put("industry", c.getIndustry());
        m.put("jobTitle", c.getJobTitle());
        m.put("workExperienceYears", c.getWorkExperienceYears());
        m.put("employmentStabilityYears", c.getEmploymentStabilityYears());
        // Financial
        m.put("monthlyIncome", c.getMonthlyIncome() != null ? c.getMonthlyIncome() : (c.getIncome() != null ? c.getIncome() / 12 : null));
        m.put("annualIncome", c.getIncome());
        m.put("emi", c.getEmi());
        m.put("existingLoans", c.getExistingLoans());
        m.put("savings", c.getSavings());
        m.put("monthlyExpenses", c.getMonthlyExpenses());
        m.put("creditUtilizationRatio", c.getCreditUtilizationRatio());
        m.put("creditScore", c.getCreditScore());
        m.put("paymentHistoryScore", c.getPaymentHistoryScore());
        m.put("creditAgeMonths", c.getCreditAgeMonths());
        m.put("debtToIncomeRatio", c.getDebtToIncomeRatio());
        // Preferences
        m.put("preferredLoanTypes", c.getPreferredLoanTypes());
        m.put("preferredTenure", c.getPreferredTenure());
        m.put("riskAppetite", c.getRiskAppetite());
        // Security & Audit
        m.put("googleConnected", c.getGoogleConnected());
        m.put("lastLoginAt", c.getLastLoginAt());
        m.put("lastFinancialUpdateAt", c.getLastFinancialUpdateAt());
        m.put("auditEntries", c.getAuditEntries());
        return m;
    }

    private Map<String, String> error(String msg) {
        return Map.of("error", msg);
    }
}
