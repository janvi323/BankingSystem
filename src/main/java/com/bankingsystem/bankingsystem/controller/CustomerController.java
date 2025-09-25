package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.Service.CustomerService;
import com.bankingsystem.bankingsystem.entity.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
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
    public ResponseEntity<List<Customer>> getAllCustomers(HttpSession session) {
        // Check if user is logged in and is admin
        Customer sessionCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (sessionCustomer == null || !"ADMIN".equals(sessionCustomer.getRole().toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // Admin-only: Get customer count
    @GetMapping("/count")
    public ResponseEntity<Integer> getCustomerCount(HttpSession session) {
        // Check if user is logged in and is admin
        Customer sessionCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (sessionCustomer == null || !"ADMIN".equals(sessionCustomer.getRole().toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(customerService.getCustomerCount());
    }
}
