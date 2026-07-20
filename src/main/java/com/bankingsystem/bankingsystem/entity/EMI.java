package com.bankingsystem.bankingsystem.entity;

import jakarta.persistence.*;
import jakarta.persistence.Transient;
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

    @Column(name = "late_fee")
    private Double lateFee = 0.0;

    @Column(name = "partial_amount_paid")
    private Double partialAmountPaid = 0.0;

    /** Legacy column: mirrors status==PAID. Always kept in sync. */
    @Column(name = "paid", nullable = false)
    private boolean paid = false;

    // Default constructor
    public EMI() {}

    // Constructor with parameters
    public EMI(Loan loan, Integer emiNumber, LocalDate dueDate, Double amount) {
        this.loan = loan;
        this.emiNumber = emiNumber;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = Status.PENDING;
        this.paid = false;
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
        // Keep legacy 'paid' column in sync
        this.paid = (status == Status.PAID);
    }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

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

    public Double getLateFee() {
        return lateFee != null ? lateFee : 0.0;
    }

    public void setLateFee(Double lateFee) {
        this.lateFee = lateFee;
    }

    public Double getPartialAmountPaid() {
        return partialAmountPaid != null ? partialAmountPaid : 0.0;
    }

    public void setPartialAmountPaid(Double partialAmountPaid) {
        this.partialAmountPaid = partialAmountPaid;
    }

    /**
     * Computes real-time late fee: 2% per month on the EMI amount,
     * applied only after a 3-day grace period past the due date.
     * Formula: amount * 0.02 * (daysOverdue / 30)
     */
    @Transient
    public double getComputedLateFee() {
        if (status == Status.PAID || dueDate == null || amount == null) return 0.0;
        long daysOver = getDaysOverdue();
        if (daysOver <= 3) return 0.0; // 3-day grace period
        // 2% per month, pro-rated daily
        return Math.round(amount * 0.02 * ((daysOver - 3) / 30.0) * 100.0) / 100.0;
    }

    @Transient
    public double getTotalPayable() {
        double base = (amount != null ? amount : 0.0);
        double penalty = getComputedLateFee();
        double partial = (partialAmountPaid != null ? partialAmountPaid : 0.0);
        return Math.round((base + penalty - partial) * 100.0) / 100.0;
    }
}
