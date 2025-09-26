package com.bankingsystem.creditscore.repository;

import com.bankingsystem.creditscore.entity.CreditScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditScoreRepository extends JpaRepository<CreditScore, Long> {

    /**
     * Find credit score by customer ID
     */
    Optional<CreditScore> findByCustomerId(Long customerId);

    /**
     * Find credit scores by score range
     */
    List<CreditScore> findByCreditScoreBetween(Integer minScore, Integer maxScore);

    /**
     * Find credit scores by grade
     */
    List<CreditScore> findByScoreGrade(String scoreGrade);

    /**
     * Check if customer already has a credit score
     */
    boolean existsByCustomerId(Long customerId);

    /**
     * Find customers with credit scores above a certain threshold
     */
    @Query("SELECT c FROM CreditScore c WHERE c.creditScore >= :threshold ORDER BY c.creditScore DESC")
    List<CreditScore> findCustomersWithScoreAbove(@Param("threshold") Integer threshold);

    /**
     * Find customers by email
     */
    Optional<CreditScore> findByCustomerEmail(String customerEmail);

    /**
     * Get average credit score
     */
    @Query("SELECT AVG(c.creditScore) FROM CreditScore c")
    Double getAverageCreditScore();

    /**
     * Count customers by score grade
     */
    @Query("SELECT c.scoreGrade, COUNT(c) FROM CreditScore c GROUP BY c.scoreGrade")
    List<Object[]> countCustomersByGrade();
}