package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AiSessionResolver {

    private final CustomerRepository customerRepository;

    public AiSessionResolver(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public AiRequestContext resolve(HttpSession session, String rawMessage, AiSecurityGuard securityGuard) {
        Customer customer = resolveCustomer(session).orElse(null);
        AiRole role = customer == null ? AiRole.ANONYMOUS : AiRole.fromCustomerRole(customer.getRole());
        String sanitized = securityGuard.sanitizeInput(rawMessage);
        return new AiRequestContext(role, customer, sanitized);
    }

    public Optional<Customer> resolveCustomer(HttpSession session) {
        Object loggedIn = session == null ? null : session.getAttribute("loggedInCustomer");
        if (!(loggedIn instanceof Customer sessionCustomer) || sessionCustomer.getId() == null) {
            return Optional.empty();
        }

        return customerRepository.findById(sessionCustomer.getId())
                .map(this::stripSensitiveFields)
                .or(() -> Optional.of(stripSensitiveFields(sessionCustomer)));
    }

    private Customer stripSensitiveFields(Customer customer) {
        customer.setPassword(null);
        return customer;
    }
}
