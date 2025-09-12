<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CredResolve - Register</title>
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
        }
        .register-container {
            background-color: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(128, 0, 128, 0.2); /* Purple shadow */
            width: 500px;
        }
        .register-header {
            text-align: center;
            margin-bottom: 30px;
            color: #663399; /* Purple text */
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
        input[type="text"], input[type="email"], input[type="password"], input[type="tel"] {
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
            background-color: #663399; /* Purple button */
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            margin-bottom: 10px;
        }
        .btn:hover {
            background-color: #4B0082; /* Darker purple on hover */
        }
        .links {
            text-align: center;
            margin-top: 20px;
        }
        .links a {
            color: #663399; /* Purple links */
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
            <h2>CredResolve</h2>
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
