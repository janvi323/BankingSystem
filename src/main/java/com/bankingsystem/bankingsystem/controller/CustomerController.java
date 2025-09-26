package com.bankingsystem.bankingsystem.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankingsystem.bankingsystem.Service.CustomerService;
import com.bankingsystem.bankingsystem.entity.Customer;

import jakarta.servlet.http.HttpSession;

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

    // Admin-only: Get customer details by ID
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id, HttpSession session) {
        // Check if user is logged in and is admin (or the customer themselves)
        Customer sessionCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (sessionCustomer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        
        // Allow admin to view any customer, or customers to view their own profile
        boolean isAdmin = "ADMIN".equals(sessionCustomer.getRole().toString());
        boolean isOwnProfile = sessionCustomer.getId().equals(id);
        
        if (!isAdmin && !isOwnProfile) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isPresent()) {
            return ResponseEntity.ok(customer.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
