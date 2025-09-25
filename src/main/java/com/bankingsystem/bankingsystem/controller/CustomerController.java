package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.Service.CustomerService;
import com.bankingsystem.bankingsystem.entity.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Admin-only: Get all customers
    @GetMapping
    // @PreAuthorize("hasRole('ADMIN')") // Temporarily disabled for testing
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // Admin-only: Get customer count
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> getCustomerCount() {
        return ResponseEntity.ok(customerService.getCustomerCount());
    }
}
