package com.bankingsystem.bankingsystem;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

@Component
@Order(1)  // <- put it on the class, NOT the method
public class DBTestRunner implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DBTestRunner started!");
        System.out.println("Number of customers: " + customerRepository.count());
    }
}
