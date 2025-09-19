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
        .success-modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            z-index: 1000;
        }
        .success-content {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            padding: 40px;
            border-radius: 15px;
            text-align: center;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            max-width: 500px;
            width: 90%;
        }
        .success-icon {
            font-size: 60px;
            color: #28a745;
            margin-bottom: 20px;
            animation: bounceIn 0.6s ease-out;
        }
        .success-title {
            color: #8B5CF6;
            font-size: 24px;
            margin-bottom: 15px;
            font-weight: bold;
        }
        .success-message {
            color: #666;
            margin-bottom: 25px;
            line-height: 1.5;
        }
        .success-buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
        }
        .success-btn {
            padding: 12px 24px;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
        }
        .btn-primary {
            background-color: #8B5CF6;
            color: white;
        }
        .btn-primary:hover {
            background-color: #7C3AED;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #5a6268;
        }
        @keyframes bounceIn {
            0% { transform: scale(0.3); opacity: 0; }
            50% { transform: scale(1.05); }
            70% { transform: scale(0.9); }
            100% { transform: scale(1); opacity: 1; }
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

    <!-- Success Modal -->
    <div id="successModal" class="success-modal">
        <div class="success-content">
            <div class="success-icon">✓</div>
            <h3 class="success-title">Loan Application Submitted!</h3>
            <div class="success-message">
                <p>Congratulations! Your loan application has been successfully submitted to our system.</p>
                <p><strong>Application ID: <span id="loanId"></span></strong></p>
                <p>You can track your application status in the loans section.</p>
            </div>
            <div class="success-buttons">
                <a href="/dashboard" class="success-btn btn-primary">Go to Dashboard</a>
                <a href="/loans" class="success-btn btn-secondary">View My Loans</a>
            </div>
        </div>
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

        function showSuccessModal(loanId) {
            document.getElementById('loanId').textContent = loanId;
            document.getElementById('successModal').style.display = 'block';
        }

        function hideSuccessModal() {
            document.getElementById('successModal').style.display = 'none';
        }

        // Close modal when clicking outside
        document.getElementById('successModal').addEventListener('click', function(e) {
            if (e.target === this) {
                hideSuccessModal();
            }
        });

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

            // Disable submit button during processing
            const submitBtn = document.querySelector('.btn');
            submitBtn.disabled = true;
            submitBtn.textContent = 'Processing...';

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
                    // Extract loan ID from response
                    const loanIdMatch = data.match(/ID: (\d+)/);
                    const loanId = loanIdMatch ? loanIdMatch[1] : 'Generated';

                    // Reset form
                    document.getElementById('loanForm').reset();

                    // Show success modal
                    showSuccessModal(loanId);
                } else {
                    showAlert('Loan application failed: ' + data, 'danger');
                }
            })
            .catch(error => {
                showAlert('Loan application failed: ' + error.message, 'danger');
            })
            .finally(() => {
                // Re-enable submit button
                submitBtn.disabled = false;
                submitBtn.textContent = 'Submit Loan Application';
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
                if (user.role === 'ADMIN') {
                    showAlert('Admins cannot apply for loans. Please use the admin panel to manage loan applications.', 'danger');
                    setTimeout(() => {
                        window.location.href = '/dashboard';
                    }, 3000);
                } else if (user.role !== 'CUSTOMER') {
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
