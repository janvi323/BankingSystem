package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.Service.CreditScoreClientService;
import com.bankingsystem.bankingsystem.Service.CustomerService;
import com.bankingsystem.bankingsystem.dto.CreditScoreDto;
import com.bankingsystem.bankingsystem.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/banking/credit-score")
@CrossOrigin(origins = "*")
public class CreditScoreIntegrationController {

    @Autowired
    private CreditScoreClientService creditScoreClientService;

    @Autowired
    private CustomerService customerService;

    /**
     * Get credit score for a customer
     * GET /api/banking/credit-score/{customerId}
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CreditScoreDto> getCustomerCreditScore(@PathVariable Long customerId) {
        try {
            // Verify customer exists in banking system
            Optional<Customer> customerOpt = customerService.getCustomerById(customerId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Customer customer = customerOpt.get();

            Optional<CreditScoreDto> creditScore = creditScoreClientService.getCreditScoreByCustomerId(customerId);
            return creditScore.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Calculate credit score for a customer with basic income information
     * POST /api/banking/credit-score/{customerId}/calculate
     */
    @PostMapping("/{customerId}/calculate")
    public ResponseEntity<CreditScoreDto> calculateCustomerCreditScore(
            @PathVariable Long customerId,
            @RequestBody Map<String, Object> requestData) {
        try {
            // Verify customer exists in banking system
            Optional<Customer> customerOpt = customerService.getCustomerById(customerId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Customer customer = customerOpt.get();

            // Extract income from request
            Double income = requestData.get("income") != null ? 
                    Double.valueOf(requestData.get("income").toString()) : 50000.0;

            // Extract optional credit information
            Double debtToIncomeRatio = requestData.get("debtToIncomeRatio") != null ?
                    Double.valueOf(requestData.get("debtToIncomeRatio").toString()) : null;
            
            Integer paymentHistoryScore = requestData.get("paymentHistoryScore") != null ?
                    Integer.valueOf(requestData.get("paymentHistoryScore").toString()) : null;
            
            Double creditUtilizationRatio = requestData.get("creditUtilizationRatio") != null ?
                    Double.valueOf(requestData.get("creditUtilizationRatio").toString()) : null;
            
            Integer creditAgeMonths = requestData.get("creditAgeMonths") != null ?
                    Integer.valueOf(requestData.get("creditAgeMonths").toString()) : null;
            
            Integer numberOfAccounts = requestData.get("numberOfAccounts") != null ?
                    Integer.valueOf(requestData.get("numberOfAccounts").toString()) : null;

            CreditScoreDto creditScore = creditScoreClientService.calculateCreditScore(
                    customer, income, debtToIncomeRatio, paymentHistoryScore,
                    creditUtilizationRatio, creditAgeMonths, numberOfAccounts);

            // Update customer's credit score in the banking system
            customer.setCreditScore(creditScore.getCreditScore());
            customerService.updateCustomer(customer);

            return ResponseEntity.status(HttpStatus.CREATED).body(creditScore);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update credit score for a customer
     * PUT /api/banking/credit-score/{customerId}
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<CreditScoreDto> updateCustomerCreditScore(
            @PathVariable Long customerId,
            @RequestBody Map<String, Object> requestData) {
        try {
            // Verify customer exists in banking system
            Optional<Customer> customerOpt = customerService.getCustomerById(customerId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Customer customer = customerOpt.get();

            // Extract data from request
            Double income = requestData.get("income") != null ? 
                    Double.valueOf(requestData.get("income").toString()) : 50000.0;

            Double debtToIncomeRatio = requestData.get("debtToIncomeRatio") != null ?
                    Double.valueOf(requestData.get("debtToIncomeRatio").toString()) : null;
            
            Integer paymentHistoryScore = requestData.get("paymentHistoryScore") != null ?
                    Integer.valueOf(requestData.get("paymentHistoryScore").toString()) : null;
            
            Double creditUtilizationRatio = requestData.get("creditUtilizationRatio") != null ?
                    Double.valueOf(requestData.get("creditUtilizationRatio").toString()) : null;
            
            Integer creditAgeMonths = requestData.get("creditAgeMonths") != null ?
                    Integer.valueOf(requestData.get("creditAgeMonths").toString()) : null;
            
            Integer numberOfAccounts = requestData.get("numberOfAccounts") != null ?
                    Integer.valueOf(requestData.get("numberOfAccounts").toString()) : null;

            CreditScoreDto creditScore = creditScoreClientService.updateCreditScore(
                    customerId, customer, income, debtToIncomeRatio, paymentHistoryScore,
                    creditUtilizationRatio, creditAgeMonths, numberOfAccounts);

            // Update customer's credit score in the banking system
            customer.setCreditScore(creditScore.getCreditScore());
            customerService.updateCustomer(customer);

            return ResponseEntity.ok(creditScore);
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
     * Check if customer has credit score
     * GET /api/banking/credit-score/{customerId}/exists
     */
    @GetMapping("/{customerId}/exists")
    public ResponseEntity<Map<String, Boolean>> hasCustomerCreditScore(@PathVariable Long customerId) {
        try {
            // Verify customer exists in banking system
            Optional<Customer> customerOpt = customerService.getCustomerById(customerId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Customer customer = customerOpt.get();

            boolean exists = creditScoreClientService.hasCustomerCreditScore(customerId);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete credit score for a customer
     * DELETE /api/banking/credit-score/{customerId}
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomerCreditScore(@PathVariable Long customerId) {
        try {
            // Verify customer exists in banking system
            Optional<Customer> customerOpt = customerService.getCustomerById(customerId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Customer customer = customerOpt.get();

            creditScoreClientService.deleteCreditScore(customerId);

            // Reset customer's credit score in the banking system to default
            customer.setCreditScore(600); // default value
            customerService.updateCustomer(customer);

            return ResponseEntity.noContent().build();
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
     * Health check for credit score service
     * GET /api/banking/credit-score/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> creditScoreServiceHealth() {
        try {
            boolean isHealthy = creditScoreClientService.isServiceHealthy();
            return ResponseEntity.ok(Map.of(
                    "creditScoreService", isHealthy ? "UP" : "DOWN",
                    "status", isHealthy ? "HEALTHY" : "UNHEALTHY",
                    "timestamp", java.time.LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "creditScoreService", "DOWN",
                    "status", "UNHEALTHY",
                    "error", e.getMessage(),
                    "timestamp", java.time.LocalDateTime.now().toString()
            ));
        }
    }
}