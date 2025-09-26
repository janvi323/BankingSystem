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
                <h3 style="color: #8B5CF6; margin-bottom: 15px; border-bottom: 2px solid #8B5CF6; padding-bottom: 5px;">Financial Information</h3>
                <p style="color: #666; font-size: 14px; margin-bottom: 20px;">This information is used to calculate your accurate credit score.</p>
                
                <div class="form-group">
                    <label for="income">Annual Income (₹):</label>
                    <input type="number" id="income" name="income" min="0" step="1000" placeholder="e.g., 500000" required>
                    <small style="color: #666;">Your total annual income before taxes</small>
                </div>

                <div class="form-group">
                    <label for="debtToIncomeRatio">Current Debt-to-Income Ratio (%):</label>
                    <input type="number" id="debtToIncomeRatio" name="debtToIncomeRatio" min="0" max="100" step="0.1" placeholder="e.g., 25.5" required>
                    <small style="color: #666;">Percentage of your income that goes to debt payments (0-100)</small>
                </div>

                <div class="form-group">
                    <label for="paymentHistoryScore">Payment History Score:</label>
                    <select id="paymentHistoryScore" name="paymentHistoryScore" required>
                        <option value="">Select your payment history</option>
                        <option value="95">Excellent (Never missed payments) - 95</option>
                        <option value="85">Very Good (1-2 late payments in 2 years) - 85</option>
                        <option value="75">Good (3-4 late payments in 2 years) - 75</option>
                        <option value="60">Fair (5-7 late payments in 2 years) - 60</option>
                        <option value="40">Poor (Many missed/late payments) - 40</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="creditUtilizationRatio">Credit Utilization Ratio (%):</label>
                    <input type="number" id="creditUtilizationRatio" name="creditUtilizationRatio" min="0" max="100" step="0.1" placeholder="e.g., 30.0" required>
                    <small style="color: #666;">How much of your available credit you're using (0-100)</small>
                </div>

                <div class="form-group">
                    <label for="creditAgeMonths">Credit History Age (months):</label>
                    <input type="number" id="creditAgeMonths" name="creditAgeMonths" min="0" max="600" placeholder="e.g., 60" required>
                    <small style="color: #666;">How long you've had credit accounts (in months)</small>
                </div>

                <div class="form-group">
                    <label for="numberOfAccounts">Number of Credit Accounts:</label>
                    <input type="number" id="numberOfAccounts" name="numberOfAccounts" min="0" max="50" placeholder="e.g., 5" required>
                    <small style="color: #666;">Total credit cards, loans, and other credit accounts</small>
                </div>
            </div>

            <div class="form-group">
                <label for="role">Role:</label>
                <select id="role" name="role" required>
                    <option value="">Select Role</option>
                    <option value="CUSTOMER">Customer</option>
                    <option value="ADMIN">Admin</option>
                </select>
            </div>

            <button type="submit" class="btn">Register</button>
        </form>

        <div class="links">
            <a href="/login">Already have an account? Login here</a>
        </div>
    </div>
</body>
</html>
