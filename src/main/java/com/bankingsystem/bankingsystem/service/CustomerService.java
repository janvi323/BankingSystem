package com.bankingsystem.bankingsystem.service;

import com.bankingsystem.bankingsystem.model.Customer;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepo;

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepo.findById(id).orElseThrow();
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepo.save(customer);
    }
}
