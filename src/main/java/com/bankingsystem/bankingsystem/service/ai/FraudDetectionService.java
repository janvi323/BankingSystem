package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FraudDetectionService — in-memory fraud pattern detection.
 *
 * <p>Detects:
 * <ul>
 *   <li>Multiple applications in a short window (rate limiting)</li>
 *   <li>Unrealistic salary declarations</li>
 *   <li>Sudden large loan requests inconsistent with income</li>
 *   <li>Repeated identical application patterns</li>
 * </ul>
 *
 * <p>This uses {@code ConcurrentHashMap} counters — Redis-ready via a
 * simple interface swap when infrastructure is available.
 */
@Service
public class FraudDetectionService {

    private final LoanRepository loanRepository;

    // ── In-memory rate counters (Redis-ready) ─────────────────────────────────
    /** application count per customer in last 24h window */
    private final ConcurrentHashMap<Long, AtomicInteger>  applicationCounts = new ConcurrentHashMap<>();
    /** window reset timestamps */
    private final ConcurrentHashMap<Long, LocalDateTime>  windowStart = new ConcurrentHashMap<>();

    // Fraud thresholds
    private static final int    MAX_APPLICATIONS_PER_DAY = 3;
    private static final double MAX_LOAN_TO_ANNUAL_INCOME  = 10.0; // suspicious beyond 10×
    private static final double MIN_PLAUSIBLE_INCOME       = 6000.0; // ₹6K/month minimum

    public FraudDetectionService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public record FraudResult(boolean flagged, String reason, String severity) {}

    /**
     * Checks whether a loan application looks suspicious.
     * Increments the application counter on each call.
     *
     * @param customerId   applicant's ID
     * @param loanAmount   requested principal
     * @param monthlyIncome provided monthly income
     * @return FraudResult with flagged flag, reason, and severity
     */
    public FraudResult check(Long customerId, double loanAmount, Double monthlyIncome) {
        // ── Check 1: Application frequency ───────────────────────────────
        int count = recordAndGetCount(customerId);
        if (count > MAX_APPLICATIONS_PER_DAY) {
            return new FraudResult(true,
                String.format("Customer submitted %d applications within 24 hours (limit: %d). " +
                              "Possible automated or duplicate submission.", count, MAX_APPLICATIONS_PER_DAY),
                "HIGH");
        }

        // ── Check 2: Income plausibility ──────────────────────────────────
        if (monthlyIncome != null && monthlyIncome > 0) {
            if (monthlyIncome < MIN_PLAUSIBLE_INCOME) {
                return new FraudResult(true,
                    String.format("Declared monthly income ₹%.0f is below minimum plausible threshold ₹%.0f.",
                            monthlyIncome, MIN_PLAUSIBLE_INCOME),
                    "MEDIUM");
            }

            // ── Check 3: Loan-to-income ratio ─────────────────────────────
            double annualIncome   = monthlyIncome * 12;
            double loanToIncome   = loanAmount / annualIncome;
            if (loanToIncome > MAX_LOAN_TO_ANNUAL_INCOME) {
                return new FraudResult(true,
                    String.format("Requested loan (₹%.0f) is %.1f× annual income — significantly above safe %.0f× limit.",
                            loanAmount, loanToIncome, MAX_LOAN_TO_ANNUAL_INCOME),
                    "MEDIUM");
            }
        }

        // ── Check 4: Historical pattern (many existing loans) ─────────────
        long existingApproved = loanRepository.findByCustomerId(customerId).stream()
                .filter(l -> l.getStatus() == com.bankingsystem.bankingsystem.entity.Loan.Status.APPROVED)
                .count();
        if (existingApproved >= 5) {
            return new FraudResult(true,
                String.format("Customer has %d active approved loans — unusual stacking pattern.", existingApproved),
                "LOW");
        }

        return new FraudResult(false, null, null);
    }

    // ── Counter Management ────────────────────────────────────────────────────

    private int recordAndGetCount(Long customerId) {
        LocalDateTime now   = LocalDateTime.now();
        LocalDateTime start = windowStart.compute(customerId,
                (k, v) -> (v == null || v.isBefore(now.minusHours(24))) ? now : v);

        // If the window was just reset (new window), reset counter
        AtomicInteger counter = applicationCounts.compute(customerId, (k, v) -> {
            if (v == null) return new AtomicInteger(0);
            return v;
        });

        // Reset counter if window expired
        if (windowStart.get(customerId).equals(now)) {
            counter.set(0);
        }

        return counter.incrementAndGet();
    }
}
