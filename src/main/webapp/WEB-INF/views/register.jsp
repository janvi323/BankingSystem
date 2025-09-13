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
            box-shadow: 0 2px 10px rgba(255, 193, 7, 0.3); /* Yellow shadow */
            width: 500px;
        }
        .register-header {
            text-align: center;
            margin-bottom: 30px;
            color: #000000; /* Black text */
        }
        .register-header h2 {
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
        input[type="text"], input[type="email"], input[type="password"], input[type="tel"], select {
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
            display: none;
        }
        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .alert-danger {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>
    <div class="register-container">
        <div class="register-header">
            <h2>DebtHues</h2>
            <p>Create your account</p>
        </div>

        <div class="alert alert-success" id="successMessage"></div>
        <div class="alert alert-danger" id="errorMessage"></div>

        <form id="registerForm">
            <div class="form-group">
                <label for="name">Full Name:</label>
                <input type="text" id="name" name="name" required>
            </div>

            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
            </div>

            <div class="form-group">
                <label for="phone">Phone Number:</label>
                <input type="tel" id="phone" name="phone" required>
            </div>

            <div class="form-group">
                <label for="address">Address:</label>
                <input type="text" id="address" name="address" required>
            </div>

            <div class="form-group">
                <label for="role">Role:</label>
                <select id="role" name="role" required>
                    <option value="" disabled selected>Select your role</option>
                    <option value="CUSTOMER">Customer</option>
                    <option value="ADMIN">Administrator</option>
                </select>
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required minlength="6">
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm Password:</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>

            <button type="submit" class="btn">Create Account</button>
        </form>

        <div class="links">
            <a href="/login">Already have an account? Sign In</a>
        </div>
    </div>

    <script>
        document.getElementById('registerForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = {
                name: document.getElementById('name').value,
                email: document.getElementById('email').value,
                phone: document.getElementById('phone').value,
                address: document.getElementById('address').value,
                role: document.getElementById('role').value,
                password: document.getElementById('password').value
            };

            fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            })
            .then(response => {
                if (response.ok) {
                    document.getElementById('successMessage').innerText = 'Registration successful! Please login.';
                    document.getElementById('successMessage').style.display = 'block';
                    setTimeout(() => {
                        window.location.href = '/login';
                    }, 2000);
                } else {
                    document.getElementById('errorMessage').innerText = 'Registration failed. Please try again.';
                    document.getElementById('errorMessage').style.display = 'block';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                document.getElementById('errorMessage').innerText = 'Registration failed. Please try again.';
                document.getElementById('errorMessage').style.display = 'block';
            });
        });
    </script>
</body>
</html>
