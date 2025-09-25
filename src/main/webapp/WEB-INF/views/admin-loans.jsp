<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DebtHues - Loan Approvals (Admin)</title>
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
            background-color: #8B5CF6;
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
            box-shadow: 0 2px 10px rgba(139, 92, 246, 0.3);
            margin-bottom: 20px;
        }
        .page-header h2 {
            color: #8B5CF6;
            margin-bottom: 10px;
        }
        .admin-warning {
            background: linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            text-align: center;
        }
        .loans-table {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(139, 92, 246, 0.3);
            overflow: hidden;
        }
        .filter-section {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(139, 92, 246, 0.3);
            margin-bottom: 20px;
        }
        .filter-buttons {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }
        .filter-btn {
            padding: 8px 16px;
            border: 2px solid #8B5CF6;
            border-radius: 4px;
            background: white;
            color: #8B5CF6;
            cursor: pointer;
            font-weight: 500;
            transition: all 0.3s;
        }
        .filter-btn.active {
            background: #8B5CF6;
            color: white;
        }
        .filter-btn:hover {
            background: #8B5CF6;
            color: white;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #8B5CF6;
            color: white;
            font-weight: bold;
        }
        tr:hover {
            background-color: #f8f4ff;
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
        .admin-actions {
            display: flex;
            gap: 8px;
        }
        .btn-approve, .btn-reject {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 500;
            transition: all 0.3s;
        }
        .btn-approve {
            background-color: #28a745;
            color: white;
        }
        .btn-approve:hover {
            background-color: #218838;
        }
        .btn-reject {
            background-color: #dc3545;
            color: white;
        }
        .btn-reject:hover {
            background-color: #c82333;
        }
        .loan-details {
            cursor: pointer;
            color: #8B5CF6;
            font-weight: 500;
        }
        .loan-details:hover {
            text-decoration: underline;
        }
        .no-loans {
            text-align: center;
            padding: 40px;
            color: #666;
        }
        .stats-summary {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 20px;
        }
        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(139, 92, 246, 0.3);
            text-align: center;
        }
        .stat-number {
            font-size: 24px;
            font-weight: bold;
            color: #8B5CF6;
            margin-bottom: 5px;
        }
        .stat-label {
            color: #666;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <h1>DebtHues - Admin Panel</h1>
            <div class="navbar-links">
                <a href="/dashboard">Dashboard</a>
                <a href="/customers">Customers</a>
                <a href="/loans">All Loans</a>
                <a href="/admin-loans">Loan Approvals</a>
                <form action="/perform_logout" method="post" style="display: inline;">
                    <button type="submit" style="background: none; border: none; color: white; cursor: pointer; font-size: 16px; font-weight: 500; padding: 8px 16px;">Logout</button>
                </form>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="page-header">
            <h2>Loan Approval Center</h2>
            <p>Review and approve pending loan applications</p>
        </div>

        <div id="accessDenied" class="admin-warning" style="display:none;">
            <h3>⚠️ Access Denied</h3>
            <p>This section is only available to administrators. You will be redirected to the dashboard.</p>
        </div>

        <div id="adminContent" style="display:none;">
            <div class="stats-summary">
                <div class="stat-card">
                    <div class="stat-number" id="pendingCount">0</div>
                    <div class="stat-label">Pending Approvals</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="approvedCount">0</div>
                    <div class="stat-label">Approved Today</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="rejectedCount">0</div>
                    <div class="stat-label">Rejected Today</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="totalAmount">&#8377;0</div>
                    <div class="stat-label">Total Pending Amount</div>
                </div>
            </div>

            <div class="filter-section">
                <h3 style="color: #8B5CF6; margin-bottom: 15px;">Filter Applications</h3>
                <div class="filter-buttons">
                    <button class="filter-btn active" onclick="filterLoans('ALL')">All Applications</button>
                    <button class="filter-btn" onclick="filterLoans('PENDING')">Pending Only</button>
                    <button class="filter-btn" onclick="filterLoans('APPROVED')">Approved</button>
                    <button class="filter-btn" onclick="filterLoans('REJECTED')">Rejected</button>
                </div>
            </div>

            <div class="loans-table">
                <div class="spinner" id="loading">Loading loan applications...</div>
                <table id="loanTable" style="display:none;">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Customer</th>
                        <th>Amount</th>
                        <th>Purpose</th>
                        <th>Tenure</th>
                        <th>Status</th>
                        <th>Applied Date</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div id="noLoans" class="no-loans" style="display:none;">
                    <h3>No loan applications found</h3>
                    <p>There are no loan applications matching the current filter.</p>
                </div>
            </div>
        </div>
    </div>

    <script>
        let allLoans = [];
        let currentFilter = 'ALL';

        // Check admin access
        fetch('/api/auth/current')
            .then(response => response.json())
            .then(user => {
                if (user.role !== 'ADMIN') {
                    document.getElementById('accessDenied').style.display = 'block';
                    setTimeout(() => {
                        window.location.href = '/dashboard';
                    }, 3000);
                } else {
                    document.getElementById('adminContent').style.display = 'block';
                    loadAllLoans();
                }
            })
            .catch(() => {
                window.location.href = '/login';
            });

        function loadAllLoans() {
            fetch('/api/loans/all')
                .then(response => response.json())
                .then(loans => {
                    allLoans = loans;
                    displayLoans(loans);
                    updateStats(loans);
                })
                .catch(() => {
                    document.getElementById('loading').textContent = 'Failed to load loan applications';
                });
        }

        function displayLoans(loans) {
            const tableBody = document.getElementById('loanTable').querySelector('tbody');
            tableBody.innerHTML = '';

            if (loans.length === 0) {
                document.getElementById('loading').style.display = 'none';
                document.getElementById('noLoans').style.display = 'block';
                document.getElementById('loanTable').style.display = 'none';
                return;
            }

            loans.forEach(loan => {
                const tr = document.createElement('tr');
                const statusClass = `status-${loan.status.toLowerCase()}`;
                const formattedDate = new Date(loan.applicationDate).toLocaleDateString();
                const isPending = loan.status === 'PENDING';

                tr.innerHTML = `
                    <td class="loan-details" onclick="showLoanDetails(${loan.id})">#${loan.id}</td>
                    <td>${loan.customer ? loan.customer.name : 'N/A'}</td>
                    <td class="loan-amount">&#8377;${loan.amount.toLocaleString()}</td>
                    <td>${loan.purpose}</td>
                    <td>${loan.tenure} months</td>
                    <td><span class="${statusClass}">${loan.status}</span></td>
                    <td>${formattedDate}</td>
                    <td>${isPending ? getPendingActions(loan.id) : getCompletedActions(loan)}</td>
                `;
                tableBody.appendChild(tr);
            });

            document.getElementById('loading').style.display = 'none';
            document.getElementById('loanTable').style.display = 'table';
            document.getElementById('noLoans').style.display = 'none';
        }

        function getPendingActions(loanId) {
            return `
                <div class="admin-actions">
                    <button class="btn-approve" onclick="updateLoanStatus(${loanId}, 'APPROVED')">✓ Approve</button>
                    <button class="btn-reject" onclick="updateLoanStatus(${loanId}, 'REJECTED')">✗ Reject</button>
                </div>
            `;
        }

        function getCompletedActions(loan) {
            const actionDate = loan.approvalDate ? new Date(loan.approvalDate).toLocaleDateString() : 'N/A';
            return `<small style="color: #666;">Processed: ${actionDate}</small>`;
        }

        function updateLoanStatus(loanId, status) {
            const action = status.toLowerCase();
            const comments = prompt(`Enter comments for ${action} this loan application:`);
            if (comments === null || comments.trim() === '') {
                alert('Comments are required for loan approval/rejection.');
                return;
            }

            fetch(`/api/loans/${loanId}/status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    status: status,
                    comments: comments.trim()
                })
            })
            .then(response => response.text())
            .then(data => {
                alert(`Loan application ${action} successfully!`);
                loadAllLoans(); // Refresh the data
            })
            .catch(error => {
                alert('Failed to update loan status: ' + error.message);
            });
        }

        function filterLoans(status) {
            currentFilter = status;
            // Update active button
            document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.remove('active'));
            event.target.classList.add('active');

            let filteredLoans = allLoans;
            if (status !== 'ALL') {
                filteredLoans = allLoans.filter(loan => loan.status === status);
            }
            displayLoans(filteredLoans);
        }

        function updateStats(loans) {
            const pending = loans.filter(loan => loan.status === 'PENDING');
            const today = new Date().toDateString();
            const approvedToday = loans.filter(loan =>
                loan.status === 'APPROVED' &&
                loan.approvalDate &&
                new Date(loan.approvalDate).toDateString() === today
            );
            const rejectedToday = loans.filter(loan =>
                loan.status === 'REJECTED' &&
                loan.approvalDate &&
                new Date(loan.approvalDate).toDateString() === today
            );
            const totalPendingAmount = pending.reduce((sum, loan) => sum + loan.amount, 0);

            document.getElementById('pendingCount').textContent = pending.length;
            document.getElementById('approvedCount').textContent = approvedToday.length;
            document.getElementById('rejectedCount').textContent = rejectedToday.length;
            document.getElementById('totalAmount').textContent = `&#8377;${totalPendingAmount.toLocaleString()}`;
        }

        function showLoanDetails(loanId) {
            const loan = allLoans.find(l => l.id === loanId);
            if (loan) {
                alert(`Loan Details:\n\nID: ${loan.id}\nCustomer: ${loan.customer?.name}\nAmount: &#8377;${loan.amount.toLocaleString()}\nPurpose: ${loan.purpose}\nTenure: ${loan.tenure} months\nStatus: ${loan.status}\nApplied: ${new Date(loan.applicationDate).toLocaleDateString()}\n\nComments: ${loan.adminComments || 'No comments yet'}`);
            }
        }
    </script>
</body>
</html>
