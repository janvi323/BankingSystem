package com.bankingsystem.bankingsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Loan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    private Double amount;
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED
    private String purpose; // Purpose of the loan
    private LocalDateTime appliedDate;
    private LocalDateTime approvedDate;
}
