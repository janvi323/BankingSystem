package com.bankingsystem.bankingsystem.repository;

import com.bankingsystem.bankingsystem.entity.EMI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EMIRepository extends JpaRepository<EMI, Long> {

    // Find EMIs by loan ID
    List<EMI> findByLoanId(Long loanId);

    // Find EMIs by customer ID through loan relationship
    @Query("SELECT e FROM EMI e WHERE e.loan.customer.id = :customerId ORDER BY e.dueDate ASC")
    List<EMI> findByCustomerId(@Param("customerId") Long customerId);

    // Find pending EMIs by customer ID
    @Query("SELECT e FROM EMI e WHERE e.loan.customer.id = :customerId AND e.status = 'PENDING' ORDER BY e.dueDate ASC")
    List<EMI> findPendingEMIsByCustomerId(@Param("customerId") Long customerId);

    // Find overdue EMIs by customer ID
    @Query("SELECT e FROM EMI e WHERE e.loan.customer.id = :customerId AND e.status = 'PENDING' AND e.dueDate < :currentDate ORDER BY e.dueDate ASC")
    List<EMI> findOverdueEMIsByCustomerId(@Param("customerId") Long customerId, @Param("currentDate") LocalDate currentDate);

    // Find EMIs due this month for a customer
    @Query("SELECT e FROM EMI e WHERE e.loan.customer.id = :customerId AND YEAR(e.dueDate) = YEAR(:currentDate) AND MONTH(e.dueDate) = MONTH(:currentDate) ORDER BY e.dueDate ASC")
    List<EMI> findEMIsDueThisMonth(@Param("customerId") Long customerId, @Param("currentDate") LocalDate currentDate);

    // Find EMIs by status
    List<EMI> findByStatus(EMI.Status status);

    // Count pending EMIs for a customer
    @Query("SELECT COUNT(e) FROM EMI e WHERE e.loan.customer.id = :customerId AND e.status = 'PENDING'")
    Long countPendingEMIsByCustomerId(@Param("customerId") Long customerId);

    // Get total pending amount for a customer
    @Query("SELECT SUM(e.amount) FROM EMI e WHERE e.loan.customer.id = :customerId AND e.status = 'PENDING'")
    Double getTotalPendingAmountByCustomerId(@Param("customerId") Long customerId);
}
