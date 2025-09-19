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

            <button type="submit" class="btn">Login</button>
        </form>

        <div class="links">
            <a href="/register">Don't have an account? Register here</a>
        </div>
    </div>
</body>
</html>
