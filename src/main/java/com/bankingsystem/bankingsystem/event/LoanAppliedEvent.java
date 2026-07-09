package com.bankingsystem.bankingsystem.event;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import org.springframework.context.ApplicationEvent;

/** Published when a customer submits a new loan application. */
public class LoanAppliedEvent extends ApplicationEvent {
    private final Loan     loan;
    private final Customer customer;

    public LoanAppliedEvent(Object source, Loan loan, Customer customer) {
        super(source);
        this.loan     = loan;
        this.customer = customer;
    }

    public Loan     getLoan()     { return loan; }
    public Customer getCustomer() { return customer; }
}
