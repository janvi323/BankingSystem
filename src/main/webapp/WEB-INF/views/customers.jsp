<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DebtHues - Customers</title>
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
        .customers-table {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(220, 20, 60, 0.3);
            overflow: hidden;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #8B5CF6;
            color: white;
            font-weight: bold;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .spinner {
            text-align: center;
            margin: 20px;
            font-weight: bold;
            color: #8B5CF6;
        }
        .role-admin {
            background-color: #8B5CF6;
            color: white;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
        }
        .role-customer {
            background-color: #28a745;
            color: white;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
        }
        .access-denied {
            text-align: center;
            padding: 50px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(220, 20, 60, 0.3);
        }
        .access-denied h3 {
            color: #8B5CF6;
            margin-bottom: 20px;
        }
        .access-denied p {
            color: #666;
            margin-bottom: 20px;
        }
        .back-btn {
            background-color: #8B5CF6;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
        }
        .back-btn:hover {
            background-color: #7C3AED;
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
        <div class="page-header">
            <h2>Customer Management</h2>
            <p>View and manage all registered customers (Admin Only)</p>
        </div>

        <div id="accessDenied" class="access-denied" style="display:none;">
            <h3>Access Denied</h3>
            <p>You don't have permission to view customer information. This section is only available to administrators.</p>
            <a href="/dashboard" class="back-btn">Back to Dashboard</a>
        </div>

        <div class="customers-table" id="customersSection">
            <div class="spinner" id="loading">Loading customers...</div>
            <table id="customerTable" style="display:none;">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Address</th>
                    <th>Credit Score</th>
                    <th>Role</th>
                </tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>
    </div>

    <script>
        // Check if user is admin first
        fetch('/api/auth/current')
            .then(response => response.json())
            .then(user => {
                if (user.role !== 'ADMIN') {
                    // Hide the customers section and show access denied
                    document.getElementById('customersSection').style.display = 'none';
                    document.getElementById('accessDenied').style.display = 'block';
                } else {
                    // User is admin, load customers
                    loadCustomers();
                }
            })
            .catch(() => {
                document.getElementById('loading').textContent = 'Please login to view customers';
            });

        function loadCustomers() {
            fetch('/api/customers')
                .then(response => response.json())
                .then(customers => {
                    displayCustomers(customers);
                })
                .catch(() => {
                    document.getElementById('loading').textContent = 'Failed to load customers';
                });
        }

        function displayCustomers(customers) {
            const tableBody = document.getElementById('customerTable').querySelector('tbody');
            tableBody.innerHTML = '';

            if (customers.length === 0) {
                const tr = document.createElement('tr');
                tr.innerHTML = '<td colspan="7" style="text-align: center; padding: 40px; color: #666;">No customers found</td>';
                tableBody.appendChild(tr);
            } else {
                customers.forEach(customer => {
                    const tr = document.createElement('tr');
                    const roleClass = customer.role === 'ADMIN' ? 'role-admin' : 'role-customer';

                    tr.innerHTML = `
                        <td>${customer.id}</td>
                        <td>${customer.name}</td>
                        <td>${customer.email}</td>
                        <td>${customer.phone || 'N/A'}</td>
                        <td>${customer.address || 'N/A'}</td>
                        <td>${customer.creditScore || 'N/A'}</td>
                        <td><span class="${roleClass}">${customer.role}</span></td>
                    `;
                    tableBody.appendChild(tr);
                });
            }

            document.getElementById('loading').style.display = 'none';
            document.getElementById('customerTable').style.display = 'table';
        }
    </script>
</body>
</html>
