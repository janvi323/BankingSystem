package com.bankingsystem.bankingsystem.dto;

/**
 * Flat DTO for EMI data — safe for Jackson serialization (no lazy loading).
 */
public class EmiDto {
    public Long    id;
    public Integer emiNumber;
    public String  dueDate;         // yyyy-MM-dd
    public Double  amount;
    public String  status;          // PENDING / PAID
    public String  paymentDate;     // nullable, yyyy-MM-dd
    public String  paymentMethod;   // nullable
    public boolean paid;

    // ── Flattened loan fields ──────────────────────────────────────────────
    public Long    loanId;
    public String  purpose;
    public Integer tenure;
    public Double  loanAmount;
    public Double  interestRate;
    public Double  emiAmount;
    public String  selectedBankName;
    public String  loanStatus;

    public EmiDto() {}
}
