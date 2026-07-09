package com.bankingsystem.bankingsystem.event;

import com.bankingsystem.bankingsystem.entity.Loan;
import org.springframework.context.ApplicationEvent;

/** Published when a loan is approved (auto or by admin). */
public class LoanApprovedEvent extends ApplicationEvent {
    private final Loan loan;

    public LoanApprovedEvent(Object source, Loan loan) {
        super(source);
        this.loan = loan;
    }

    public Loan getLoan() { return loan; }
}
