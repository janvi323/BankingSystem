<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DebtHues - Register</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5dc; /* Ivory background */
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            color: #000000; /* Black font color */
        }
        .register-container {
            background-color: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(220, 20, 60, 0.3); /* Red shadow */
            width: 500px;
        }
        .register-header {
            text-align: center;
            margin-bottom: 30px;
            color: #000000; /* Black text */
        }
        .register-header h2 {
            color: #8B5CF6; /* Changed to vibrant purple */
            margin-bottom: 10px;
            font-size: 28px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #000000; /* Black text */
            font-weight: bold;
        }
        input[type="text"], input[type="email"], input[type="password"], input[type="tel"], input[type="number"], select {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 16px;
            color: #000000; /* Black text */
        }
        input:focus, select:focus {
            border-color: #8B5CF6;
            outline: none;
        }
        .btn {
            width: 100%;
            padding: 12px;
            background-color: #8B5CF6; /* Changed to vibrant purple */
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            margin-bottom: 10px;
            font-weight: bold;
        }
        .btn:hover {
            background-color: #7C3AED;
        }
        .links {
            text-align: center;
            margin-top: 20px;
        }
        .links a {
            color: #8B5CF6; /* Changed to vibrant purple */
            text-decoration: none;
        }
        .links a:hover {
            text-decoration: underline;
        }
        .financial-section {
            background-color: #f8f9ff;
            padding: 20px;
            border-radius: 6px;
            margin: 20px 0;
            border-left: 4px solid #8B5CF6;
        }
        .financial-section h3 {
            margin-top: 0;
        }
        small {
            display: block;
            margin-top: 5px;
            font-style: italic;
        }
        .alert {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        .alert-danger {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
    </style>
</head>
<body>
    <div class="register-container">
        <div class="register-header">
            <h2>DebtHues</h2>
            <p>Create your account</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <c:if test="${not empty message}">
            <div class="alert alert-success">${message}</div>
        </c:if>

        <form action="/perform_register" method="post">
            <div class="form-group">
                <label for="name">Full Name:</label>
                <input type="text" id="name" name="name" required>
            </div>

            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>

            <div class="form-group">
                <label for="phone">Phone Number:</label>
                <input type="tel" id="phone" name="phone" required>
            </div>

            <div class="form-group">
                <label for="address">Address:</label>
                <input type="text" id="address" name="address" required>
            </div>

            <!-- Financial Information for Credit Score Calculation -->
            <div class="financial-section">
                <h3 style="color: #8B5CF6; margin-bottom: 15px; border-bottom: 2px solid #8B5CF6; padding-bottom: 5px;">Loan & Income Details</h3>
                <p style="color: #666; font-size: 14px; margin-bottom: 20px;">Enter your income and loan details. Your credit profile will be calculated automatically.</p>

                <div class="form-group">
                    <label for="income">Monthly Income (₹):</label>
                    <input type="number" id="income" name="income" min="0" step="100" placeholder="e.g., 40000" required>
                    <small style="color: #666;">Your total monthly income before taxes</small>
                </div>

                <div class="form-group">
                    <label for="loanAmount">Loan Amount (₹):</label>
                    <input type="number" id="loanAmount" name="loanAmount" min="0" step="1000" placeholder="e.g., 500000" required>
                    <small style="color: #666;">Total principal amount of your loan</small>
                </div>

                <div class="form-group">
                    <label for="interestRate">Interest Rate (% per annum):</label>
                    <input type="number" id="interestRate" name="interestRate" min="0" max="100" step="0.01" placeholder="e.g., 8.5" required>
                    <small style="color: #666;">Annual interest rate for your loan</small>
                </div>

                <div class="form-group">
                    <label for="tenure">Loan Tenure (months):</label>
                    <input type="number" id="tenure" name="tenure" min="1" max="600" placeholder="e.g., 60" required>
                    <small style="color: #666;">Number of months to repay the loan</small>
                </div>

                <!-- Placeholder for calculated results (to be filled after backend integration) -->
                <div id="calculationResults" style="margin-top:20px; color:#333; font-size:15px;"></div>
            </div>

            <div class="form-group">
                <label for="role">Role:</label>
                <select id="role" name="role" required onchange="toggleFinancialFields()">
                    <option value="">Select Role</option>
                    <option value="CUSTOMER">Customer</option>
                    <option value="ADMIN">Admin</option>
                </select>
            </div>

            <button type="submit" class="btn">Register</button>
        <script>
        function toggleFinancialFields() {
            var role = document.getElementById('role').value;
            var finSection = document.querySelector('.financial-section');
            if (role === 'CUSTOMER') {
                finSection.style.display = '';
                // Set required for all inputs inside financial-section
                finSection.querySelectorAll('input').forEach(function(input) { input.required = true; });
            } else {
                finSection.style.display = 'none';
                finSection.querySelectorAll('input').forEach(function(input) { input.required = false; });
            }
        }
        // On page load, hide if not customer
        window.onload = function() {
            toggleFinancialFields();
        };
        </script>
        </form>

        <div class="links">
            <a href="/login">Already have an account? Login here</a>
        </div>
    </div>
</body>
</html>
