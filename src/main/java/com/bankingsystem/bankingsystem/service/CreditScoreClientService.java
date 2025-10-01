package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.dto.CreditScoreDto;
import com.bankingsystem.bankingsystem.dto.CreditScoreRequestDto;
import com.bankingsystem.bankingsystem.entity.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Service
public class CreditScoreClientService {

    private final WebClient webClient;

    @Value("${credit.score.service.url:http://localhost:8083}")
    private String creditScoreServiceUrl;

    public CreditScoreClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Get credit score for a customer by customer ID
     */
    public Optional<CreditScoreDto> getCreditScoreByCustomerId(Long customerId) {
        try {
            CreditScoreDto creditScore = webClient.get()
                    .uri(creditScoreServiceUrl + "/api/credit-scores/customer/{customerId}", customerId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(CreditScoreDto.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            return Optional.ofNullable(creditScore);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
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

            CreditScoreDto creditScore = webClient.post()
                    .uri(creditScoreServiceUrl + "/api/credit-scores")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(request))
                    .retrieve()
                    .bodyToMono(CreditScoreDto.class)
                    .timeout(Duration.ofSeconds(15))
                    .block();

            return creditScore;
        } catch (WebClientResponseException e) {
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

            CreditScoreDto creditScore = webClient.put()
                    .uri(creditScoreServiceUrl + "/api/credit-scores/customer/{customerId}", customerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(request))
                    .retrieve()
                    .bodyToMono(CreditScoreDto.class)
                    .timeout(Duration.ofSeconds(15))
                    .block();

            return creditScore;
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
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
    public boolean hasCustomerCreditScore(Long customerId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Boolean> response = webClient.get()
                    .uri(creditScoreServiceUrl + "/api/credit-scores/exists/customer/{customerId}", customerId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            return response != null && Boolean.TRUE.equals(response.get("exists"));
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
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
            webClient.delete()
                    .uri(creditScoreServiceUrl + "/api/credit-scores/customer/{customerId}", customerId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
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

            CreditScoreDto creditScore = webClient.put()
                    .uri(creditScoreServiceUrl + "/api/credit-scores/customer/{customerId}/loan-status", customerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(loanStatusData))
                    .retrieve()
                    .bodyToMono(CreditScoreDto.class)
                    .timeout(Duration.ofSeconds(15))
                    .block();

            return creditScore;
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
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
    public boolean isServiceHealthy() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> response = webClient.get()
                    .uri(creditScoreServiceUrl + "/api/credit-scores/health")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            
            return response != null && "UP".equals(response.get("status"));
        } catch (Exception e) {
            return false;
        }
    }
}