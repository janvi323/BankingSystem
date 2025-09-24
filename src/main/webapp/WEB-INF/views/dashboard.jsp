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
            <h3>Welcome Back, ${username}!</h3>
            <p>Hello ${username} (${userRole})</p>
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
        document.addEventListener('DOMContentLoaded', function() {
            // Get current user info and display actual username
            fetch('/api/auth/current')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Not authenticated');
                    }
                    return response.json();
                })
                .then(user => {
                    if (user && user.name) {
                        // Display the actual username from the session
                        const role = user.role || 'User';
                        document.getElementById('userInfo').textContent = `Hello ${user.name} (${role})`;
                        document.getElementById('welcomeHeader').textContent = `Welcome Back, ${user.name}!`;

                        // Show different actions based on role
                        const actionButtons = document.getElementById('actionButtons');
                        if (role === 'ADMIN') {
                            actionButtons.innerHTML = `
                                <a href="/customers" class="action-btn">Manage Customers</a>
                                <a href="/loans" class="action-btn">Manage All Loans</a>
                                <a href="/admin-loans" class="action-btn">Loan Approvals</a>
                            `;
                        } else {
                            actionButtons.innerHTML = `
                                <a href="/apply-loan" class="action-btn">Apply for Loan</a>
                                <a href="/loans" class="action-btn">View My Loans</a>
                            `;
                        }
                    } else {
                        // Fallback if no username available
                        document.getElementById('userInfo').textContent = 'Hello User - Welcome to DebtHues';
                        document.getElementById('welcomeHeader').textContent = 'Welcome Back!';
                    }
                })
                .catch(() => {
                    // Error fallback
                    document.getElementById('userInfo').textContent = 'Welcome to DebtHues';
                    document.getElementById('welcomeHeader').textContent = 'Welcome Back!';
                    window.location.href = '/login'; // Redirect to login if not authenticated
                });

            // Load statistics
            Promise.all([
                fetch('/api/customers').then(r => r.json()).catch(() => []),
                fetch('/api/loans/my-loans').then(r => r.json()).catch(() => [])
            ]).then(([customers, loans]) => {
                document.getElementById('totalCustomers').textContent = customers.length || 0;
                document.getElementById('totalLoans').textContent = loans.length || 0;
                document.getElementById('pendingLoans').textContent = loans.filter(l => l.status === 'PENDING').length || 0;
            });
        });
    </script>
</body>
</html>
