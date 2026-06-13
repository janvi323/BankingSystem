package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.entity.Customer;

public class AiRequestContext {

    private final AiRole role;
    private final Customer customer;
    private final String sanitizedMessage;

    public AiRequestContext(AiRole role, Customer customer, String sanitizedMessage) {
        this.role = role;
        this.customer = customer;
        this.sanitizedMessage = sanitizedMessage;
    }

    public AiRole getRole() {
        return role;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getSanitizedMessage() {
        return sanitizedMessage;
    }

    public Long getCustomerId() {
        return customer == null ? null : customer.getId();
    }
}
