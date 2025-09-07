package com.bankingsystem.bankingsystem.repository;

import com.bankingsystem.bankingsystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
