<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DebtHues - Customer Profile</title>
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
            font-weight: bold;
        }
        .navbar-links {
            display: flex;
            gap: 15px;
        }
        .navbar-links a {
            color: white;
            text-decoration: none;
            font-weight: 500;
            padding: 8px 16px;
            border-radius: 4px;
            transition: background-color 0.3s;
        }
        .navbar-links a:hover {
            background-color: rgba(255,255,255,0.2);
        }
        .container {
            max-width: 1200px;
            margin: 20px auto;
            padding: 0 20px;
        }
        .page-header {
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(220, 20, 60, 0.3);
            margin-bottom: 20px;
        }
        .page-header h2 {
            color: #8B5CF6;
            margin-bottom: 10px;
        }
        .back-link {
            color: #8B5CF6;
            text-decoration: none;
            font-weight: bold;
            display: inline-block;
            margin-bottom: 20px;
        }
        .back-link:hover {
            text-decoration: underline;
        }
        .profile-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(220, 20, 60, 0.3);
            overflow: hidden;
            margin-bottom: 20px;
        }
        .profile-header {
            background-color: #8B5CF6;
            color: white;
            padding: 20px;
            text-align: center;
        }
        .profile-avatar {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            background-color: rgba(255,255,255,0.2);
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 15px;
            font-size: 36px;
            font-weight: bold;
        }
        .profile-details {
            padding: 30px;
        }
        .detail-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 0;
            border-bottom: 1px solid #eee;
        }
        .detail-row:last-child {
            border-bottom: none;
        }
        .detail-label {
            font-weight: bold;
            color: #666;
            min-width: 150px;
        }
        .detail-value {
            flex: 1;
            text-align: right;
            color: #333;
        }
        .role-admin {
            background-color: #8B5CF6;
            color: white;
            padding: 4px 12px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
        }
        .role-customer {
            background-color: #28a745;
            color: white;
            padding: 4px 12px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
        }
        .credit-score {
            font-size: 18px;
            font-weight: bold;
        }
        .credit-excellent {
            color: #28a745;
        }
        .credit-good {
            color: #007bff;
        }
        .credit-fair {
            color: #ffc107;
        }
        .credit-poor {
            color: #dc3545;
        }
        .spinner {
            text-align: center;
            margin: 40px;
            font-weight: bold;
            color: #8B5CF6;
        }
        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            padding: 20px;
            border-radius: 8px;
            margin: 20px 0;
            text-align: center;
        }
        .loans-section {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(220, 20, 60, 0.3);
            overflow: hidden;
        }
        .loans-header {
            background-color: #f8f9fa;
            padding: 20px;
            border-bottom: 1px solid #eee;
        }
        .loans-header h3 {
            color: #8B5CF6;
            margin: 0;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <h1>DebtHues</h1>
            <div class="navbar-links">
                <a href="/dashboard">Dashboard</a>
                <% if ((Boolean) request.getAttribute("isAdmin")) { %>
                    <a href="/customers">Customers</a>
                    <a href="/admin-loans">Admin Loans</a>
                <% } else { %>
                    <a href="/loans">My Loans</a>
                    <a href="/apply-loan">Apply Loan</a>
                <% } %>
                <form action="/perform_logout" method="post" style="display: inline;">
                    <button type="submit" style="background: none; border: none; color: white; cursor: pointer; font-size: 16px; font-weight: 500; padding: 8px 16px;">Logout</button>
                </form>
            </div>
        </div>
    </nav>

    <div class="container">
        <% if ((Boolean) request.getAttribute("isAdmin")) { %>
            <a href="/customers" class="back-link">← Back to Customers</a>
        <% } else { %>
            <a href="/dashboard" class="back-link">← Back to Dashboard</a>
        <% } %>
        
        <div class="page-header">
            <h2>Customer Profile</h2>
            <p>Detailed customer information</p>
        </div>

        <div id="loading" class="spinner">Loading customer profile...</div>
        <div id="errorMessage" class="error-message" style="display: none;"></div>
        
        <div id="profileContent" style="display: none;">
            <div class="profile-card">
                <div class="profile-header">
                    <div class="profile-avatar" id="profileAvatar"></div>
                    <h3 id="customerName">Customer Name</h3>
                    <p id="customerEmail">customer@example.com</p>
                </div>
                
                <div class="profile-details">
                    <div class="detail-row">
                        <span class="detail-label">Customer ID:</span>
                        <span class="detail-value" id="customerId"></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Name:</span>
                        <span class="detail-value" id="customerNameDetail"></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Email:</span>
                        <span class="detail-value" id="customerEmailDetail"></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Phone:</span>
                        <span class="detail-value" id="customerPhone"></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Address:</span>
                        <span class="detail-value" id="customerAddress"></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Credit Score:</span>
                        <span class="detail-value credit-score" id="customerCreditScore"></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Role:</span>
                        <span class="detail-value" id="customerRole"></span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        const customerId = '<%= request.getAttribute("customerId") %>';
        
        document.addEventListener('DOMContentLoaded', function() {
            loadCustomerProfile();
        });

        function loadCustomerProfile() {
            console.log('Loading customer profile for ID:', customerId);
            
            fetch(`/api/customers/${customerId}`)
                .then(response => {
                    console.log('Response status:', response.status);
                    if (response.ok) {
                        return response.json();
                    } else if (response.status === 403) {
                        throw new Error('Access denied. You don\'t have permission to view this profile.');
                    } else if (response.status === 404) {
                        throw new Error('Customer not found.');
                    } else if (response.status === 401) {
                        throw new Error('Please log in to view this profile.');
                    } else {
                        throw new Error('Failed to load customer profile - Status: ' + response.status);
                    }
                })
                .then(customer => {
                    console.log('Customer data received:', customer);
                    displayCustomerProfile(customer);
                })
                .catch((error) => {
                    console.error('Error loading customer profile:', error);
                    showError(error.message);
                });
        }

        function displayCustomerProfile(customer) {
            // Update profile header
            const name = customer.name || 'N/A';
            const email = customer.email || 'N/A';
            
            document.getElementById('profileAvatar').textContent = name.charAt(0).toUpperCase();
            document.getElementById('customerName').textContent = name;
            document.getElementById('customerEmail').textContent = email;
            
            // Update profile details
            document.getElementById('customerId').textContent = customer.id || 'N/A';
            document.getElementById('customerNameDetail').textContent = name;
            document.getElementById('customerEmailDetail').textContent = email;
            document.getElementById('customerPhone').textContent = customer.phone || 'N/A';
            document.getElementById('customerAddress').textContent = customer.address || 'N/A';
            
            // Handle credit score with color coding
            const creditScore = customer.creditScore;
            const creditScoreElement = document.getElementById('customerCreditScore');
            if (creditScore !== null && creditScore !== undefined) {
                creditScoreElement.textContent = creditScore;
                
                // Add appropriate color class based on credit score
                if (creditScore >= 750) {
                    creditScoreElement.className = 'detail-value credit-score credit-excellent';
                } else if (creditScore >= 700) {
                    creditScoreElement.className = 'detail-value credit-score credit-good';
                } else if (creditScore >= 650) {
                    creditScoreElement.className = 'detail-value credit-score credit-fair';
                } else {
                    creditScoreElement.className = 'detail-value credit-score credit-poor';
                }
            } else {
                creditScoreElement.textContent = 'N/A';
                creditScoreElement.className = 'detail-value credit-score';
            }
            
            // Handle role with styling
            const role = customer.role || 'N/A';
            const roleElement = document.getElementById('customerRole');
            if (role === 'ADMIN') {
                roleElement.innerHTML = '<span class="role-admin">' + role + '</span>';
            } else if (role === 'CUSTOMER') {
                roleElement.innerHTML = '<span class="role-customer">' + role + '</span>';
            } else {
                roleElement.textContent = role;
            }
            
            // Hide loading and show content
            document.getElementById('loading').style.display = 'none';
            document.getElementById('profileContent').style.display = 'block';
        }

        function showError(message) {
            document.getElementById('loading').style.display = 'none';
            document.getElementById('errorMessage').textContent = message;
            document.getElementById('errorMessage').style.display = 'block';
        }
    </script>
</body>
</html>