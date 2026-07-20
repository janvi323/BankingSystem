<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Loan Applications — DebtHues</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
    <style>
        /* ── Reset & Base ─────────────────────────────────────────────────── */
        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: 'Inter', Arial, sans-serif;
            background: #f3f0ff; /* light purple tint */
            color: #000000;
            min-height: 100vh;
        }

        /* ── Navbar ─────────────────────────────────────────────────────────── */
        .navbar {
            background: linear-gradient(135deg, #7c3aed 0%, #8b5cf6 100%);
            padding: 0 0;
            box-shadow: 0 2px 16px rgba(124,58,237,0.25);
            position: sticky;
            top: 0;
            z-index: 100;
        }
        .navbar-content {
            max-width: 1280px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 24px;
            height: 62px;
        }
        .navbar-brand {
            font-size: 1.35rem;
            font-weight: 800;
            color: white;
            text-decoration: none;
            letter-spacing: -0.02em;
        }
        .navbar-links { display: flex; align-items: center; gap: 4px; }
        .navbar-links a {
            color: rgba(255,255,255,0.88);
            text-decoration: none;
            padding: 7px 14px;
            border-radius: 7px;
            font-size: 0.875rem;
            font-weight: 500;
            transition: background 0.2s, color 0.2s;
        }
        .navbar-links a:hover, .navbar-links a.active {
            background: rgba(255,255,255,0.18);
            color: white;
        }
        .navbar-links form button {
            background: none;
            border: none;
            color: rgba(255,255,255,0.88);
            cursor: pointer;
            font-size: 0.875rem;
            font-weight: 500;
            padding: 7px 14px;
            border-radius: 7px;
            transition: background 0.2s;
        }
        .navbar-links form button:hover { background: rgba(255,255,255,0.18); color: white; }

        /* ── Page Wrapper ───────────────────────────────────────────────────── */
        .container {
            max-width: 1280px;
            margin: 28px auto;
            padding: 0 24px 60px;
        }

        /* ── Page Header ─────────────────────────────────────────────────────── */
        .page-header {
            background: white;
            border-radius: 16px;
            padding: 28px 32px;
            margin-bottom: 24px;
            border-left: 5px solid #8b5cf6;
            box-shadow: 0 2px 16px rgba(124,58,237,0.10);
            display: flex;
            align-items: center;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 12px;
        }
        .page-header h2 {
            font-size: 1.6rem;
            font-weight: 800;
            color: #000000;
            margin-bottom: 4px;
        }
        .page-header p { font-size: 0.9rem; color: #000000; }
        .live-badge {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            background: #f3f0ff;
            border: 1.5px solid #c4b5fd;
            border-radius: 100px;
            padding: 5px 14px;
            font-size: 0.78rem;
            font-weight: 700;
            color: #7c3aed;
        }
        .live-dot {
            width: 8px; height: 8px;
            background: #22c55e;
            border-radius: 50%;
            animation: pulse-dot 1.5s ease-in-out infinite;
        }
        @keyframes pulse-dot {
            0%, 100% { opacity: 1; transform: scale(1); }
            50%       { opacity: 0.5; transform: scale(0.7); }
        }

        /* ── Portfolio Stats ──────────────────────────────────────────────────── */
        .portfolio-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
            gap: 16px;
            margin-bottom: 24px;
        }
        .stat-card {
            background: white;
            border-radius: 16px;
            padding: 22px 24px;
            box-shadow: 0 2px 12px rgba(124,58,237,0.08);
            border: 1.5px solid #ede9fe;
            position: relative;
            overflow: hidden;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .stat-card:hover { transform: translateY(-3px); box-shadow: 0 8px 24px rgba(124,58,237,0.14); }
        .stat-card::before {
            content: '';
            position: absolute;
            top: 0; left: 0; right: 0;
            height: 4px;
        }
        .stat-total::before   { background: linear-gradient(90deg, #7c3aed, #a78bfa); }
        .stat-approved::before { background: linear-gradient(90deg, #059669, #34d399); }
        .stat-pending::before  { background: linear-gradient(90deg, #d97706, #fcd34d); }
        .stat-rejected::before { background: linear-gradient(90deg, #dc2626, #f87171); }
        .stat-amount::before   { background: linear-gradient(90deg, #7c3aed, #4f46e5); }

        .stat-icon {
            width: 40px; height: 40px;
            border-radius: 10px;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.1rem;
            margin-bottom: 12px;
        }
        .icon-total    { background: #f3f0ff; color: #7c3aed; }
        .icon-approved { background: #d1fae5; color: #059669; }
        .icon-pending  { background: #fef3c7; color: #d97706; }
        .icon-rejected { background: #fee2e2; color: #dc2626; }
        .icon-amount   { background: #f3f0ff; color: #4f46e5; }

        .stat-number {
            font-size: 2rem;
            font-weight: 800;
            color: #000000;
            line-height: 1;
            margin-bottom: 6px;
        }
        .stat-label {
            font-size: 0.8rem;
            font-weight: 600;
            color: #000000;
            text-transform: uppercase;
            letter-spacing: 0.06em;
        }

        /* ── Filter Bar ──────────────────────────────────────────────────────── */
        .filter-bar {
            background: white;
            border-radius: 14px;
            padding: 16px 20px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
            flex-wrap: wrap;
            box-shadow: 0 2px 10px rgba(124,58,237,0.07);
            border: 1.5px solid #ede9fe;
        }
        .filter-btn {
            padding: 7px 18px;
            border-radius: 100px;
            border: 2px solid #ede9fe;
            background: #faf8ff;
            color: #000000;
            font-size: 0.83rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
        }
        .filter-btn:hover { border-color: #a78bfa; background: #f3f0ff; }
        .filter-btn.active { background: #7c3aed; border-color: #7c3aed; color: white; }
        .filter-spacer { flex: 1; }
        .search-box {
            padding: 8px 14px;
            border: 2px solid #ede9fe;
            border-radius: 10px;
            font-size: 0.85rem;
            font-family: 'Inter', sans-serif;
            outline: none;
            color: #000000;
            transition: border-color 0.2s;
            min-width: 220px;
        }
        .search-box:focus { border-color: #8b5cf6; }

        /* ── Table ──────────────────────────────────────────────────────────── */
        .table-card {
            background: white;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 2px 16px rgba(124,58,237,0.10);
            border: 1.5px solid #ede9fe;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        thead tr {
            background: linear-gradient(135deg, #7c3aed 0%, #8b5cf6 100%);
        }
        th {
            padding: 14px 16px;
            text-align: left;
            color: white;
            font-size: 0.8rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.07em;
            white-space: nowrap;
        }
        td {
            padding: 14px 16px;
            border-bottom: 1px solid #f3f0ff;
            font-size: 0.88rem;
            color: #000000;
            vertical-align: middle;
        }
        tbody tr { transition: background 0.15s; }
        tbody tr:hover { background: #faf8ff; }
        tbody tr:last-child td { border-bottom: none; }

        /* ── Status Pills ───────────────────────────────────────────────────── */
        .status-pill {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            padding: 5px 14px;
            border-radius: 100px;
            font-size: 0.76rem;
            font-weight: 700;
            letter-spacing: 0.04em;
            white-space: nowrap;
        }
        .status-pending  { background: #fef3c7; color: #000000; border: 2px solid #f59e0b; }
        .status-approved { background: #d1fae5; color: #000000; border: 2px solid #10b981; }
        .status-rejected { background: #fee2e2; color: #000000; border: 2px solid #ef4444; }
        .status-manual_review { background: #f3f0ff; color: #000000; border: 2px solid #8b5cf6; }
        .status-dot {
            width: 7px; height: 7px;
            border-radius: 50%;
            display: inline-block;
        }
        .dot-pending  { background: #f59e0b; }
        .dot-approved { background: #10b981; }
        .dot-rejected { background: #ef4444; }
        .dot-manual_review { background: #8b5cf6; }

        /* ── Amount cell ─────────────────────────────────────────────────────── */
        .loan-amount {
            font-weight: 800;
            color: #000000;
            font-size: 0.95rem;
        }

        /* ── ID badge ─────────────────────────────────────────────────────── */
        .loan-id-badge {
            background: #f3f0ff;
            color: #7c3aed;
            padding: 3px 10px;
            border-radius: 6px;
            font-size: 0.78rem;
            font-weight: 700;
            font-family: monospace;
        }

        /* ── Admin Actions ───────────────────────────────────────────────────── */
        .admin-actions { display: flex; gap: 6px; }
        .btn-approve, .btn-reject {
            padding: 6px 14px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 0.78rem;
            font-weight: 700;
            transition: opacity 0.2s, transform 0.15s;
        }
        .btn-approve { background: #059669; color: white; }
        .btn-reject  { background: #dc2626; color: white; }
        .btn-approve:hover, .btn-reject:hover { opacity: 0.85; transform: scale(1.04); }

        /* ── Empty State ─────────────────────────────────────────────────────── */
        .no-loans {
            padding: 64px 24px;
            text-align: center;
            color: #000000;
        }
        .no-loans .empty-icon {
            font-size: 4rem;
            margin-bottom: 1rem;
        }
        .no-loans h3 { font-size: 1.3rem; font-weight: 700; color: #000000; margin-bottom: 8px; }
        .no-loans p  { color: #000000; margin-bottom: 20px; }
        .btn-apply {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            background: linear-gradient(135deg, #7c3aed, #8b5cf6);
            color: white;
            text-decoration: none;
            padding: 12px 28px;
            border-radius: 12px;
            font-weight: 700;
            font-size: 0.9rem;
            transition: opacity 0.2s;
        }
        .btn-apply:hover { opacity: 0.88; }

        /* ── Loading ─────────────────────────────────────────────────────────── */
        .loading-row td {
            text-align: center;
            padding: 48px;
            color: #7c3aed;
            font-weight: 600;
        }
        .spin { animation: spin 0.9s linear infinite; display: inline-block; }
        @keyframes spin { to { transform: rotate(360deg); } }

        /* ── Toast ───────────────────────────────────────────────────────────── */
        .toast {
            position: fixed;
            bottom: 24px; right: 24px;
            background: #7c3aed;
            color: white;
            padding: 12px 22px;
            border-radius: 12px;
            font-weight: 600;
            font-size: 0.88rem;
            box-shadow: 0 4px 20px rgba(124,58,237,0.4);
            opacity: 0;
            transform: translateY(16px);
            transition: all 0.35s;
            z-index: 9999;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .toast.show { opacity: 1; transform: translateY(0); }
        .toast.success { background: #059669; box-shadow: 0 4px 20px rgba(5,150,105,0.3); }
        .toast.error   { background: #dc2626; box-shadow: 0 4px 20px rgba(220,38,38,0.3); }

        /* ── Progress bar for amounts ─────────────────────────────────────────── */
        .progress-bar-wrap { height: 4px; background: #ede9fe; border-radius: 100px; margin-top: 8px; overflow: hidden; }
        .progress-bar-fill { height: 100%; border-radius: 100px; background: linear-gradient(90deg, #7c3aed, #a78bfa); transition: width 0.8s ease; }

        @media (max-width: 768px) {
            .portfolio-grid { grid-template-columns: 1fr 1fr; }
            .filter-bar { flex-direction: column; align-items: flex-start; }
            .search-box { width: 100%; }
            .page-header { flex-direction: column; align-items: flex-start; }
            th, td { padding: 10px; font-size: 0.8rem; }
        }
    </style>
</head>
<body>

<!-- ── Navbar ─────────────────────────────────────────────────────────────── -->
<nav class="navbar">
    <div class="navbar-content">
        <a href="/dashboard" class="navbar-brand">&#127912; DebtHues</a>
        <div class="navbar-links">
            <a href="/dashboard">Dashboard</a>
            <a href="/loans" class="active">My Loans</a>
            <a href="/apply-loan">Apply Loan</a>
            <a href="/emi">EMI Payments</a>
            <a href="/profile">Profile</a>
            <form action="/perform_logout" method="post" style="display:inline;">
                <button type="submit">Logout</button>
            </form>
        </div>
    </div>
</nav>

<!-- ── Main Container ─────────────────────────────────────────────────────── -->
<div class="container">

    <!-- Page Header -->
    <div class="page-header">
        <div>
            <h2 id="pageTitle">My Loan Applications</h2>
            <p id="pageDescription">Track and manage your loan application history</p>
        </div>
        <div style="display:flex;align-items:center;gap:12px;flex-wrap:wrap;">
            <div class="live-badge">
                <span class="live-dot"></span>
                LIVE &bull; auto-refresh
            </div>
            <a href="/apply-loan" class="btn-apply"><i class="fas fa-plus"></i> New Application</a>
        </div>
    </div>

    <!-- ── Portfolio Statistics ─────────────────────────────────────────── -->
    <div class="portfolio-grid" id="portfolioGrid">
        <div class="stat-card stat-total">
            <div class="stat-icon icon-total"><i class="fas fa-layer-group"></i></div>
            <div class="stat-number" id="statTotal">0</div>
            <div class="stat-label">Total Applications</div>
        </div>
        <div class="stat-card stat-approved">
            <div class="stat-icon icon-approved"><i class="fas fa-check-circle"></i></div>
            <div class="stat-number" id="statApproved">0</div>
            <div class="stat-label">Approved</div>
        </div>
        <div class="stat-card stat-pending">
            <div class="stat-icon icon-pending"><i class="fas fa-hourglass-half"></i></div>
            <div class="stat-number" id="statPending">0</div>
            <div class="stat-label">Pending / Under Review</div>
        </div>
        <div class="stat-card stat-rejected">
            <div class="stat-icon icon-rejected"><i class="fas fa-times-circle"></i></div>
            <div class="stat-number" id="statRejected">0</div>
            <div class="stat-label">Rejected</div>
        </div>
        <div class="stat-card stat-amount">
            <div class="stat-icon icon-amount"><i class="fas fa-rupee-sign"></i></div>
            <div class="stat-number" id="statAmount">&#8377;0</div>
            <div class="stat-label">Total Amount Applied</div>
            <div class="progress-bar-wrap"><div class="progress-bar-fill" id="amountBar" style="width:0%"></div></div>
        </div>
    </div>

    <!-- ── Filter Bar ──────────────────────────────────────────────────── -->
    <div class="filter-bar">
        <button class="filter-btn active" id="filterAll"      onclick="applyFilter('ALL')">All</button>
        <button class="filter-btn"        id="filterPending"  onclick="applyFilter('PENDING')">&#9679; Pending</button>
        <button class="filter-btn"        id="filterApproved" onclick="applyFilter('APPROVED')">&#10003; Approved</button>
        <button class="filter-btn"        id="filterReview"   onclick="applyFilter('MANUAL_REVIEW')">&#9203; Under Review</button>
        <button class="filter-btn"        id="filterRejected" onclick="applyFilter('REJECTED')">&#10005; Rejected</button>
        <div class="filter-spacer"></div>
        <input type="text" class="search-box" id="searchBox" placeholder="&#128269; Search by purpose or amount..." oninput="renderTable()">
    </div>

    <!-- ── Loans Table ─────────────────────────────────────────────────── -->
    <div class="table-card">
        <table id="loanTable">
            <thead>
                <tr>
                    <th>App. ID</th>
                    <th>Customer</th>
                    <th>Amount</th>
                    <th>Purpose</th>
                    <th>Tenure</th>
                    <th>Interest Rate</th>
                    <th>Bank</th>
                    <th>Status</th>
                    <th>Date Applied</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody id="loanTableBody">
                <tr class="loading-row">
                    <td colspan="10">
                        <i class="fas fa-circle-notch spin"></i>&nbsp; Loading your loan applications...
                    </td>
                </tr>
            </tbody>
        </table>
        <div id="noLoans" class="no-loans" style="display:none;">
            <div class="empty-icon">&#128196;</div>
            <h3>No loan applications yet</h3>
            <p>You haven't applied for any loans. Get started with your first application.</p>
            <a href="/apply-loan" class="btn-apply"><i class="fas fa-plus"></i> Apply for a Loan</a>
        </div>
    </div>

</div><!-- /container -->

<!-- Toast -->
<div class="toast" id="toast"></div>

<script>
// ── State ─────────────────────────────────────────────────────────────────
var allLoans    = [];
var currentUser = null;
var activeFilter = 'ALL';
var refreshTimer = null;

// ── Toast ─────────────────────────────────────────────────────────────────
function showToast(msg, type) {
    var t = document.getElementById('toast');
    t.className = 'toast ' + (type || '');
    t.innerHTML = '<i class="fas fa-' + (type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle') + '"></i> ' + msg;
    t.classList.add('show');
    setTimeout(function() { t.classList.remove('show'); }, 4000);
}

// ── Format Rupee (always correct symbol) ─────────────────────────────────
function fmtRupee(n) {
    if (!n && n !== 0) return '\u20b90';
    return '\u20b9' + Math.round(n).toLocaleString('en-IN');
}

// ── Update Stats ──────────────────────────────────────────────────────────
function updateStats(loans) {
    var total    = loans.length;
    var approved = loans.filter(function(l) { return l.status === 'APPROVED'; }).length;
    var pending  = loans.filter(function(l) { return l.status === 'PENDING' || l.status === 'MANUAL_REVIEW'; }).length;
    var rejected = loans.filter(function(l) { return l.status === 'REJECTED' || l.status === 'AUTO_REJECTED'; }).length;
    var totalAmt = loans.reduce(function(s, l) { return s + (l.amount || 0); }, 0);

    animateNumber('statTotal',    total);
    animateNumber('statApproved', approved);
    animateNumber('statPending',  pending);
    animateNumber('statRejected', rejected);
    document.getElementById('statAmount').textContent = fmtRupee(totalAmt);

    // Progress bar: approved / total
    var pct = total > 0 ? Math.round((approved / total) * 100) : 0;
    document.getElementById('amountBar').style.width = pct + '%';
}

// ── Animate counter ───────────────────────────────────────────────────────
function animateNumber(id, target) {
    var el  = document.getElementById(id);
    var cur = parseInt(el.textContent) || 0;
    if (cur === target) return;
    var step = target > cur ? 1 : -1;
    var interval = setInterval(function() {
        cur += step;
        el.textContent = cur;
        if (cur === target) clearInterval(interval);
    }, 30);
}

// ── Status pill HTML ──────────────────────────────────────────────────────
function statusPill(status) {
    var label = status.replace('_', ' ');
    var cls   = status.toLowerCase().replace('_', '_');
    var dot   = 'dot-' + cls;
    if (status === 'AUTO_REJECTED') { cls = 'rejected'; dot = 'dot-rejected'; label = 'Rejected'; }
    return '<span class="status-pill status-' + cls + '">'
         + '<span class="status-dot ' + dot + '"></span>'
         + label + '</span>';
}

// ── Apply Filter ──────────────────────────────────────────────────────────
function applyFilter(f) {
    activeFilter = f;
    document.querySelectorAll('.filter-btn').forEach(function(b) { b.classList.remove('active'); });
    var map = { ALL:'filterAll', PENDING:'filterPending', APPROVED:'filterApproved', MANUAL_REVIEW:'filterReview', REJECTED:'filterRejected' };
    if (map[f]) document.getElementById(map[f]).classList.add('active');
    renderTable();
}

// ── Render Table ──────────────────────────────────────────────────────────
function renderTable() {
    var search  = (document.getElementById('searchBox').value || '').toLowerCase();
    var tbody   = document.getElementById('loanTableBody');
    var noLoans = document.getElementById('noLoans');

    var filtered = allLoans.filter(function(l) {
        var statusMatch = activeFilter === 'ALL'
            || l.status === activeFilter
            || (activeFilter === 'REJECTED' && l.status === 'AUTO_REJECTED')
            || (activeFilter === 'PENDING'  && l.status === 'MANUAL_REVIEW');
        var searchMatch = !search
            || (l.purpose && l.purpose.toLowerCase().indexOf(search) >= 0)
            || String(l.amount).indexOf(search) >= 0
            || (l.selectedBankName && l.selectedBankName.toLowerCase().indexOf(search) >= 0)
            || String(l.id).indexOf(search) >= 0;
        return statusMatch && searchMatch;
    });

    if (filtered.length === 0 && allLoans.length === 0) {
        tbody.innerHTML = '';
        noLoans.style.display = 'block';
        return;
    }

    noLoans.style.display = 'none';

    var isAdmin = currentUser && currentUser.role === 'ADMIN';
    var rows = '';

    if (filtered.length === 0) {
        rows = '<tr><td colspan="10" style="text-align:center;padding:40px;color:#7c3aed;font-weight:600;">No applications match this filter.</td></tr>';
    } else {
        filtered.forEach(function(loan) {
            var date = loan.applicationDate
                ? new Date(loan.applicationDate).toLocaleDateString('en-IN', { day:'2-digit', month:'short', year:'numeric' })
                : '—';
            var actions = (isAdmin && (loan.status === 'PENDING' || loan.status === 'MANUAL_REVIEW'))
                ? '<div class="admin-actions">'
                  + '<button class="btn-approve" onclick="updateLoanStatus(' + loan.id + ',\'APPROVED\')"><i class="fas fa-check"></i> Approve</button>'
                  + '<button class="btn-reject"  onclick="updateLoanStatus(' + loan.id + ',\'REJECTED\')"><i class="fas fa-times"></i> Reject</button>'
                  + '</div>'
                : '<span style="color:#a78bfa;font-size:0.8rem;">—</span>';

            var rate = loan.interestRate ? loan.interestRate + '%' : '—';
            var bank = loan.selectedBankName || '<span style="color:#a78bfa;font-size:0.8rem;">Best Avail.</span>';

            rows += '<tr>'
                + '<td><span class="loan-id-badge">#' + loan.id + '</span></td>'
                + '<td><strong>' + (loan.customer ? loan.customer.name : 'You') + '</strong></td>'
                + '<td class="loan-amount">' + fmtRupee(loan.amount) + '</td>'
                + '<td>' + (loan.purpose || '—') + '</td>'
                + '<td>' + (loan.tenure || '—') + ' mo</td>'
                + '<td style="font-weight:600;color:#7c3aed;">' + rate + '</td>'
                + '<td>' + bank + '</td>'
                + '<td>' + statusPill(loan.status) + '</td>'
                + '<td style="white-space:nowrap;">' + date + '</td>'
                + '<td>' + actions + '</td>'
                + '</tr>';
        });
    }

    tbody.innerHTML = rows;
}

// ── Load Loans ────────────────────────────────────────────────────────────
function loadLoans(silent) {
    var isAdmin = currentUser && currentUser.role === 'ADMIN';
    var url = isAdmin ? '/api/loans/all' : '/api/loans/my-loans';

    fetch(url)
    .then(function(r) { return r.json(); })
    .then(function(loans) {
        var prevTotal = allLoans.length;
        allLoans = loans;
        updateStats(loans);
        renderTable();
        // Notify if new loan appeared
        if (!silent && prevTotal > 0 && loans.length > prevTotal) {
            showToast('New application detected!', 'success');
        }
    })
    .catch(function() {
        if (!silent) {
            document.getElementById('loanTableBody').innerHTML =
                '<tr><td colspan="10" style="text-align:center;padding:40px;color:#dc2626;font-weight:600;">Failed to load loans. Please refresh.</td></tr>';
        }
    });
}

// ── Admin: update status ──────────────────────────────────────────────────
function updateLoanStatus(loanId, status) {
    var comments = prompt('Enter comments for ' + status.toLowerCase() + ' loan #' + loanId + ':');
    if (comments === null) return;
    if (!comments.trim()) { showToast('Comments are required.', 'error'); return; }

    fetch('/api/loans/' + loanId + '/status', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status: status, comments: comments.trim() })
    })
    .then(function(r) {
        if (!r.ok) throw new Error('HTTP ' + r.status);
        return r.text();
    })
    .then(function() {
        showToast('Loan #' + loanId + ' ' + status.toLowerCase() + ' successfully.', 'success');
        loadLoans(true);
    })
    .catch(function(e) {
        showToast('Failed: ' + e.message, 'error');
    });
}

// ── Init ──────────────────────────────────────────────────────────────────
fetch('/api/auth/current')
.then(function(r) { return r.json(); })
.then(function(user) {
    currentUser = user;
    if (user.role === 'ADMIN') {
        document.getElementById('pageTitle').textContent       = 'All Loan Applications';
        document.getElementById('pageDescription').textContent = 'Manage and review all customer loan applications';
    } else {
        document.getElementById('pageTitle').textContent       = 'My Loan Applications';
        document.getElementById('pageDescription').textContent = 'Track your loan application history and status';
    }
    loadLoans(false);
    // Auto-refresh every 15 seconds
    refreshTimer = setInterval(function() { loadLoans(true); }, 15000);
})
.catch(function() {
    document.getElementById('loanTableBody').innerHTML =
        '<tr><td colspan="10" style="text-align:center;padding:40px;color:#dc2626;font-weight:600;"><a href="/login" style="color:#7c3aed;">Please login</a> to view your loans.</td></tr>';
});

// Cleanup on page leave
window.addEventListener('beforeunload', function() {
    if (refreshTimer) clearInterval(refreshTimer);
});
</script>

<%@ include file="fragments/hue-chatbot-widget.jspf" %>
</body>
</html>
