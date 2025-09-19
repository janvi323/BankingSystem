function updateLoanStatus(loanId, status) {
            const comments = prompt(`Enter comments for ${status.toLowerCase()} this loan:`);
            if (comments === null) return;

            fetch(`/api/loans/${loanId}/status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    status: status,
                    comments: comments
                })
            })
            .then(response => response.text())
            .then(data => {
                alert(data);
                if (currentUser.role === 'ADMIN') {
                    loadAllLoans();
                } else {
                    loadMyLoans();
                }
            })
            .catch(error => {
                alert('Failed to update loan status: ' + error.message);
            });
        }
    </script>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DebtHues - Loans</title>
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
        .loans-table {
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
        .status-pending {
            background-color: #fff3cd;
            color: #856404;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
            border: 2px solid #ffeaa7;
        }
        .status-approved {
            background-color: #d4edda;
            color: #155724;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
            border: 2px solid #28a745;
        }
        .status-rejected {
            background-color: #f8d7da;
            color: #721c24;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
            border: 2px solid #dc3545;
        }
        .loan-amount {
            font-weight: bold;
            color: #8B5CF6;
            font-size: 16px;
        }
        .loan-row {
            transition: all 0.3s ease;
        }
        .loan-row:hover {
            background-color: #f8f4ff;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(139, 92, 246, 0.15);
        }
        .loan-summary {
            background: linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            text-align: center;
        }
        .summary-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 20px;
            margin-top: 15px;
        }
        .stat-item {
            background: rgba(255, 255, 255, 0.2);
            padding: 15px;
            border-radius: 8px;
            backdrop-filter: blur(10px);
        }
        .stat-number {
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 5px;
        }
        .stat-label {
            font-size: 12px;
            opacity: 0.9;
        }
        .status-icon {
            margin-right: 8px;
            font-size: 14px;
        }
        .loan-details {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }
        .loan-meta {
            font-size: 12px;
            color: #666;
        }
        .admin-actions {
            display: flex;
            gap: 5px;
        }
        .btn-approve, .btn-reject {
            padding: 4px 8px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
        }
        .btn-approve {
            background-color: #28a745;
            color: white;
        }
        .btn-reject {
            background-color: #dc3545;
            color: white;
        }
        .no-loans {
            text-align: center;
            padding: 40px;
            color: #666;
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
            <h2 id="pageTitle">Loan Applications</h2>
            <p id="pageDescription">View and manage loan applications</p>
        </div>

        <!-- Loan Summary Section -->
        <div id="loanSummary" class="loan-summary" style="display:none;">
            <h3>Your Loan Portfolio</h3>
            <div class="summary-stats">
                <div class="stat-item">
                    <div class="stat-number" id="totalApplications">0</div>
                    <div class="stat-label">Total Applications</div>
                </div>
                <div class="stat-item">
                    <div class="stat-number" id="approvedLoans">0</div>
                    <div class="stat-label">Approved</div>
                </div>
                <div class="stat-item">
                    <div class="stat-number" id="pendingLoans">0</div>
                    <div class="stat-label">Pending</div>
                </div>
                <div class="stat-item">
                    <div class="stat-number" id="totalAmount">₹0</div>
                    <div class="stat-label">Total Amount</div>
                </div>
            </div>
        </div>

        <div class="loans-table">
            <div class="spinner" id="loading">Loading loans...</div>
            <table id="loanTable" style="display:none;">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Customer</th>
                    <th>Amount</th>
                    <th>Purpose</th>
                    <th>Tenure (Months)</th>
                    <th>Status</th>
                    <th>Application Date</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody></tbody>
            </table>
            <div id="noLoans" class="no-loans" style="display:none;">
                <h3>No loan applications found</h3>
                <p>You haven't applied for any loans yet.</p>
                <a href="/apply-loan" style="color: #8B5CF6; text-decoration: none;">Apply for your first loan</a>
            </div>
        </div>
    </div>

    <script>
        let currentUser = null;

        // Get current user first
        fetch('/api/auth/current')
            .then(response => response.json())
            .then(user => {
                currentUser = user;
                if (user.role === 'ADMIN') {
                    document.getElementById('pageTitle').textContent = 'All Loan Applications';
                    document.getElementById('pageDescription').textContent = 'Manage all customer loan applications';
                    loadAllLoans();
                } else {
                    document.getElementById('pageTitle').textContent = 'My Loan Applications';
                    document.getElementById('pageDescription').textContent = 'View your loan application history';
                    loadMyLoans();
                }
            })
            .catch(() => {
                document.getElementById('loading').textContent = 'Please login to view loans';
            });

        function loadMyLoans() {
            fetch('/api/loans/my-loans')
                .then(response => response.json())
                .then(loans => {
                    displayLoans(loans, false);
                    updateLoanSummary(loans);
                })
                .catch(() => {
                    document.getElementById('loading').textContent = 'Failed to load loans';
                });
        }

        function loadAllLoans() {
            fetch('/api/loans/all')
                .then(response => response.json())
                .then(loans => {
                    displayLoans(loans, true);
                    updateLoanSummary(loans);
                })
                .catch(() => {
                    document.getElementById('loading').textContent = 'Failed to load loans';
                });
        }

        function displayLoans(loans, isAdmin) {
            const tableBody = document.getElementById('loanTable').querySelector('tbody');
            tableBody.innerHTML = '';

            if (loans.length === 0) {
                document.getElementById('loading').style.display = 'none';
                document.getElementById('noLoans').style.display = 'block';
                return;
            }

            loans.forEach(loan => {
                const tr = document.createElement('tr');
                const statusClass = `status-${loan.status.toLowerCase()}`;
                const formattedDate = new Date(loan.applicationDate).toLocaleDateString();

                tr.innerHTML = `
                    <td>${loan.id}</td>
                    <td>${loan.customer ? loan.customer.name : 'N/A'}</td>
                    <td class="loan-amount">₹${loan.amount.toLocaleString()}</td>
                    <td>${loan.purpose}</td>
                    <td>${loan.tenure}</td>
                    <td><span class="${statusClass}">${loan.status}</span></td>
                    <td>${formattedDate}</td>
                    <td>${isAdmin && loan.status === 'PENDING' ? getAdminActions(loan.id) : '-'}</td>
                `;
                tr.classList.add('loan-row');
                tableBody.appendChild(tr);
            });

            document.getElementById('loading').style.display = 'none';
            document.getElementById('loanTable').style.display = 'table';
        }

        function getAdminActions(loanId) {
            return `
                <div class="admin-actions">
                    <button class="btn-approve" onclick="updateLoanStatus(${loanId}, 'APPROVED')">Approve</button>
                    <button class="btn-reject" onclick="updateLoanStatus(${loanId}, 'REJECTED')">Reject</button>
                </div>
            `;
        }

        function updateLoanSummary(loans) {
            const totalApplications = loans.length;
            const approvedLoans = loans.filter(loan => loan.status === 'APPROVED').length;
            const pendingLoans = loans.filter(loan => loan.status === 'PENDING').length;
            const totalAmount = loans.reduce((sum, loan) => sum + loan.amount, 0);

            document.getElementById('totalApplications').textContent = totalApplications;
            document.getElementById('approvedLoans').textContent = approvedLoans;
            document.getElementById('pendingLoans').textContent = pendingLoans;
            document.getElementById('totalAmount').textContent = `₹${totalAmount.toLocaleString()}`;

            document.getElementById('loanSummary').style.display = 'block';
        }
    </script>
</body>
</html>
