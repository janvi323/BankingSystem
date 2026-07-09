package com.bankingsystem.bankingsystem.event;

import com.bankingsystem.bankingsystem.entity.Loan;
import org.springframework.context.ApplicationEvent;

/** Published after the credit score microservice is queried and a score is available. */
public class CreditScoreGeneratedEvent extends ApplicationEvent {
    private final Loan loan;
    private final int  creditScore;

    public CreditScoreGeneratedEvent(Object source, Loan loan, int creditScore) {
        super(source);
        this.loan        = loan;
        this.creditScore = creditScore;
    }

    public Loan getLoan()        { return loan; }
    public int  getCreditScore() { return creditScore; }
}
