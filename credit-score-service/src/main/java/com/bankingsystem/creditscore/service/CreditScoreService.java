package com.bankingsystem.creditscore.service;

import com.bankingsystem.creditscore.dto.CreditScoreRequest;
import com.bankingsystem.creditscore.dto.CreditScoreResponse;
import com.bankingsystem.creditscore.entity.CreditScore;
import com.bankingsystem.creditscore.repository.CreditScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CreditScoreService {

    @Autowired
    private CreditScoreRepository creditScoreRepository;

    /**
     * Calculate and store credit score for a customer
     */
    public CreditScoreResponse calculateCreditScore(CreditScoreRequest request) {
        // Check if customer already has a credit score
        Optional<CreditScore> existingScore = creditScoreRepository.findByCustomerId(request.getCustomerId());
        
        CreditScore creditScore;
        if (existingScore.isPresent()) {
            creditScore = existingScore.get();
            updateCreditScoreData(creditScore, request);
        } else {
            creditScore = new CreditScore(
                request.getCustomerId(),
                request.getCustomerName(),
                request.getCustomerEmail(),
                request.getIncome()
            );
            updateCreditScoreData(creditScore, request);
        }

        // Calculate the credit score using our algorithm
        int calculatedScore = calculateScore(request);
        creditScore.setCreditScore(calculatedScore);

        // Save the credit score
        CreditScore savedScore = creditScoreRepository.save(creditScore);
        
        return mapToResponse(savedScore);
    }

    /**
     * Get credit score by customer ID
     */
    @Transactional(readOnly = true)
    public Optional<CreditScoreResponse> getCreditScoreByCustomerId(Long customerId) {
        return creditScoreRepository.findByCustomerId(customerId)
                .map(this::mapToResponse);
    }

    /**
     * Get credit score by customer email
     */
    @Transactional(readOnly = true)
    public Optional<CreditScoreResponse> getCreditScoreByEmail(String email) {
        return creditScoreRepository.findByCustomerEmail(email)
                .map(this::mapToResponse);
    }

    /**
     * Get all credit scores
     */
    @Transactional(readOnly = true)
    public List<CreditScoreResponse> getAllCreditScores() {
        return creditScoreRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get credit scores by score range
     */
    @Transactional(readOnly = true)
    public List<CreditScoreResponse> getCreditScoresByRange(Integer minScore, Integer maxScore) {
        return creditScoreRepository.findByCreditScoreBetween(minScore, maxScore)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get credit scores by grade
     */
    @Transactional(readOnly = true)
    public List<CreditScoreResponse> getCreditScoresByGrade(String grade) {
        return creditScoreRepository.findByScoreGrade(grade)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update existing credit score
     */
    public CreditScoreResponse updateCreditScore(Long customerId, CreditScoreRequest request) {
        CreditScore creditScore = creditScoreRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Credit score not found for customer ID: " + customerId));

        updateCreditScoreData(creditScore, request);
        int calculatedScore = calculateScore(request);
        creditScore.setCreditScore(calculatedScore);

        CreditScore savedScore = creditScoreRepository.save(creditScore);
        return mapToResponse(savedScore);
    }

    /**
     * Delete credit score by customer ID
     */
    public void deleteCreditScore(Long customerId) {
        CreditScore creditScore = creditScoreRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Credit score not found for customer ID: " + customerId));
        
        creditScoreRepository.delete(creditScore);
    }

    /**
     * Check if customer has credit score
     */
    @Transactional(readOnly = true)
    public boolean hasCustomerCreditScore(Long customerId) {
        return creditScoreRepository.existsByCustomerId(customerId);
    }

    /**
     * Get customers with high credit scores (above threshold)
     */
    @Transactional(readOnly = true)
    public List<CreditScoreResponse> getHighCreditScoreCustomers(Integer threshold) {
        return creditScoreRepository.findCustomersWithScoreAbove(threshold)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get average credit score
     */
    @Transactional(readOnly = true)
    public Double getAverageCreditScore() {
        return creditScoreRepository.getAverageCreditScore();
    }

    /**
     * Update credit score based on loan status change
     * This method adjusts credit score when loans are approved or rejected
     */
    public CreditScoreResponse updateCreditScoreForLoanStatus(Long customerId, String loanStatus, Double loanAmount) {
        Optional<CreditScore> creditScoreOpt = creditScoreRepository.findByCustomerId(customerId);
        
        if (creditScoreOpt.isEmpty()) {
            throw new RuntimeException("Credit score not found for customer ID: " + customerId);
        }
        
        CreditScore creditScore = creditScoreOpt.get();
        int currentScore = creditScore.getCreditScore();
        int newScore = calculateNewScoreForLoanStatus(currentScore, loanStatus, loanAmount);
        
        // Ensure score stays within valid range (300-850)
        newScore = Math.min(850, Math.max(300, newScore));
        
        creditScore.setCreditScore(newScore);
        creditScore.setLastUpdated(LocalDateTime.now());
        
        CreditScore savedScore = creditScoreRepository.save(creditScore);
        return mapToResponse(savedScore);
    }

    /**
     * Calculate new credit score based on loan status
     * Approved loans increase score, rejected loans decrease score
     * The impact varies based on loan amount
     */
    private int calculateNewScoreForLoanStatus(int currentScore, String loanStatus, Double loanAmount) {
        int scoreChange = 0;
        
        // Base score adjustment
        int baseAdjustment = 10; // Base adjustment points
        
        // Adjust based on loan amount (larger loans have more impact)
        // Small loans (< 50K): 1x multiplier
        // Medium loans (50K-200K): 1.5x multiplier  
        // Large loans (> 200K): 2x multiplier
        double amountMultiplier = 1.0;
        if (loanAmount != null) {
            if (loanAmount >= 200000) {
                amountMultiplier = 2.0;
            } else if (loanAmount >= 50000) {
                amountMultiplier = 1.5;
            }
        }
        
        switch (loanStatus.toUpperCase()) {
            case "APPROVED":
                // Loan approval increases credit score
                scoreChange = (int) (baseAdjustment * amountMultiplier);
                
                // Higher boost for lower credit scores (helps build credit)
                if (currentScore < 600) {
                    scoreChange = (int) (scoreChange * 1.3);
                } else if (currentScore < 700) {
                    scoreChange = (int) (scoreChange * 1.1);
                }
                break;
                
            case "REJECTED":
                // Loan rejection decreases credit score (indicates creditworthiness issues)
                scoreChange = -(int) (baseAdjustment * amountMultiplier * 0.7); // Less harsh than approval boost
                
                // Less penalty for higher credit scores
                if (currentScore > 750) {
                    scoreChange = (int) (scoreChange * 0.7);
                } else if (currentScore > 650) {
                    scoreChange = (int) (scoreChange * 0.85);
                }
                break;
                
            default:
                // PENDING or other statuses don't change score
                scoreChange = 0;
                break;
        }
        
        return currentScore + scoreChange;
    }

    /**
     * Credit score calculation algorithm
     * This is a simplified algorithm - in real world, this would be much more complex
     */
    private int calculateScore(CreditScoreRequest request) {
        int baseScore = 300; // Minimum credit score
        
        // Income factor (max 200 points)
        int incomeScore = Math.min(200, (int) (request.getIncome() / 1000));
        
        // Payment history factor (max 150 points)
        int paymentScore = 0;
        if (request.getPaymentHistoryScore() != null) {
            paymentScore = Math.min(150, (int) (request.getPaymentHistoryScore() * 1.5));
        }
        
        // Debt to income ratio factor (max 100 points, lower ratio = higher score)
        int debtScore = 0;
        if (request.getDebtToIncomeRatio() != null) {
            debtScore = Math.max(0, 100 - (int) (request.getDebtToIncomeRatio() * 200));
        }
        
        // Credit utilization factor (max 100 points, lower utilization = higher score)
        int utilizationScore = 0;
        if (request.getCreditUtilizationRatio() != null) {
            utilizationScore = Math.max(0, 100 - (int) (request.getCreditUtilizationRatio() * 200));
        }
        
        // Credit age factor (max 100 points)
        int ageScore = 0;
        if (request.getCreditAgeMonths() != null) {
            ageScore = Math.min(100, request.getCreditAgeMonths() / 2);
        }
        
        // Number of accounts factor (max 50 points, but diminishing returns)
        int accountsScore = 0;
        if (request.getNumberOfAccounts() != null) {
            accountsScore = Math.min(50, request.getNumberOfAccounts() * 5);
        }

        int totalScore = baseScore + incomeScore + paymentScore + debtScore + utilizationScore + ageScore + accountsScore;
        
        // Ensure score is within valid range (300-850)
        return Math.min(850, Math.max(300, totalScore));
    }

    /**
     * Update credit score entity with request data
     */
    private void updateCreditScoreData(CreditScore creditScore, CreditScoreRequest request) {
        creditScore.setCustomerName(request.getCustomerName());
        creditScore.setCustomerEmail(request.getCustomerEmail());
        creditScore.setIncome(request.getIncome());
        creditScore.setDebtToIncomeRatio(request.getDebtToIncomeRatio());
        creditScore.setPaymentHistoryScore(request.getPaymentHistoryScore());
        creditScore.setCreditUtilizationRatio(request.getCreditUtilizationRatio());
        creditScore.setCreditAgeMonths(request.getCreditAgeMonths());
        creditScore.setNumberOfAccounts(request.getNumberOfAccounts());
        creditScore.setLastUpdated(LocalDateTime.now());
    }

    /**
     * Map CreditScore entity to CreditScoreResponse DTO
     */
    private CreditScoreResponse mapToResponse(CreditScore creditScore) {
        CreditScoreResponse response = new CreditScoreResponse();
        response.setId(creditScore.getId());
        response.setCustomerId(creditScore.getCustomerId());
        response.setCustomerName(creditScore.getCustomerName());
        response.setCustomerEmail(creditScore.getCustomerEmail());
        response.setCreditScore(creditScore.getCreditScore());
        response.setScoreGrade(creditScore.getScoreGrade());
        response.setIncome(creditScore.getIncome());
        response.setDebtToIncomeRatio(creditScore.getDebtToIncomeRatio());
        response.setPaymentHistoryScore(creditScore.getPaymentHistoryScore());
        response.setCreditUtilizationRatio(creditScore.getCreditUtilizationRatio());
        response.setCreditAgeMonths(creditScore.getCreditAgeMonths());
        response.setNumberOfAccounts(creditScore.getNumberOfAccounts());
        response.setLastUpdated(creditScore.getLastUpdated());
        response.setCreatedAt(creditScore.getCreatedAt());
        return response;
    }
}