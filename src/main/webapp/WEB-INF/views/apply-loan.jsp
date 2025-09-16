<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Apply for Loan - DebtHues</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f5dc; /* Ivory background */
            margin: 0;
            padding: 0;
            color: #000000; /* Black font color */
        }

        .navbar {
            background-color: #ffc107; /* Yellow navbar */
            padding: 1rem;
            color: #000000; /* Black text on navbar */
        }

        .navbar h1 {
            display: inline;
            font-size: 24px;
            color: #000000; /* Black text */
            font-weight: bold;
        }

        .navbar a {
            color: #000000; /* Black text on yellow navbar */
            text-decoration: none;
            margin-left: 20px;
            font-weight: 500;
        }

        .navbar a:hover {
            text-decoration: underline;
        }

        .container {
            max-width: 600px;
            margin: 40px auto;
            background-color: #ffffff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(255, 193, 7, 0.2); /* Yellow shadow */
        }

        h2 {
            text-align: center;
            color: #ffc107; /* Yellow heading */
            margin-bottom: 25px;
        }

        .form-group {
            margin-bottom: 15px;
        }

        label {
            display: block;
            font-weight: 600;
            margin-bottom: 6px;
            color: #000000; /* Black text */
        }

        input[type="number"],
        input[type="text"],
        select {
            width: 100%;
            padding: 10px;
            font-size: 16px;
            border-radius: 4px;
            border: 1px solid #ddd;
            box-sizing: border-box;
            color: #000000; /* Black text */
        }

        .btn {
            width: 100%;
            padding: 12px;
            background-color: #ffc107; /* Yellow button */
            color: #000000; /* Black text on button */
            border: none;
            border-radius: 4px;
            font-size: 18px;
            cursor: pointer;
            margin-top: 10px;
            font-weight: bold;
        }

        .btn:hover {
            background-color: #ffb300; /* Darker yellow on hover */
        }

        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background-color: #5a6268;
        }

        .alert {
            padding: 12px;
            border-radius: 4px;
            margin-bottom: 20px;
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

<nav class="navbar">
    <h1>DebtHues</h1>
    <a href="/dashboard">Dashboard</a>
    <a href="/apply-loan">Apply Loan</a>
    <a href="/customers">Customers</a>
    <form action="/perform_logout" method="post" style="display: inline;">
        <button type="submit" style="background: none; border: none; color: #000000; cursor: pointer; font-size: inherit; font-weight: 500;">Logout</button>
    </form>
</nav>

<div class="container">
    <h2>Apply for Loan</h2>

    <div class="alert alert-success" id="successAlert"></div>
    <div class="alert alert-danger" id="errorAlert"></div>

    <form id="loanForm">
        <div class="form-group">
            <label for="amount">Loan Amount</label>
            <input type="number" id="amount" name="amount" placeholder="Min: 50,000" min="50000" max="50000000" required>
        </div>

        <div class="form-group">
            <label for="purpose">Loan Purpose</label>
            <select id="purpose" name="purpose" required>
                <option value="" disabled selected>Select purpose</option>
                <option value="Home Purchase">Home Purchase</option>
                <option value="Car Purchase">Car Purchase</option>
                <option value="Personal Loan">Personal Loan</option>
                <option value="Business Loan">Business Loan</option>
                <option value="Education Loan">Education Loan</option>
                <option value="Gold Loan">Gold Loan</option>
                <option value="Agricultural Loan">Agricultural Loan</option>
                <option value="Two Wheeler Loan">Two Wheeler Loan</option>
            </select>
        </div>

        <button type="submit" class="btn">Submit Application</button>
        <button type="button" class="btn btn-secondary" onclick="window.location.href='/dashboard'">Back to Dashboard</button>
    </form>
</div>

<script>
    document.getElementById('loanForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const formData = {
            amount: parseFloat(document.getElementById('amount').value),
            purpose: document.getElementById('purpose').value
        };

        fetch('/api/loans/apply', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(formData)
        })
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error('Application failed');
            }
        })
        .then data => {
            showAlert('success', 'Loan application submitted successfully!');
            document.getElementById('loanForm').reset();
            setTimeout(() => window.location.href = '/dashboard', 3000);
        })
        .catch(error => {
            showAlert('error', 'Loan application failed. Please make sure you are logged in.');
        });
    });

    function showAlert(type, message) {
        const alertEl = document.getElementById(type === 'success' ? 'successAlert' : 'errorAlert');
        alertEl.textContent = message;
        alertEl.style.display = 'block';
        setTimeout(() => alertEl.style.display = 'none', 5000);
    }
</script>

</body>
</html>
