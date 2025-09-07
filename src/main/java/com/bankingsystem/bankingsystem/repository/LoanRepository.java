package com.bankingsystem.bankingsystem.repository;

import com.bankingsystem.bankingsystem.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
