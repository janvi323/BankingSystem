package com.bankingsystem.creditscore.controller;

import com.bankingsystem.creditscore.dto.CreditScoreRequest;
import com.bankingsystem.creditscore.dto.CreditScoreResponse;
import com.bankingsystem.creditscore.service.CreditScoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/credit-scores")
@CrossOrigin(origins = "*")
public class CreditScoreController {

    @Autowired
    private CreditScoreService creditScoreService;

    /**
     * Calculate and store credit score for a customer
     * POST /api/credit-scores
     */
    @PostMapping
    public ResponseEntity<CreditScoreResponse> calculateCreditScore(@Valid @RequestBody CreditScoreRequest request) {
        try {
            CreditScoreResponse response = creditScoreService.calculateCreditScore(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get credit score by customer ID
     * GET /api/credit-scores/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CreditScoreResponse> getCreditScoreByCustomerId(@PathVariable Long customerId) {
        try {
            Optional<CreditScoreResponse> creditScore = creditScoreService.getCreditScoreByCustomerId(customerId);
            return creditScore.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get credit score by customer email
     * GET /api/credit-scores/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<CreditScoreResponse> getCreditScoreByEmail(@PathVariable String email) {
        try {
            Optional<CreditScoreResponse> creditScore = creditScoreService.getCreditScoreByEmail(email);
            return creditScore.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all credit scores
     * GET /api/credit-scores
     */
    @GetMapping
    public ResponseEntity<List<CreditScoreResponse>> getAllCreditScores() {
        try {
            List<CreditScoreResponse> creditScores = creditScoreService.getAllCreditScores();
            return ResponseEntity.ok(creditScores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get credit scores by score range
     * GET /api/credit-scores/range?min={minScore}&max={maxScore}
     */
    @GetMapping("/range")
    public ResponseEntity<List<CreditScoreResponse>> getCreditScoresByRange(
            @RequestParam Integer min,
            @RequestParam Integer max) {
        try {
            if (min < 300 || max > 850 || min > max) {
                return ResponseEntity.badRequest().build();
            }
            List<CreditScoreResponse> creditScores = creditScoreService.getCreditScoresByRange(min, max);
            return ResponseEntity.ok(creditScores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get credit scores by grade
     * GET /api/credit-scores/grade/{grade}
     */
    @GetMapping("/grade/{grade}")
    public ResponseEntity<List<CreditScoreResponse>> getCreditScoresByGrade(@PathVariable String grade) {
        try {
            List<String> validGrades = List.of("Excellent", "Very Good", "Good", "Fair", "Poor");
            if (!validGrades.contains(grade)) {
                return ResponseEntity.badRequest().build();
            }
            List<CreditScoreResponse> creditScores = creditScoreService.getCreditScoresByGrade(grade);
            return ResponseEntity.ok(creditScores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update existing credit score
     * PUT /api/credit-scores/customer/{customerId}
     */
    @PutMapping("/customer/{customerId}")
    public ResponseEntity<CreditScoreResponse> updateCreditScore(
            @PathVariable Long customerId,
            @Valid @RequestBody CreditScoreRequest request) {
        try {
            CreditScoreResponse response = creditScoreService.updateCreditScore(customerId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete credit score by customer ID
     * DELETE /api/credit-scores/customer/{customerId}
     */
    @DeleteMapping("/customer/{customerId}")
    public ResponseEntity<Void> deleteCreditScore(@PathVariable Long customerId) {
        try {
            creditScoreService.deleteCreditScore(customerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Check if customer has credit score
     * GET /api/credit-scores/exists/customer/{customerId}
     */
    @GetMapping("/exists/customer/{customerId}")
    public ResponseEntity<Map<String, Boolean>> hasCustomerCreditScore(@PathVariable Long customerId) {
        try {
            boolean exists = creditScoreService.hasCustomerCreditScore(customerId);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get customers with high credit scores (above threshold)
     * GET /api/credit-scores/high-score?threshold={threshold}
     */
    @GetMapping("/high-score")
    public ResponseEntity<List<CreditScoreResponse>> getHighCreditScoreCustomers(
            @RequestParam(defaultValue = "700") Integer threshold) {
        try {
            if (threshold < 300 || threshold > 850) {
                return ResponseEntity.badRequest().build();
            }
            List<CreditScoreResponse> creditScores = creditScoreService.getHighCreditScoreCustomers(threshold);
            return ResponseEntity.ok(creditScores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get average credit score
     * GET /api/credit-scores/average
     */
    @GetMapping("/average")
    public ResponseEntity<Map<String, Double>> getAverageCreditScore() {
        try {
            Double average = creditScoreService.getAverageCreditScore();
            return ResponseEntity.ok(Map.of("averageCreditScore", average != null ? average : 0.0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update credit score based on loan status
     * PUT /api/credit-scores/customer/{customerId}/loan-status
     */
    @PutMapping("/customer/{customerId}/loan-status")
    public ResponseEntity<CreditScoreResponse> updateCreditScoreForLoanStatus(
            @PathVariable Long customerId,
            @RequestBody Map<String, Object> loanStatusData) {
        try {
            String loanStatus = (String) loanStatusData.get("status");
            Double loanAmount = loanStatusData.get("amount") != null ? 
                    Double.valueOf(loanStatusData.get("amount").toString()) : null;
            
            if (loanStatus == null) {
                return ResponseEntity.badRequest().build();
            }
            
            CreditScoreResponse response = creditScoreService.updateCreditScoreForLoanStatus(
                    customerId, loanStatus, loanAmount);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Health check endpoint
     * GET /api/credit-scores/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Credit Score Service",
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}