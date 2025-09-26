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
    public boolean deleteCustomer(Long id) {
        try {
            if (customerRepository.existsById(id)) {
                customerRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting customer: " + e.getMessage(), e);
        }
    }

    // 7️⃣ Get Customer Count (For Dashboard Statistics)
    public int getCustomerCount() {
        return (int) customerRepository.count();
    }
}
