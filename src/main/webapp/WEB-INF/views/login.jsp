<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><!DOCTYPE html>
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
            box-shadow: 0 2px 10px rgba(220, 20, 60, 0.3); /* Red shadow */
            width: 400px;
        }
        .login-header {
            text-align: center;
            margin-bottom: 30px;
            color: #000000; /* Black text */
        }
        .login-header h2 {
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
        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 16px;
            color: #000000; /* Black text */
        }
        input[type="text"]:focus, input[type="password"]:focus {
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
        .divider {
            align-items: center;
            color: #666;
            display: flex;
            font-size: 14px;
            gap: 12px;
            margin: 18px 0;
        }
        .divider::before,
        .divider::after {
            background: #ddd;
            content: "";
            flex: 1;
            height: 1px;
        }
        .google-btn {
            align-items: center;
            background: #ffffff;
            border: 1px solid #d1d5db;
            border-radius: 4px;
            box-sizing: border-box;
            color: #1f2937;
            display: flex;
            font-size: 16px;
            font-weight: bold;
            gap: 10px;
            justify-content: center;
            padding: 12px;
            text-decoration: none;
            width: 100%;
        }
        .google-btn:hover {
            background: #f9fafb;
            border-color: #9ca3af;
        }
        .google-icon {
            height: 20px;
            width: 20px;
        }
        .links {
            text-align: center;
            margin-top: 20px;
        }
        .links a {
            color: #8B5CF6; /* Changed to vibrant purple */
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
            <p>Please login to your account</p>
        </div>

        <c:if test="${param.error != null}">
            <div class="alert alert-danger">${loginError != null ? loginError : 'Invalid credentials'}</div>
        </c:if>

        <c:if test="${param.logout != null}">
            <div class="alert alert-info">${logoutMessage != null ? logoutMessage : 'You have been logged out'}</div>
        </c:if>

        <form action="/perform_login" method="post">

            <div class="form-group">
                <label for="username">Email:</label>
                <input type="text" id="username" name="username" required>
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>

            <input type="hidden" id="role" name="role" value="CUSTOMER">

            <div style="display: flex; gap: 10px;">
                <button type="submit" class="btn" onclick="document.getElementById('role').value='CUSTOMER'">Sign in as Customer</button>
                <button type="submit" class="btn" onclick="document.getElementById('role').value='ADMIN'">Sign in as Admin</button>
            </div>
        </form>

        <div class="divider">or</div>

        <a class="google-btn" href="/oauth2/authorization/google">
            <svg class="google-icon" viewBox="0 0 24 24" aria-hidden="true">
                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path fill="#FBBC05" d="M5.84 14.1c-.22-.66-.35-1.36-.35-2.1s.13-1.44.35-2.1V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l3.66-2.84z"/>
                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06L5.84 9.9C6.71 7.3 9.14 5.38 12 5.38z"/>
            </svg>
            Sign in with Google
        </a>

        <div class="links">
            <a href="/register">Don't have an account? Register here</a>
        </div>
    </div>
</body>
</html>
