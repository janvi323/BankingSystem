package com.bankingsystem.bankingsystem.repository;

import com.bankingsystem.bankingsystem.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Find loans by customer ID
    List<Loan> findByCustomerId(Long customerId);

    // Find loans by status
    List<Loan> findByStatus(Loan.Status status);

    // Find loans by customer ID and status
    List<Loan> findByCustomerIdAndStatus(Long customerId, Loan.Status status);
}
