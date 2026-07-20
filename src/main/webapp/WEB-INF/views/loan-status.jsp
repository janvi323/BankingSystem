<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Loan Application Status — DebtHues</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
    <style>
        /* ════════════════════════════════════════════════════════════════════
           LIGHT THEME — Purple & White, Black Typography
        ════════════════════════════════════════════════════════════════════ */
        *, *::before, *::after { margin: 0; padding: 0; box-sizing: border-box; }

        :root {
            --purple:        #6366f1;
            --purple-dark:   #4f46e5;
            --purple-light:  #ede9fe;
            --purple-mid:    #c4b5fd;
            --green:         #059669;
            --green-light:   #d1fae5;
            --red:           #dc2626;
            --red-light:     #fee2e2;
            --amber:         #d97706;
            --amber-light:   #fef3c7;
            --black:         #000000;
            --ink:           #111111;
            --body-bg:       #f9f8fc;
            --card-bg:       #ffffff;
            --border:        #e5e3f0;
            --shadow:        0 2px 20px rgba(99,102,241,0.10);
            --shadow-lg:     0 8px 40px rgba(99,102,241,0.15);
        }

        body {
            font-family: 'Inter', sans-serif;
            background: var(--body-bg);
            color: var(--black);
            min-height: 100vh;
        }

        /* ── Navbar ─────────────────────────────────────────────────────── */
        .navbar {
            background: var(--purple);
            height: 60px;
            display: flex;
            align-items: center;
            padding: 0 2rem;
            justify-content: space-between;
            box-shadow: 0 2px 12px rgba(99,102,241,0.25);
            position: sticky;
            top: 0;
            z-index: 100;
        }
        .nav-brand { color: white; font-size: 1.2rem; font-weight: 800; text-decoration: none; }
        .nav-links { display: flex; gap: 6px; }
        .nav-links a {
            color: rgba(255,255,255,0.88);
            text-decoration: none;
            padding: 6px 14px;
            border-radius: 8px;
            font-size: 0.85rem;
            font-weight: 500;
            transition: background 0.18s;
        }
        .nav-links a:hover { background: rgba(255,255,255,0.18); color: white; }

        /* ── Page ───────────────────────────────────────────────────────── */
        .page {
            max-width: 820px;
            margin: 0 auto;
            padding: 2.5rem 1.25rem 5rem;
        }

        /* ── Loading Screen ─────────────────────────────────────────────── */
        #loadingScreen {
            text-align: center;
            padding: 6rem 2rem;
        }
        .loading-spinner {
            width: 52px; height: 52px;
            border: 4px solid var(--purple-light);
            border-top-color: var(--purple);
            border-radius: 50%;
            animation: spin 0.75s linear infinite;
            margin: 0 auto 1.5rem;
        }
        @keyframes spin { to { transform: rotate(360deg); } }
        .loading-title { font-size: 1.15rem; font-weight: 700; color: var(--ink); margin-bottom: 0.4rem; }
        .loading-sub   { font-size: 0.88rem; color: #555555; }

        /* ── Decision Banner ─────────────────────────────────────────────── */
        .banner {
            border-radius: 20px;
            padding: 2.5rem 2rem;
            text-align: center;
            margin-bottom: 1.5rem;
            position: relative;
            overflow: hidden;
        }
        /* APPROVED */
        .banner-approved {
            background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
            border: 2px solid #6ee7b7;
        }
        /* REVIEW */
        .banner-review {
            background: linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%);
            border: 2px solid #fcd34d;
        }
        /* REJECTED */
        .banner-rejected {
            background: linear-gradient(135deg, #fff1f2 0%, #fee2e2 100%);
            border: 2px solid #fca5a5;
        }

        .banner-icon  { font-size: 3.5rem; margin-bottom: 0.75rem; display: block; }
        .banner-title { font-size: 1.9rem; font-weight: 900; color: var(--ink); margin-bottom: 0.5rem; }
        .banner-sub   { font-size: 0.95rem; color: #333333; line-height: 1.6; max-width: 520px; margin: 0 auto; }

        .app-id {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            margin-top: 1.1rem;
            background: white;
            border: 1.5px solid var(--border);
            border-radius: 100px;
            padding: 5px 16px;
            font-size: 0.8rem;
            font-weight: 700;
            color: var(--ink);
            font-family: 'Courier New', monospace;
        }

        /* ── Score Row ──────────────────────────────────────────────────── */
        .score-row {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 14px;
            margin-bottom: 1.5rem;
        }
        .score-card {
            background: var(--card-bg);
            border: 1.5px solid var(--border);
            border-radius: 14px;
            padding: 1.25rem 1rem;
            text-align: center;
            box-shadow: var(--shadow);
        }
        .score-val {
            font-size: 1.9rem;
            font-weight: 900;
            color: var(--purple);
            line-height: 1;
            margin-bottom: 0.3rem;
        }
        .score-lbl {
            font-size: 0.72rem;
            font-weight: 700;
            color: #333333;
            text-transform: uppercase;
            letter-spacing: 0.07em;
        }

        /* ── Section Card ────────────────────────────────────────────────── */
        .section {
            background: var(--card-bg);
            border: 1.5px solid var(--border);
            border-radius: 16px;
            margin-bottom: 1.25rem;
            overflow: hidden;
            box-shadow: var(--shadow);
        }
        .section-head {
            padding: 1.1rem 1.5rem;
            border-bottom: 1.5px solid var(--border);
            display: flex;
            align-items: center;
            gap: 0.65rem;
            background: #faf9ff;
        }
        .section-head-icon {
            width: 34px; height: 34px;
            border-radius: 9px;
            display: flex; align-items: center; justify-content: center;
            font-size: 0.95rem;
            flex-shrink: 0;
        }
        .icon-purple  { background: var(--purple-light); color: var(--purple-dark); }
        .icon-green   { background: var(--green-light);  color: var(--green); }
        .icon-red     { background: var(--red-light);    color: var(--red); }
        .icon-amber   { background: var(--amber-light);  color: var(--amber); }

        .section-head-title { font-size: 0.95rem; font-weight: 700; color: var(--ink); }
        .section-body { padding: 1.5rem; }

        /* ── Loan Summary Grid ──────────────────────────────────────────── */
        .summary-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(175px, 1fr));
            gap: 12px;
        }
        .summary-item {
            background: var(--body-bg);
            border: 1.5px solid var(--border);
            border-radius: 12px;
            padding: 0.9rem 1rem;
        }
        .summary-lbl {
            font-size: 0.7rem;
            font-weight: 700;
            color: #555555;
            text-transform: uppercase;
            letter-spacing: 0.07em;
            margin-bottom: 0.35rem;
        }
        .summary-val {
            font-size: 1.05rem;
            font-weight: 800;
            color: var(--ink);
        }
        .summary-val.accent { color: var(--purple); }

        /* ── AI Explanation ─────────────────────────────────────────────── */
        .ai-chip {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            background: var(--purple-light);
            color: var(--purple-dark);
            border-radius: 100px;
            padding: 4px 12px;
            font-size: 0.72rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.06em;
            margin-bottom: 0.85rem;
        }
        .ai-text {
            font-size: 0.92rem;
            line-height: 1.75;
            color: var(--ink);
        }

        /* ── Rejection Reasons ──────────────────────────────────────────── */
        .reason-list, .rec-list { list-style: none; display: flex; flex-direction: column; gap: 8px; }
        .reason-item {
            display: flex;
            align-items: flex-start;
            gap: 10px;
            padding: 0.85rem 1rem;
            background: var(--red-light);
            border: 1.5px solid #fca5a5;
            border-radius: 10px;
            font-size: 0.88rem;
            line-height: 1.55;
            color: var(--ink);
            font-weight: 500;
        }
        .reason-icon { color: var(--red); flex-shrink: 0; margin-top: 2px; font-size: 0.95rem; }

        /* ── Recommendations ────────────────────────────────────────────── */
        .rec-item {
            display: flex;
            align-items: flex-start;
            gap: 10px;
            padding: 0.85rem 1rem;
            background: var(--green-light);
            border: 1.5px solid #6ee7b7;
            border-radius: 10px;
            font-size: 0.88rem;
            line-height: 1.55;
            color: var(--ink);
            font-weight: 500;
        }
        .rec-icon { color: var(--green); flex-shrink: 0; margin-top: 2px; font-size: 0.95rem; }

        /* ── Score Breakdown ────────────────────────────────────────────── */
        .breakdown-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
        }
        .breakdown-item {
            padding: 0.75rem 1rem;
            background: var(--body-bg);
            border: 1.5px solid var(--border);
            border-radius: 10px;
            font-size: 0.84rem;
            color: var(--ink);
            font-weight: 500;
        }

        /* ── EMI Success Message ─────────────────────────────────────────── */
        .emi-success {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 1.1rem 1.25rem;
            background: var(--green-light);
            border: 1.5px solid #6ee7b7;
            border-radius: 12px;
            font-size: 0.9rem;
            font-weight: 600;
            color: var(--ink);
        }
        .emi-icon { font-size: 1.4rem; flex-shrink: 0; }

        /* ── Action Row ─────────────────────────────────────────────────── */
        .action-row {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 12px;
            margin-top: 0.5rem;
        }
        .action-btn {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 7px;
            padding: 0.9rem 1rem;
            border-radius: 12px;
            font-size: 0.9rem;
            font-weight: 700;
            text-decoration: none;
            border: none;
            cursor: pointer;
            transition: all 0.18s;
            color: var(--ink);
        }
        .btn-primary {
            background: var(--purple);
            color: white !important;
            box-shadow: 0 4px 14px rgba(99,102,241,0.35);
        }
        .btn-primary:hover { background: var(--purple-dark); transform: translateY(-1px); }
        .btn-outline {
            background: white;
            border: 2px solid var(--purple);
            color: var(--purple) !important;
        }
        .btn-outline:hover { background: var(--purple-light); }
        .btn-ghost {
            background: white;
            border: 2px solid var(--border);
            color: var(--ink) !important;
        }
        .btn-ghost:hover { background: var(--body-bg); border-color: var(--purple-mid); }

        /* ── Risk Badge ─────────────────────────────────────────────────── */
        .risk-badge {
            display: inline-block;
            padding: 4px 14px;
            border-radius: 100px;
            font-size: 0.78rem;
            font-weight: 700;
            color: var(--ink);
        }
        .risk-LOW    { background: var(--green-light);  border: 1.5px solid #6ee7b7; }
        .risk-MEDIUM { background: var(--amber-light);  border: 1.5px solid #fcd34d; }
        .risk-HIGH   { background: var(--red-light);    border: 1.5px solid #fca5a5; }
        .risk-VERY_HIGH { background: #fee2e2; border: 1.5px solid #f87171; }

        @media (max-width: 600px) {
            .score-row     { grid-template-columns: 1fr 1fr; }
            .action-row    { grid-template-columns: 1fr; }
            .breakdown-grid{ grid-template-columns: 1fr; }
            .banner-title  { font-size: 1.4rem; }
            .page          { padding: 1.5rem 1rem 4rem; }
        }
    </style>
</head>
<body>

<!-- ── Navbar ─────────────────────────────────────────────────────────────── -->
<nav class="navbar">
    <a href="/dashboard" class="nav-brand">&#127912; DebtHues</a>
    <div class="nav-links">
        <a href="/dashboard">Dashboard</a>
        <a href="/loans">My Loans</a>
        <a href="/apply-loan">Apply Loan</a>
        <a href="/profile">Profile</a>
    </div>
</nav>

<!-- ── Page ───────────────────────────────────────────────────────────────── -->
<div class="page" id="mainPage">

    <!-- Loading (shown while JS reads sessionStorage) -->
    <div id="loadingScreen">
        <div class="loading-spinner"></div>
        <div class="loading-title">&#129302; AI Decision Engine Processing</div>
        <div class="loading-sub">Evaluating your financial profile&hellip;</div>
    </div>

    <!-- ── APPROVED LAYOUT ──────────────────────────────────────────────── -->
    <div id="approvedLayout" style="display:none;">

        <!-- Banner -->
        <div class="banner banner-approved">
            <span class="banner-icon">&#9989;</span>
            <div class="banner-title">Loan Approved!</div>
            <div class="banner-sub">
                Congratulations! Your application has been successfully approved by our AI underwriting engine.
                Your EMI schedule has been generated.
            </div>
            <div class="app-id"><i class="fas fa-hashtag"></i> <span id="appIdApproved">—</span></div>
        </div>

        <!-- Score Row -->
        <div class="score-row">
            <div class="score-card">
                <div class="score-val" id="aiScoreApproved">—</div>
                <div class="score-lbl">AI Confidence</div>
            </div>
            <div class="score-card">
                <div class="score-val" id="healthScoreApproved">—</div>
                <div class="score-lbl">Financial Health</div>
            </div>
            <div class="score-card">
                <div id="riskApproved" style="margin-bottom:0.3rem;">—</div>
                <div class="score-lbl">Risk Profile</div>
            </div>
        </div>

        <!-- EMI Success Notice -->
        <div class="emi-success" style="margin-bottom:1.25rem;">
            <span class="emi-icon">&#128197;</span>
            <span id="emiSuccessMsg">Your EMI schedule has been generated. Visit <strong>EMI Payments</strong> to view your repayment plan.</span>
        </div>

        <!-- Loan Summary -->
        <div class="section">
            <div class="section-head">
                <div class="section-head-icon icon-purple"><i class="fas fa-file-invoice"></i></div>
                <div class="section-head-title">Loan Application Summary</div>
            </div>
            <div class="section-body">
                <div class="summary-grid" id="summaryGridApproved"></div>
            </div>
        </div>

        <!-- AI Explanation (positive only) -->
        <div class="section">
            <div class="section-head">
                <div class="section-head-icon icon-green"><i class="fas fa-robot"></i></div>
                <div class="section-head-title">AI Decision Explanation</div>
            </div>
            <div class="section-body">
                <div class="ai-chip"><i class="fas fa-microchip"></i> AI Generated</div>
                <div class="ai-text" id="aiTextApproved"></div>
            </div>
        </div>

        <!-- Score Breakdown -->
        <div class="section" id="breakdownApprovedSection" style="display:none;">
            <div class="section-head">
                <div class="section-head-icon icon-purple"><i class="fas fa-chart-bar"></i></div>
                <div class="section-head-title">AI Score Breakdown</div>
            </div>
            <div class="section-body">
                <div class="breakdown-grid" id="breakdownApproved"></div>
            </div>
        </div>

        <!-- Actions -->
        <div class="section">
            <div class="section-body">
                <div class="action-row">
                    <a href="/emi"        class="action-btn btn-primary"><i class="fas fa-calendar-check"></i> View EMI Plan</a>
                    <a href="/loans"      class="action-btn btn-outline"><i class="fas fa-list"></i> My Loans</a>
                    <a href="/dashboard"  class="action-btn btn-ghost"><i class="fas fa-home"></i> Dashboard</a>
                </div>
            </div>
        </div>
    </div>
    <!-- /approvedLayout -->

    <!-- ── MANUAL REVIEW LAYOUT ─────────────────────────────────────────── -->
    <div id="reviewLayout" style="display:none;">

        <div class="banner banner-review">
            <span class="banner-icon">&#9203;</span>
            <div class="banner-title">Under Manual Review</div>
            <div class="banner-sub">
                Your application is in our review queue. An underwriter will contact you within
                <strong>2 business days</strong> with a final decision.
            </div>
            <div class="app-id"><i class="fas fa-hashtag"></i> <span id="appIdReview">—</span></div>
        </div>

        <div class="score-row">
            <div class="score-card">
                <div class="score-val" id="aiScoreReview">—</div>
                <div class="score-lbl">AI Confidence</div>
            </div>
            <div class="score-card">
                <div class="score-val" id="healthScoreReview">—</div>
                <div class="score-lbl">Financial Health</div>
            </div>
            <div class="score-card">
                <div id="riskReview" style="margin-bottom:0.3rem;">—</div>
                <div class="score-lbl">Risk Profile</div>
            </div>
        </div>

        <div class="section">
            <div class="section-head">
                <div class="section-head-icon icon-purple"><i class="fas fa-file-invoice"></i></div>
                <div class="section-head-title">Loan Application Summary</div>
            </div>
            <div class="section-body">
                <div class="summary-grid" id="summaryGridReview"></div>
            </div>
        </div>

        <div class="section">
            <div class="section-head">
                <div class="section-head-icon icon-amber"><i class="fas fa-robot"></i></div>
                <div class="section-head-title">AI Assessment</div>
            </div>
            <div class="section-body">
                <div class="ai-chip"><i class="fas fa-microchip"></i> AI Generated</div>
                <div class="ai-text" id="aiTextReview"></div>
            </div>
        </div>

        <div class="section" id="recsReviewSection" style="display:none;">
            <div class="section-head">
                <div class="section-head-icon icon-amber"><i class="fas fa-lightbulb"></i></div>
                <div class="section-head-title">Recommendations to Strengthen Your Application</div>
            </div>
            <div class="section-body">
                <ul class="rec-list" id="recsReview"></ul>
            </div>
        </div>

        <div class="section">
            <div class="section-body">
                <div class="action-row">
                    <a href="/loans"     class="action-btn btn-primary"><i class="fas fa-list"></i> My Loans</a>
                    <a href="/profile"   class="action-btn btn-outline"><i class="fas fa-user"></i> Update Profile</a>
                    <a href="/dashboard" class="action-btn btn-ghost"><i class="fas fa-home"></i> Dashboard</a>
                </div>
            </div>
        </div>
    </div>
    <!-- /reviewLayout -->

    <!-- ── REJECTED LAYOUT ──────────────────────────────────────────────── -->
    <div id="rejectedLayout" style="display:none;">

        <div class="banner banner-rejected">
            <span class="banner-icon">&#10060;</span>
            <div class="banner-title">Application Not Approved</div>
            <div class="banner-sub">
                Our AI engine has reviewed your financial profile and is unable to approve this
                application at this time. Please review the reasons below and our recommendations
                to improve your eligibility.
            </div>
            <div class="app-id"><i class="fas fa-hashtag"></i> <span id="appIdRejected">—</span></div>
        </div>

        <div class="score-row">
            <div class="score-card">
                <div class="score-val" id="aiScoreRejected">—</div>
                <div class="score-lbl">AI Score</div>
            </div>
            <div class="score-card">
                <div class="score-val" id="healthScoreRejected">—</div>
                <div class="score-lbl">Financial Health</div>
            </div>
            <div class="score-card">
                <div id="riskRejected" style="margin-bottom:0.3rem;">—</div>
                <div class="score-lbl">Risk Profile</div>
            </div>
        </div>

        <!-- REJECTION REASONS — strict section, only shown when rejected -->
        <div class="section" id="reasonsSection">
            <div class="section-head">
                <div class="section-head-icon icon-red"><i class="fas fa-times-circle"></i></div>
                <div class="section-head-title">Why Your Application Was Not Approved</div>
            </div>
            <div class="section-body">
                <ul class="reason-list" id="reasonsList"></ul>
            </div>
        </div>

        <!-- RECOMMENDATIONS — improvement tips -->
        <div class="section" id="recsRejectedSection" style="display:none;">
            <div class="section-head">
                <div class="section-head-icon icon-green"><i class="fas fa-lightbulb"></i></div>
                <div class="section-head-title">How to Improve Your Eligibility</div>
            </div>
            <div class="section-body">
                <ul class="rec-list" id="recsRejected"></ul>
            </div>
        </div>

        <!-- Loan Summary -->
        <div class="section">
            <div class="section-head">
                <div class="section-head-icon icon-purple"><i class="fas fa-file-invoice"></i></div>
                <div class="section-head-title">Application Details</div>
            </div>
            <div class="section-body">
                <div class="summary-grid" id="summaryGridRejected"></div>
            </div>
        </div>

        <!-- AI Explanation -->
        <div class="section">
            <div class="section-head">
                <div class="section-head-icon icon-red"><i class="fas fa-robot"></i></div>
                <div class="section-head-title">AI Decision Explanation</div>
            </div>
            <div class="section-body">
                <div class="ai-chip"><i class="fas fa-microchip"></i> AI Generated</div>
                <div class="ai-text" id="aiTextRejected"></div>
            </div>
        </div>

        <!-- Score Breakdown -->
        <div class="section" id="breakdownRejectedSection" style="display:none;">
            <div class="section-head">
                <div class="section-head-icon icon-purple"><i class="fas fa-chart-bar"></i></div>
                <div class="section-head-title">AI Score Breakdown</div>
            </div>
            <div class="section-body">
                <div class="breakdown-grid" id="breakdownRejected"></div>
            </div>
        </div>

        <div class="section">
            <div class="section-body">
                <div class="action-row">
                    <a href="/apply-loan" class="action-btn btn-primary"><i class="fas fa-plus"></i> Apply Again</a>
                    <a href="/profile"    class="action-btn btn-outline"><i class="fas fa-user-edit"></i> Update Profile</a>
                    <a href="/dashboard"  class="action-btn btn-ghost"><i class="fas fa-home"></i> Dashboard</a>
                </div>
            </div>
        </div>
    </div>
    <!-- /rejectedLayout -->

</div><!-- /page -->

<script>
// ═══════════════════════════════════════════════════════════════════════════
// Helpers
// ═══════════════════════════════════════════════════════════════════════════
function rupee(n) {
    if (n === null || n === undefined || isNaN(n)) return '\u20b90';
    return '\u20b9' + Math.round(n).toLocaleString('en-IN');
}

function pct(n) { return (n !== null && n !== undefined) ? n + '%' : '\u2014'; }

function riskBadge(r) {
    if (!r) return '\u2014';
    var display = r.replace('_', ' ');
    var cls     = 'risk-' + r.replace(' ', '_');
    return '<span class="risk-badge ' + cls + '">' + display + '</span>';
}

function buildSummaryGrid(data, containerId) {
    var items = [
        { lbl: 'Loan Amount',    val: rupee(data.amount),      accent: true },
        { lbl: 'Purpose',        val: data.purpose || '\u2014' },
        { lbl: 'Tenure',         val: data.tenure ? data.tenure + ' months' : '\u2014' },
        { lbl: 'Interest Rate',  val: (data.interestRate || (data.decision && data.decision.personalizedRate) || '\u2014') + '%' },
        { lbl: 'Monthly EMI',    val: rupee(data.emiAmount),    accent: true },
        { lbl: 'Total Payable',  val: rupee(data.totalAmount) },
        { lbl: 'Selected Bank',  val: data.bankName || 'Best Available' },
        { lbl: 'Loan Status',    val: data.status   || '\u2014' }
    ];
    var html = '';
    items.forEach(function(it) {
        html += '<div class="summary-item">'
              + '<div class="summary-lbl">' + it.lbl + '</div>'
              + '<div class="summary-val' + (it.accent ? ' accent' : '') + '">' + it.val + '</div>'
              + '</div>';
    });
    document.getElementById(containerId).innerHTML = html;
}

function buildBreakdown(items, gridId, sectionId) {
    if (!items || !items.length) return;
    document.getElementById(sectionId).style.display = 'block';
    document.getElementById(gridId).innerHTML = items.map(function(b) {
        return '<div class="breakdown-item">' + b + '</div>';
    }).join('');
}

function buildReasonsList(reasons, listId) {
    document.getElementById(listId).innerHTML = reasons.map(function(r) {
        return '<li class="reason-item"><i class="fas fa-exclamation-circle reason-icon"></i>' + r + '</li>';
    }).join('');
}

function buildRecsList(recs, listId, sectionId) {
    if (!recs || !recs.length) return;
    document.getElementById(sectionId).style.display = 'block';
    document.getElementById(listId).innerHTML = recs.map(function(r) {
        return '<li class="rec-item"><i class="fas fa-check-circle rec-icon"></i>' + r + '</li>';
    }).join('');
}

// ═══════════════════════════════════════════════════════════════════════════
// Main render — reads sessionStorage, picks ONE layout, shows it
// ═══════════════════════════════════════════════════════════════════════════
function render() {
    var raw = sessionStorage.getItem('loanSubmitResult');
    if (!raw) { window.location.href = '/apply-loan'; return; }

    var data;
    try { data = JSON.parse(raw); }
    catch(e) { window.location.href = '/apply-loan'; return; }

    sessionStorage.removeItem('loanSubmitResult');   // clear after reading

    var decision = data.decision || {};
    var dt       = decision.decisionType || '';
    var appId    = 'APP-' + (data.loanId || data.id || '\u2014');
    var aiScore  = pct(decision.confidencePercent);
    var health   = (decision.financialHealthScore || '0') + '/100';
    var risk     = riskBadge(decision.riskProfile || 'MEDIUM');

    /* ── Determine which layout to show ──────────────────────────────────
       Priority: AUTO_APPROVED → approved, AUTO_REJECTED → rejected,
                 MANUAL_REVIEW or anything else → review
    ──────────────────────────────────────────────────────────────────── */
    var isApproved = (dt === 'AUTO_APPROVED');
    var isRejected = (dt === 'AUTO_REJECTED');
    // everything else (MANUAL_REVIEW / unknown) → review

    // Hide loader
    document.getElementById('loadingScreen').style.display = 'none';

    if (isApproved) {
        // ── APPROVED ──────────────────────────────────────────────────────
        document.getElementById('approvedLayout').style.display = 'block';

        document.getElementById('appIdApproved').textContent      = appId;
        document.getElementById('aiScoreApproved').textContent    = aiScore;
        document.getElementById('healthScoreApproved').textContent = health;
        document.getElementById('riskApproved').innerHTML          = risk;

        document.getElementById('emiSuccessMsg').innerHTML =
            'Your EMI schedule has been generated for <strong>' + (data.tenure || '—') + ' months</strong>. '
            + 'Visit <a href="/emi" style="color:var(--green);font-weight:700;">EMI Payments</a> to view your repayment plan.';

        document.getElementById('aiTextApproved').textContent =
            decision.decisionSummary
            || 'Your application was approved because your financial profile meets our lending criteria. '
            + 'Your debt-to-income ratio, credit history, and employment stability are all within acceptable thresholds. '
            + 'Congratulations — your loan funds will be disbursed as per the selected bank\'s timeline.';

        buildSummaryGrid(data, 'summaryGridApproved');
        buildBreakdown(decision.scoreBreakdown, 'breakdownApproved', 'breakdownApprovedSection');

        // STRICTLY NO rejection reasons / negative sections in approved layout

    } else if (isRejected) {
        // ── REJECTED ──────────────────────────────────────────────────────
        document.getElementById('rejectedLayout').style.display = 'block';

        document.getElementById('appIdRejected').textContent      = appId;
        document.getElementById('aiScoreRejected').textContent    = aiScore;
        document.getElementById('healthScoreRejected').textContent = health;
        document.getElementById('riskRejected').innerHTML          = risk;

        document.getElementById('aiTextRejected').textContent =
            decision.decisionSummary
            || 'Based on the current assessment of your financial profile, your application did not meet '
            + 'our minimum approval criteria. Please review the rejection reasons and follow the '
            + 'personalised recommendations below to improve your eligibility for future applications.';

        var reasons = decision.rejectionReasons || [];
        if (reasons.length) {
            buildReasonsList(reasons, 'reasonsList');
        } else {
            document.getElementById('reasonsList').innerHTML =
                '<li class="reason-item"><i class="fas fa-exclamation-circle reason-icon"></i>'
                + 'Your application did not meet the minimum score threshold for automatic approval.</li>';
        }

        buildRecsList(decision.recommendations, 'recsRejected', 'recsRejectedSection');
        buildSummaryGrid(data, 'summaryGridRejected');
        buildBreakdown(decision.scoreBreakdown, 'breakdownRejected', 'breakdownRejectedSection');

        // STRICTLY NO approval/EMI sections in rejected layout

    } else {
        // ── MANUAL REVIEW ─────────────────────────────────────────────────
        document.getElementById('reviewLayout').style.display = 'block';

        document.getElementById('appIdReview').textContent      = appId;
        document.getElementById('aiScoreReview').textContent    = aiScore;
        document.getElementById('healthScoreReview').textContent = health;
        document.getElementById('riskReview').innerHTML          = risk;

        document.getElementById('aiTextReview').textContent =
            decision.decisionSummary
            || 'Your application has scored within the manual review range. Our underwriting team will '
            + 'perform a detailed assessment of your profile. You may be contacted for additional '
            + 'documentation. A final decision will be communicated within 2 business days.';

        buildSummaryGrid(data, 'summaryGridReview');
        buildRecsList(decision.recommendations, 'recsReview', 'recsReviewSection');
    }
}

// Run immediately
render();
</script>

</body>
</html>
