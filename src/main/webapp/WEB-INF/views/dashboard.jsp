<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DebtHues - Banking Dashboard</title>
    <link rel="stylesheet" href="/css/credit-score-odometer.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        :root {
            --primary-color: #8B5CF6;
            --secondary-color: #7C3AED;
            --accent-color: #EC4899;
            --success-color: #10B981;
            --warning-color: #F59E0B;
            --danger-color: #EF4444;
            --dark-color: #1F2937;
            --light-bg: #F8FAFC;
            --card-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
            --hover-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
            --border-radius: 16px;
            --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: var(--dark-color);
        }

        .navbar {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border-bottom: 1px solid rgba(255, 255, 255, 0.2);
            padding: 1rem 0;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            z-index: 1000;
            box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
        }

        .navbar-content {
            max-width: 1400px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 2rem;
        }

        .navbar h1 {
            font-size: 2rem;
            background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            font-weight: 800;
            letter-spacing: -0.02em;
        }

        .navbar-links {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .navbar-links a {
            color: var(--dark-color);
            text-decoration: none;
            padding: 0.75rem 1.5rem;
            border-radius: 12px;
            font-weight: 600;
            transition: var(--transition);
            position: relative;
            overflow: hidden;
        }

        .navbar-links a:hover {
            background: var(--primary-color);
            color: white;
            transform: translateY(-2px);
        }

        .navbar-links a.active {
            background: var(--primary-color);
            color: white;
        }

        .container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 120px 2rem 2rem;
            min-height: 100vh;
        }

        .welcome-banner {
            background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
            backdrop-filter: blur(20px);
            border-radius: var(--border-radius);
            padding: 2.5rem;
            margin-bottom: 2rem;
            box-shadow: var(--card-shadow);
            border: 1px solid rgba(255, 255, 255, 0.3);
            position: relative;
            overflow: hidden;
        }

        .welcome-banner::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
        }

        .welcome-content {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 1.5rem;
        }

        .welcome-text h2 {
            font-size: 2.5rem;
            font-weight: 800;
            margin-bottom: 0.5rem;
            background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .welcome-text p {
            font-size: 1.1rem;
            color: #6B7280;
            margin-bottom: 1rem;
        }

        .user-info-chip {
            background: var(--primary-color);
            color: white;
            padding: 0.5rem 1rem;
            border-radius: 50px;
            font-weight: 600;
            font-size: 0.9rem;
        }

        .datetime-info {
            text-align: right;
            color: #6B7280;
        }

        .datetime-info .time {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--primary-color);
        }

        .stats-overview {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        .stat-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border-radius: var(--border-radius);
            padding: 2rem;
            box-shadow: var(--card-shadow);
            border: 1px solid rgba(255, 255, 255, 0.3);
            transition: var(--transition);
            position: relative;
            overflow: hidden;
        }

        .stat-card:hover {
            transform: translateY(-4px);
            box-shadow: var(--hover-shadow);
        }

        .stat-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
        }

        .stat-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
        }

        .stat-icon {
            width: 50px;
            height: 50px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            color: white;
        }

        .stat-value {
            font-size: 2.5rem;
            font-weight: 800;
            margin-bottom: 0.5rem;
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .stat-label {
            color: #6B7280;
            font-weight: 600;
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .stat-trend {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin-top: 0.5rem;
            font-size: 0.9rem;
            font-weight: 600;
        }

        .trend-up {
            color: var(--success-color);
        }

        .trend-down {
            color: var(--danger-color);
        }

        .dashboard-sections {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 2rem;
            margin-bottom: 2rem;
        }

        .main-content {
            display: flex;
            flex-direction: column;
            gap: 2rem;
        }

        .sidebar-content {
            display: flex;
            flex-direction: column;
            gap: 2rem;
        }

        .section-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border-radius: var(--border-radius);
            padding: 2rem;
            box-shadow: var(--card-shadow);
            border: 1px solid rgba(255, 255, 255, 0.3);
        }

        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
            padding-bottom: 1rem;
            border-bottom: 1px solid #E5E7EB;
        }

        .section-title {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--dark-color);
        }

        .section-action {
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 600;
            font-size: 0.9rem;
            transition: var(--transition);
        }

        .section-action:hover {
            color: var(--secondary-color);
        }

        .activity-list {
            list-style: none;
        }

        .activity-item {
            display: flex;
            align-items: center;
            padding: 1rem 0;
            border-bottom: 1px solid #F3F4F6;
            transition: var(--transition);
        }

        .activity-item:last-child {
            border-bottom: none;
        }

        .activity-item:hover {
            background: rgba(139, 92, 246, 0.05);
            margin: 0 -1rem;
            padding: 1rem;
            border-radius: 8px;
        }

        .activity-icon {
            width: 40px;
            height: 40px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 1rem;
            color: white;
        }

        .activity-content {
            flex: 1;
        }

        .activity-title {
            font-weight: 600;
            margin-bottom: 0.25rem;
        }

        .activity-subtitle {
            color: #6B7280;
            font-size: 0.9rem;
        }

        .activity-time {
            color: #9CA3AF;
            font-size: 0.8rem;
            font-weight: 500;
        }

        .activity-priority {
            margin-left: 0.5rem;
            font-size: 0.7rem;
        }

        .priority-urgent {
            border-left: 4px solid var(--danger-color);
        }

        .priority-high {
            border-left: 4px solid var(--warning-color);
        }

        .priority-normal {
            border-left: 4px solid var(--primary-color);
        }

        .priority-low {
            border-left: 4px solid #9CA3AF;
        }

        .priority-urgent .activity-priority {
            color: var(--danger-color);
        }

        .priority-high .activity-priority {
            color: var(--warning-color);
        }

        .priority-normal .activity-priority {
            color: var(--primary-color);
        }

        .priority-low .activity-priority {
            color: #9CA3AF;
        }

        .quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
        }

        .quick-action-btn {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 2rem 1rem;
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border: 2px solid transparent;
            border-radius: var(--border-radius);
            text-decoration: none;
            color: var(--dark-color);
            transition: var(--transition);
            box-shadow: var(--card-shadow);
        }

        .quick-action-btn:hover {
            border-color: var(--primary-color);
            transform: translateY(-4px);
            box-shadow: var(--hover-shadow);
        }

        .quick-action-icon {
            width: 60px;
            height: 60px;
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.8rem;
            color: white;
            margin-bottom: 1rem;
        }

        .quick-action-title {
            font-weight: 700;
            margin-bottom: 0.5rem;
        }

        .quick-action-desc {
            color: #6B7280;
            font-size: 0.9rem;
            text-align: center;
        }

        .chart-container {
            height: 300px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #F9FAFB;
            border-radius: 12px;
            margin-top: 1rem;
        }

        .notification-item {
            display: flex;
            align-items: flex-start;
            padding: 1rem;
            background: #F9FAFB;
            border-radius: 12px;
            margin-bottom: 1rem;
            border-left: 4px solid var(--primary-color);
        }

        .notification-item:last-child {
            margin-bottom: 0;
        }

        .notification-icon {
            width: 40px;
            height: 40px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 1rem;
            color: white;
            flex-shrink: 0;
        }

        .notification-content h4 {
            font-weight: 600;
            margin-bottom: 0.5rem;
        }

        .notification-content p {
            color: #6B7280;
            font-size: 0.9rem;
            line-height: 1.5;
        }

        .notification-urgent {
            background: rgba(239, 68, 68, 0.05);
            border-left-width: 4px;
        }

        .notification-warning {
            background: rgba(245, 158, 11, 0.05);
            border-left-width: 4px;
        }

        .notification-success {
            background: rgba(16, 185, 129, 0.05);
            border-left-width: 4px;
        }

        .notification-info {
            background: rgba(139, 92, 246, 0.05);
            border-left-width: 4px;
        }

        /* EMI Styles */
        .emi-item {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 12px;
            padding: 1.5rem;
            margin-bottom: 1rem;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
            border-left: 4px solid var(--primary-color);
            transition: var(--transition);
        }

        .emi-item:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        }

        .emi-item:last-child {
            margin-bottom: 0;
        }

        .emi-overdue {
            border-left-color: var(--danger-color);
            background: rgba(239, 68, 68, 0.02);
        }

        .emi-due {
            border-left-color: var(--warning-color);
            background: rgba(245, 158, 11, 0.02);
        }

        .emi-upcoming {
            border-left-color: var(--success-color);
            background: rgba(16, 185, 129, 0.02);
        }

        .emi-paid {
            border-left-color: #6B7280;
            background: rgba(107, 114, 128, 0.02);
            opacity: 0.8;
        }

        .emi-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
        }

        .emi-loan-info h4 {
            font-weight: 700;
            margin-bottom: 0.25rem;
            color: var(--dark-color);
        }

        .emi-loan-id {
            font-size: 0.8rem;
            color: #6B7280;
            font-weight: 500;
        }

        .emi-status {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            font-size: 0.9rem;
            font-weight: 600;
        }

        .emi-overdue .emi-status {
            color: var(--danger-color);
        }

        .emi-due .emi-status {
            color: var(--warning-color);
        }

        .emi-upcoming .emi-status {
            color: var(--success-color);
        }

        .emi-paid .emi-status {
            color: #6B7280;
        }

        .emi-details {
            display: flex;
            justify-content: space-between;
            margin-bottom: 1rem;
            gap: 1rem;
        }

        .emi-amount-section {
            flex: 1;
        }

        .emi-amount, .emi-penalty, .emi-due-date {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
        }

        .emi-label {
            font-size: 0.9rem;
            color: #6B7280;
        }

        .emi-value {
            font-weight: 600;
            color: var(--dark-color);
        }

        .emi-value.penalty {
            color: var(--danger-color);
        }

        .emi-progress-section {
            flex: 1;
        }

        .emi-progress-info {
            display: flex;
            justify-content: space-between;
            font-size: 0.8rem;
            color: #6B7280;
            margin-bottom: 0.5rem;
        }

        .emi-progress-bar {
            width: 100%;
            height: 6px;
            background: #E5E7EB;
            border-radius: 3px;
            overflow: hidden;
        }

        .emi-progress-fill {
            height: 100%;
            background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
            border-radius: 3px;
            transition: var(--transition);
        }

        .emi-actions {
            display: flex;
            gap: 0.75rem;
        }

        .emi-pay-btn, .emi-details-btn {
            padding: 0.5rem 1rem;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            font-size: 0.9rem;
            cursor: pointer;
            transition: var(--transition);
        }

        .emi-pay-btn.urgent {
            background: var(--danger-color);
            color: white;
        }

        .emi-pay-btn.primary {
            background: var(--primary-color);
            color: white;
        }

        .emi-pay-btn.secondary {
            background: var(--success-color);
            color: white;
        }

        .emi-details-btn {
            background: #F3F4F6;
            color: var(--dark-color);
            border: 1px solid #E5E7EB;
        }

        .emi-pay-btn:hover, .emi-details-btn:hover {
            transform: translateY(-1px);
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
        }

        .progress-bar {
            width: 100%;
            height: 8px;
            background: #E5E7EB;
            border-radius: 4px;
            overflow: hidden;
            margin-top: 0.5rem;
        }

        .progress-fill {
            height: 100%;
            background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
            border-radius: 4px;
            transition: var(--transition);
        }

        @media (max-width: 768px) {
            .dashboard-sections {
                grid-template-columns: 1fr;
            }

            .container {
                padding: 100px 1rem 1rem;
            }

            .navbar-content {
                padding: 0 1rem;
            }

            .welcome-content {
                text-align: center;
            }

            .datetime-info {
                text-align: center;
            }
        }

        .loading-skeleton {
            background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
            background-size: 200% 100%;
            animation: loading 1.5s infinite;
        }

        @keyframes loading {
            0% {
                background-position: 200% 0;
            }
            100% {
                background-position: -200% 0;
            }
        }

        .fade-in {
            animation: fadeIn 0.6s ease-out;
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .pulse {
            animation: pulse 2s infinite;
        }

        @keyframes pulse {
            0%, 100% {
                opacity: 1;
            }
            50% {
                opacity: 0.7;
            }
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <h1><i class="fas fa-university"></i> DebtHues</h1>
            <div class="navbar-links">
                <a href="/dashboard" class="active"><i class="fas fa-tachometer-alt"></i> Dashboard</a>
                <c:if test="${userRole == 'ADMIN'}">
                    <a href="/customers"><i class="fas fa-users"></i> Customers</a>
                </c:if>
                <a href="/loans"><i class="fas fa-file-invoice-dollar"></i> Loans</a>
                <c:if test="${userRole == 'CUSTOMER'}">
                    <a href="/apply-loan"><i class="fas fa-plus-circle"></i> Apply Loan</a>
                    <a href="/emi"><i class="fas fa-credit-card"></i> EMI Payments</a>
                </c:if>
                <form action="/perform_logout" method="post" style="display: inline; margin-left: 1rem;">
                    <button type="submit" style="background: var(--danger-color); color: white; border: none; padding: 0.75rem 1.5rem; border-radius: 12px; font-weight: 600; cursor: pointer; transition: var(--transition);">
                        <i class="fas fa-sign-out-alt"></i> Logout
                    </button>
                </form>
            </div>
        </div>
    </nav>

    <div class="container">
        <!-- Welcome Banner -->
        <div class="welcome-banner fade-in">
            <div class="welcome-content">
                <div class="welcome-text">
                    <h2>Welcome back, ${username}!</h2>
                    <p>Here's what's happening with your financial portfolio today</p>
                    <div class="user-info-chip">
                        <i class="fas fa-user-shield"></i> ${userRole} Account
                    </div>
                </div>
                <div class="datetime-info">
                    <div class="time" id="currentTime"></div>
                    <div id="currentDate"></div>
                    <div style="margin-top: 0.5rem;">
                        <i class="fas fa-map-marker-alt"></i> Banking Center Online
                    </div>
                </div>
            </div>
        </div>

        <!-- Credit Score Odometer for Customers -->
        <c:if test="${userRole == 'CUSTOMER'}">
            <div id="credit-score-odometer" class="fade-in"></div>
        </c:if>

        <!-- Statistics Overview -->
        <div class="stats-overview fade-in">
            <c:if test="${userRole == 'ADMIN'}">
                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-icon" style="background: var(--success-color);">
                            <i class="fas fa-users"></i>
                        </div>
                    </div>
                    <div class="stat-value" id="totalCustomers">-</div>
                    <div class="stat-label">Total Customers</div>
                    <div class="stat-trend trend-up">
                        <i class="fas fa-arrow-up"></i>
                        <span>+12% this month</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-icon" style="background: var(--primary-color);">
                            <i class="fas fa-file-invoice-dollar"></i>
                        </div>
                    </div>
                    <div class="stat-value" id="totalLoans">-</div>
                    <div class="stat-label">Total Loan Applications</div>
                    <div class="stat-trend trend-up">
                        <i class="fas fa-arrow-up"></i>
                        <span>+8% this week</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-icon" style="background: var(--warning-color);">
                            <i class="fas fa-clock"></i>
                        </div>
                    </div>
                    <div class="stat-value" id="pendingApprovals">-</div>
                    <div class="stat-label">Pending Approvals</div>
                    <div class="stat-trend">
                        <i class="fas fa-exclamation-triangle"></i>
                        <span>Requires attention</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-icon" style="background: var(--accent-color);">
                            <i class="fas fa-chart-line"></i>
                        </div>
                    </div>
                    <div class="stat-value">₹45.2M</div>
                    <div class="stat-label">Total Portfolio Value</div>
                    <div class="stat-trend trend-up">
                        <i class="fas fa-arrow-up"></i>
                        <span>+15% YTD</span>
                    </div>
                </div>
            </c:if>

            <c:if test="${userRole == 'CUSTOMER'}">
                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-icon" style="background: var(--primary-color);">
                            <i class="fas fa-hand-holding-usd"></i>
                        </div>
                    </div>
                    <div class="stat-value" id="myLoans">-</div>
                    <div class="stat-label">My Active Loans</div>
                    <div class="stat-trend">
                        <i class="fas fa-info-circle"></i>
                        <span>Total portfolio</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-icon" style="background: var(--warning-color);">
                            <i class="fas fa-hourglass-half"></i>
                        </div>
                    </div>
                    <div class="stat-value" id="pendingLoans">-</div>
                    <div class="stat-label">Pending Applications</div>
                    <div class="stat-trend">
                        <i class="fas fa-clock"></i>
                        <span>Under review</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-icon" style="background: var(--success-color);">
                            <i class="fas fa-check-circle"></i>
                        </div>
                    </div>
                    <div class="stat-value" id="approvedLoans">-</div>
                    <div class="stat-label">Approved Loans</div>
                    <div class="stat-trend trend-up">
                        <i class="fas fa-thumbs-up"></i>
                        <span>Great progress!</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-icon" style="background: var(--accent-color);">
                            <i class="fas fa-credit-card"></i>
                        </div>
                    </div>
                    <div class="stat-value" id="nextEMI">₹0</div>
                    <div class="stat-label">Upcoming Payment</div>
                    <div class="stat-trend" id="emiDueDate">
                        <i class="fas fa-calendar-alt"></i>
                        <span>Loading...</span>
                    </div>
                </div>
            </c:if>
        </div>

        <!-- Main Dashboard Sections -->
        <div class="dashboard-sections">
            <div class="main-content">
                <!-- Recent Activity Section -->
                <div class="section-card fade-in">
                    <div class="section-header">
                        <h3 class="section-title">
                            <i class="fas fa-history"></i> Recent Activity
                        </h3>
                        <a href="#" class="section-action">View All <i class="fas fa-arrow-right"></i></a>
                    </div>
                    <ul class="activity-list" id="recentActivity">
                        <!-- Activities will be loaded dynamically -->
                    </ul>
                </div>

                <!-- Quick Actions -->
                <div class="section-card fade-in">
                    <div class="section-header">
                        <h3 class="section-title">
                            <i class="fas fa-bolt"></i> Quick Actions
                        </h3>
                    </div>
                    <div class="quick-actions">
                        <c:if test="${userRole == 'CUSTOMER'}">
                            <a href="/apply-loan" class="quick-action-btn">
                                <div class="quick-action-icon" style="background: var(--primary-color);">
                                    <i class="fas fa-plus-circle"></i>
                                </div>
                                <div class="quick-action-title">Apply for Loan</div>
                                <div class="quick-action-desc">Start your loan application process</div>
                            </a>

                            <a href="/emi" class="quick-action-btn">
                                <div class="quick-action-icon" style="background: var(--success-color);">
                                    <i class="fas fa-credit-card"></i>
                                </div>
                                <div class="quick-action-title">Pay EMI</div>
                                <div class="quick-action-desc">Make your monthly EMI payments</div>
                            </a>

                            <a href="/loans" class="quick-action-btn">
                                <div class="quick-action-icon" style="background: var(--accent-color);">
                                    <i class="fas fa-file-invoice-dollar"></i>
                                </div>
                                <div class="quick-action-title">View Loans</div>
                                <div class="quick-action-desc">Check your loan portfolio</div>
                            </a>
                        </c:if>

                        <c:if test="${userRole == 'ADMIN'}">
                            <a href="/loans" class="quick-action-btn">
                                <div class="quick-action-icon" style="background: var(--primary-color);">
                                    <i class="fas fa-tasks"></i>
                                </div>
                                <div class="quick-action-title">Review Applications</div>
                                <div class="quick-action-desc">Process pending loan requests</div>
                            </a>

                            <a href="/customers" class="quick-action-btn">
                                <div class="quick-action-icon" style="background: var(--success-color);">
                                    <i class="fas fa-users-cog"></i>
                                </div>
                                <div class="quick-action-title">Manage Customers</div>
                                <div class="quick-action-desc">View customer profiles & data</div>
                            </a>

                            <a href="#" class="quick-action-btn" id="generateEMIsAction">
                                <div class="quick-action-icon" style="background: var(--warning-color);">
                                    <i class="fas fa-cogs"></i>
                                </div>
                                <div class="quick-action-title">Generate EMIs</div>
                                <div class="quick-action-desc">Create EMIs for approved loans</div>
                            </a>
                        </c:if>
                    </div>
                </div>

                <!-- Due EMIs Section for Customers -->
                <c:if test="${userRole == 'CUSTOMER'}">
                    <div class="section-card fade-in">
                        <div class="section-header">
                            <h3 class="section-title">
                                <i class="fas fa-calendar-check"></i> Due EMIs & Payment Schedule
                            </h3>
                            <a href="/emi" class="section-action">Pay Now <i class="fas fa-arrow-right"></i></a>
                        </div>
                        <div id="dueEMIsList">
                            <!-- Due EMIs will be loaded dynamically -->
                        </div>
                    </div>
                </c:if>
            </div>

            <div class="sidebar-content">
                <!-- Notifications -->
                <div class="section-card fade-in">
                    <div class="section-header">
                        <h3 class="section-title">
                            <i class="fas fa-bell"></i> Notifications
                        </h3>
                    </div>
                    <div id="notificationsList">
                        <!-- Notifications will be loaded dynamically -->
                    </div>
                </div>

                <!-- Performance Summary -->
                <div class="section-card fade-in">
                    <div class="section-header">
                        <h3 class="section-title">
                            <i class="fas fa-chart-pie"></i> Performance Summary
                        </h3>
                    </div>
                    <div class="chart-container">
                        <div style="text-align: center; color: #6B7280;">
                            <i class="fas fa-chart-line" style="font-size: 3rem; margin-bottom: 1rem; opacity: 0.3;"></i>
                            <p>Analytics dashboard coming soon</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Real-time clock
        function updateDateTime() {
            const now = new Date();
            document.getElementById('currentTime').textContent = now.toLocaleTimeString();
            document.getElementById('currentDate').textContent = now.toLocaleDateString('en-US', {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
        }

        // Update time every second
        setInterval(updateDateTime, 1000);
        updateDateTime();

        // Load dashboard data
        document.addEventListener('DOMContentLoaded', function() {
            const userRole = '${userRole}';

            // Add loading animations
            addLoadingSkeletons();

            // Load data based on user role
            if (userRole === 'ADMIN') {
                loadAdminStats();
                loadAdminActivity();
                loadAdminNotifications();
            } else if (userRole === 'CUSTOMER') {
                loadCustomerStats();
                loadCustomerActivity();
                loadCustomerNotifications();
                initializeCreditScore();
            }

            // Remove loading skeletons after data loads
            setTimeout(removeLoadingSkeletons, 2000);
        });

        function addLoadingSkeletons() {
            const statValues = document.querySelectorAll('.stat-value');
            statValues.forEach(el => {
                if (el.textContent === '-') {
                    el.classList.add('loading-skeleton');
                    el.style.height = '2.5rem';
                    el.style.borderRadius = '8px';
                }
            });
        }

        function removeLoadingSkeletons() {
            document.querySelectorAll('.loading-skeleton').forEach(el => {
                el.classList.remove('loading-skeleton');
            });
        }

        function loadAdminStats() {
            // Load total customers
            fetch('/api/customers/count')
                .then(response => response.json())
                .then(count => {
                    animateCounter('totalCustomers', count);
                })
                .catch(() => animateCounter('totalCustomers', 0));

            // Load total loan applications
            fetch('/api/loans/count')
                .then(response => response.json())
                .then(count => {
                    animateCounter('totalLoans', count);
                })
                .catch(() => animateCounter('totalLoans', 0));

            // Load pending approvals
            fetch('/api/loans/pending/count')
                .then(response => response.json())
                .then(count => {
                    animateCounter('pendingApprovals', count);
                })
                .catch(() => animateCounter('pendingApprovals', 0));
        }

        function loadCustomerStats() {
            // Load customer's own loan statistics
            fetch('/api/loans/my/count')
                .then(response => response.json())
                .then(count => {
                    animateCounter('myLoans', count);
                })
                .catch(() => animateCounter('myLoans', 0));

            // Load customer's pending applications
            fetch('/api/loans/my/pending/count')
                .then(response => response.json())
                .then(count => {
                    animateCounter('pendingLoans', count);
                })
                .catch(() => animateCounter('pendingLoans', 0));

            // Load customer's approved loans
            fetch('/api/loans/my/approved/count')
                .then(response => response.json())
                .then(count => {
                    animateCounter('approvedLoans', count);
                })
                .catch(() => animateCounter('approvedLoans', 0));

            // Load EMI information
            loadEMIInfo();
            loadDueEMIs();
        }

        function loadEMIInfo() {
            fetch('/api/emi/due-this-month')
                .then(response => response.json())
                .then(emis => {
                    if (emis.length > 0) {
                        const nextEMI = emis[0];
                        document.getElementById('nextEMI').textContent = '₹' + nextEMI.amount.toLocaleString('en-IN');
                        document.getElementById('emiDueDate').innerHTML =
                            '<i class="fas fa-calendar-alt"></i> <span>Due ' + new Date(nextEMI.dueDate).toLocaleDateString() + '</span>';
                    } else {
                        document.getElementById('nextEMI').textContent = '₹0';
                        document.getElementById('emiDueDate').innerHTML =
                            '<i class="fas fa-check-circle"></i> <span>Payments Current</span>';
                    }
                })
                .catch(() => {
                    document.getElementById('nextEMI').textContent = '₹0';
                    document.getElementById('emiDueDate').innerHTML =
                        '<i class="fas fa-info-circle"></i> <span>No data</span>';
                });
        }

        function loadDueEMIs() {
            const userRole = '${userRole}';
            if (userRole !== 'CUSTOMER') return;

            fetch('/api/emi/my-emis')
                .then(response => response.json())
                .then(emis => {
                    if (emis && Array.isArray(emis)) {
                        // Process the EMI data to create comprehensive information
                        const processedEMIs = emis.map(emi => {
                            const dueDate = new Date(emi.dueDate);
                            const today = new Date();
                            const daysUntilDue = Math.ceil((dueDate - today) / (1000 * 60 * 60 * 24));
                            
                            let status = 'upcoming';
                            let daysPastDue = 0;
                            
                            if (emi.paymentStatus === 'PAID') {
                                status = 'paid';
                            } else if (daysUntilDue < 0) {
                                status = 'overdue';
                                daysPastDue = Math.abs(daysUntilDue);
                            } else if (daysUntilDue <= 3) {
                                status = 'due';
                            }

                            return {
                                loanId: emi.loan?.loanId || `LOAN-\${emi.loanId}`,
                                loanType: emi.loan?.purpose || 'Personal Loan',
                                amount: emi.amount,
                                dueDate: emi.dueDate,
                                status: status,
                                lateFeePenalty: emi.lateFeePenalty || 0,
                                remainingAmount: emi.loan?.remainingAmount || 0,
                                totalPaid: emi.loan?.totalPaid || 0,
                                daysPastDue: daysPastDue,
                                emiId: emi.id,
                                paymentStatus: emi.paymentStatus
                            };
                        });
                        
                        // Sort EMIs: overdue first, then due, then upcoming
                        processedEMIs.sort((a, b) => {
                            const statusPriority = { overdue: 1, due: 2, upcoming: 3, paid: 4 };
                            if (statusPriority[a.status] !== statusPriority[b.status]) {
                                return statusPriority[a.status] - statusPriority[b.status];
                            }
                            return new Date(a.dueDate) - new Date(b.dueDate);
                        });
                        
                        displayDueEMIs(processedEMIs.slice(0, 5)); // Show top 5 EMIs
                    } else {
                        displayNoEMIs();
                    }
                })
                .catch(error => {
                    console.error('Error loading EMIs:', error);
                    displayNoEMIs();
                });
        }

        function displayNoEMIs() {
            const dueEMIsList = document.getElementById('dueEMIsList');
            if (!dueEMIsList) return;
            
            dueEMIsList.innerHTML = `
                <div class="no-emis-message">
                    <div style="text-align: center; padding: 2rem; color: #6B7280;">
                        <i class="fas fa-check-circle" style="font-size: 3rem; margin-bottom: 1rem; color: var(--success-color);"></i>
                        <h4>No EMIs Due</h4>
                        <p>You're all caught up with your payments!</p>
                    </div>
                </div>
            `;
        }

        function displayDueEMIs(emis) {
            const dueEMIsList = document.getElementById('dueEMIsList');
            if (!dueEMIsList) return;

            if (emis.length === 0) {
                displayNoEMIs();
                return;
            }

            dueEMIsList.innerHTML = emis.map(emi => {
                const dueDate = new Date(emi.dueDate);
                const today = new Date();
                const daysUntilDue = Math.ceil((dueDate - today) / (1000 * 60 * 60 * 24));
                
                let statusClass = '';
                let statusIcon = '';
                let statusText = '';
                let actionButton = '';

                if (emi.status === 'paid') {
                    statusClass = 'emi-paid';
                    statusIcon = 'fas fa-check-circle';
                    statusText = 'Paid';
                    actionButton = '<button class="emi-details-btn">View Receipt</button>';
                } else if (emi.status === 'overdue') {
                    statusClass = 'emi-overdue';
                    statusIcon = 'fas fa-exclamation-triangle';
                    statusText = `Overdue by \${emi.daysPastDue} days`;
                    actionButton = `<button class="emi-pay-btn urgent" onclick="payEMI(\${emi.emiId}, \${emi.amount + emi.lateFeePenalty})">Pay Now</button>`;
                } else if (emi.status === 'due') {
                    statusClass = 'emi-due';
                    statusIcon = 'fas fa-clock';
                    statusText = daysUntilDue <= 0 ? 'Due Today' : `Due in \${daysUntilDue} days`;
                    actionButton = `<button class="emi-pay-btn primary" onclick="payEMI(\${emi.emiId}, \${emi.amount})">Pay EMI</button>`;
                } else {
                    statusClass = 'emi-upcoming';
                    statusIcon = 'fas fa-calendar-alt';
                    statusText = `Due in \${daysUntilDue} days`;
                    actionButton = `<button class="emi-pay-btn secondary" onclick="schedulePayment(\${emi.emiId})">Schedule Payment</button>`;
                }

                // Calculate loan progress if data is available
                const totalLoanAmount = emi.totalPaid + emi.remainingAmount;
                const progressPercentage = totalLoanAmount > 0 ? (emi.totalPaid / totalLoanAmount) * 100 : 0;

                return `
                    <div class="emi-item \${statusClass}">
                        <div class="emi-header">
                            <div class="emi-loan-info">
                                <h4>\${emi.loanType}</h4>
                                <span class="emi-loan-id">\${emi.loanId}</span>
                            </div>
                            <div class="emi-status">
                                <i class="\${statusIcon}"></i>
                                <span>\${statusText}</span>
                            </div>
                        </div>
                        <div class="emi-details">
                            <div class="emi-amount-section">
                                <div class="emi-amount">
                                    <span class="emi-label">EMI Amount</span>
                                    <span class="emi-value">₹\${emi.amount.toLocaleString('en-IN')}</span>
                                </div>
                                \${emi.lateFeePenalty > 0 ? `
                                    <div class="emi-penalty">
                                        <span class="emi-label">Late Fee</span>
                                        <span class="emi-value penalty">₹\${emi.lateFeePenalty.toLocaleString('en-IN')}</span>
                                    </div>
                                ` : ''}
                                <div class="emi-due-date">
                                    <span class="emi-label">Due Date</span>
                                    <span class="emi-value">\${dueDate.toLocaleDateString('en-IN')}</span>
                                </div>
                            </div>
                            \${totalLoanAmount > 0 ? `
                                <div class="emi-progress-section">
                                    <div class="emi-progress-info">
                                        <span>Remaining: ₹\${emi.remainingAmount.toLocaleString('en-IN')}</span>
                                        <span>Paid: ₹\${emi.totalPaid.toLocaleString('en-IN')}</span>
                                    </div>
                                    <div class="emi-progress-bar">
                                        <div class="emi-progress-fill" style="width: \${progressPercentage}%"></div>
                                    </div>
                                </div>
                            ` : ''}
                        </div>
                        <div class="emi-actions">
                            \${actionButton}
                            <button class="emi-details-btn" onclick="viewEMIDetails(\${emi.emiId})">View Details</button>
                        </div>
                    </div>
                `;
            }).join('');
        }

        // EMI Action Functions
        function payEMI(emiId, amount) {
            if (confirm(`Pay EMI amount of ₹\${amount.toLocaleString('en-IN')}?`)) {
                fetch(`/api/emi/pay/\${emiId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        paymentMethod: 'Online Banking'
                    })
                })
                .then(response => response.text())
                .then(message => {
                    alert(message);
                    loadDueEMIs(); // Refresh the EMI list
                    loadCustomerStats(); // Refresh stats
                })
                .catch(error => {
                    alert('Error processing payment: ' + error.message);
                });
            }
        }

        function schedulePayment(emiId) {
            alert('Schedule payment feature will be available soon!');
        }

        function viewEMIDetails(emiId) {
            window.location.href = `/emi#emi-${emiId}`;
        }

        function animateCounter(elementId, targetValue) {
            const element = document.getElementById(elementId);
            if (!element) return;

            let currentValue = 0;
            const increment = targetValue / 50;
            const timer = setInterval(() => {
                currentValue += increment;
                if (currentValue >= targetValue) {
                    element.textContent = targetValue;
                    clearInterval(timer);
                } else {
                    element.textContent = Math.floor(currentValue);
                }
            }, 30);
        }

        function loadAdminActivity() {
            const activities = [
                {
                    icon: 'fas fa-user-plus',
                    iconBg: 'var(--success-color)',
                    title: 'New Customer Registration',
                    subtitle: 'John Doe registered as a new customer - KYC completed',
                    time: '2 minutes ago',
                    priority: 'normal'
                },
                {
                    icon: 'fas fa-file-invoice-dollar',
                    iconBg: 'var(--primary-color)',
                    title: 'Loan Application Submitted',
                    subtitle: 'Personal loan for ₹2,50,000 submitted by Sarah Wilson',
                    time: '15 minutes ago',
                    priority: 'high'
                },
                {
                    icon: 'fas fa-check-circle',
                    iconBg: 'var(--success-color)',
                    title: 'Loan Approved',
                    subtitle: 'Home loan for ₹15,00,000 approved for Mike Johnson',
                    time: '1 hour ago',
                    priority: 'normal'
                },
                {
                    icon: 'fas fa-credit-card',
                    iconBg: 'var(--accent-color)',
                    title: 'EMI Payment Received',
                    subtitle: '₹45,000 EMI payment processed for Lisa Brown',
                    time: '2 hours ago',
                    priority: 'normal'
                },
                {
                    icon: 'fas fa-exclamation-triangle',
                    iconBg: 'var(--warning-color)',
                    title: 'Overdue Payment Alert',
                    subtitle: 'EMI overdue alert for customer #1234 - 5 days late',
                    time: '3 hours ago',
                    priority: 'urgent'
                },
                {
                    icon: 'fas fa-chart-line',
                    iconBg: 'var(--primary-color)',
                    title: 'Monthly Report Generated',
                    subtitle: 'September 2025 loan performance report is ready for review',
                    time: '4 hours ago',
                    priority: 'low'
                },
                {
                    icon: 'fas fa-user-shield',
                    iconBg: 'var(--success-color)',
                    title: 'Credit Score Updated',
                    subtitle: 'Credit scores updated for 150+ customers this week',
                    time: '6 hours ago',
                    priority: 'normal'
                },
                {
                    icon: 'fas fa-ban',
                    iconBg: 'var(--danger-color)',
                    title: 'Loan Application Rejected',
                    subtitle: 'Business loan application rejected due to insufficient credit score',
                    time: '8 hours ago',
                    priority: 'normal'
                }
            ];

            displayActivity(activities);
        }

        function loadCustomerActivity() {
            const activities = [
                {
                    icon: 'fas fa-credit-card',
                    iconBg: 'var(--success-color)',
                    title: 'EMI Payment Successful',
                    subtitle: 'Your monthly EMI of ₹12,500 has been processed successfully',
                    time: '2 days ago',
                    priority: 'normal'
                },
                {
                    icon: 'fas fa-file-invoice-dollar',
                    iconBg: 'var(--primary-color)',
                    title: 'Loan Application Under Review',
                    subtitle: 'Your personal loan application of ₹3,00,000 is being processed',
                    time: '1 week ago',
                    priority: 'high'
                },
                {
                    icon: 'fas fa-star',
                    iconBg: 'var(--success-color)',
                    title: 'Credit Score Updated',
                    subtitle: 'Your credit score has improved by 25 points to 750',
                    time: '2 weeks ago',
                    priority: 'normal'
                },
                {
                    icon: 'fas fa-bell',
                    iconBg: 'var(--warning-color)',
                    title: 'EMI Due Reminder',
                    subtitle: 'Your next EMI of ₹12,500 is due on October 5th, 2025',
                    time: '3 weeks ago',
                    priority: 'high'
                },
                {
                    icon: 'fas fa-check-double',
                    iconBg: 'var(--success-color)',
                    title: 'Document Verification Complete',
                    subtitle: 'All your KYC documents have been verified and approved',
                    time: '1 month ago',
                    priority: 'normal'
                },
                {
                    icon: 'fas fa-gift',
                    iconBg: 'var(--accent-color)',
                    title: 'Special Offer Available',
                    subtitle: 'You are eligible for a personal loan at reduced interest rate',
                    time: '1 month ago',
                    priority: 'low'
                },
                {
                    icon: 'fas fa-handshake',
                    iconBg: 'var(--primary-color)',
                    title: 'Welcome to DebtHues',
                    subtitle: 'Your account has been successfully created and activated',
                    time: '2 months ago',
                    priority: 'normal'
                }
            ];

            displayActivity(activities);
        }

        function displayActivity(activities) {
            const activityList = document.getElementById('recentActivity');
            activityList.innerHTML = activities.map(activity => {
                const priorityClass = activity.priority === 'urgent' ? 'priority-urgent' : 
                                    activity.priority === 'high' ? 'priority-high' : 
                                    activity.priority === 'low' ? 'priority-low' : 'priority-normal';
                const priorityIcon = activity.priority === 'urgent' ? 'fas fa-exclamation-circle' : 
                                   activity.priority === 'high' ? 'fas fa-arrow-up' : 
                                   activity.priority === 'low' ? 'fas fa-arrow-down' : 'fas fa-circle';
                
                return `
                    <li class="activity-item \${priorityClass}">
                        <div class="activity-icon" style="background: \${activity.iconBg};">
                            <i class="\${activity.icon}"></i>
                        </div>
                        <div class="activity-content">
                            <div class="activity-title">
                                \${activity.title}
                                <span class="activity-priority">
                                    <i class="\${priorityIcon}"></i>
                                </span>
                            </div>
                            <div class="activity-subtitle">\${activity.subtitle}</div>
                        </div>
                        <div class="activity-time">\${activity.time}</div>
                    </li>
                `;
            }).join('');
        }

        function loadAdminNotifications() {
            const notifications = [
                {
                    icon: 'fas fa-exclamation-circle',
                    iconBg: 'var(--warning-color)',
                    title: '15 Loan Applications Pending',
                    desc: 'Multiple loan applications require your immediate attention for approval or rejection. Average processing time: 2-3 business days.',
                    type: 'urgent'
                },
                {
                    icon: 'fas fa-clock',
                    iconBg: 'var(--danger-color)',
                    title: 'Overdue EMI Payments',
                    desc: '8 customers have EMI payments overdue by more than 7 days. Follow-up actions recommended.',
                    type: 'urgent'
                },
                {
                    icon: 'fas fa-chart-line',
                    iconBg: 'var(--success-color)',
                    title: 'Monthly Targets Achieved',
                    desc: 'Congratulations! This month\'s loan approval targets have been successfully met with 125% achievement.',
                    type: 'success'
                },
                {
                    icon: 'fas fa-users',
                    iconBg: 'var(--primary-color)',
                    title: 'New Customer Registrations',
                    desc: '23 new customers have registered this week, representing a 15% increase from last week.',
                    type: 'info'
                },
                {
                    icon: 'fas fa-shield-alt',
                    iconBg: 'var(--warning-color)',
                    title: 'System Maintenance Scheduled',
                    desc: 'Planned maintenance window on October 5th, 2025 from 2:00 AM to 4:00 AM IST.',
                    type: 'warning'
                },
                {
                    icon: 'fas fa-file-alt',
                    iconBg: 'var(--accent-color)',
                    title: 'Monthly Report Ready',
                    desc: 'September 2025 comprehensive loan performance and analytics report is available for download.',
                    type: 'info'
                }
            ];

            displayNotifications(notifications);
        }

        function loadCustomerNotifications() {
            const notifications = [
                {
                    icon: 'fas fa-credit-card',
                    iconBg: 'var(--warning-color)',
                    title: 'EMI Due in 3 Days',
                    desc: 'Your next EMI payment of ₹12,500 is due on October 5th. Set up auto-pay to avoid late fees of ₹500.',
                    type: 'urgent'
                },
                {
                    icon: 'fas fa-star',
                    iconBg: 'var(--success-color)',
                    title: 'Credit Score Improved',
                    desc: 'Great news! Your credit score has increased from 725 to 750. You\'re now eligible for premium loan rates.',
                    type: 'success'
                },
                {
                    icon: 'fas fa-gift',
                    iconBg: 'var(--accent-color)',
                    title: 'Special Offer Available',
                    desc: 'Limited time offer: Apply for a personal loan at just 9.5% interest rate until October 31st. Save up to ₹50,000 in interest.',
                    type: 'info'
                },
                {
                    icon: 'fas fa-file-check',
                    iconBg: 'var(--primary-color)',
                    title: 'Loan Application Update',
                    desc: 'Your personal loan application (#PL-2025-001) is under final review. Decision expected within 2 business days.',
                    type: 'info'
                },
                {
                    icon: 'fas fa-chart-line',
                    iconBg: 'var(--success-color)',
                    title: 'Monthly Spending Analysis',
                    desc: 'Your September spending pattern shows 15% reduction in expenses. Great financial discipline!',
                    type: 'success'
                },
                {
                    icon: 'fas fa-shield-alt',
                    iconBg: 'var(--warning-color)',
                    title: 'Security Alert',
                    desc: 'Login detected from new device. If this wasn\'t you, please change your password immediately.',
                    type: 'warning'
                }
            ];

            displayNotifications(notifications);
        }

        function displayNotifications(notifications) {
            const notificationsList = document.getElementById('notificationsList');
            notificationsList.innerHTML = notifications.map(notification => {
                const typeClass = notification.type ? `notification-${notification.type}` : 'notification-info';
                const borderColor = notification.type === 'urgent' ? 'var(--danger-color)' : 
                                  notification.type === 'warning' ? 'var(--warning-color)' : 
                                  notification.type === 'success' ? 'var(--success-color)' : 
                                  'var(--primary-color)';
                
                return `
                    <div class="notification-item \${typeClass}" style="border-left-color: \${borderColor};">
                        <div class="notification-icon" style="background: \${notification.iconBg};">
                            <i class="\${notification.icon}"></i>
                        </div>
                        <div class="notification-content">
                            <h4>\${notification.title}</h4>
                            <p>\${notification.desc}</p>
                        </div>
                    </div>
                `;
            }).join('');
        }

        // Initialize Credit Score Odometer for customers
        function initializeCreditScore() {
            const userRole = '${userRole}';
            const userId = '${userId}';

            if (userRole === 'CUSTOMER' && userId) {
                const odometer = new CreditScoreOdometer('credit-score-odometer', {
                    showDetails: true,
                    animationDuration: 2500
                });
                
                odometer.fetchAndDisplayScore(userId);
            }
        }

        // Admin EMI generation
        document.getElementById('generateEMIsAction')?.addEventListener('click', function(e) {
            e.preventDefault();

            if (confirm('Generate missing EMIs for all approved loans?')) {
                fetch('/api/loans/generate-emis', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' }
                })
                .then(response => response.json())
                .then(data => {
                    alert(data.message || 'EMIs generated successfully!');
                })
                .catch(error => {
                    alert('Error generating EMIs: ' + error.message);
                });
            }
        });
    </script>
    <script src="/js/credit-score-odometer.js"></script>
</body>
</html>
