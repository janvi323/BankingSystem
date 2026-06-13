package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.dto.CreditScoreDto;
import com.bankingsystem.bankingsystem.dto.CreditScoreRequestDto;
import com.bankingsystem.bankingsystem.entity.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;
import java.util.Optional;

@Service
public class CreditScoreClientService {

    private final RestClient restClient;

    @Value("${credit.score.service.url}")
    private String creditScoreServiceUrl;

    public CreditScoreClientService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    /**
     * Get credit score for a customer by customer ID
     */
    public Optional<CreditScoreDto> getCreditScoreByCustomerId(Long customerId) {
        try {
            CreditScoreDto creditScore = restClient.get()
                    .uri(creditScoreServiceUrl + "/api/credit-scores/customer/{customerId}", customerId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(CreditScoreDto.class);
            
            return Optional.ofNullable(creditScore);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                return Optional.empty();
            }
            throw new RuntimeException("Error fetching credit score: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with credit score service: " + e.getMessage(), e);
        }
    }

    /**
     * Calculate and create credit score for a customer
     */
    public CreditScoreDto calculateCreditScore(Customer customer, Double income) {
        return calculateCreditScore(customer, income, null, null, null, null, null);
    }

    /**
     * Calculate and create credit score for a customer with detailed information
     */
    public CreditScoreDto calculateCreditScore(Customer customer, Double income, 
                                               Double debtToIncomeRatio, Integer paymentHistoryScore,
                                               Double creditUtilizationRatio, Integer creditAgeMonths,
                                               Integer numberOfAccounts) {
        try {
            CreditScoreRequestDto request = new CreditScoreRequestDto(
                    customer.getId(),
                    customer.getName(),
                    customer.getEmail(),
                    income
            );
            
            request.setDebtToIncomeRatio(debtToIncomeRatio);
            request.setPaymentHistoryScore(paymentHistoryScore);
            request.setCreditUtilizationRatio(creditUtilizationRatio);
            request.setCreditAgeMonths(creditAgeMonths);
            request.setNumberOfAccounts(numberOfAccounts);
            request.setEmi(customer.getEmi());

            CreditScoreDto creditScore = restClient.post()
                    .uri(creditScoreServiceUrl + "/api/credit-scores")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(CreditScoreDto.class);

            return creditScore;
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Error calculating credit score: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with credit score service: " + e.getMessage(), e);
        }
    }

    /**
     * Update existing credit score for a customer
     */
    public CreditScoreDto updateCreditScore(Long customerId, Customer customer, Double income,
                                            Double debtToIncomeRatio, Integer paymentHistoryScore,
                                            Double creditUtilizationRatio, Integer creditAgeMonths,
                                            Integer numberOfAccounts) {
        try {
            CreditScoreRequestDto request = new CreditScoreRequestDto(
                    customer.getId(),
                    customer.getName(),
                    customer.getEmail(),
                    income
            );
            
            request.setDebtToIncomeRatio(debtToIncomeRatio);
            request.setPaymentHistoryScore(paymentHistoryScore);
            request.setCreditUtilizationRatio(creditUtilizationRatio);
            request.setCreditAgeMonths(creditAgeMonths);
            request.setNumberOfAccounts(numberOfAccounts);

            CreditScoreDto creditScore = restClient.put()
                    .uri(creditScoreServiceUrl + "/api/credit-scores/customer/{customerId}", customerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(CreditScoreDto.class);

            return creditScore;
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                throw new RuntimeException("Credit score not found for customer ID: " + customerId);
            }
            throw new RuntimeException("Error updating credit score: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with credit score service: " + e.getMessage(), e);
        }
    }

    /**
     * Check if customer has a credit score
     */
    @SuppressWarnings("unchecked")
    public boolean hasCustomerCreditScore(Long customerId) {
        try {
            Map<String, Boolean> response = restClient.get()
                    .uri(creditScoreServiceUrl + "/api/credit-scores/exists/customer/{customerId}", customerId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(Map.class);
            
            return response != null && Boolean.TRUE.equals(response.get("exists"));
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                return false;
            }
            throw new RuntimeException("Error checking credit score existence: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with credit score service: " + e.getMessage(), e);
        }
    }

    /**
     * Delete credit score for a customer
     */
    public void deleteCreditScore(Long customerId) {
        try {
            restClient.delete()
                    .uri(creditScoreServiceUrl + "/api/credit-scores/customer/{customerId}", customerId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                throw new RuntimeException("Credit score not found for customer ID: " + customerId);
            }
            throw new RuntimeException("Error deleting credit score: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with credit score service: " + e.getMessage(), e);
        }
    }

    /**
     * Update credit score based on loan status change
     */
    public CreditScoreDto updateCreditScoreForLoanStatus(Long customerId, String loanStatus, Double loanAmount) {
        try {
            Map<String, Object> loanStatusData = Map.of(
                "status", loanStatus,
                "amount", loanAmount != null ? loanAmount : 0.0
            );

            CreditScoreDto creditScore = restClient.put()
                    .uri(creditScoreServiceUrl + "/api/credit-scores/customer/{customerId}/loan-status", customerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(loanStatusData)
                    .retrieve()
                    .body(CreditScoreDto.class);

            return creditScore;
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                throw new RuntimeException("Credit score not found for customer ID: " + customerId);
            }
            throw new RuntimeException("Error updating credit score for loan status: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with credit score service: " + e.getMessage(), e);
        }
    }

    /**
     * Health check for credit score service
     */
    @SuppressWarnings("unchecked")
    public boolean isServiceHealthy() {
        try {
            Map<String, String> response = restClient.get()
                    .uri(creditScoreServiceUrl + "/api/credit-scores/health")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(Map.class);
            
            return response != null && "UP".equals(response.get("status"));
        } catch (Exception e) {
            return false;
        }
    }
}
