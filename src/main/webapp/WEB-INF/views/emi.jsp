<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DebtHues - My EMIs</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5dc;
            color: #000000;
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
            max-width: 1000px;
            margin: 40px auto;
            padding: 0 20px;
        }
        h2 {
            text-align: center;
            color: #8B5CF6;
            margin-bottom: 30px;
            font-size: 28px;
        }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background-color: #ffffff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            text-align: center;
            border-left: 4px solid #8B5CF6;
        }
        .stat-card.overdue {
            border-left-color: #dc3545;
        }
        .stat-card.pending {
            border-left-color: #ffc107;
        }
        .stat-card.paid {
            border-left-color: #28a745;
        }
        .stat-value {
            font-size: 24px;
            font-weight: bold;
            color: #8B5CF6;
            margin-bottom: 5px;
        }
        .stat-card.overdue .stat-value {
            color: #dc3545;
        }
        .stat-card.pending .stat-value {
            color: #ffc107;
        }
        .stat-card.paid .stat-value {
            color: #28a745;
        }
        .stat-label {
            font-size: 14px;
            color: #666;
        }
        .section {
            background-color: #ffffff;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        .section h3 {
            color: #8B5CF6;
            margin-bottom: 15px;
            font-size: 20px;
        }
        .emi-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        .emi-table th, .emi-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        .emi-table th {
            background-color: #8B5CF6;
            color: white;
            font-weight: 600;
        }
        .emi-table tr:nth-child(even) {
            background-color: #f8f9fa;
        }
        .emi-table tr:hover {
            background-color: #e9ecef;
        }
        .status-badge {
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
        }
        .status-pending {
            background-color: #fff3cd;
            color: #856404;
        }
        .status-paid {
            background-color: #d4edda;
            color: #155724;
        }
        .status-overdue {
            background-color: #f8d7da;
            color: #721c24;
        }
        .pay-btn {
            background-color: #28a745;
            color: white;
            border: none;
            padding: 6px 12px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
            transition: background-color 0.3s;
        }
        .pay-btn:hover {
            background-color: #218838;
        }
        .pay-btn:disabled {
            background-color: #6c757d;
            cursor: not-allowed;
        }
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 6px;
            font-weight: 500;
        }
        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .alert-danger {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .alert-info {
            background-color: #d1ecf1;
            color: #0c5460;
            border: 1px solid #b6d7e3;
        }
        .empty-state {
            text-align: center;
            padding: 40px;
            color: #666;
        }
        .empty-state i {
            font-size: 48px;
            color: #ccc;
            margin-bottom: 15px;
        }
        .payment-modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            z-index: 1000;
        }
        .payment-content {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            padding: 30px;
            border-radius: 15px;
            text-align: center;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            max-width: 400px;
            width: 90%;
        }
        .payment-form {
            margin-top: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
        }
        .form-group select {
            width: 100%;
            padding: 10px;
            border: 2px solid #e0e0e0;
            border-radius: 6px;
        }
        .btn-group {
            display: flex;
            gap: 10px;
            justify-content: center;
            margin-top: 20px;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 600;
            transition: background-color 0.3s;
        }
        .btn-primary {
            background-color: #8B5CF6;
            color: white;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        .loading {
            display: none;
            text-align: center;
            margin: 20px 0;
        }
        .loader {
            border: 4px solid #f3f3f3;
            border-top: 4px solid #8B5CF6;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 2s linear infinite;
            margin: 0 auto;
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
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
                <a href="/emi" style="background-color: rgba(255,255,255,0.2);">EMI Payments</a>
                <form action="/perform_logout" method="post" style="display: inline;">
                    <button type="submit" style="background: none; border: none; color: white; cursor: pointer; font-size: 16px; font-weight: 500; padding: 8px 16px;">Logout</button>
                </form>
            </div>
        </div>
    </nav>

    <div class="container">
        <h2>📊 My EMI Payments</h2>

        <div id="alertContainer"></div>
        <div id="loading" class="loading">
            <div class="loader"></div>
            <p>Loading EMI information...</p>
        </div>

        <!-- EMI Statistics -->
        <div id="statsSection" class="stats-grid" style="display: none;">
            <div class="stat-card">
                <div class="stat-value" id="totalEMIs">0</div>
                <div class="stat-label">Total EMIs</div>
            </div>
            <div class="stat-card pending">
                <div class="stat-value" id="pendingEMIs">0</div>
                <div class="stat-label">Pending EMIs</div>
            </div>
            <div class="stat-card overdue">
                <div class="stat-value" id="overdueEMIs">0</div>
                <div class="stat-label">Overdue EMIs</div>
            </div>
            <div class="stat-card paid">
                <div class="stat-value" id="paidEMIs">0</div>
                <div class="stat-label">Paid EMIs</div>
            </div>
            <div class="stat-card pending">
                <div class="stat-value" id="totalPending">₹0</div>
                <div class="stat-label">Total Pending Amount</div>
            </div>
            <div class="stat-card overdue">
                <div class="stat-value" id="totalOverdue">₹0</div>
                <div class="stat-label">Total Overdue Amount</div>
            </div>
        </div>

        <!-- EMIs Due This Month -->
        <div id="thisMonthSection" class="section" style="display: none;">
            <h3>📅 EMIs Due This Month</h3>
            <div id="thisMonthContent"></div>
        </div>

        <!-- Overdue EMIs -->
        <div id="overdueSection" class="section" style="display: none;">
            <h3>⚠️ Overdue EMIs</h3>
            <div id="overdueContent"></div>
        </div>

        <!-- All EMIs -->
        <div id="allEMIsSection" class="section" style="display: none;">
            <h3>📋 All My EMIs</h3>
            <div id="allEMIsContent"></div>
        </div>

        <!-- Empty State -->
        <div id="emptyState" class="section" style="display: none;">
            <div class="empty-state">
                <div style="font-size: 48px; margin-bottom: 15px;">💳</div>
                <h3>No EMIs Found</h3>
                <p>You don't have any active loans with EMI payments.</p>
                <br>
                <a href="/apply-loan" class="btn btn-primary">Apply for a Loan</a>
            </div>
        </div>
    </div>

    <!-- Payment Modal -->
    <div id="paymentModal" class="payment-modal">
        <div class="payment-content">
            <h3>💳 Pay EMI</h3>
            <p id="paymentDetails"></p>
            <div class="payment-form">
                <div class="form-group">
                    <label for="paymentMethod">Payment Method:</label>
                    <select id="paymentMethod" name="paymentMethod">
                        <option value="Online Banking">Online Banking</option>
                        <option value="Debit Card">Debit Card</option>
                        <option value="Credit Card">Credit Card</option>
                        <option value="UPI">UPI</option>
                        <option value="Net Banking">Net Banking</option>
                    </select>
                </div>
                <div class="btn-group">
                    <button class="btn btn-primary" onclick="processPayment()">Pay Now</button>
                    <button class="btn btn-secondary" onclick="closePaymentModal()">Cancel</button>
                </div>
            </div>
        </div>
    </div>

    <script>
        let currentEMI = null;

        function showAlert(message, type) {
            const alertContainer = document.getElementById('alertContainer');
            alertContainer.innerHTML = `
                <div class="alert alert-${type}">
                    ${message}
                </div>
            `;
            setTimeout(() => {
                alertContainer.innerHTML = '';
            }, 5000);
        }

        function formatCurrency(amount) {
            return '₹' + parseFloat(amount).toLocaleString('en-IN');
        }

        function formatDate(dateString) {
            const date = new Date(dateString);
            return date.toLocaleDateString('en-IN');
        }

        function getStatusBadge(status, dueDate) {
            const today = new Date();
            const due = new Date(dueDate);

            if (status === 'PAID') {
                return '<span class="status-badge status-paid">Paid</span>';
            } else if (status === 'PENDING' && due < today) {
                return '<span class="status-badge status-overdue">Overdue</span>';
            } else {
                return '<span class="status-badge status-pending">Pending</span>';
            }
        }

        function createEMITable(emis, showPayButton) {
            if (showPayButton === undefined) showPayButton = true;

            if (emis.length === 0) {
                return '<p class="empty-state">No EMIs found.</p>';
            }

            var html = '<table class="emi-table"><thead><tr>';
            html += '<th>EMI #</th><th>Loan Purpose</th><th>Due Date</th><th>Amount</th><th>Status</th>';
            if (showPayButton) {
                html += '<th>Action</th>';
            }
            html += '</tr></thead><tbody>';

            emis.forEach(function(emi) {
                var canPay = emi.status === 'PENDING' && showPayButton;
                html += '<tr>';
                html += '<td>' + emi.emiNumber + '/' + emi.loan.tenure + '</td>';
                html += '<td>' + emi.loan.purpose + '</td>';
                html += '<td>' + formatDate(emi.dueDate) + '</td>';
                html += '<td>' + formatCurrency(emi.amount) + '</td>';
                html += '<td>' + getStatusBadge(emi.status, emi.dueDate) + '</td>';
                if (showPayButton) {
                    if (canPay) {
                        html += '<td><button class="pay-btn" onclick="openPaymentModal(' + emi.id + ', \'' +
                               formatCurrency(emi.amount) + '\', \'' + emi.loan.purpose + '\', \'' +
                               formatDate(emi.dueDate) + '\')">Pay Now</button></td>';
                    } else {
                        html += '<td>-</td>';
                    }
                }
                html += '</tr>';
            });

            html += '</tbody></table>';
            return html;
        }

        function openPaymentModal(emiId, amount, purpose, dueDate) {
            currentEMI = emiId;
            var detailsHtml = '<strong>Amount:</strong> ' + amount + '<br>';
            detailsHtml += '<strong>Loan:</strong> ' + purpose + '<br>';
            detailsHtml += '<strong>Due Date:</strong> ' + dueDate;
            document.getElementById('paymentDetails').innerHTML = detailsHtml;
            document.getElementById('paymentModal').style.display = 'block';
        }

        function closePaymentModal() {
            document.getElementById('paymentModal').style.display = 'none';
            currentEMI = null;
        }

        function processPayment() {
            if (!currentEMI) return;

            const paymentMethod = document.getElementById('paymentMethod').value;

            // Show loading state
            const payButton = document.querySelector('.btn-primary');
            const originalText = payButton.textContent;
            payButton.textContent = 'Processing...';
            payButton.disabled = true;

            fetch('/api/emi/pay/' + currentEMI, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ paymentMethod: paymentMethod })
            })
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('Payment failed');
                }
                return response.text();
            })
            .then(function(data) {
                if (data.includes('successful')) {
                    showAlert('🎉 ' + data, 'success');
                    closePaymentModal();

                    // Force reload EMI data to show updated status
                    setTimeout(function() {
                        loadEMIData();
                    }, 500);
                } else {
                    showAlert('❌ ' + data, 'danger');
                }
            })
            .catch(function(error) {
                showAlert('❌ Payment failed: ' + error.message, 'danger');
            })
            .finally(function() {
                // Reset button state
                payButton.textContent = originalText;
                payButton.disabled = false;
            });
        }

        function loadEMIData() {
            document.getElementById('loading').style.display = 'block';

            Promise.all([
                fetch('/api/emi/stats').then(function(r) { return r.json(); }),
                fetch('/api/emi/my-emis').then(function(r) { return r.json(); }),
                fetch('/api/emi/due-this-month').then(function(r) { return r.json(); }),
                fetch('/api/emi/overdue').then(function(r) { return r.json(); })
            ])
            .then(function(results) {
                var stats = results[0];
                var allEMIs = results[1];
                var thisMonth = results[2];
                var overdue = results[3];

                document.getElementById('loading').style.display = 'none';

                if (allEMIs.length === 0) {
                    document.getElementById('emptyState').style.display = 'block';
                    return;
                }

                // Update statistics
                document.getElementById('totalEMIs').textContent = stats.totalEMIs;
                document.getElementById('pendingEMIs').textContent = stats.pendingEMIs;
                document.getElementById('overdueEMIs').textContent = stats.overdueEMIs;
                document.getElementById('paidEMIs').textContent = stats.paidEMIs;
                document.getElementById('totalPending').textContent = formatCurrency(stats.totalPendingAmount);
                document.getElementById('totalOverdue').textContent = formatCurrency(stats.totalOverdueAmount);

                document.getElementById('statsSection').style.display = 'grid';

                // This month EMIs
                if (thisMonth.length > 0) {
                    document.getElementById('thisMonthContent').innerHTML = createEMITable(thisMonth);
                    document.getElementById('thisMonthSection').style.display = 'block';
                }

                // Overdue EMIs
                if (overdue.length > 0) {
                    document.getElementById('overdueContent').innerHTML = createEMITable(overdue);
                    document.getElementById('overdueSection').style.display = 'block';
                }

                // All EMIs
                document.getElementById('allEMIsContent').innerHTML = createEMITable(allEMIs);
                document.getElementById('allEMIsSection').style.display = 'block';

            })
            .catch(function(error) {
                document.getElementById('loading').style.display = 'none';
                showAlert('Error loading EMI data: ' + error.message, 'danger');
            });
        }

        // Close modal when clicking outside
        document.getElementById('paymentModal').addEventListener('click', function(e) {
            if (e.target === this) {
                closePaymentModal();
            }
        });

        // Check authentication and load data
        fetch('/api/auth/current')
            .then(response => {
                if (!response.ok) {
                    window.location.href = '/login';
                    return;
                }
                return response.json();
            })
            .then(user => {
                if (user.role === 'ADMIN') {
                    showAlert('Admins cannot access EMI payments. Please use the admin panel.', 'danger');
                    setTimeout(() => {
                        window.location.href = '/dashboard';
                    }, 3000);
                } else {
                    loadEMIData();
                }
            })
            .catch(() => {
                window.location.href = '/login';
            });
    </script>
</body>
</html>
