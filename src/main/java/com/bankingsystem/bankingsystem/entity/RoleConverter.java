package com.bankingsystem.bankingsystem.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RoleConverter implements AttributeConverter<Customer.Role, String> {

    @Override
    public String convertToDatabaseColumn(Customer.Role role) {
        return role != null ? role.name() : null;
    }

    @Override
    public Customer.Role convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }

        try {
            // Try uppercase first (new format)
            return Customer.Role.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Fallback for legacy lowercase values
            switch (dbData.toLowerCase()) {
                case "admin":
                    return Customer.Role.ADMIN;
                case "customer":
                    return Customer.Role.CUSTOMER;
                default:
                    throw new IllegalArgumentException("Invalid role value: " + dbData);
            }
        }
    }
}
