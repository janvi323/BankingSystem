package com.bankingsystem.bankingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Loan {

    public enum Status { PENDING, APPROVED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private Double amount;
    private String purpose;
    private Integer tenure;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private LocalDateTime applicationDate = LocalDateTime.now();
    private LocalDateTime approvalDate;
    private String adminComments;
}
