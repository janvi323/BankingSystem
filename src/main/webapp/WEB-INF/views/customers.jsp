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
            background-color: #ffc107; /* Yellow navbar */
            padding: 1rem 0;
            color: #000000; /* Black text on navbar */
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
            color: #000000; /* Black text */
            font-weight: bold;
        }
        .navbar-links a {
            color: #000000; /* Black text on yellow navbar */
            text-decoration: none;
            margin: 0 15px;
            padding: 8px 16px;
            border-radius: 4px;
            transition: background-color 0.3s;
            font-weight: 500;
        }
        .navbar-links a:hover {
            background-color: rgba(0,0,0,0.1); /* Darker overlay on hover */
        }
        .container {
            max-width: 1200px;
            margin: 20px auto;
            padding: 0 20px;
        }
        .customers-container {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(255, 193, 7, 0.2); /* Yellow shadow */
        }
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 1px solid #eee;
        }
        .page-header h2 {
            color: #ffc107; /* Yellow heading */
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
            font-weight: bold;
        }
        .btn-primary {
            background-color: #ffc107; /* Yellow button */
            color: #000000; /* Black text */
        }
        .btn-primary:hover {
            background-color: #ffb300;
        }
        .btn-success {
            background-color: #28a745;
            color: white;
        }
        .btn-success:hover {
            background-color: #218838;
        }
        .btn-danger {
            background-color: #dc3545;
            color: white;
        }
        .btn-danger:hover {
            background-color: #c82333;
        }
        .search-box {
            margin-bottom: 20px;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            width: 300px;
            font-size: 16px;
            color: #000000; /* Black text */
        }
        .customers-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .customers-table th,
        .customers-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
            color: #000000; /* Black text */
        }
        .customers-table th {
            background-color: #fff3cd; /* Light yellow background */
            font-weight: bold;
            color: #000000; /* Black table headers */
        }
        .customers-table tr:hover {
            background-color: #fff3cd; /* Light yellow hover */
        }
        .loading {
            text-align: center;
            padding: 40px;
            color: #000000; /* Black text */
        }
        .no-customers {
            text-align: center;
            padding: 40px;
            color: #000000; /* Black text */
        }
        .customer-actions {
            display: flex;
            gap: 10px;
        }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background-color: #fff3cd; /* Light yellow background */
            padding: 20px;
            border-radius: 8px;
            text-align: center;
        }
        .stat-number {
            font-size: 32px;
            font-weight: bold;
            color: #ffc107; /* Yellow stats */
        }
        .stat-label {
            color: #000000; /* Black text */
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <h1>DebtHues</h1>
            <div class="navbar-links">
                <a href="/dashboard">Dashboard</a>
                <a href="/apply-loan">Apply Loan</a>
                <a href="/customers">Customers</a>
                <a href="/logout">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="customers-container">
            <div class="page-header">
                <h2>Customer Management</h2>
                <button class="btn btn-success" onclick="addNewCustomer()">+ Add New Customer</button>
            </div>

            <!-- Statistics -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-number" id="totalCustomers">-</div>
                    <div class="stat-label">Total Customers</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="activeLoans">-</div>
                    <div class="stat-label">Active Loans</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="totalLoanAmount">-</div>
                    <div class="stat-label">Total Loan Amount</div>
                </div>
            </div>

            <!-- Search -->
            <input type="text" class="search-box" id="searchBox" placeholder="Search customers by name or email...">

            <!-- Customers Table -->
            <div id="customersTableContainer">
                <div class="loading">Loading customers...</div>
            </div>
        </div>
    </div>

    <script>
        let customers = [];
        let filteredCustomers = [];

        // Load customers when page loads
        document.addEventListener('DOMContentLoaded', function() {
            loadCustomers();
            loadStatistics();

            // Setup search
            document.getElementById('searchBox').addEventListener('input', function(e) {
                filterCustomers(e.target.value);
            });
        });

        function loadCustomers() {
            fetch('/api/customers')
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else if (response.status === 403) {
                        throw new Error('Access denied. Admin privileges required.');
                    } else {
                        throw new Error('Failed to load customers');
                    }
                })
                .then(data => {
                    customers = data;
                    filteredCustomers = customers;
                    displayCustomers(customers);
                    updateStatistics();
                })
                .catch(error => {
                    console.error('Error:', error);
                    document.getElementById('customersTableContainer').innerHTML =
                        `<div class="no-customers">${error.message}</div>`;
                });
        }

        function loadStatistics() {
            // Load loan statistics
            fetch('/api/loans')
                .then(response => response.json())
                .then(loans => {
                    const activeLoans = loans.filter(loan => loan.status === 'APPROVED').length;
                    const totalAmount = loans.reduce((sum, loan) => sum + (loan.amount || 0), 0);

                    document.getElementById('activeLoans').textContent = activeLoans;
                    document.getElementById('totalLoanAmount').textContent = totalAmount.toLocaleString();
                })
                .catch(error => {
                    console.error('Error loading loan statistics:', error);
                });
        }

        function updateStatistics() {
            document.getElementById('totalCustomers').textContent = customers.length;
        }

        function displayCustomers(customersData) {
            if (customersData.length === 0) {
                document.getElementById('customersTableContainer').innerHTML =
                    '<div class="no-customers">No customers found</div>';
                return;
            }

            let tableHTML = '<table class="customers-table">' +
                '<thead>' +
                '<tr>' +
                '<th>ID</th>' +
                '<th>Name</th>' +
                '<th>Email</th>' +
                '<th>Phone</th>' +
                '<th>Address</th>' +
                '<th>Actions</th>' +
                '</tr>' +
                '</thead>' +
                '<tbody>';

            customersData.forEach(function(customer) {
                tableHTML += '<tr>' +
                    '<td>' + customer.id + '</td>' +
                    '<td>' + (customer.name || 'N/A') + '</td>' +
                    '<td>' + (customer.email || 'N/A') + '</td>' +
                    '<td>' + (customer.phone || 'N/A') + '</td>' +
                    '<td>' + (customer.address || 'N/A') + '</td>' +
                    '<td class="customer-actions">' +
                    '<button class="btn btn-primary" onclick="viewCustomer(' + customer.id + ')">View</button>' +
                    '<button class="btn btn-danger" onclick="deleteCustomer(' + customer.id + ')">Delete</button>' +
                    '</td>' +
                    '</tr>';
            });

            tableHTML += '</tbody></table>';

            document.getElementById('customersTableContainer').innerHTML = tableHTML;
        }

        function filterCustomers(searchTerm) {
            if (!searchTerm) {
                filteredCustomers = customers;
            } else {
                filteredCustomers = customers.filter(customer =>
                    (customer.name && customer.name.toLowerCase().includes(searchTerm.toLowerCase())) ||
                    (customer.email && customer.email.toLowerCase().includes(searchTerm.toLowerCase()))
                );
            }
            displayCustomers(filteredCustomers);
        }

        function addNewCustomer() {
            window.location.href = '/register';
        }

        function viewCustomer(customerId) {
            fetch(`/api/customers/${customerId}`)
                .then(response => response.json())
                .then(customer => {
                    alert(`Customer Details:\n\nID: ${customer.id}\nName: ${customer.name}\nEmail: ${customer.email}\nPhone: ${customer.phone}\nAddress: ${customer.address}`);
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error loading customer details');
                });
        }

        function deleteCustomer(customerId) {
            if (confirm('Are you sure you want to delete this customer?')) {
                fetch(`/api/customers/${customerId}`, {
                    method: 'DELETE'
                })
                .then(response => {
                    if (response.ok) {
                        alert('Customer deleted successfully');
                        loadCustomers(); // Reload the list
                    } else {
                        alert('Error deleting customer');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error deleting customer');
                });
            }
        }
    </script>
</body>
</html>
