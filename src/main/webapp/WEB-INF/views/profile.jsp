<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile — DebtHues</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --primary: #6366f1;
            --primary-dark: #4f46e5;
            --secondary: #8b5cf6;
            --success: #10b981;
            --warning: #f59e0b;
            --danger: #ef4444;
            --bg: #0f0f1a;
            --card: #1a1a2e;
            --card2: #16213e;
            --border: rgba(99,102,241,0.2);
            --text: #e2e8f0;
            --muted: #94a3b8;
            --glass: rgba(255,255,255,0.05);
        }
        * { margin:0; padding:0; box-sizing:border-box; }
        body { font-family:'Inter',sans-serif; background:var(--bg); color:var(--text); min-height:100vh; }

        /* ── Navbar ── */
        .navbar {
            background: rgba(26,26,46,0.95);
            backdrop-filter: blur(20px);
            border-bottom: 1px solid var(--border);
            padding: 0 2rem;
            height: 64px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: sticky;
            top: 0;
            z-index: 100;
        }
        .nav-brand { font-size:1.3rem; font-weight:800; background:linear-gradient(135deg,#6366f1,#a78bfa); -webkit-background-clip:text; -webkit-text-fill-color:transparent; text-decoration:none; }
        .nav-links { display:flex; gap:1rem; align-items:center; }
        .nav-links a { color:var(--muted); text-decoration:none; font-size:0.9rem; font-weight:500; padding:0.4rem 0.8rem; border-radius:8px; transition:all 0.2s; }
        .nav-links a:hover, .nav-links a.active { color:white; background:var(--glass); }
        .nav-back { display:flex; align-items:center; gap:0.5rem; color:var(--muted); text-decoration:none; font-size:0.9rem; font-weight:500; }
        .nav-back:hover { color:white; }

        /* ── Layout ── */
        .page-wrapper { max-width:1100px; margin:0 auto; padding:2rem 1.5rem 4rem; }

        /* ── Profile Hero ── */
        .profile-hero {
            background: linear-gradient(135deg,#1e1b4b 0%,#312e81 50%,#4c1d95 100%);
            border-radius:20px;
            padding:2.5rem;
            margin-bottom:2rem;
            position:relative;
            overflow:hidden;
        }
        .profile-hero::before {
            content:'';
            position:absolute; top:-50px; right:-50px;
            width:250px; height:250px;
            background:rgba(99,102,241,0.15);
            border-radius:50%;
        }
        .hero-content { display:flex; align-items:center; gap:1.5rem; position:relative; }
        .avatar {
            width:80px; height:80px;
            background:linear-gradient(135deg,#6366f1,#a78bfa);
            border-radius:50%;
            display:flex; align-items:center; justify-content:center;
            font-size:2rem; font-weight:700; color:white;
            flex-shrink:0;
            border:3px solid rgba(255,255,255,0.2);
        }
        .hero-info h1 { font-size:1.6rem; font-weight:700; color:white; }
        .hero-info p { color:rgba(255,255,255,0.7); font-size:0.9rem; margin-top:0.2rem; }
        .hero-info .member-since { font-size:0.8rem; color:rgba(255,255,255,0.5); margin-top:0.4rem; }

        /* ── Completion Bar ── */
        .completion-bar-wrap { margin-top:1.5rem; position:relative; }
        .completion-label { display:flex; justify-content:space-between; margin-bottom:0.5rem; font-size:0.85rem; }
        .completion-label span:first-child { color:rgba(255,255,255,0.8); font-weight:600; }
        .completion-pct { color:#a78bfa; font-weight:700; font-size:1rem; }
        .completion-track { background:rgba(255,255,255,0.15); border-radius:100px; height:8px; overflow:hidden; }
        .completion-fill { height:100%; border-radius:100px; background:linear-gradient(90deg,#6366f1,#a78bfa,#c4b5fd); transition:width 1s ease; }
        .missing-chips { display:flex; flex-wrap:wrap; gap:0.4rem; margin-top:0.8rem; }
        .missing-chip { background:rgba(239,68,68,0.15); border:1px solid rgba(239,68,68,0.3); color:#fca5a5; font-size:0.72rem; padding:0.25rem 0.7rem; border-radius:100px; }

        /* ── Section Grid ── */
        .sections-grid { display:grid; gap:1.5rem; }

        /* ── Card ── */
        .card {
            background:var(--card);
            border:1px solid var(--border);
            border-radius:16px;
            overflow:hidden;
        }
        .card-header {
            padding:1.25rem 1.5rem;
            border-bottom:1px solid var(--border);
            display:flex; align-items:center; justify-content:space-between;
            cursor:pointer;
            user-select:none;
        }
        .card-header-left { display:flex; align-items:center; gap:0.75rem; }
        .card-icon { width:36px; height:36px; border-radius:10px; display:flex; align-items:center; justify-content:center; font-size:1rem; flex-shrink:0; }
        .icon-personal { background:rgba(99,102,241,0.15); color:#a78bfa; }
        .icon-employment { background:rgba(16,185,129,0.15); color:#6ee7b7; }
        .icon-financial { background:rgba(245,158,11,0.15); color:#fcd34d; }
        .icon-preferences { background:rgba(59,130,246,0.15); color:#93c5fd; }
        .icon-security { background:rgba(239,68,68,0.15); color:#fca5a5; }
        .icon-audit { background:rgba(139,92,246,0.15); color:#c4b5fd; }
        .icon-snapshot { background:rgba(16,185,129,0.15); color:#6ee7b7; }
        .icon-offers { background:rgba(245,158,11,0.15); color:#fcd34d; }

        .card-title { font-size:1rem; font-weight:600; color:white; }
        .card-subtitle { font-size:0.75rem; color:var(--muted); margin-top:0.1rem; }
        .card-chevron { color:var(--muted); transition:transform 0.3s; }
        .card-chevron.open { transform:rotate(180deg); }

        .card-body { padding:1.5rem; }
        .card-body.collapsed { display:none; }

        /* ── Financial Snapshot ── */
        .snapshot-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(160px,1fr)); gap:1rem; }
        .snap-item {
            background:var(--glass);
            border:1px solid var(--border);
            border-radius:12px;
            padding:1rem;
            text-align:center;
        }
        .snap-value { font-size:1.8rem; font-weight:800; color:white; }
        .snap-label { font-size:0.72rem; color:var(--muted); margin-top:0.3rem; text-transform:uppercase; letter-spacing:0.05em; }
        .snap-badge { display:inline-block; padding:0.25rem 0.8rem; border-radius:100px; font-size:0.75rem; font-weight:600; margin-top:0.3rem; }
        .badge-low { background:rgba(16,185,129,0.15); color:#6ee7b7; border:1px solid rgba(16,185,129,0.3); }
        .badge-medium { background:rgba(245,158,11,0.15); color:#fcd34d; border:1px solid rgba(245,158,11,0.3); }
        .badge-high { background:rgba(239,68,68,0.15); color:#fca5a5; border:1px solid rgba(239,68,68,0.3); }
        .health-ring-wrap { display:flex; justify-content:center; margin-bottom:1.5rem; }
        .snap-summary { text-align:center; font-size:0.85rem; color:var(--muted); margin-top:1rem; line-height:1.5; }

        /* ── Form ── */
        .form-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(220px,1fr)); gap:1rem; }
        .form-group { display:flex; flex-direction:column; gap:0.4rem; }
        .form-group.full-width { grid-column:1/-1; }
        label { font-size:0.78rem; font-weight:600; color:var(--muted); text-transform:uppercase; letter-spacing:0.05em; }
        input, select, textarea {
            background:rgba(255,255,255,0.05);
            border:1px solid var(--border);
            border-radius:10px;
            padding:0.65rem 0.9rem;
            color:var(--text);
            font-family:'Inter',sans-serif;
            font-size:0.9rem;
            transition:border-color 0.2s;
            width:100%;
        }
        input:focus, select:focus { outline:none; border-color:var(--primary); background:rgba(99,102,241,0.05); }
        select option { background:#1a1a2e; }
        .form-hint { font-size:0.72rem; color:var(--muted); }

        .save-btn {
            margin-top:1.25rem;
            padding:0.75rem 2rem;
            background:linear-gradient(135deg,var(--primary),var(--secondary));
            color:white;
            border:none;
            border-radius:10px;
            font-size:0.9rem;
            font-weight:600;
            cursor:pointer;
            transition:all 0.2s;
            display:flex; align-items:center; gap:0.5rem;
        }
        .save-btn:hover { opacity:0.9; transform:translateY(-1px); }
        .save-btn:active { transform:translateY(0); }
        .save-btn:disabled { opacity:0.5; cursor:not-allowed; transform:none; }

        /* ── Offers ── */
        .offers-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(240px,1fr)); gap:1rem; }
        .offer-card {
            background:var(--card2);
            border:1px solid var(--border);
            border-radius:14px;
            padding:1.25rem;
            position:relative;
            overflow:hidden;
        }
        .offer-card::before { content:''; position:absolute; top:0; left:0; right:0; height:3px; }
        .offer-personal::before { background:linear-gradient(90deg,#6366f1,#a78bfa); }
        .offer-home::before { background:linear-gradient(90deg,#10b981,#34d399); }
        .offer-car::before { background:linear-gradient(90deg,#f59e0b,#fcd34d); }
        .offer-card-icon { font-size:1.5rem; margin-bottom:0.5rem; }
        .offer-type { font-size:0.8rem; font-weight:600; color:var(--muted); text-transform:uppercase; }
        .offer-rate { font-size:1.6rem; font-weight:800; color:white; margin:0.3rem 0; }
        .offer-emi { font-size:0.85rem; color:var(--muted); }
        .offer-chip { display:inline-block; background:rgba(99,102,241,0.15); color:#a78bfa; border-radius:100px; font-size:0.7rem; padding:0.2rem 0.6rem; margin-top:0.5rem; }

        /* ── Security ── */
        .security-item { display:flex; justify-content:space-between; align-items:center; padding:1rem 0; border-bottom:1px solid var(--border); }
        .security-item:last-child { border-bottom:none; }
        .security-label { font-size:0.9rem; font-weight:500; color:white; }
        .security-value { font-size:0.8rem; color:var(--muted); margin-top:0.2rem; }
        .security-btn { padding:0.4rem 1rem; border:1px solid var(--border); border-radius:8px; background:transparent; color:var(--primary); font-size:0.8rem; font-weight:600; cursor:pointer; transition:all 0.2s; }
        .security-btn:hover { background:rgba(99,102,241,0.1); }

        /* ── Audit Log ── */
        .audit-entry { display:flex; gap:0.75rem; padding:0.75rem 0; border-bottom:1px solid var(--border); }
        .audit-entry:last-child { border-bottom:none; }
        .audit-dot { width:8px; height:8px; border-radius:50%; background:var(--primary); margin-top:6px; flex-shrink:0; }
        .audit-text { font-size:0.85rem; color:var(--text); }
        .audit-empty { color:var(--muted); font-size:0.85rem; text-align:center; padding:1rem 0; }

        /* ── Password Modal ── */
        .modal-overlay { position:fixed; inset:0; background:rgba(0,0,0,0.7); z-index:1000; display:flex; align-items:center; justify-content:center; opacity:0; pointer-events:none; transition:opacity 0.3s; }
        .modal-overlay.open { opacity:1; pointer-events:all; }
        .modal { background:var(--card); border:1px solid var(--border); border-radius:20px; padding:2rem; width:min(420px,90vw); }
        .modal h3 { font-size:1.1rem; font-weight:700; color:white; margin-bottom:1.5rem; }
        .modal-btns { display:flex; gap:0.75rem; margin-top:1.25rem; }
        .btn-cancel { flex:1; padding:0.75rem; border:1px solid var(--border); border-radius:10px; background:transparent; color:var(--muted); cursor:pointer; font-size:0.9rem; }
        .btn-confirm { flex:1; padding:0.75rem; border:none; border-radius:10px; background:linear-gradient(135deg,var(--primary),var(--secondary)); color:white; cursor:pointer; font-size:0.9rem; font-weight:600; }

        /* ── Toast ── */
        .toast {
            position:fixed; bottom:2rem; right:2rem;
            padding:0.85rem 1.5rem;
            border-radius:12px;
            font-size:0.9rem; font-weight:500;
            opacity:0; transform:translateY(20px);
            transition:all 0.3s;
            z-index:9999;
            max-width:360px;
            display:flex; align-items:center; gap:0.75rem;
        }
        .toast.show { opacity:1; transform:translateY(0); }
        .toast.success { background:#065f46; border:1px solid #10b981; color:#6ee7b7; }
        .toast.error   { background:#7f1d1d; border:1px solid #ef4444; color:#fca5a5; }

        /* ── Checkboxes for loan types ── */
        .loan-type-grid { display:flex; flex-wrap:wrap; gap:0.75rem; }
        .loan-type-btn {
            padding:0.5rem 1.1rem;
            border:1px solid var(--border);
            border-radius:100px;
            background:transparent;
            color:var(--muted);
            font-size:0.85rem; font-weight:500;
            cursor:pointer; transition:all 0.2s;
        }
        .loan-type-btn.selected { background:rgba(99,102,241,0.2); border-color:var(--primary); color:#a78bfa; }

        @media (max-width:600px) {
            .hero-content { flex-direction:column; text-align:center; }
            .nav-links { display:none; }
            .form-grid { grid-template-columns:1fr; }
        }
    </style>
</head>
<body>

<!-- ── Navbar ─────────────────────────────────────────────────────────────── -->
<nav class="navbar">
    <a href="/dashboard" class="nav-brand">🎨 DebtHues</a>
    <div class="nav-links">
        <a href="/dashboard">Dashboard</a>
        <a href="/apply-loan">Apply Loan</a>
        <a href="/emi">My EMIs</a>
        <a href="/profile" class="active">Profile</a>
    </div>
    <a href="/dashboard" class="nav-back"><i class="fas fa-arrow-left"></i> Back to Dashboard</a>
</nav>

<!-- ── Main Content ───────────────────────────────────────────────────────── -->
<div class="page-wrapper">

    <!-- Profile Hero -->
    <div class="profile-hero">
        <div class="hero-content">
            <div class="avatar" id="avatarInitials">
                ${customer.name != null ? customer.name.substring(0,1).toUpperCase() : '?'}
            </div>
            <div class="hero-info">
                <h1>${customer.name != null ? customer.name : 'Your Profile'}</h1>
                <p>${customer.email}</p>
                <p class="member-since">
                    <i class="fas fa-shield-alt"></i>
                    ${customer.role.name()} Account
                    <c:if test="${customer.lastFinancialUpdateAt != null}">
                        · Last updated: ${customer.lastFinancialUpdateAt}
                    </c:if>
                </p>
            </div>
        </div>

        <!-- Profile Completion Bar -->
        <div class="completion-bar-wrap" id="completionSection">
            <div class="completion-label">
                <span>Profile Completion</span>
                <span class="completion-pct" id="completionPct">Loading...</span>
            </div>
            <div class="completion-track">
                <div class="completion-fill" id="completionFill" style="width:10%"></div>
            </div>
            <div class="missing-chips" id="missingChips"></div>
        </div>
    </div>

    <div class="sections-grid">

        <!-- ── 1. Financial Snapshot ── -->
        <div class="card">
            <div class="card-header" onclick="toggleCard('snapshot')">
                <div class="card-header-left">
                    <div class="card-icon icon-snapshot"><i class="fas fa-chart-line"></i></div>
                    <div>
                        <div class="card-title">Financial Snapshot</div>
                        <div class="card-subtitle">Your AI-computed financial health overview</div>
                    </div>
                </div>
                <i class="fas fa-chevron-down card-chevron open" id="chevron-snapshot"></i>
            </div>
            <div class="card-body" id="body-snapshot">
                <div class="snapshot-grid" id="snapshotGrid">
                    <div class="snap-item">
                        <div class="snap-value" id="snapHealth">—</div>
                        <div class="snap-label">Health Score</div>
                        <span class="snap-badge" id="snapGrade">—</span>
                    </div>
                    <div class="snap-item">
                        <div class="snap-value" id="snapRisk">—</div>
                        <div class="snap-label">Risk Profile</div>
                    </div>
                    <div class="snap-item">
                        <div class="snap-value" id="snapApproval">—</div>
                        <div class="snap-label">Approval Probability</div>
                    </div>
                    <div class="snap-item">
                        <div class="snap-value" id="snapMaxLoan">—</div>
                        <div class="snap-label">Max Eligible Loan</div>
                    </div>
                    <div class="snap-item">
                        <div class="snap-value" id="snapEMILimit">—</div>
                        <div class="snap-label">Recommended EMI Limit</div>
                    </div>
                </div>
                <p class="snap-summary" id="snapSummary"></p>
            </div>
        </div>

        <!-- ── 2. Pre-Approved Offers ── -->
        <div class="card">
            <div class="card-header" onclick="toggleCard('offers')">
                <div class="card-header-left">
                    <div class="card-icon icon-offers"><i class="fas fa-gift"></i></div>
                    <div>
                        <div class="card-title">Pre-Approved Offers</div>
                        <div class="card-subtitle">Personalised loan offers based on your profile</div>
                    </div>
                </div>
                <i class="fas fa-chevron-down card-chevron open" id="chevron-offers"></i>
            </div>
            <div class="card-body" id="body-offers">
                <div class="offers-grid" id="offersGrid">
                    <div style="color:var(--muted);font-size:0.85rem;text-align:center;padding:1rem;grid-column:1/-1;">Loading offers...</div>
                </div>
            </div>
        </div>

        <!-- ── 3. Personal Details ── -->
        <div class="card">
            <div class="card-header" onclick="toggleCard('personal')">
                <div class="card-header-left">
                    <div class="card-icon icon-personal"><i class="fas fa-user"></i></div>
                    <div>
                        <div class="card-title">Personal Details</div>
                        <div class="card-subtitle">Name, contact, and basic information</div>
                    </div>
                </div>
                <i class="fas fa-chevron-down card-chevron open" id="chevron-personal"></i>
            </div>
            <div class="card-body" id="body-personal">
                <div class="form-grid">
                    <div class="form-group">
                        <label>Full Name</label>
                        <input type="text" id="p_name" value="${customer.name}" placeholder="Your full name">
                    </div>
                    <div class="form-group">
                        <label>Email Address</label>
                        <input type="email" id="p_email" value="${customer.email}" placeholder="email@example.com" readonly style="opacity:0.6;cursor:not-allowed;">
                        <span class="form-hint">Email cannot be changed</span>
                    </div>
                    <div class="form-group">
                        <label>Phone Number</label>
                        <input type="tel" id="p_phone" value="${customer.phone}" placeholder="+91 XXXXX XXXXX">
                    </div>
                    <div class="form-group">
                        <label>Date of Birth</label>
                        <input type="date" id="p_dob" value="${customer.dateOfBirth}">
                    </div>
                    <div class="form-group">
                        <label>City</label>
                        <input type="text" id="p_city" value="${customer.city}" placeholder="e.g. Mumbai">
                    </div>
                    <div class="form-group">
                        <label>Marital Status</label>
                        <select id="p_marital">
                            <option value="">Select...</option>
                            <option value="SINGLE" ${customer.maritalStatus == 'SINGLE' ? 'selected' : ''}>Single</option>
                            <option value="MARRIED" ${customer.maritalStatus == 'MARRIED' ? 'selected' : ''}>Married</option>
                            <option value="DIVORCED" ${customer.maritalStatus == 'DIVORCED' ? 'selected' : ''}>Divorced</option>
                            <option value="WIDOWED" ${customer.maritalStatus == 'WIDOWED' ? 'selected' : ''}>Widowed</option>
                        </select>
                    </div>
                    <div class="form-group full-width">
                        <label>Address</label>
                        <input type="text" id="p_address" value="${customer.address}" placeholder="Full address">
                    </div>
                </div>
                <button class="save-btn" onclick="savePersonal()"><i class="fas fa-save"></i> Save Personal Details</button>
            </div>
        </div>

        <!-- ── 4. Employment Details ── -->
        <div class="card">
            <div class="card-header" onclick="toggleCard('employment')">
                <div class="card-header-left">
                    <div class="card-icon icon-employment"><i class="fas fa-briefcase"></i></div>
                    <div>
                        <div class="card-title">Employment Details</div>
                        <div class="card-subtitle">Affects loan approval probability</div>
                    </div>
                </div>
                <i class="fas fa-chevron-down card-chevron open" id="chevron-employment"></i>
            </div>
            <div class="card-body" id="body-employment">
                <div class="form-grid">
                    <div class="form-group">
                        <label>Employment Type</label>
                        <select id="e_type">
                            <option value="">Select...</option>
                            <option value="SALARIED" ${customer.employmentType == 'SALARIED' ? 'selected' : ''}>Salaried Employee</option>
                            <option value="SELF_EMPLOYED" ${customer.employmentType == 'SELF_EMPLOYED' ? 'selected' : ''}>Self Employed</option>
                            <option value="BUSINESS" ${customer.employmentType == 'BUSINESS' ? 'selected' : ''}>Business Owner</option>
                            <option value="FREELANCER" ${customer.employmentType == 'FREELANCER' ? 'selected' : ''}>Freelancer</option>
                            <option value="RETIRED" ${customer.employmentType == 'RETIRED' ? 'selected' : ''}>Retired</option>
                            <option value="UNEMPLOYED" ${customer.employmentType == 'UNEMPLOYED' ? 'selected' : ''}>Unemployed</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Employer / Company Name</label>
                        <input type="text" id="e_employer" value="${customer.employerName}" placeholder="e.g. Infosys Ltd">
                    </div>
                    <div class="form-group">
                        <label>Industry</label>
                        <select id="e_industry">
                            <option value="">Select...</option>
                            <option value="IT" ${customer.industry == 'IT' ? 'selected' : ''}>Information Technology</option>
                            <option value="BANKING" ${customer.industry == 'BANKING' ? 'selected' : ''}>Banking & Finance</option>
                            <option value="HEALTHCARE" ${customer.industry == 'HEALTHCARE' ? 'selected' : ''}>Healthcare</option>
                            <option value="EDUCATION" ${customer.industry == 'EDUCATION' ? 'selected' : ''}>Education</option>
                            <option value="MANUFACTURING" ${customer.industry == 'MANUFACTURING' ? 'selected' : ''}>Manufacturing</option>
                            <option value="RETAIL" ${customer.industry == 'RETAIL' ? 'selected' : ''}>Retail & E-commerce</option>
                            <option value="GOVERNMENT" ${customer.industry == 'GOVERNMENT' ? 'selected' : ''}>Government / PSU</option>
                            <option value="REAL_ESTATE" ${customer.industry == 'REAL_ESTATE' ? 'selected' : ''}>Real Estate</option>
                            <option value="OTHER" ${customer.industry == 'OTHER' ? 'selected' : ''}>Other</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Job Title</label>
                        <input type="text" id="e_title" value="${customer.jobTitle}" placeholder="e.g. Software Engineer">
                    </div>
                    <div class="form-group">
                        <label>Total Work Experience (Years)</label>
                        <input type="number" id="e_experience" value="${customer.workExperienceYears}" placeholder="e.g. 5" min="0" max="50">
                    </div>
                    <div class="form-group">
                        <label>Years at Current Employer</label>
                        <input type="number" id="e_stability" value="${customer.employmentStabilityYears}" placeholder="e.g. 3" min="0" max="50">
                        <span class="form-hint">Higher stability improves approval chances</span>
                    </div>
                </div>
                <button class="save-btn" onclick="saveEmployment()"><i class="fas fa-save"></i> Save Employment Details</button>
            </div>
        </div>

        <!-- ── 5. Financial Details ── -->
        <div class="card">
            <div class="card-header" onclick="toggleCard('financial')">
                <div class="card-header-left">
                    <div class="card-icon icon-financial"><i class="fas fa-rupee-sign"></i></div>
                    <div>
                        <div class="card-title">Financial Details</div>
                        <div class="card-subtitle">Income, EMIs, savings — drives all AI calculations</div>
                    </div>
                </div>
                <i class="fas fa-chevron-down card-chevron open" id="chevron-financial"></i>
            </div>
            <div class="card-body" id="body-financial">
                <div class="form-grid">
                    <div class="form-group">
                        <label>Monthly Income (₹)</label>
                        <input type="number" id="f_monthly" placeholder="e.g. 60000" min="0"
                               value="${customer.monthlyIncome != null ? customer.monthlyIncome : (customer.income != null ? customer.income / 12 : '')}">
                    </div>
                    <div class="form-group">
                        <label>Annual Income (₹)</label>
                        <input type="number" id="f_annual" placeholder="e.g. 720000" min="0"
                               value="${customer.income}">
                        <span class="form-hint">Auto-synced with monthly income</span>
                    </div>
                    <div class="form-group">
                        <label>Total Monthly EMIs (₹)</label>
                        <input type="number" id="f_emi" placeholder="e.g. 12000" min="0"
                               value="${customer.emi}">
                        <span class="form-hint">All existing loan EMIs combined</span>
                    </div>
                    <div class="form-group">
                        <label>Number of Active Loans</label>
                        <input type="number" id="f_loans" placeholder="e.g. 2" min="0"
                               value="${customer.existingLoans}">
                    </div>
                    <div class="form-group">
                        <label>Total Savings / Liquid Assets (₹)</label>
                        <input type="number" id="f_savings" placeholder="e.g. 200000" min="0"
                               value="${customer.savings}">
                    </div>
                    <div class="form-group">
                        <label>Monthly Expenses (₹)</label>
                        <input type="number" id="f_expenses" placeholder="e.g. 30000" min="0"
                               value="${customer.monthlyExpenses}">
                    </div>
                    <div class="form-group">
                        <label>Credit Score</label>
                        <input type="number" id="f_credit" placeholder="300–900" min="300" max="900"
                               value="${customer.creditScore}">
                    </div>
                    <div class="form-group">
                        <label>Payment History Score (0–100)</label>
                        <input type="number" id="f_payment" placeholder="e.g. 85" min="0" max="100"
                               value="${customer.paymentHistoryScore}">
                    </div>
                    <div class="form-group">
                        <label>Credit Utilization (0.0–1.0)</label>
                        <input type="number" id="f_util" placeholder="e.g. 0.30" min="0" max="1" step="0.01"
                               value="${customer.creditUtilizationRatio}">
                        <span class="form-hint">Keep below 0.30 for best results</span>
                    </div>
                    <div class="form-group">
                        <label>Credit Age (Months)</label>
                        <input type="number" id="f_age" placeholder="e.g. 36" min="0"
                               value="${customer.creditAgeMonths}">
                    </div>
                </div>
                <button class="save-btn" onclick="saveFinancial()"><i class="fas fa-save"></i> Save Financial Details</button>
            </div>
        </div>

        <!-- ── 6. Loan Preferences ── -->
        <div class="card">
            <div class="card-header" onclick="toggleCard('preferences')">
                <div class="card-header-left">
                    <div class="card-icon icon-preferences"><i class="fas fa-sliders-h"></i></div>
                    <div>
                        <div class="card-title">Loan Preferences</div>
                        <div class="card-subtitle">Personalises your offers and recommendations</div>
                    </div>
                </div>
                <i class="fas fa-chevron-down card-chevron" id="chevron-preferences"></i>
            </div>
            <div class="card-body collapsed" id="body-preferences">
                <div class="form-group" style="margin-bottom:1rem;">
                    <label>Preferred Loan Types (select all that apply)</label>
                    <div class="loan-type-grid" id="loanTypeBtns">
                        <button class="loan-type-btn" data-type="PERSONAL" onclick="toggleLoanType(this)">Personal Loan</button>
                        <button class="loan-type-btn" data-type="HOME" onclick="toggleLoanType(this)">Home Loan</button>
                        <button class="loan-type-btn" data-type="CAR" onclick="toggleLoanType(this)">Car Loan</button>
                        <button class="loan-type-btn" data-type="EDUCATION" onclick="toggleLoanType(this)">Education Loan</button>
                        <button class="loan-type-btn" data-type="BUSINESS" onclick="toggleLoanType(this)">Business Loan</button>
                    </div>
                </div>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Preferred Tenure (Months)</label>
                        <select id="pref_tenure">
                            <option value="">Select...</option>
                            <option value="12" ${customer.preferredTenure == 12 ? 'selected' : ''}>12 months</option>
                            <option value="24" ${customer.preferredTenure == 24 ? 'selected' : ''}>24 months</option>
                            <option value="36" ${customer.preferredTenure == 36 ? 'selected' : ''}>36 months</option>
                            <option value="48" ${customer.preferredTenure == 48 ? 'selected' : ''}>48 months</option>
                            <option value="60" ${customer.preferredTenure == 60 ? 'selected' : ''}>60 months</option>
                            <option value="84" ${customer.preferredTenure == 84 ? 'selected' : ''}>84 months</option>
                            <option value="120" ${customer.preferredTenure == 120 ? 'selected' : ''}>120 months</option>
                            <option value="240" ${customer.preferredTenure == 240 ? 'selected' : ''}>240 months</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Risk Appetite</label>
                        <select id="pref_risk">
                            <option value="">Select...</option>
                            <option value="LOW" ${customer.riskAppetite == 'LOW' ? 'selected' : ''}>Low — Conservative</option>
                            <option value="MEDIUM" ${customer.riskAppetite == 'MEDIUM' ? 'selected' : ''}>Medium — Balanced</option>
                            <option value="HIGH" ${customer.riskAppetite == 'HIGH' ? 'selected' : ''}>High — Aggressive</option>
                        </select>
                    </div>
                </div>
                <button class="save-btn" onclick="savePreferences()"><i class="fas fa-save"></i> Save Preferences</button>
            </div>
        </div>

        <!-- ── 7. Security Settings ── -->
        <div class="card">
            <div class="card-header" onclick="toggleCard('security')">
                <div class="card-header-left">
                    <div class="card-icon icon-security"><i class="fas fa-lock"></i></div>
                    <div>
                        <div class="card-title">Security Settings</div>
                        <div class="card-subtitle">Password, connected accounts, sessions</div>
                    </div>
                </div>
                <i class="fas fa-chevron-down card-chevron" id="chevron-security"></i>
            </div>
            <div class="card-body collapsed" id="body-security">
                <div class="security-item">
                    <div>
                        <div class="security-label"><i class="fas fa-key" style="color:var(--primary);margin-right:0.5rem;"></i>Password</div>
                        <div class="security-value">Last changed: recently</div>
                    </div>
                    <button class="security-btn" onclick="openPasswordModal()">Change Password</button>
                </div>
                <div class="security-item">
                    <div>
                        <div class="security-label"><i class="fab fa-google" style="color:#ea4335;margin-right:0.5rem;"></i>Google Account</div>
                        <div class="security-value">${customer.googleConnected ? 'Connected' : 'Not connected'}</div>
                    </div>
                    <button class="security-btn" style="cursor:default;opacity:0.5;">${customer.googleConnected ? 'Connected' : 'Connect'}</button>
                </div>
                <div class="security-item">
                    <div>
                        <div class="security-label"><i class="fas fa-clock" style="color:var(--warning);margin-right:0.5rem;"></i>Last Login</div>
                        <div class="security-value">${customer.lastLoginAt != null ? customer.lastLoginAt : 'Current session'}</div>
                    </div>
                </div>
                <div class="security-item">
                    <div>
                        <div class="security-label"><i class="fas fa-sign-out-alt" style="color:var(--danger);margin-right:0.5rem;"></i>Sessions</div>
                        <div class="security-value">1 active session (this device)</div>
                    </div>
                    <a href="/perform_logout" style="text-decoration:none;"><button class="security-btn" style="color:var(--danger);border-color:rgba(239,68,68,0.4);">Sign Out</button></a>
                </div>
            </div>
        </div>

        <!-- ── 8. Audit Log ── -->
        <div class="card">
            <div class="card-header" onclick="toggleCard('audit')">
                <div class="card-header-left">
                    <div class="card-icon icon-audit"><i class="fas fa-history"></i></div>
                    <div>
                        <div class="card-title">Profile Change History</div>
                        <div class="card-subtitle">Important financial updates audit trail</div>
                    </div>
                </div>
                <i class="fas fa-chevron-down card-chevron" id="chevron-audit"></i>
            </div>
            <div class="card-body collapsed" id="body-audit">
                <c:choose>
                    <c:when test="${not empty customer.auditEntries}">
                        <c:forEach var="entry" items="${customer.auditEntries}">
                            <div class="audit-entry">
                                <div class="audit-dot"></div>
                                <div class="audit-text">${entry}</div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p class="audit-empty"><i class="fas fa-info-circle"></i> No financial changes recorded yet. Updates to Employment and Financial sections will appear here.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

    </div><!-- /sections-grid -->
</div><!-- /page-wrapper -->

<!-- ── Password Modal ─────────────────────────────────────────────────────── -->
<div class="modal-overlay" id="pwdModal">
    <div class="modal">
        <h3><i class="fas fa-key" style="color:var(--primary);margin-right:0.5rem;"></i> Change Password</h3>
        <div class="form-group" style="margin-bottom:0.75rem;">
            <label>Current Password</label>
            <input type="password" id="pwd_current" placeholder="Enter current password">
        </div>
        <div class="form-group" style="margin-bottom:0.75rem;">
            <label>New Password</label>
            <input type="password" id="pwd_new" placeholder="Min 8 characters">
        </div>
        <div class="form-group">
            <label>Confirm New Password</label>
            <input type="password" id="pwd_confirm" placeholder="Repeat new password">
        </div>
        <div class="modal-btns">
            <button class="btn-cancel" onclick="closePwdModal()">Cancel</button>
            <button class="btn-confirm" onclick="changePassword()">Update Password</button>
        </div>
    </div>
</div>

<!-- ── Toast ─────────────────────────────────────────────────────────────── -->
<div class="toast" id="toast"></div>

<script>
// ── Helpers ────────────────────────────────────────────────────────────────
function showToast(msg, type) {
    var t = document.getElementById('toast');
    t.className = 'toast ' + type;
    t.innerHTML = (type === 'success' ? '<i class="fas fa-check-circle"></i>' : '<i class="fas fa-exclamation-circle"></i>') + ' ' + msg;
    t.classList.add('show');
    setTimeout(function() { t.classList.remove('show'); }, 4000);
}

function toggleCard(id) {
    var body = document.getElementById('body-' + id);
    var chev = document.getElementById('chevron-' + id);
    if (body.classList.contains('collapsed')) {
        body.classList.remove('collapsed');
        chev.classList.add('open');
    } else {
        body.classList.add('collapsed');
        chev.classList.remove('open');
    }
}

function fmt(num) {
    if (!num && num !== 0) return '—';
    return '\u20b9' + Math.round(num).toLocaleString('en-IN');
}

// ── Load completion ────────────────────────────────────────────────────────
function loadCompletion() {
    fetch('/api/profile/completion')
    .then(function(r) { return r.json(); })
    .then(function(d) {
        document.getElementById('completionPct').textContent = d.percent + '%';
        document.getElementById('completionFill').style.width = d.percent + '%';
        var chips = document.getElementById('missingChips');
        chips.innerHTML = '';
        if (d.missingFields && d.missingFields.length) {
            d.missingFields.slice(0,5).forEach(function(f) {
                chips.innerHTML += '<span class="missing-chip">' + f + '</span>';
            });
            if (d.missingFields.length > 5) {
                chips.innerHTML += '<span class="missing-chip">+' + (d.missingFields.length - 5) + ' more</span>';
            }
        }
    })
    .catch(function() { document.getElementById('completionPct').textContent = 'N/A'; });
}

// ── Load snapshot ──────────────────────────────────────────────────────────
function loadSnapshot() {
    fetch('/api/profile/snapshot')
    .then(function(r) { return r.json(); })
    .then(function(d) {
        document.getElementById('snapHealth').textContent = d.healthScore + '/100';
        document.getElementById('snapRisk').textContent = (d.riskProfile || '').replace('_',' ');
        document.getElementById('snapApproval').textContent = d.approvalProbability + '%';
        document.getElementById('snapMaxLoan').textContent = fmt(d.maxEligibleLoan);
        document.getElementById('snapEMILimit').textContent = fmt(d.recommendedEMILimit);
        document.getElementById('snapSummary').textContent = d.summary || '';

        var gradeEl = document.getElementById('snapGrade');
        gradeEl.textContent = d.grade || '';
        var cls = d.healthScore >= 75 ? 'badge-low' : d.healthScore >= 50 ? 'badge-medium' : 'badge-high';
        gradeEl.className = 'snap-badge ' + cls;

        var riskEl = document.getElementById('snapRisk');
        var riskCls = d.riskProfile === 'LOW' ? 'snap-value' : d.riskProfile === 'MEDIUM' ? 'snap-value' : 'snap-value';
        riskEl.style.color = d.riskProfile === 'LOW' ? '#6ee7b7' : d.riskProfile === 'MEDIUM' ? '#fcd34d' : '#fca5a5';
    })
    .catch(function() {
        document.getElementById('snapHealth').textContent = 'N/A';
        document.getElementById('snapSummary').textContent = 'Complete your financial profile to see your snapshot';
    });
}

// ── Load offers ────────────────────────────────────────────────────────────
function loadOffers() {
    fetch('/api/profile/offers')
    .then(function(r) { return r.json(); })
    .then(function(d) {
        var grid = document.getElementById('offersGrid');
        var offers = d.offers || [];
        if (!offers.length) {
            grid.innerHTML = '<div style="color:var(--muted);font-size:0.85rem;text-align:center;padding:1.5rem;grid-column:1/-1;">Complete your Financial Profile to unlock personalised pre-approved offers.</div>';
            return;
        }
        grid.innerHTML = '';
        offers.forEach(function(o) {
            var typeClass = o.loanType === 'HOME' ? 'offer-home' : o.loanType === 'CAR' ? 'offer-car' : 'offer-personal';
            var icon = o.loanType === 'HOME' ? '\ud83c\udfe0' : o.loanType === 'CAR' ? '\ud83d\ude97' : '\ud83d\udcb3';
            grid.innerHTML += '<div class="offer-card ' + typeClass + '">'
                + '<div class="offer-card-icon">' + icon + '</div>'
                + '<div class="offer-type">' + (o.loanType || 'Personal') + ' Loan</div>'
                + '<div class="offer-rate">' + (o.interestRate || '—') + '%</div>'
                + '<div class="offer-emi">EMI from ' + fmt(o.emiAmount) + '/mo</div>'
                + '<span class="offer-chip">Up to ' + fmt(o.maxAmount) + '</span>'
                + '</div>';
        });
    })
    .catch(function() {
        document.getElementById('offersGrid').innerHTML = '<div style="color:var(--muted);font-size:0.85rem;text-align:center;padding:1rem;grid-column:1/-1;">Could not load offers.</div>';
    });
}

// ── Save Personal ──────────────────────────────────────────────────────────
function savePersonal() {
    var btn = event.target.closest('button');
    btn.disabled = true; btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
    var data = {
        name:          document.getElementById('p_name').value,
        phone:         document.getElementById('p_phone').value,
        city:          document.getElementById('p_city').value,
        maritalStatus: document.getElementById('p_marital').value,
        address:       document.getElementById('p_address').value,
        dateOfBirth:   document.getElementById('p_dob').value
    };
    fetch('/api/profile/personal', { method:'PUT', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data) })
    .then(function(r) { return r.json(); })
    .then(function(d) {
        if (d.success) { showToast(d.message, 'success'); loadCompletion(); }
        else showToast(d.error || 'Failed to save', 'error');
    })
    .catch(function() { showToast('Network error', 'error'); })
    .finally(function() { btn.disabled = false; btn.innerHTML = '<i class="fas fa-save"></i> Save Personal Details'; });
}

// ── Save Employment ────────────────────────────────────────────────────────
function saveEmployment() {
    var btn = event.target.closest('button');
    btn.disabled = true; btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
    var data = {
        employmentType:           document.getElementById('e_type').value,
        employerName:             document.getElementById('e_employer').value,
        industry:                 document.getElementById('e_industry').value,
        jobTitle:                 document.getElementById('e_title').value,
        workExperienceYears:      document.getElementById('e_experience').value,
        employmentStabilityYears: document.getElementById('e_stability').value
    };
    fetch('/api/profile/employment', { method:'PUT', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data) })
    .then(function(r) { return r.json(); })
    .then(function(d) {
        if (d.success) {
            showToast(d.message, 'success');
            loadSnapshot(); loadCompletion();
        } else showToast(d.error || 'Failed to save', 'error');
    })
    .catch(function() { showToast('Network error', 'error'); })
    .finally(function() { btn.disabled = false; btn.innerHTML = '<i class="fas fa-save"></i> Save Employment Details'; });
}

// ── Save Financial ─────────────────────────────────────────────────────────
function saveFinancial() {
    var btn = event.target.closest('button');
    btn.disabled = true; btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
    var monthly = parseFloat(document.getElementById('f_monthly').value);
    var annual  = parseFloat(document.getElementById('f_annual').value);
    if (monthly && !annual) document.getElementById('f_annual').value = monthly * 12;
    if (annual && !monthly) document.getElementById('f_monthly').value = (annual / 12).toFixed(0);

    var data = {
        monthlyIncome:          document.getElementById('f_monthly').value,
        annualIncome:           document.getElementById('f_annual').value,
        emi:                    document.getElementById('f_emi').value,
        existingLoans:          document.getElementById('f_loans').value,
        savings:                document.getElementById('f_savings').value,
        monthlyExpenses:        document.getElementById('f_expenses').value,
        creditScore:            document.getElementById('f_credit').value,
        paymentHistoryScore:    document.getElementById('f_payment').value,
        creditUtilizationRatio: document.getElementById('f_util').value,
        creditAgeMonths:        document.getElementById('f_age').value
    };
    fetch('/api/profile/financial', { method:'PUT', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data) })
    .then(function(r) { return r.json(); })
    .then(function(d) {
        if (d.success) {
            showToast(d.message, 'success');
            loadSnapshot(); loadCompletion(); loadOffers();
            // Update snapshot display immediately if returned
            if (d.snapshot) {
                document.getElementById('snapHealth').textContent = d.snapshot.healthScore + '/100';
                document.getElementById('snapRisk').textContent   = (d.snapshot.riskProfile || '').replace('_',' ');
                document.getElementById('snapApproval').textContent = d.snapshot.approvalProbability + '%';
                document.getElementById('snapMaxLoan').textContent  = fmt(d.snapshot.maxEligibleLoan);
                document.getElementById('snapEMILimit').textContent = fmt(d.snapshot.recommendedEMILimit);
            }
        } else showToast(d.error || 'Failed to save', 'error');
    })
    .catch(function() { showToast('Network error', 'error'); })
    .finally(function() { btn.disabled = false; btn.innerHTML = '<i class="fas fa-save"></i> Save Financial Details'; });
}

// ── Loan Type toggle ───────────────────────────────────────────────────────
var selectedTypes = [];
var savedTypes = '${customer.preferredLoanTypes}';
if (savedTypes) {
    selectedTypes = savedTypes.split('|');
    document.querySelectorAll('.loan-type-btn').forEach(function(b) {
        if (selectedTypes.indexOf(b.getAttribute('data-type')) >= 0) b.classList.add('selected');
    });
}
function toggleLoanType(btn) {
    var t = btn.getAttribute('data-type');
    var idx = selectedTypes.indexOf(t);
    if (idx >= 0) { selectedTypes.splice(idx,1); btn.classList.remove('selected'); }
    else { selectedTypes.push(t); btn.classList.add('selected'); }
}

// ── Save Preferences ───────────────────────────────────────────────────────
function savePreferences() {
    var btn = event.target.closest('button');
    btn.disabled = true; btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
    var data = {
        preferredLoanTypes: selectedTypes.join('|'),
        preferredTenure:    document.getElementById('pref_tenure').value,
        riskAppetite:       document.getElementById('pref_risk').value
    };
    fetch('/api/profile/preferences', { method:'PUT', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data) })
    .then(function(r) { return r.json(); })
    .then(function(d) {
        if (d.success) { showToast(d.message, 'success'); loadCompletion(); }
        else showToast(d.error || 'Failed to save', 'error');
    })
    .catch(function() { showToast('Network error', 'error'); })
    .finally(function() { btn.disabled = false; btn.innerHTML = '<i class="fas fa-save"></i> Save Preferences'; });
}

// ── Password Modal ─────────────────────────────────────────────────────────
function openPasswordModal() { document.getElementById('pwdModal').classList.add('open'); }
function closePwdModal()     { document.getElementById('pwdModal').classList.remove('open'); }

function changePassword() {
    var current = document.getElementById('pwd_current').value;
    var newPwd   = document.getElementById('pwd_new').value;
    var confirm  = document.getElementById('pwd_confirm').value;
    if (!current || !newPwd) { showToast('Please fill all fields', 'error'); return; }
    if (newPwd !== confirm) { showToast('Passwords do not match', 'error'); return; }
    if (newPwd.length < 8)  { showToast('Password must be at least 8 characters', 'error'); return; }
    fetch('/api/profile/change-password', {
        method:'POST', headers:{'Content-Type':'application/json'},
        body:JSON.stringify({ currentPassword: current, newPassword: newPwd })
    })
    .then(function(r) { return r.json(); })
    .then(function(d) {
        if (d.success) { closePwdModal(); showToast(d.message, 'success'); }
        else showToast(d.error || 'Failed to change password', 'error');
    })
    .catch(function() { showToast('Network error', 'error'); });
}

// ── Monthly ↔ Annual sync ──────────────────────────────────────────────────
document.getElementById('f_monthly').addEventListener('input', function() {
    var v = parseFloat(this.value);
    if (!isNaN(v)) document.getElementById('f_annual').value = (v * 12).toFixed(0);
});
document.getElementById('f_annual').addEventListener('input', function() {
    var v = parseFloat(this.value);
    if (!isNaN(v)) document.getElementById('f_monthly').value = (v / 12).toFixed(0);
});

// ── Init ───────────────────────────────────────────────────────────────────
loadCompletion();
loadSnapshot();
loadOffers();
</script>

<%@ include file="fragments/hue-chatbot-widget.jspf" %>
</body>
</html>
