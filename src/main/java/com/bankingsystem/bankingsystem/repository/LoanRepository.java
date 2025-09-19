package com.bankingsystem.bankingsystem.repository;

import com.bankingsystem.bankingsystem.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByCustomerId(Long customerId);

    List<Loan> findByStatus(Loan.Status status);
}
