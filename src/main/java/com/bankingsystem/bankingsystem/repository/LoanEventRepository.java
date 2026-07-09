package com.bankingsystem.bankingsystem.repository;

import com.bankingsystem.bankingsystem.entity.LoanEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanEventRepository extends JpaRepository<LoanEvent, Long> {

    /** Returns full event timeline for a loan ordered chronologically. */
    List<LoanEvent> findByLoanIdOrderByOccurredAtAsc(Long loanId);

    /** Count events of a specific type for a loan. */
    long countByLoanIdAndEventType(Long loanId, LoanEvent.EventType eventType);

    /** Latest event for a given loan (for status summary). */
    @Query("SELECT e FROM LoanEvent e WHERE e.loanId = :loanId ORDER BY e.occurredAt DESC LIMIT 1")
    java.util.Optional<LoanEvent> findLatestByLoanId(@Param("loanId") Long loanId);
}
