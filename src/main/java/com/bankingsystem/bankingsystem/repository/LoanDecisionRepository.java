package com.bankingsystem.bankingsystem.repository;

import com.bankingsystem.bankingsystem.entity.LoanDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanDecisionRepository extends JpaRepository<LoanDecision, Long> {

    Optional<LoanDecision> findByLoanId(Long loanId);

    boolean existsByLoanId(Long loanId);
}
