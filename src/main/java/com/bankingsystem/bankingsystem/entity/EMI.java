package com.bankingsystem.bankingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class EMI {

    public enum Status { PENDING, PAID, OVERDUE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "emi_number")
    private Integer emiNumber; // Which EMI number (1, 2, 3, etc.)

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_method")
    private String paymentMethod;

    // Default constructor
    public EMI() {}

    // Constructor with parameters
    public EMI(Loan loan, Integer emiNumber, LocalDate dueDate, Double amount) {
        this.loan = loan;
        this.emiNumber = emiNumber;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = Status.PENDING;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public Integer getEmiNumber() {
        return emiNumber;
    }

    public void setEmiNumber(Integer emiNumber) {
        this.emiNumber = emiNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // Helper method to check if EMI is overdue
    public boolean isOverdue() {
        return status == Status.PENDING && dueDate.isBefore(LocalDate.now());
    }

    // Helper method to get days until due
    public long getDaysUntilDue() {
        return LocalDate.now().until(dueDate).getDays();
    }

    // Helper method to get days overdue
    public long getDaysOverdue() {
        if (isOverdue()) {
            return dueDate.until(LocalDate.now()).getDays();
        }
        return 0;
    }
}
