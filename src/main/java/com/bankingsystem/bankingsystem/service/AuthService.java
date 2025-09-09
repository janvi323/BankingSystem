package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.entity.Customer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(CustomerService customerService, PasswordEncoder passwordEncoder) {
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer register(Customer customer) {
        // Set default role if not provided
        if (customer.getRole() == null || customer.getRole().isEmpty()) {
            customer.setRole("CUSTOMER");
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerService.registerNewCustomer(customer);
    }

    public Customer login(String email, String password) {
        Optional<Customer> customerOpt = customerService.getCustomerByEmail(email);

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (passwordEncoder.matches(password, customer.getPassword())) {
                return customer;
            }
        }

        return null;
    }
}
