<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DebtHues - Dashboard</title>
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
        <div class="user-info">
            <h3 id="welcomeTitle">Welcome Back!</h3>
            <p id="userInfo">Loading user information...</p>
        </div>

        <div class="welcome-section">
            <h2>Welcome to DebtHues</h2>
            <p>Manage your loan applications and financial needs efficiently and securely.</p>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <h3 id="totalCustomers">-</h3>
                <p>Total Customers</p>
            </div>
            <div class="stat-card">
                <h3 id="totalLoans">-</h3>
                <p>Total Loan Applications</p>
            </div>
            <div class="stat-card">
                <h3 id="approvedLoans">-</h3>
                <p>Approved Loans</p>
            </div>
            <div class="stat-card">
                <h3 id="pendingLoans">-</h3>
                <p>Pending Approvals</p>
            </div>
        </div>

        <div class="quick-actions">
            <h2>Quick Actions</h2>
            <div class="action-buttons" id="actionButtons">
                <a href="/apply-loan" class="action-btn">Apply for Loan</a>
                <a href="/loans" class="action-btn">View My Loans</a>
            </div>
        </div>
    </div>

    <script>
        let currentUser = null;

        // Listen for loan status updates from other windows/tabs
        window.addEventListener('loanStatusUpdated', function(event) {
            console.log('Loan status updated, refreshing dashboard statistics...');
            if (currentUser && currentUser.role === 'ADMIN') {
                loadAdminStatistics();
            } else if (currentUser) {
                loadCustomerStatistics();
            }
        });

        // Listen for messages from popup windows or iframes
        window.addEventListener('message', function(event) {
            if (event.data && event.data.type === 'loanStatusUpdated') {
                console.log('Received loan update message, refreshing statistics...');
                if (currentUser && currentUser.role === 'ADMIN') {
                    loadAdminStatistics();
                } else if (currentUser) {
                    loadCustomerStatistics();
                }
            }
        });

        // Get current user info
        fetch('/api/auth/current')
            .then(response => response.json())
            .then(user => {
                currentUser = user;
                if (user && user.name) {
                    // Update welcome message with user name and role
                    document.getElementById('welcomeTitle').textContent = 'Welcome Back, ' + user.name + '!';
                    document.getElementById('userInfo').textContent = 'You are logged in as ' + (user.role === 'ADMIN' ? 'an Administrator' : 'a Customer');

                    // Show different actions based on role
                    const actionButtons = document.getElementById('actionButtons');
                    if (user.role === 'ADMIN') {
                        actionButtons.innerHTML =
                            '<a href="/customers" class="action-btn">Manage Customers</a>' +
                            '<a href="/loans" class="action-btn">Manage All Loans</a>' +
                            '<a href="/admin-loans" class="action-btn">Loan Approvals</a>';
                        // Load admin statistics
                        loadAdminStatistics();
                        // Set up auto-refresh for admin dashboard every 30 seconds
                        setInterval(loadAdminStatistics, 30000);
                    } else {
                        actionButtons.innerHTML =
                            '<a href="/apply-loan" class="action-btn">Apply for Loan</a>' +
                            '<a href="/loans" class="action-btn">View My Loans</a>';
                        // Load customer statistics
                        loadCustomerStatistics();
                        // Set up auto-refresh for customer dashboard every 60 seconds
                        setInterval(loadCustomerStatistics, 60000);
                    }
                }
            })
            .catch(() => {
                document.getElementById('welcomeTitle').textContent = 'Welcome to DebtHues';
                document.getElementById('userInfo').textContent = 'Please login to access your dashboard';
            });

        // Load statistics for admin users
        function loadAdminStatistics() {
            Promise.all([
                fetch('/api/customers').then(r => r.ok ? r.json() : []).catch(() => []),
                fetch('/api/loans/all').then(r => r.ok ? r.json() : []).catch(() => [])
            ]).then(([customers, loans]) => {
                const approvedLoans = loans.filter(l => l.status === 'APPROVED').length;
                const pendingLoans = loans.filter(l => l.status === 'PENDING').length;

                // Update the dashboard with animation effect
                animateCounter('totalCustomers', customers.length || 0);
                animateCounter('totalLoans', loans.length || 0);
                animateCounter('approvedLoans', approvedLoans || 0);
                animateCounter('pendingLoans', pendingLoans || 0);

                console.log('Admin statistics updated:', {
                    customers: customers.length,
                    totalLoans: loans.length,
                    approved: approvedLoans,
                    pending: pendingLoans
                });
            }).catch(error => {
                console.error('Error loading admin statistics:', error);
                // Set default values on error
                document.getElementById('totalCustomers').textContent = '0';
                document.getElementById('totalLoans').textContent = '0';
                document.getElementById('approvedLoans').textContent = '0';
                document.getElementById('pendingLoans').textContent = '0';
            });
        }

        // Load statistics for customer users
        function loadCustomerStatistics() {
            fetch('/api/loans/my-loans')
                .then(r => r.ok ? r.json() : [])
                .then(loans => {
                    const approvedLoans = loans.filter(l => l.status === 'APPROVED').length;
                    const pendingLoans = loans.filter(l => l.status === 'PENDING').length;

                    document.getElementById('totalCustomers').textContent = 'N/A';
                    animateCounter('totalLoans', loans.length || 0);
                    animateCounter('approvedLoans', approvedLoans || 0);
                    animateCounter('pendingLoans', pendingLoans || 0);

                    // Update labels for customer view
                    document.querySelector('#totalCustomers').parentElement.querySelector('p').textContent = 'Not Available';
                    document.querySelector('#totalLoans').parentElement.querySelector('p').textContent = 'My Loan Applications';
                    document.querySelector('#approvedLoans').parentElement.querySelector('p').textContent = 'My Approved Loans';
                    document.querySelector('#pendingLoans').parentElement.querySelector('p').textContent = 'My Pending Loans';

                    console.log('Customer statistics updated:', {
                        totalLoans: loans.length,
                        approved: approvedLoans,
                        pending: pendingLoans
                    });
                })
                .catch(error => {
                    console.error('Error loading customer statistics:', error);
                    // Set default values on error
                    document.getElementById('totalCustomers').textContent = 'N/A';
                    document.getElementById('totalLoans').textContent = '0';
                    document.getElementById('approvedLoans').textContent = '0';
                    document.getElementById('pendingLoans').textContent = '0';
                });
        }

        // Animate counter changes for better user experience
        function animateCounter(elementId, newValue) {
            const element = document.getElementById(elementId);
            const currentValue = parseInt(element.textContent) || 0;

            if (currentValue !== newValue) {
                element.style.color = '#28a745'; // Highlight in green
                element.textContent = newValue;

                // Reset color after animation
                setTimeout(() => {
                    element.style.color = '#8B5CF6';
                }, 1000);
            } else {
                element.textContent = newValue;
            }
        }

        // Refresh statistics when the page becomes visible (user switches back to tab)
        document.addEventListener('visibilitychange', function() {
            if (!document.hidden && currentUser) {
                if (currentUser.role === 'ADMIN') {
                    loadAdminStatistics();
                } else {
                    loadCustomerStatistics();
                }
            }
        });
    </script>
</body>
</html>
