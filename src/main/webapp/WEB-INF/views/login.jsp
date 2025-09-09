<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Banking System - Login</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .login-container {
            background-color: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            width: 400px;
        }
        .login-header {
            text-align: center;
            margin-bottom: 30px;
            color: #333;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #555;
            font-weight: bold;
        }
        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 16px;
        }
        .btn {
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            margin-bottom: 10px;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .btn-oauth {
            background-color: #dc3545;
        }
        .btn-oauth:hover {
            background-color: #c82333;
        }
        .links {
            text-align: center;
            margin-top: 20px;
        }
        .links a {
            color: #007bff;
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
            <h2>Banking System</h2>
            <p>Please sign in to your account</p>
        </div>

        <!-- Display error message if login failed -->
        <c:if test="${param.error != null}">
            <div class="alert alert-danger">
                Invalid username or password. Please try again.
            </div>
        </c:if>

        <!-- Display logout message -->
        <c:if test="${param.logout != null}">
            <div class="alert alert-info">
                You have been successfully logged out.
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

        <!-- OAuth2 Login -->
        <div style="text-align: center; margin: 20px 0;">
            <p>Or sign in with:</p>
            <a href="/oauth2/authorization/google" class="btn btn-oauth">Sign in with Google</a>
        </div>

        <!-- Links -->
        <div class="links">
            <a href="/register">Create Account</a> |
            <a href="/api/customers">View All Customers</a> |
            <a href="/h2-console">H2 Database</a>
        </div>

        <!-- API Endpoints Info -->
        <div style="margin-top: 30px; padding: 20px; background-color: #f8f9fa; border-radius: 4px;">
            <h4>Available API Endpoints:</h4>
            <ul style="font-size: 14px; color: #666;">
                <li><strong>POST /api/auth/login</strong> - API Login</li>
                <li><strong>POST /api/auth/register</strong> - API Registration</li>
                <li><strong>POST /api/loans/apply</strong> - Apply for Loan</li>
                <li><strong>GET /api/customers</strong> - Get Customers</li>
                <li><strong>GET /h2-console</strong> - Database Console</li>
            </ul>
        </div>
    </div>
</body>
</html>
