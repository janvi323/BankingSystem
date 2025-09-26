<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DebtHues - Dashboard</title>
    <link rel="stylesheet" href="/css/credit-score-odometer.css">
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
            max-width: 1200px;
            margin: 20px auto;
            padding: 0 20px;
        }
        .welcome-section {
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(220, 20, 60, 0.3);
            margin-bottom: 30px;
        }
        .welcome-section h2 {
            color: #8B5CF6;
            margin-bottom: 10px;
        }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: white;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(220, 20, 60, 0.3);
            text-align: center;
        }
        .stat-card h3 {
            color: #8B5CF6;
            font-size: 24px;
            margin-bottom: 10px;
        }
        .stat-card p {
            color: #666;
            font-size: 16px;
        }
        .quick-actions {
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(220, 20, 60, 0.3);
        }
        .quick-actions h2 {
            margin-bottom: 20px;
            color: #8B5CF6;
        }
        .action-buttons {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
        }
        .action-btn {
            background-color: #8B5CF6;
            color: white;
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 4px;
            font-weight: 500;
            transition: background-color 0.3s;
        }
        .action-btn:hover {
            background-color: #7C3AED;
        }
        .user-info {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(220, 20, 60, 0.3);
            margin-bottom: 20px;
        }
        .user-info h3 {
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
                <!-- Only show Customers link to admins -->
                <c:if test="${userRole == 'ADMIN'}">
                    <a href="/customers">Customers</a>
                </c:if>
                <a href="/loans">Loans</a>
                <!-- Only show Apply Loan link to customers -->
                <c:if test="${userRole == 'CUSTOMER'}">
                    <a href="/apply-loan">Apply Loan</a>
                </c:if>
                <form action="/perform_logout" method="post" style="display: inline;">
                    <button type="submit" style="background: none; border: none; color: white; cursor: pointer; font-size: 16px; font-weight: 500; padding: 8px 16px;">Logout</button>
                </form>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="user-info">
            <h3>Welcome Back, ${username}!</h3>
            <p>Hello ${username} (${userRole})</p>
        </div>

        <!-- Credit Score Odometer for Customers -->
        <c:if test="${userRole == 'CUSTOMER'}">
            <div id="credit-score-odometer"></div>
        </c:if>

        <div class="welcome-section">
            <h2>Welcome to DebtHues</h2>
            <p>Manage your loan applications and financial needs efficiently and securely.</p>
        </div>

        <div class="stats-grid">
            <c:if test="${userRole == 'ADMIN'}">
                <div class="stat-card">
                    <h3 id="totalCustomers">-</h3>
                    <p>Total Customers</p>
                </div>
                <div class="stat-card">
                    <h3 id="totalLoans">-</h3>
                    <p>Total Loan Applications</p>
                </div>
                <div class="stat-card">
                    <h3 id="pendingApprovals">-</h3>
                    <p>Pending Loan Approvals</p>
                </div>
            </c:if>
            <c:if test="${userRole == 'CUSTOMER'}">
                <div class="stat-card">
                    <h3 id="myLoans">-</h3>
                    <p>My Loans</p>
                </div>
                <div class="stat-card">
                    <h3 id="pendingLoans">-</h3>
                    <p>Pending Applications</p>
                </div>
                <div class="stat-card">
                    <h3 id="approvedLoans">-</h3>
                    <p>Approved Loans</p>
                </div>
            </c:if>
        </div>

        <div class="quick-actions">
            <c:if test="${userRole == 'ADMIN'}">
                <h2>Loan Approvals</h2>
                <div class="action-buttons">
                    <a href="/loans" class="action-btn">View Loan Applications</a>
                    <a href="/customers" class="action-btn">View Customer Details</a>
                </div>
            </c:if>
            <c:if test="${userRole == 'CUSTOMER'}">
                <h2>Quick Actions</h2>
                <div class="action-buttons">
                    <a href="/loans" class="action-btn">View My Loans</a>
                    <a href="/apply-loan" class="action-btn">Apply Loan</a>
                </div>
            </c:if>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Load statistics based on user role
            const userRole = '${userRole}';

            if (userRole === 'ADMIN') {
                loadAdminStats();
            } else if (userRole === 'CUSTOMER') {
                loadCustomerStats();
            }
        });

        function loadAdminStats() {
            // Load total customers
            fetch('/api/customers/count')
                .then(response => response.json())
                .then(count => {
                    document.getElementById('totalCustomers').textContent = count;
                })
                .catch(error => {
                    console.error('Error loading customer count:', error);
                    document.getElementById('totalCustomers').textContent = '0';
                });

            // Load total loan applications
            fetch('/api/loans/count')
                .then(response => response.json())
                .then(count => {
                    document.getElementById('totalLoans').textContent = count;
                })
                .catch(error => {
                    console.error('Error loading loan count:', error);
                    document.getElementById('totalLoans').textContent = '0';
                });

            // Load pending approvals
            fetch('/api/loans/pending/count')
                .then(response => response.json())
                .then(count => {
                    document.getElementById('pendingApprovals').textContent = count;
                })
                .catch(error => {
                    console.error('Error loading pending approvals:', error);
                    document.getElementById('pendingApprovals').textContent = '0';
                });
        }

        function loadCustomerStats() {
            // Load customer's own loan statistics
            fetch('/api/loans/my/count')
                .then(response => response.json())
                .then(count => {
                    document.getElementById('myLoans').textContent = count;
                })
                .catch(error => {
                    console.error('Error loading my loans count:', error);
                    document.getElementById('myLoans').textContent = '0';
                });

            // Load customer's pending applications
            fetch('/api/loans/my/pending/count')
                .then(response => response.json())
                .then(count => {
                    document.getElementById('pendingLoans').textContent = count;
                })
                .catch(error => {
                    console.error('Error loading pending loans:', error);
                    document.getElementById('pendingLoans').textContent = '0';
                });

            // Load customer's approved loans
            fetch('/api/loans/my/approved/count')
                .then(response => response.json())
                .then(count => {
                    document.getElementById('approvedLoans').textContent = count;
                })
                .catch(error => {
                    console.error('Error loading approved loans:', error);
                    document.getElementById('approvedLoans').textContent = '0';
                });
        }

        // Initialize Credit Score Odometer for customers
        function initializeCreditScore() {
            const userRole = '${userRole}';
            const userId = '${userId}'; // Assuming you have userId in the session
            
            if (userRole === 'CUSTOMER' && userId) {
                // Initialize the credit score odometer
                const odometer = new CreditScoreOdometer('credit-score-odometer', {
                    showDetails: true,
                    animationDuration: 2500
                });
                
                // Fetch and display the credit score
                odometer.fetchAndDisplayScore(userId);
            }
        }

        // Call initialization when DOM is ready
        document.addEventListener('DOMContentLoaded', function() {
            initializeCreditScore();
        });
    </script>
    <script src="/js/credit-score-odometer.js"></script>
</body>
</html>
