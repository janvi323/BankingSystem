package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // 1️⃣ Register New Customer
    public Customer registerNewCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // 2️⃣ Find Customer By Email (For Login Purpose)
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    // 3️⃣ Find Customer By ID
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    // 4️⃣ Get All Customers
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // 5️⃣ Update Customer (Optional)
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // 6️⃣ Delete Customer
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}
