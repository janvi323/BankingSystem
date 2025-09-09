package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (customerRepository.count() == 0) {
            initializeTestData();
        }
    }

    private void initializeTestData() {
        // Create test admin user
        Customer admin = new Customer();
        admin.setName("Admin User");
        admin.setEmail("admin@bank.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ADMIN");
        admin.setCreditScore(800);
        customerRepository.save(admin);

        // Create test customer
        Customer customer1 = new Customer();
        customer1.setName("John Doe");
        customer1.setEmail("john@email.com");
        customer1.setPassword(passwordEncoder.encode("password123"));
        customer1.setRole("CUSTOMER");
        customer1.setCreditScore(650);
        Customer savedCustomer1 = customerRepository.save(customer1);

        // Create another test customer
        Customer customer2 = new Customer();
        customer2.setName("Jane Smith");
        customer2.setEmail("jane@email.com");
        customer2.setPassword(passwordEncoder.encode("password123"));
        customer2.setRole("CUSTOMER");
        customer2.setCreditScore(720);
        Customer savedCustomer2 = customerRepository.save(customer2);

        // Create sample loan applications
        Loan loan1 = new Loan();
        loan1.setCustomerId(savedCustomer1.getId());
        loan1.setAmount(50000.0);
        loan1.setPurpose("Home Purchase");
        loan1.setStatus("PENDING");
        loan1.setAppliedDate(LocalDateTime.now().minusDays(3));
        loanRepository.save(loan1);

        Loan loan2 = new Loan();
        loan2.setCustomerId(savedCustomer2.getId());
        loan2.setAmount(15000.0);
        loan2.setPurpose("Car Purchase");
        loan2.setStatus("APPROVED");
        loan2.setAppliedDate(LocalDateTime.now().minusDays(7));
        loan2.setApprovedDate(LocalDateTime.now().minusDays(2));
        loanRepository.save(loan2);

        System.out.println("Test data initialized successfully!");
        System.out.println("Admin login: admin@bank.com / admin123");
        System.out.println("Customer login: john@email.com / password123");
        System.out.println("Customer login: jane@email.com / password123");
    }
}
