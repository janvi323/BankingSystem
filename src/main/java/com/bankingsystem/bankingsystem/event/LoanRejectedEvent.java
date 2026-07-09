package com.bankingsystem.bankingsystem.event;

import com.bankingsystem.bankingsystem.entity.Loan;
import org.springframework.context.ApplicationEvent;

/** Published when a loan is rejected (auto or by admin). */
public class LoanRejectedEvent extends ApplicationEvent {
    private final Loan   loan;
    private final String reason;

    public LoanRejectedEvent(Object source, Loan loan, String reason) {
        super(source);
        this.loan   = loan;
        this.reason = reason;
    }

    public Loan   getLoan()   { return loan; }
    public String getReason() { return reason; }
}
