<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Banking System - Dashboard</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: Arial, sans-serif;
            background-color: #f8f9fa;
        }
        .navbar {
            background-color: #007bff;
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
        }
        .navbar-links a {
            color: white;
            text-decoration: none;
            margin: 0 15px;
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
        .welcome-section {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 30px;
            text-align: center;
        }
        .cards-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .card {
            background-color: white;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            text-align: center;
            transition: transform 0.3s;
        }
        .card:hover {
            transform: translateY(-5px);
        }
        .card h3 {
            color: #333;
            margin-bottom: 10px;
        }
        .card p {
            color: #666;
            margin-bottom: 20px;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s;
            border: none;
            cursor: pointer;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .btn-success {
            background-color: #28a745;
        }
        .btn-success:hover {
            background-color: #218838;
        }
        .btn-warning {
            background-color: #ffc107;
        }
        .btn-warning:hover {
            background-color: #e0a800;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <h1>Banking System</h1>
            <div class="navbar-links">
                <a href="/dashboard">Dashboard</a>
                <a href="/apply-loan">Apply Loan</a>
                <a href="/customers">Customers</a>
                <a href="/logout">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="welcome-section">
            <h2>Welcome to Your Banking Dashboard</h2>
            <p>Manage your banking operations from this central hub</p>
        </div>

        <div class="cards-grid">
            <div class="card">
                <h3>Apply for Loan</h3>
                <p>Submit a new loan application with automatic credit score verification</p>
                <a href="/apply-loan" class="btn btn-success">Apply Now</a>
            </div>

            <div class="card">
                <h3>Customer Management</h3>
                <p>View and manage customer accounts and information</p>
                <a href="/customers" class="btn">View Customers</a>
            </div>

            <div class="card">
                <h3>Loan Status</h3>
                <p>Check the status of all loan applications</p>
                <button class="btn btn-warning" onclick="viewLoans()">View Loans</button>
            </div>
        </div>

        <!-- Recent Activity Section -->
        <div class="card">
            <h3>Recent Activity</h3>
            <div id="recentActivity">
                <p>Loading recent activities...</p>
            </div>
        </div>
    </div>

    <script>
        // Load recent activities
        function loadRecentActivity() {
            fetch('/api/loans')
                .then(response => response.json())
                .then(data => {
                    const activityDiv = document.getElementById('recentActivity');
                    if (data.length > 0) {
                        activityDiv.innerHTML = data.slice(0, 5).map(loan =>
                            `<p>Loan #${loan.id} - Status: <strong>${loan.status}</strong> - Amount: ₹${loan.amount}</p>`
                        ).join('');
                    } else {
                        activityDiv.innerHTML = '<p>No recent loan activities</p>';
                    }
                })
                .catch(error => {
                    console.error('Error loading activities:', error);
                    document.getElementById('recentActivity').innerHTML = '<p>Unable to load recent activities</p>';
                });
        }

        function viewLoans() {
            fetch('/api/loans')
                .then(response => response.json())
                .then(data => {
                    let loansInfo = 'Recent Loans:\n\n';
                    data.forEach(loan => {
                        loansInfo += `Loan #${loan.id}\nAmount: ₹${loan.amount}\nStatus: ${loan.status}\nCustomer ID: ${loan.customerId}\n\n`;
                    });

                    if (data.length === 0) {
                        loansInfo = 'No loans found in the system.';
                    }

                    alert(loansInfo);
                })
                .catch(error => {
                    console.error('Error loading loans:', error);
                    alert('Error loading loans. Check console for details.');
                });
        }

        // Load recent activity when page loads
        document.addEventListener('DOMContentLoaded', loadRecentActivity);
    </script>

</body>
</html>
