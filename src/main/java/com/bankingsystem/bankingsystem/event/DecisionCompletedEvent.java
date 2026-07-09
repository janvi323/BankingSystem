package com.bankingsystem.bankingsystem.event;

import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.entity.LoanDecision;
import org.springframework.context.ApplicationEvent;

/** Published after the AI decision engine completes its evaluation. */
public class DecisionCompletedEvent extends ApplicationEvent {
    private final Loan         loan;
    private final LoanDecision decision;

    public DecisionCompletedEvent(Object source, Loan loan, LoanDecision decision) {
        super(source);
        this.loan     = loan;
        this.decision = decision;
    }

    public Loan         getLoan()     { return loan; }
    public LoanDecision getDecision() { return decision; }
}
