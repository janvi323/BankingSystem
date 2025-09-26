package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.Service.AuthService;
import com.bankingsystem.bankingsystem.entity.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

@Controller
public class WebController {

    private final AuthService authService;

    public WebController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer != null) {
            String customerName = loggedInCustomer.getName();
            // Handle cases where name might be null or empty
            if (customerName == null || customerName.trim().isEmpty()) {
                customerName = loggedInCustomer.getEmail(); // Fallback to email
            }
            model.addAttribute("username", customerName);
            model.addAttribute("userRole", loggedInCustomer.getRole().toString());
        } else {
            model.addAttribute("username", "User");
            model.addAttribute("userRole", "Guest");
        }
        return "dashboard";
    }

    @GetMapping("/apply-loan")
    public String applyLoan(HttpSession session) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) {
            return "redirect:/login";
        }
        // Prevent admins from accessing apply-loan page
        if (loggedInCustomer.getRole() == Customer.Role.ADMIN) {
            return "redirect:/dashboard";
        }
        return "apply-loan";
    }

    @GetMapping("/customers")
    public String customers(HttpSession session) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) {
            return "redirect:/login";
        }
        // Only admins can access customers page
        if (loggedInCustomer.getRole() != Customer.Role.ADMIN) {
            return "redirect:/dashboard";
        }
        return "customers";
    }

    @GetMapping("/customers/{id}")
    public String customerProfile(@PathVariable Long id, HttpSession session, Model model) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) {
            return "redirect:/login";
        }
        
        // Allow admin to view any customer profile, or customers to view their own profile
        boolean isAdmin = loggedInCustomer.getRole() == Customer.Role.ADMIN;
        boolean isOwnProfile = loggedInCustomer.getId().equals(id);
        
        if (!isAdmin && !isOwnProfile) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("customerId", id);
        model.addAttribute("isAdmin", isAdmin);
        return "customer-profile";
    }

    @GetMapping("/loans")
    public String loans() {
        return "loans";
    }

    @GetMapping("/admin-loans")
    public String adminLoans() {
        return "admin-loans";
    }

    // Handle JSP registration form submission
    @PostMapping("/perform_register")
    public String performRegister(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String password,
                                @RequestParam String phone,
                                @RequestParam String address,
                                @RequestParam String role,
                                RedirectAttributes redirectAttributes) {
        try {
            Customer customer = new Customer();
            customer.setName(name);
            customer.setEmail(email);
            customer.setPassword(password);
            customer.setPhone(phone);
            customer.setAddress(address);
            customer.setRole(Customer.Role.valueOf(role));

            authService.register(customer);
            redirectAttributes.addFlashAttribute("message", "Registration successful! You can now login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }

    // Handle JSP login form submission
    @PostMapping("/perform_login")
    public String performLogin(@RequestParam String username, // Note: This is actually email from the form
                             @RequestParam String password,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            // The username parameter actually contains the email from the login form
            Customer customer = authService.login(username, password);

            if (customer != null) {
                // Store customer in session
                session.setAttribute("loggedInCustomer", customer);
                return "redirect:/dashboard";
            } else {
                // Only add error message for invalid credentials
                redirectAttributes.addFlashAttribute("loginError", "Invalid email or password. Please try again.");
                return "redirect:/login?error";
            }
        } catch (Exception e) {
            // Only add error message for login failures
            redirectAttributes.addFlashAttribute("loginError", "Login failed: " + e.getMessage());
            return "redirect:/login?error";
        }
    }

    // Handle JSP logout
    @PostMapping("/perform_logout")
    public String performLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        // Only add logout message when user actually logs out
        redirectAttributes.addFlashAttribute("logoutMessage", "You have been successfully logged out.");
        return "redirect:/login?logout";
    }
}
