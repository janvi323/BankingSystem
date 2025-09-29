<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Registration - Banking System</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Arial', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        .container {
            background: white;
            border-radius: 15px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            padding: 40px;
            max-width: 600px;
            width: 100%;
        }
        h2 {
            text-align: center;
            color: #333;
            margin-bottom: 30px;
            font-size: 28px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-weight: bold;
        }
        input[type="text"], input[type="email"], input[type="password"],
        input[type="number"], select, textarea {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s ease;
        }
        input:focus, select:focus, textarea:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        .radio-group {
            display: flex;
            gap: 20px;
            margin-top: 10px;
        }
        .radio-option {
            display: flex;
            align-items: center;
            gap: 8px;
            cursor: pointer;
            padding: 10px;
            border: 2px solid #ddd;
            border-radius: 8px;
            transition: all 0.3s ease;
        }
        .radio-option:hover {
            background-color: #f8f9ff;
            border-color: #667eea;
        }
        .radio-option.selected {
            background-color: #667eea;
            color: white;
            border-color: #667eea;
        }
        .loan-section {
            background-color: #f8f9ff;
            border: 2px solid #e1e8ff;
            border-radius: 12px;
            padding: 25px;
            margin: 20px 0;
            transition: all 0.3s ease;
        }
        .loan-section.hidden {
            display: none;
        }
        .no-loan-info {
            background-color: #e8f5e8;
            border: 2px solid #c3e6c3;
            border-radius: 12px;
            padding: 20px;
            margin: 20px 0;
            text-align: center;
        }
        .no-loan-info.hidden {
            display: none;
        }
        .credit-score-preview {
            background: linear-gradient(135deg, #4CAF50, #45a049);
            color: white;
            padding: 15px;
            border-radius: 8px;
            text-align: center;
            font-weight: bold;
            margin-top: 10px;
        }
        .btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px 30px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            width: 100%;
            transition: transform 0.2s ease;
        }
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.1);
        }
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 8px;
            font-weight: bold;
        }
        .alert-success {
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
        }
        .alert-error {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
        }
        .back-link {
            text-align: center;
            margin-top: 20px;
        }
        .back-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: bold;
        }
        .back-link a:hover {
            text-decoration: underline;
        }
        .section-title {
            color: #667eea;
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 15px;
            border-bottom: 2px solid #e1e8ff;
            padding-bottom: 8px;
        }
        .hidden {
            display: none;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>🏦 Customer Registration</h2>

        <!-- Display success or error messages -->
        <% if (request.getAttribute("message") != null) { %>
            <div class="alert alert-success">
                <%= request.getAttribute("message") %>
            </div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form action="/perform_register" method="post" id="registrationForm" onsubmit="return validateForm()">
            <!-- Basic Information -->
            <div class="section-title">📝 Basic Information</div>

            <div class="form-group">
                <label for="name">Full Name:</label>
                <input type="text" id="name" name="name" required>
            </div>

            <div class="form-group">
                <label for="email">Email Address:</label>
                <input type="email" id="email" name="email" required>
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required minlength="6">
            </div>

            <div class="form-group">
                <label for="phone">Phone Number:</label>
                <input type="text" id="phone" name="phone" required pattern="[0-9]{10}">
            </div>

            <div class="form-group">
                <label for="address">Address:</label>
                <textarea id="address" name="address" rows="3" required></textarea>
            </div>

            <!-- Role Selection -->
            <div class="form-group">
                <label>Account Type:</label>
                <div class="radio-group">
                    <div class="radio-option" onclick="selectRole('CUSTOMER')">
                        <input type="radio" id="customer" name="role" value="CUSTOMER" required>
                        <label for="customer">👤 Customer</label>
                    </div>
                    <div class="radio-option" onclick="selectRole('ADMIN')">
                        <input type="radio" id="admin" name="role" value="ADMIN" required>
                        <label for="admin">🔐 Admin</label>
                    </div>
                </div>
            </div>

            <!-- Customer-specific fields -->
            <div id="customerFields" class="hidden">
                <div class="section-title">💰 Financial Information</div>

                <div class="form-group">
                    <label for="income">Monthly Income (₹):</label>
                    <input type="number" id="income" name="income" min="1" step="0.01" onchange="updateCreditPreview()">
                </div>

                <!-- Loan Status Selection -->
                <div class="form-group">
                    <label>Do you currently have any loans or need a loan?</label>
                    <div class="radio-group">
                        <div class="radio-option" onclick="selectLoanStatus('yes')">
                            <input type="radio" id="hasLoan" name="loanStatus" value="yes">
                            <label for="hasLoan">📊 Yes, I have/need a loan</label>
                        </div>
                        <div class="radio-option" onclick="selectLoanStatus('no')">
                            <input type="radio" id="noLoan" name="loanStatus" value="no">
                            <label for="noLoan">✅ No loans, debt-free</label>
                        </div>
                    </div>
                </div>

                <!-- Loan Details Section -->
                <div id="loanSection" class="loan-section hidden">
                    <div class="section-title">🏠 Loan Details</div>

                    <div class="form-group">
                        <label for="loanAmount">Loan Amount (₹):</label>
                        <input type="number" id="loanAmount" name="loanAmount" min="1" step="0.01" onchange="updateCreditPreview()">
                    </div>

                    <div class="form-group">
                        <label for="interestRate">Interest Rate (% per annum):</label>
                        <input type="number" id="interestRate" name="interestRate" min="0" step="0.01" onchange="updateCreditPreview()">
                    </div>

                    <div class="form-group">
                        <label for="tenure">Loan Tenure (months):</label>
                        <input type="number" id="tenure" name="tenure" min="1" step="1" onchange="updateCreditPreview()">
                    </div>
                </div>

                <!-- No Loan Information -->
                <div id="noLoanSection" class="no-loan-info hidden">
                    <h3>🌟 Excellent Financial Profile!</h3>
                    <p>Since you have no loans and are debt-free, you'll receive an excellent credit score and premium banking benefits!</p>
                    <div class="credit-score-preview">
                        🎉 Expected Credit Score: 800+ (Excellent)
                    </div>
                    <!-- Hidden fields for no-loan customers that WON'T conflict -->
                    <input type="hidden" id="hiddenLoanAmount" value="0" disabled>
                    <input type="hidden" id="hiddenInterestRate" value="0" disabled>
                    <input type="hidden" id="hiddenTenure" value="1" disabled>
                </div>

                <!-- Credit Preview -->
                <div id="creditPreview" class="hidden">
                    <div class="section-title">📊 Credit Assessment Preview</div>
                    <div id="creditInfo"></div>
                </div>
            </div>

            <button type="submit" class="btn">🚀 Register Account</button>
        </form>

        <div class="back-link">
            <a href="/login">← Back to Login</a>
        </div>
    </div>

    <script>
        function selectRole(role) {
            // Update radio button selection
            document.getElementById(role.toLowerCase()).checked = true;

            // Update visual selection
            document.querySelectorAll('.radio-option').forEach(option => {
                option.classList.remove('selected');
            });
            event.currentTarget.classList.add('selected');

            // Show/hide customer fields
            const customerFields = document.getElementById('customerFields');
            if (role === 'CUSTOMER') {
                customerFields.classList.remove('hidden');
            } else {
                customerFields.classList.add('hidden');
            }
        }

        function selectLoanStatus(status) {
            // Update radio button selection
            if (status === 'yes') {
                document.getElementById('hasLoan').checked = true;
            } else {
                document.getElementById('noLoan').checked = true;
            }

            // Update visual selection
            document.querySelectorAll('.radio-option').forEach(option => {
                option.classList.remove('selected');
            });
            event.currentTarget.classList.add('selected');

            // Show/hide appropriate sections
            const loanSection = document.getElementById('loanSection');
            const noLoanSection = document.getElementById('noLoanSection');

            if (status === 'yes') {
                loanSection.classList.remove('hidden');
                noLoanSection.classList.add('hidden');
                // Make loan fields required and clear hidden fields
                document.getElementById('loanAmount').required = true;
                document.getElementById('interestRate').required = true;
                document.getElementById('tenure').required = true;
                // Disable hidden fields to prevent conflicts
                document.getElementById('hiddenLoanAmount').disabled = true;
                document.getElementById('hiddenInterestRate').disabled = true;
                document.getElementById('hiddenTenure').disabled = true;
            } else {
                loanSection.classList.add('hidden');
                noLoanSection.classList.remove('hidden');
                // Remove required from visible loan fields and clear their values
                const loanAmountField = document.getElementById('loanAmount');
                const interestRateField = document.getElementById('interestRate');
                const tenureField = document.getElementById('tenure');

                loanAmountField.required = false;
                interestRateField.required = false;
                tenureField.required = false;

                // Clear visible field values to prevent conflicts
                loanAmountField.value = '';
                interestRateField.value = '';
                tenureField.value = '';

                // Enable hidden fields for debt-free customers
                document.getElementById('hiddenLoanAmount').disabled = false;
                document.getElementById('hiddenInterestRate').disabled = false;
                document.getElementById('hiddenTenure').disabled = false;
            }
        }

        function updateCreditPreview() {
            const income = parseFloat(document.getElementById('income').value) || 0;
            const loanAmount = parseFloat(document.getElementById('loanAmount').value) || 0;
            const interestRate = parseFloat(document.getElementById('interestRate').value) || 0;
            const tenure = parseInt(document.getElementById('tenure').value) || 1;

            if (income > 0 && loanAmount > 0) {
                // Calculate EMI
                const monthlyRate = interestRate / 12.0 / 100.0;
                const emi = monthlyRate === 0 ? (loanAmount / tenure) :
                    (loanAmount * monthlyRate * Math.pow(1 + monthlyRate, tenure)) /
                    (Math.pow(1 + monthlyRate, tenure) - 1);

                // Calculate DTI
                const dti = (emi / income) * 100;

                // Simple credit score estimation
                let creditScore = 750; // Base score
                if (dti > 50) creditScore -= 150;
                else if (dti > 30) creditScore -= 100;
                else if (dti > 20) creditScore -= 50;

                let grade = "Good";
                if (creditScore >= 750) grade = "Excellent";
                else if (creditScore >= 650) grade = "Good";
                else if (creditScore >= 550) grade = "Fair";
                else grade = "Poor";

                document.getElementById('creditInfo').innerHTML = `
                    <div style="background: #f8f9ff; padding: 15px; border-radius: 8px;">
                        <p><strong>💰 Monthly EMI:</strong> ₹${emi.toFixed(2)}</p>
                        <p><strong>📊 Debt-to-Income Ratio:</strong> ${dti.toFixed(2)}%</p>
                        <p><strong>🎯 Expected Credit Score:</strong> ${creditScore} (${grade})</p>
                    </div>
                `;
                document.getElementById('creditPreview').classList.remove('hidden');
            }
        }

        function validateForm() {
            const selectedRole = document.querySelector('input[name="role"]:checked');

            // Only require loan status for customers, not for admins
            if (selectedRole && selectedRole.value === 'CUSTOMER') {
                const loanStatusSelected = document.querySelector('input[name="loanStatus"]:checked');
                if (!loanStatusSelected) {
                    alert('Please select your loan status.');
                    return false;
                }

                // If "No loans, debt-free" is selected, ensure loan fields are not required
                if (loanStatusSelected.value === 'no') {
                    document.getElementById('loanAmount').required = false;
                    document.getElementById('interestRate').required = false;
                    document.getElementById('tenure').required = false;
                }
            }

            return true;
        }
    </script>
</body>
</html>
