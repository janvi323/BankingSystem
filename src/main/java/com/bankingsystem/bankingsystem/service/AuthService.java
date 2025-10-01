package com.bankingsystem.bankingsystem.Service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bankingsystem.bankingsystem.entity.Customer;

@Service
public class AuthService {

    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final CreditScoreClientService creditScoreClientService;

    public AuthService(CustomerService customerService, PasswordEncoder passwordEncoder, 
                      CreditScoreClientService creditScoreClientService) {
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
        this.creditScoreClientService = creditScoreClientService;
    }

    public Customer register(Customer customer) {
        // Set default role if not provided
        if (customer.getRole() == null) {
            customer.setRole(Customer.Role.CUSTOMER);  // Set default role properly
        }

        if (customer.getPassword() == null || customer.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        // Save customer first to get the ID
        Customer savedCustomer = customerService.registerNewCustomer(customer);
        
        // Calculate credit score using the microservice (only for customers, not admins)
        if (savedCustomer.getRole() == Customer.Role.CUSTOMER && savedCustomer.getIncome() != null) {
            try {
                var creditScoreDto = creditScoreClientService.calculateCreditScore(
                    savedCustomer,
                    savedCustomer.getIncome(),
                    savedCustomer.getDebtToIncomeRatio(),
                    savedCustomer.getPaymentHistoryScore(),
                    savedCustomer.getCreditUtilizationRatio(),
                    savedCustomer.getCreditAgeMonths(),
                    savedCustomer.getNumberOfAccounts()
                );
                
                // Update customer with the calculated credit score
                savedCustomer.setCreditScore(creditScoreDto.getCreditScore());
                savedCustomer = customerService.updateCustomer(savedCustomer);
                
            } catch (Exception e) {
                // Log the error but don't fail registration
                System.err.println("Warning: Failed to calculate credit score for customer " + 
                    savedCustomer.getId() + ": " + e.getMessage());
                // Set a default credit score if microservice fails
                savedCustomer.setCreditScore(500); // Default neutral score
                savedCustomer = customerService.updateCustomer(savedCustomer);
            }
        }
        
        return savedCustomer;
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
