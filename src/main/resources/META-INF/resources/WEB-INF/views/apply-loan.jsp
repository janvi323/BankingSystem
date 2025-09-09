<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Apply for Loan - Banking System</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            padding: 0;
        }

        .navbar {
            background-color: #007bff;
            padding: 1rem;
            color: white;
        }

        .navbar h1 {
            display: inline;
            font-size: 24px;
        }

        .navbar a {
            color: white;
            text-decoration: none;
            margin-left: 20px;
            font-weight: 500;
        }

        .container {
            max-width: 600px;
            margin: 40px auto;
            background-color: #ffffff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            color: #333;
            margin-bottom: 25px;
        }

        .form-group {
            margin-bottom: 15px;
        }

        label {
            display: block;
            font-weight: 600;
            margin-bottom: 6px;
            color: #555;
        }

        input[type="number"],
        select {
            width: 100%;
            padding: 10px;
            font-size: 16px;
            border-radius: 4px;
            border: 1px solid #ddd;
        }

        .btn {
            width: 100%;
            padding: 12px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 18px;
            cursor: pointer;
            margin-top: 10px;
        }

        .btn:hover {
            background-color: #218838;
        }

        .btn-secondary {
            background-color: #6c757d;
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
        }

        .alert-danger {
            background-color: #f8d7da;
            color: #721c24;
        }
    </style>
</head>
<body>

<nav class="navbar">
    <h1>🏦 Banking System</h1>
    <a href="/dashboard">Dashboard</a>
    <a href="/apply-loan">Apply Loan</a>
    <a href="/customers">Customers</a>
    <a href="/h2-console">Database</a>
    <a href="/logout">Logout</a>
</nav>

<div class="container">
    <h2> Apply for Loan</h2>

    <div class="alert alert-success" id="successAlert"></div>
    <div class="alert alert-danger" id="errorAlert"></div>

    <form id="loanForm">
        <div class="form-group">
            <label for="customerId">Customer ID</label>
            <input type="number" id="customerId" name="customerId" placeholder="Enter your customer ID" required>
        </div>

        <div class="form-group">
            <label for="amount">Loan Amount ($)</label>
            <input type="number" id="amount" name="amount" placeholder="Min: $1,000" min="1000" max="1000000" required>
        </div>

        <div class="form-group">
            <label for="purpose">Loan Purpose</label>
            <select id="purpose" name="purpose" required>
                <option value="" disabled selected>Select purpose</option>
                <option value="HOME_PURCHASE">Home Purchase</option>
                <option value="AUTO_LOAN">Auto Loan</option>
                <option value="PERSONAL">Personal Loan</option>
                <option value="BUSINESS">Business Loan</option>
                <option value="EDUCATION">Education Loan</option>
                <option value="DEBT_CONSOLIDATION">Debt Consolidation</option>
            </select>
        </div>

        <div class="form-group">
            <label for="termMonths">Loan Term (Months)</label>
            <select id="termMonths" name="termMonths" required>
                <option value="" disabled selected>Select term</option>
                <option value="12">12 months</option>
                <option value="24">24 months</option>
                <option value="36">36 months</option>
                <option value="48">48 months</option>
                <option value="60">60 months</option>
                <option value="120">120 months (10 years)</option>
                <option value="240">240 months (20 years)</option>
                <option value="360">360 months (30 years)</option>
            </select>
        </div>

        <button type="submit" class="btn">🚀 Submit Application</button>
        <button type="button" class="btn btn-secondary" onclick="window.location.href='/dashboard'"><- Back to Dashboard</button>
    </form>
</div>

