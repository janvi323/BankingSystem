<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DebtHues - Login</title>
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
        .login-container {
            background-color: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(255, 193, 7, 0.3); /* Yellow shadow */
            width: 400px;
        }
        .login-header {
            text-align: center;
            margin-bottom: 30px;
            color: #000000; /* Black text */
        }
        .login-header h2 {
            color: #ffc107; /* Yellow brand color */
            margin-bottom: 10px;
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
        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 16px;
            color: #000000; /* Black text */
        }
        .btn {
            width: 100%;
            padding: 12px;
            background-color: #ffc107; /* Yellow button */
            color: #000000; /* Black text on button */
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            margin-bottom: 10px;
            font-weight: bold;
        }
        .btn:hover {
            background-color: #ffb300; /* Darker yellow on hover */
        }
        .links {
            text-align: center;
            margin-top: 20px;
        }
        .links a {
            color: #ffc107; /* Yellow links */
            text-decoration: none;
            margin: 0 10px;
        }
        .links a:hover {
            text-decoration: underline;
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
        .alert-info {
            background-color: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <h2>DebtHues</h2>
            <p>Please sign in to your account</p>
        </div>

        <!-- Display error message if login failed -->
        <c:if test="${param.error != null && loginError != null}">
            <div class="alert alert-danger">
                ${loginError}
            </div>
        </c:if>

        <!-- Display logout message -->
        <c:if test="${param.logout != null && logoutMessage != null}">
            <div class="alert alert-info">
                ${logoutMessage}
            </div>
        </c:if>

        <!-- Login Form -->
        <form action="/perform_login" method="post">
            <div class="form-group">
                <label for="username">Email:</label>
                <input type="text" id="username" name="username" required>
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>

            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <button type="submit" class="btn">Sign In</button>
        </form>

        <!-- Links -->
        <div class="links">
            <a href="/register">Create Account</a>
        </div>

        <!-- API Endpoints Info -->
        <div style="margin-top: 30px; padding: 20px; background-color: #f8f9fa; border-radius: 4px;">
            <h4 style="color: #000000;">Available API Endpoints:</h4>
            <ul style="font-size: 14px; color: #666;">
                <li><strong>POST /api/auth/login</strong> - API Login</li>
                <li><strong>POST /api/auth/register</strong> - API Registration</li>
                <li><strong>POST /api/loans/apply</strong> - Apply for Loan</li>
                <li><strong>GET /api/customers</strong> - Get Customers</li>
            </ul>
        </div>
    </div>
</body>
</html>
