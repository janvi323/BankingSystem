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
import com.bankingsystem.bankingsystem.repository.CustomerRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {

    private final AuthService             authService;
    private final CustomerService          customerService;
    private final CustomerRepository       customerRepository;

    public WebController(AuthService authService, CustomerService customerService,
                         CustomerRepository customerRepository) {
        this.authService            = authService;
        this.customerService        = customerService;
        this.customerRepository     = customerRepository;
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer != null) {
            return "redirect:/dashboard";
        }
        return "welcome";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/register")
    public String register(HttpSession session) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer != null) {
            return "redirect:/dashboard";
        }
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) {
            return "redirect:/login";
        }

        addLoggedInCustomer(model, loggedInCustomer);
        return "dashboard";
    }

    @GetMapping("/dashboard-test")
    public String dashboardTest(HttpSession session, Model model) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) {
            return "redirect:/login";
        }

        addLoggedInCustomer(model, loggedInCustomer);
        return "dashboard-test";
    }

    @GetMapping("/hue")
    public String hueChat(HttpSession session, Model model) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) {
            return "redirect:/login";
        }

        addLoggedInCustomer(model, loggedInCustomer);
        return "hue-chat";
    }

    @GetMapping("/apply-loan")
    public String applyLoan(HttpSession session, org.springframework.ui.Model model) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) return "redirect:/login";
        if (loggedInCustomer.getRole() == Customer.Role.ADMIN) return "redirect:/dashboard";
        // Pass fresh customer data so apply-loan form can pre-fill from profile
        Customer fresh = customerRepository.findById(loggedInCustomer.getId()).orElse(loggedInCustomer);
        model.addAttribute("profileData", fresh);
        model.addAttribute("financialProfileComplete", isFinancialProfileComplete(fresh));
        return "apply-loan";
    }

    @GetMapping("/loan-status")
    public String loanStatus(HttpSession session) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) return "redirect:/login";
        return "loan-status";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, org.springframework.ui.Model model) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) return "redirect:/login";
        // Always load fresh from DB
        Customer fresh = customerRepository.findById(loggedInCustomer.getId()).orElse(loggedInCustomer);
        model.addAttribute("customer", fresh);
        model.addAttribute("username", fresh.getName());
        model.addAttribute("userRole", fresh.getRole().name());
        return "profile";
    }

    @GetMapping("/emi")
    public String emi(HttpSession session) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) {
            return "redirect:/login";
        }
        // Prevent admins from accessing EMI page
        if (loggedInCustomer.getRole() == Customer.Role.ADMIN) {
            return "redirect:/dashboard";
        }
        return "emi";
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
    public String loans(HttpSession session) {
        if (session.getAttribute("loggedInCustomer") == null) {
            return "redirect:/login";
        }
        return "loans";
    }

    @GetMapping("/admin-loans")
    public String adminLoans(HttpSession session) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) {
            return "redirect:/login";
        }
        if (loggedInCustomer.getRole() != Customer.Role.ADMIN) {
            return "redirect:/dashboard";
        }
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
                                @RequestParam(required = false) Double income,
                                @RequestParam(required = false) Double loanAmount,
                                @RequestParam(required = false) Double interestRate,
                                @RequestParam(required = false) Integer tenure,
                                @RequestParam(required = false) String loanStatus,
                                RedirectAttributes redirectAttributes) {
        try {
            // DEBUG: Log what we're receiving
            System.out.println("DEBUG - Registration attempt:");
            System.out.println("Role: " + role);
            System.out.println("Income: " + income);
            System.out.println("LoanStatus: '" + loanStatus + "'");
            System.out.println("LoanAmount: " + loanAmount);

            Customer customer = new Customer();
            customer.setName(name);
            customer.setEmail(email);
            customer.setPassword(password);
            customer.setPhone(phone);
            customer.setAddress(address);
            customer.setRole(Customer.Role.valueOf(role));

            if (role.equalsIgnoreCase("CUSTOMER")) {
                if (income == null || income <= 0) {
                    redirectAttributes.addFlashAttribute("error", "Monthly income is required and must be a positive number for customers.");
                    return "redirect:/register";
                }

                double monthlyIncome = income;
                double annualIncome = monthlyIncome * 12.0;

                // Set financial information. The registration form collects
                // monthly income; the scoring service expects annual income.
                customer.setMonthlyIncome(monthlyIncome);
                customer.setIncome(annualIncome);

                // Handle debt-free customers (no loans) - CHECK THIS FIRST
                if ("no".equals(loanStatus)) {
                    System.out.println("DEBUG - Processing debt-free customer");

                    // Set excellent credit profile for debt-free customers
                    customer.setDebtToIncomeRatio(0.0); // No debt
                    customer.setEmi(0.0); // No EMI
                    customer.setCreditScore(850); // Excellent credit score
                    customer.setPaymentHistoryScore(100); // Perfect payment history
                    customer.setCreditUtilizationRatio(0.05); // Very low utilization (5%)
                    customer.setCreditAgeMonths(60); // 5 years credit age
                    customer.setNumberOfAccounts(3); // Optimal number of accounts

                    // Register the debt-free customer. AuthService calculates
                    // and stores the microservice score with annual income.
                    Customer registeredCustomer = authService.register(customer);
                    int finalCreditScore = registeredCustomer.getCreditScore() != null
                        ? registeredCustomer.getCreditScore()
                        : 850;

                    redirectAttributes.addFlashAttribute("message",
                        "Registration successful! As a debt-free customer, you have an EXCELLENT credit score of " +
                        finalCreditScore + "! Enjoy premium banking benefits and the best loan rates when you need them.");
                    return "redirect:/login";
                }

                // Handle customers with loans - ONLY validate loan fields if they have loans
                else if ("yes".equals(loanStatus)) {
                    System.out.println("DEBUG - Processing customer with loans");

                    if (loanAmount == null || loanAmount <= 0) {
                        redirectAttributes.addFlashAttribute("error", "Loan Amount is required and must be a positive number for customers with loans.");
                        return "redirect:/register";
                    }
                    if (interestRate == null || interestRate < 0) {
                        redirectAttributes.addFlashAttribute("error", "Interest Rate is required and must be zero or positive for customers with loans.");
                        return "redirect:/register";
                    }
                    if (tenure == null || tenure <= 0) {
                        redirectAttributes.addFlashAttribute("error", "Tenure is required and must be a positive integer for customers with loans.");
                        return "redirect:/register";
                    }

                    // Calculate EMI (Equated Monthly Installment)
                    double principal = loanAmount;
                    double monthlyRate = interestRate / 12.0 / 100.0;
                    int n = tenure;
                    double emi = (monthlyRate == 0) ? (principal / n) :
                        (principal * monthlyRate * Math.pow(1 + monthlyRate, n)) / (Math.pow(1 + monthlyRate, n) - 1);

                    // Calculate Debt-to-Income Ratio (DTI)
                    double dti = (emi / monthlyIncome);
                    customer.setDebtToIncomeRatio(dti); // Store as ratio (0.0 to 1.0)
                    customer.setEmi(emi);

                    // Calculate initial credit score based on DTI
                    int creditScore = 750; // Base score
                    if (dti > 0.5) creditScore = 450; // Poor
                    else if (dti > 0.3) creditScore = 550; // Fair
                    else if (dti > 0.2) creditScore = 650; // Good
                    else creditScore = 750; // Very Good

                    customer.setCreditScore(creditScore);

                    // Register customer with loan. AuthService calls the credit
                    // score service once with the normalized annual income.
                    Customer registeredCustomer = authService.register(customer);
                    creditScore = registeredCustomer.getCreditScore() != null
                        ? registeredCustomer.getCreditScore()
                        : creditScore;

                    String grade = creditScore >= 750 ? "Excellent" :
                                  creditScore >= 650 ? "Good" :
                                  creditScore >= 550 ? "Fair" : "Poor";

                    redirectAttributes.addFlashAttribute("message",
                        String.format("Registration successful! EMI: ₹%.2f, DTI: %.2f%%, Credit Score: %d (%s). You can now login.",
                        emi, dti * 100, creditScore, grade));
                    return "redirect:/login";
                }

                // If loanStatus is not provided or invalid, show error
                else {
                    System.out.println("DEBUG - Invalid or missing loan status: '" + loanStatus + "'");
                    redirectAttributes.addFlashAttribute("error", "Please select whether you have loans or are debt-free. LoanStatus received: '" + loanStatus + "'");
                    return "redirect:/register";
                }
            } else if (role.equalsIgnoreCase("ADMIN")) {
                // Only basic info required for admin
                authService.register(customer);
                redirectAttributes.addFlashAttribute("message", "Admin registration successful! You can now login.");
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid role selected.");
                return "redirect:/register";
            }
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
            Customer currentUser = (Customer) session.getAttribute("loggedInCustomer");
            if (currentUser == null || currentUser.getRole() != Customer.Role.ADMIN) {
                redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
                return "redirect:/login";
            }

            CustomerService.CreditScoreSyncSummary summary = customerService.refreshAllCreditScores();
            redirectAttributes.addFlashAttribute("message",
                "Credit scores refreshed: " + summary.refreshed() + " updated, " +
                summary.skipped() + " skipped, " + summary.localOnly() + " local-only, " +
                summary.failed() + " failed.");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error synchronizing credit scores: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    private void addLoggedInCustomer(Model model, Customer loggedInCustomer) {
        String customerName = loggedInCustomer.getName();
        if (customerName == null || customerName.trim().isEmpty()) {
            customerName = loggedInCustomer.getEmail();
        }
        model.addAttribute("username", customerName);
        model.addAttribute("userRole", loggedInCustomer.getRole().toString());
        model.addAttribute("userId", loggedInCustomer.getId());
    }

    private boolean isFinancialProfileComplete(Customer customer) {
        return customer.effectiveMonthlyIncome() > 0
                && customer.getEmi() != null
                && customer.getExistingLoans() != null
                && customer.getCreditUtilizationRatio() != null
                && customer.getEmploymentType() != null
                && !customer.getEmploymentType().isBlank()
                && customer.getEmploymentStabilityYears() != null;
    }
}
