package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.Service.AuthService;
import com.bankingsystem.bankingsystem.entity.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

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
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/apply-loan")
    public String applyLoan() {
        return "apply-loan";
    }

    @GetMapping("/customers")
    public String customers() {
        return "customers";
    }

    @GetMapping("/loans")
    public String loans() {
        return "loans";
    }

    // Handle JSP login form submission
    @PostMapping("/perform_login")
    public String performLogin(@RequestParam String username,
                             @RequestParam String password,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            Customer customer = authService.login(username, password);

            if (customer != null) {
                // Store customer in session
                session.setAttribute("loggedInCustomer", customer);
                return "redirect:/dashboard";
            } else {
                // Only add error message for invalid credentials
                redirectAttributes.addFlashAttribute("loginError", "Invalid username or password. Please try again.");
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
