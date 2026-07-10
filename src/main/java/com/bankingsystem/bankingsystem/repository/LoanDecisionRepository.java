package com.bankingsystem.bankingsystem.repository;

import com.bankingsystem.bankingsystem.entity.LoanDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanDecisionRepository extends JpaRepository<LoanDecision, Long> {

    Optional<LoanDecision> findByLoanId(Long loanId);

    boolean existsByLoanId(Long loanId);

    /** All AI decisions for a given customer (used by chatbot factor analysis). */
    @org.springframework.data.jpa.repository.Query("SELECT d FROM LoanDecision d WHERE d.loan.customer.id = :customerId")
    java.util.List<LoanDecision> findByCustomerId(@org.springframework.data.repository.query.Param("customerId") Long customerId);


}
