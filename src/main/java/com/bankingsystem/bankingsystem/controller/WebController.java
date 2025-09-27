package com.bankingsystem.bankingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bankingsystem.bankingsystem.Service.AuthService;
import com.bankingsystem.bankingsystem.Service.CustomerService;
import com.bankingsystem.bankingsystem.entity.Customer;

import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {

    private final AuthService authService;
    private final CustomerService customerService;

    public WebController(AuthService authService, CustomerService customerService) {
        this.authService = authService;
        this.customerService = customerService;
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
            model.addAttribute("userId", loggedInCustomer.getId());
        } else {
            model.addAttribute("username", "User");
            model.addAttribute("userRole", "Guest");
            model.addAttribute("userId", null);
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
                                @RequestParam Double income,
                                @RequestParam Double loanAmount,
                                @RequestParam Double interestRate,
                                @RequestParam Integer tenure,
                                RedirectAttributes redirectAttributes) {
        try {
            Customer customer = new Customer();
            customer.setName(name);
            customer.setEmail(email);
            customer.setPassword(password);
            customer.setPhone(phone);
            customer.setAddress(address);
            customer.setRole(Customer.Role.valueOf(role));

            // Set financial information
            customer.setIncome(income);

            // Calculate EMI (Equated Monthly Installment)
            double principal = loanAmount;
            double monthlyRate = interestRate / 12.0 / 100.0;
            int n = tenure;
            double emi = (monthlyRate == 0) ? (principal / n) : (principal * monthlyRate * Math.pow(1 + monthlyRate, n)) / (Math.pow(1 + monthlyRate, n) - 1);

            // Calculate Debt-to-Income Ratio (DTI)
            double dti = (emi / income);
            customer.setDebtToIncomeRatio(dti); // Store as ratio (0.0 to 1.0)

            customer.setEmi(emi);

            // You can call your credit score microservice here with these values if needed

            authService.register(customer);
            redirectAttributes.addFlashAttribute("message", String.format("Registration successful! EMI: ₹%.2f, DTI: %.2f%%. You can now login.", emi, dti * 100));
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
                             @RequestParam String role,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            // The username parameter actually contains the email from the login form
            Customer customer = authService.login(username, password);

            if (customer != null) {
                // Check if the selected role matches the user's actual role
                if (!customer.getRole().toString().equalsIgnoreCase(role)) {
                    redirectAttributes.addFlashAttribute("loginError", "Role mismatch. Please select the correct role.");
                    return "redirect:/login?error";
                }
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

    // Endpoint to synchronize credit scores for all customers
    @PostMapping("/admin/sync-credit-scores")
    public String syncCreditScores(RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            // Check if user is admin
            Customer currentUser = (Customer) session.getAttribute("customer");
            if (currentUser == null || currentUser.getRole() != Customer.Role.ADMIN) {
                redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
                return "redirect:/login";
            }

            customerService.synchronizeAllCreditScores();
            redirectAttributes.addFlashAttribute("message", "Credit scores synchronized successfully for all customers!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error synchronizing credit scores: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }
}