<script>
    document.getElementById('loanForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const formData = {
            customerId: parseInt(document.getElementById('customerId').value),
            amount: parseFloat(document.getElementById('amount').value),
            purpose: document.getElementById('purpose').value,
            termMonths: parseInt(document.getElementById('termMonths').value),
            status: 'PENDING'
        };

        fetch('/api/loans/apply', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (response.ok) return response.json();
                else throw new Error('Application failed');
            })
            .then(data => {
                showAlert('success', `✅ Loan applied successfully! ID: ${data.id}, Status: ${data.status}`);
                document.getElementById('loanForm').reset();
                setTimeout(() => window.location.href = '/dashboard', 3000);
            })
            .catch(error => showAlert('error', '❌ Loan application failed. Check your customer ID.'));
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
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Apply for Loan - Banking System</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            padding: 0;
        }

        .navbar {
            background-color: #007bff;
            padding: 1rem;
            color: white;
        }

        .navbar h1 {
            display: inline;
            font-size: 24px;
        }

        .navbar a {
            color: white;
            text-decoration: none;
            margin-left: 20px;
            font-weight: 500;
        }

        .container {
            max-width: 600px;
            margin: 40px auto;
            background-color: #ffffff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            color: #333;
            margin-bottom: 25px;
        }

        .form-group {
            margin-bottom: 15px;
        }

        label {
            display: block;
            font-weight: 600;
            margin-bottom: 6px;
            color: #555;
        }

        input[type="number"],
        select {
            width: 100%;
            padding: 10px;
            font-size: 16px;
            border-radius: 4px;
            border: 1px solid #ddd;
        }

        .btn {
            width: 100%;
            padding: 12px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 18px;
            cursor: pointer;
            margin-top: 10px;
        }

        .btn:hover {
            background-color: #218838;
        }

        .btn-secondary {
            background-color: #6c757d;
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
        }

        .alert-danger {
            background-color: #f8d7da;
            color: #721c24;
        }
    </style>
</head>
<body>

<nav class="navbar">
    <h1>🏦 Banking System</h1>
    <a href="/dashboard">Dashboard</a>
    <a href="/apply-loan">Apply Loan</a>
    <a href="/customers">Customers</a>
    <a href="/h2-console">Database</a>
    <a href="/logout">Logout</a>
</nav>

<div class="container">
    <h2>💳 Apply for Loan</h2>

    <div class="alert alert-success" id="successAlert"></div>
    <div class="alert alert-danger" id="errorAlert"></div>

    <form id="loanForm">
        <div class="form-group">
            <label for="customerId">Customer ID</label>
            <input type="number" id="customerId" name="customerId" placeholder="Enter your customer ID" required>
        </div>

        <div class="form-group">
            <label for="amount">Loan Amount ($)</label>
            <input type="number" id="amount" name="amount" placeholder="Min: $1,000" min="1000" max="1000000" required>
        </div>

        <div class="form-group">
            <label for="purpose">Loan Purpose</label>
            <select id="purpose" name="purpose" required>
                <option value="" disabled selected>Select purpose</option>
                <option value="HOME_PURCHASE">Home Purchase</option>
                <option value="AUTO_LOAN">Auto Loan</option>
                <option value="PERSONAL">Personal Loan</option>
                <option value="BUSINESS">Business Loan</option>
                <option value="EDUCATION">Education Loan</option>
                <option value="DEBT_CONSOLIDATION">Debt Consolidation</option>
            </select>
        </div>

        <div class="form-group">
            <label for="termMonths">Loan Term (Months)</label>
            <select id="termMonths" name="termMonths" required>
                <option value="" disabled selected>Select term</option>
                <option value="12">12 months</option>
                <option value="24">24 months</option>
                <option value="36">36 months</option>
                <option value="48">48 months</option>
                <option value="60">60 months</option>
                <option value="120">120 months (10 years)</option>
                <option value="240">240 months (20 years)</option>
                <option value="360">360 months (30 years)</option>
            </select>
        </div>

        <button type="submit" class="btn">🚀 Submit Application</button>
        <button type="button" class="btn btn-secondary" onclick="window.location.href='/dashboard'">← Back to Dashboard</button>
    </form>
</div>

<script>
    document.getElementById('loanForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const formData = {
            customerId: parseInt(document.getElementById('customerId').value),
            amount: parseFloat(document.getElementById('amount').value),
            purpose: document.getElementById('purpose').value,
            termMonths: parseInt(document.getElementById('termMonths').value),
            status: 'PENDING'
        };

        fetch('/api/loans/apply', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (response.ok) return response.json();
                else throw new Error('Application failed');
            })
            .then(data => {
                showAlert('success', `✅ Loan applied successfully! ID: ${data.id}, Status: ${data.status}`);
                document.getElementById('loanForm').reset();
                setTimeout(() => window.location.href = '/dashboard', 3000);
            })
            .catch(error => showAlert('error', '❌ Loan application failed. Check your customer ID.'));
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
