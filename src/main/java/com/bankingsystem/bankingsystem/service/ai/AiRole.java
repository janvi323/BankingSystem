package com.bankingsystem.bankingsystem.Service.ai;

public enum AiRole {
    ANONYMOUS,
    CUSTOMER,
    ADMIN;

    public static AiRole fromCustomerRole(com.bankingsystem.bankingsystem.entity.Customer.Role role) {
        if (role == com.bankingsystem.bankingsystem.entity.Customer.Role.ADMIN) {
            return ADMIN;
        }
        if (role == com.bankingsystem.bankingsystem.entity.Customer.Role.CUSTOMER) {
            return CUSTOMER;
        }
        return ANONYMOUS;
    }
}
