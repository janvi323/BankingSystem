<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DebtHues - Apply for Loan</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5dc; /* Ivory background */
            color: #000000; /* Black font color */
        }
        .navbar {
            background-color: #8B5CF6; /* Changed to vibrant purple */
            padding: 1rem 0;
            color: white;
        }
        .navbar-content {
            max-width: 1200px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 20px;
        }
        .navbar h1 {
            font-size: 24px;
            color: white;
            font-weight: bold;
        }
        .navbar-links a {
            color: white;
            text-decoration: none;
            margin: 0 15px;
            padding: 8px 16px;
            border-radius: 4px;
            transition: background-color 0.3s;
            font-weight: 500;
        }
        .navbar-links a:hover {
            background-color: rgba(255,255,255,0.2);
        }
        .container {
            max-width: 600px;
            margin: 40px auto;
            background-color: #ffffff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(220, 20, 60, 0.3);
        }
        h2 {
            text-align: center;
            color: #8B5CF6;
            margin-bottom: 30px;
            font-size: 28px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            color: #000000;
            font-weight: 600;
        }
        input[type="number"],
        input[type="text"],
        select {
            width: 100%;
            padding: 12px;
            border: 2px solid #e0e0e0;
            border-radius: 6px;
            font-size: 16px;
            transition: border-color 0.3s;
            box-sizing: border-box;
            color: #000000;
        }
        input[type="number"]:focus,
        input[type="text"]:focus,
        select:focus {
            outline: none;
            border-color: #8B5CF6;
        }
        .btn {
            width: 100%;
            padding: 15px;
            background-color: #8B5CF6;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 18px;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        .btn:hover {
            background-color: #7C3AED;
        }
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 6px;
            font-weight: 500;
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
        .loan-info {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 6px;
            margin-bottom: 20px;
            border-left: 4px solid #8B5CF6;
        }
        .loan-info h4 {
            color: #8B5CF6;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <h1>DebtHues</h1>
            <div class="navbar-links">
                <a href="/dashboard">Dashboard</a>
                <a href="/customers">Customers</a>
                <a href="/loans">Loans</a>
                <a href="/apply-loan">Apply Loan</a>
                <form action="/perform_logout" method="post" style="display: inline;">
                    <button type="submit" style="background: none; border: none; color: white; cursor: pointer; font-size: 16px; font-weight: 500; padding: 8px 16px;">Logout</button>
                </form>
            </div>
        </div>
    </nav>

    <div class="container">
        <h2>Apply for Loan</h2>

        <div class="loan-info">
            <h4>Loan Application Information</h4>
            <p>• Minimum loan amount: ₹1,000</p>
            <p>• Maximum loan amount: ₹1,000,000</p>
            <p>• Interest rates vary based on loan type and credit score</p>
            <p>• All applications are subject to approval</p>
        </div>

        <div id="alertContainer"></div>

        <form id="loanForm">
            <div class="form-group">
                <label for="amount">Loan Amount (₹):</label>
                <input type="number" id="amount" name="amount" min="1000" max="1000000" step="100" required>
            </div>

            <div class="form-group">
                <label for="purpose">Loan Purpose:</label>
                <select id="purpose" name="purpose" required>
                    <option value="">Select Loan Purpose</option>
                    <option value="Personal">Personal</option>
                    <option value="Home Purchase">Home Purchase</option>
                    <option value="Home Improvement">Home Improvement</option>
                    <option value="Car Purchase">Car Purchase</option>
                    <option value="Education">Education</option>
                    <option value="Business">Business</option>
                    <option value="Debt Consolidation">Debt Consolidation</option>
                    <option value="Medical Expenses">Medical Expenses</option>
                    <option value="Other">Other</option>
                </select>
            </div>

            <div class="form-group">
                <label for="tenure">Loan Tenure (Months):</label>
                <select id="tenure" name="tenure" required>
                    <option value="">Select Tenure</option>
                    <option value="12">12 Months</option>
                    <option value="24">24 Months</option>
                    <option value="36">36 Months</option>
                    <option value="48">48 Months</option>
                    <option value="60">60 Months</option>
                    <option value="84">84 Months</option>
                    <option value="120">120 Months (10 years)</option>
                    <option value="240">240 Months (20 years)</option>
                    <option value="360">360 Months (30 years)</option>
                </select>
            </div>

            <button type="submit" class="btn">Submit Loan Application</button>
        </form>
    </div>

    <script>
        function showAlert(message, type) {
            const alertContainer = document.getElementById('alertContainer');
            alertContainer.innerHTML = `
                <div class="alert alert-${type}">
                    ${message}
                </div>
            `;
            setTimeout(() => {
                alertContainer.innerHTML = '';
            }, 5000);
        }

        document.getElementById('loanForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = {
                amount: parseFloat(document.getElementById('amount').value),
                purpose: document.getElementById('purpose').value,
                tenure: parseInt(document.getElementById('tenure').value)
            };

            // Validate form
            if (formData.amount < 1000) {
                showAlert('Minimum loan amount is ₹1,000', 'danger');
                return;
            }

            if (formData.amount > 1000000) {
                showAlert('Maximum loan amount is ₹1,000,000', 'danger');
                return;
            }

            // Submit loan application
            fetch('/api/loans/apply', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            })
            .then(response => response.text())
            .then(data => {
                if (data.includes('successfully')) {
                    showAlert('Loan application submitted successfully! You can track its status in the Loans section.', 'success');
                    document.getElementById('loanForm').reset();

                    // Redirect to loans page after 3 seconds
                    setTimeout(() => {
                        window.location.href = '/loans';
                    }, 3000);
                } else {
                    showAlert('Loan application failed: ' + data, 'danger');
                }
            })
            .catch(error => {
                showAlert('Loan application failed: ' + error.message, 'danger');
            });
        });

        // Check if user is logged in
        fetch('/api/auth/current')
            .then(response => {
                if (!response.ok) {
                    window.location.href = '/login';
                }
                return response.json();
            })
            .then(user => {
                if (user.role !== 'CUSTOMER') {
                    showAlert('Only customers can apply for loans. Admins can view loan applications in the Loans section.', 'danger');
                    setTimeout(() => {
                        window.location.href = '/loans';
                    }, 3000);
                }
            })
            .catch(() => {
                window.location.href = '/login';
            });
    </script>
</body>
</html>
