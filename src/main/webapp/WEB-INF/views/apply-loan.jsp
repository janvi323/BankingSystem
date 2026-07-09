<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DebtHues - Apply for Loan</title>
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
            max-width: 600px;
            margin: 40px auto;
            background-color: #ffffff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(220, 20, 60, 0.3);
        }
        h2 {
            text-align: center;
            color: #8B5CF6;
            margin-bottom: 30px;
            font-size: 28px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            color: #000000;
            font-weight: 600;
        }
        input[type="number"],
        input[type="text"],
        select {
            width: 100%;
            padding: 12px;
            border: 2px solid #e0e0e0;
            border-radius: 6px;
            font-size: 16px;
            transition: border-color 0.3s;
            box-sizing: border-box;
            color: #000000;
        }
        input[type="number"]:focus,
        input[type="text"]:focus,
        select:focus {
            outline: none;
            border-color: #8B5CF6;
        }
        .btn {
            width: 100%;
            padding: 15px;
            background-color: #8B5CF6;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 18px;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        .btn:hover {
            background-color: #7C3AED;
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
        .success-modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            z-index: 1000;
        }
        .success-content {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            padding: 40px;
            border-radius: 15px;
            text-align: center;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            max-width: 500px;
            width: 90%;
        }
        .success-icon {
            font-size: 60px;
            color: #28a745;
            margin-bottom: 20px;
            animation: bounceIn 0.6s ease-out;
        }
        .success-title {
            color: #8B5CF6;
            font-size: 24px;
            margin-bottom: 15px;
            font-weight: bold;
        }
        .success-message {
            color: #666;
            margin-bottom: 25px;
            line-height: 1.5;
        }
        .success-buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
        }
        .success-btn {
            padding: 12px 24px;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
        }
        .btn-primary {
            background-color: #8B5CF6;
            color: white;
        }
        .btn-primary:hover {
            background-color: #7C3AED;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #5a6268;
        }
        @keyframes bounceIn {
            0% { transform: scale(0.3); opacity: 0; }
            50% { transform: scale(1.05); }
            70% { transform: scale(0.9); }
            100% { transform: scale(1); opacity: 1; }
        }
        .loan-info {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 6px;
            margin-bottom: 20px;
            border-left: 4px solid #8B5CF6;
        }
        .loan-info h4 {
            color: #8B5CF6;
            margin-bottom: 10px;
        }
        .loan-calculation {
            background-color: #f0f9ff;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            border-left: 4px solid #28a745;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        .loan-calculation h4 {
            color: #28a745;
            margin-bottom: 15px;
            font-size: 18px;
        }
        .calculation-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
            margin-top: 10px;
        }
        .calc-item {
            display: flex;
            justify-content: space-between;
            padding: 10px;
            background-color: #f1f1f1;
            border-radius: 4px;
        }
        .calc-label {
            font-weight: 500;
            color: #333;
        }
        .calc-value {
            font-weight: 600;
            color: #8B5CF6;
        }
        .calculation-note {
            margin-top: 10px;
            font-size: 14px;
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
        <h2>Apply for Loan</h2>

        <div class="loan-info">
            <h4>Loan Application Information</h4>
            <p>&bull; Minimum loan amount: &#8377;1,000</p>
            <p>&bull; Maximum loan amount: &#8377;1,000,000</p>
            <p>&bull; Interest rates vary based on loan type and credit score</p>
            <p>&bull; All applications are subject to approval</p>
        </div>

        <div id="alertContainer"></div>

        <form id="loanForm">
            <div class="form-group">
                <label for="amount">Loan Amount (&#8377;):</label>
                <input type="number" id="amount" name="amount" min="1000" max="1000000" step="100" required>
            </div>

            <div class="form-group">
                <label for="purpose">Loan Purpose:</label>
                <select id="purpose" name="purpose" required>
                    <option value="">Select Loan Purpose</option>
                    <option value="Personal">Personal</option>
                    <option value="Home Purchase">Home Purchase</option>
                    <option value="Home Improvement">Home Improvement</option>
                    <option value="Car Purchase">Car Purchase</option>
                    <option value="Education">Education</option>
                    <option value="Business">Business</option>
                    <option value="Debt Consolidation">Debt Consolidation</option>
                    <option value="Medical Expenses">Medical Expenses</option>
                    <option value="Other">Other</option>
                </select>
            </div>

            <div class="form-group">
                <label for="tenure">Loan Tenure (Months):</label>
                <select id="tenure" name="tenure" required>
                    <option value="">Select Tenure</option>
                    <option value="12">12 Months</option>
                    <option value="24">24 Months</option>
                    <option value="36">36 Months</option>
                    <option value="48">48 Months</option>
                    <option value="60">60 Months</option>
                    <option value="84">84 Months</option>
                    <option value="120">120 Months (10 years)</option>
                    <option value="240">240 Months (20 years)</option>
                    <option value="360">360 Months (30 years)</option>
                </select>
            </div>

            </div>

            <!-- ── NEW: Employment Info ───────────────────────────────────── -->
            <div style="background:#f0f4ff;padding:20px;border-radius:8px;margin-bottom:20px;border-left:4px solid #6366f1;">
                <h4 style="color:#6366f1;margin-bottom:15px;">👔 Employment Information <small style="font-weight:400;color:#888;">(improves AI decision accuracy)</small></h4>
                <div class="form-group">
                    <label for="employmentType">Employment Type:</label>
                    <select id="employmentType" name="employmentType">
                        <option value="SALARIED">Salaried Employee</option>
                        <option value="SELF_EMPLOYED">Self-Employed / Freelancer</option>
                        <option value="BUSINESS">Business Owner</option>
                        <option value="UNEMPLOYED">Unemployed</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="employmentYears">Years at Current Employer:</label>
                    <select id="employmentYears" name="employmentYears">
                        <option value="0">Less than 1 year</option>
                        <option value="1">1 year</option>
                        <option value="2" selected>2 years</option>
                        <option value="3">3 years</option>
                        <option value="5">5+ years</option>
                        <option value="10">10+ years</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="monthlyIncome">Monthly Income (&#8377;):</label>
                    <input type="number" id="monthlyIncome" name="monthlyIncome" min="0" step="1000" placeholder="e.g. 50000">
                </div>
            </div>

            <!-- ── NEW: Compare Banks Button ─────────────────────────────── -->
            <div style="margin-bottom:20px;">
                <button type="button" id="compareBanksBtn" onclick="compareBanks()"
                    style="width:100%;padding:14px;background:linear-gradient(135deg,#667eea,#764ba2);color:white;border:none;border-radius:8px;font-size:16px;font-weight:600;cursor:pointer;transition:opacity 0.2s;">
                    🏦 Compare Bank Offers &amp; Rates
                </button>
            </div>

            <!-- ── NEW: Bank Comparison Table (hidden until API call) ──────── -->
            <div id="bankComparisonPanel" style="display:none;margin-bottom:24px;">
                <div style="background:linear-gradient(135deg,#1e1b4b,#312e81);color:white;padding:20px;border-radius:12px 12px 0 0;">
                    <h3 style="margin:0;font-size:18px;">🏦 Best Loan Offers For You</h3>
                    <p style="margin:6px 0 0;font-size:13px;opacity:0.8;">Ranked by AI • Select a bank to apply</p>
                </div>
                <div id="bankTableContainer" style="background:white;border:1px solid #e0e0e0;border-top:none;border-radius:0 0 12px 12px;overflow:hidden;">
                    <table style="width:100%;border-collapse:collapse;font-size:14px;">
                        <thead>
                            <tr style="background:#f8f7ff;">
                                <th style="padding:12px;text-align:left;">Bank</th>
                                <th style="padding:12px;text-align:center;">Rate</th>
                                <th style="padding:12px;text-align:center;">Approval %</th>
                                <th style="padding:12px;text-align:center;">Monthly EMI</th>
                                <th style="padding:12px;text-align:center;">Select</th>
                            </tr>
                        </thead>
                        <tbody id="bankTableBody"></tbody>
                    </table>
                </div>
                <div id="aiRecommendationBox" style="background:#f0fdf4;border:1px solid #86efac;border-radius:8px;padding:14px;margin-top:12px;font-size:14px;"></div>
            </div>

            <!-- Hidden selected bank field -->
            <input type="hidden" id="selectedBankName" name="selectedBankName" value="">

            <!-- Loan Calculation Display -->
            <div id="loanCalculation" class="loan-calculation" style="display: none;">
                <h4>📊 Loan Calculation Details</h4>
                <div class="calculation-grid">
                    <div class="calc-item">
                        <span class="calc-label">Interest Rate:</span>
                        <span id="interestRateDisplay" class="calc-value">-</span>
                    </div>
                    <div class="calc-item">
                        <span class="calc-label">Monthly EMI:</span>
                        <span id="emiAmountDisplay" class="calc-value">-</span>
                    </div>
                    <div class="calc-item">
                        <span class="calc-label">Total Amount:</span>
                        <span id="totalAmountDisplay" class="calc-value">-</span>
                    </div>
                    <div class="calc-item">
                        <span class="calc-label">Total Interest:</span>
                        <span id="totalInterestDisplay" class="calc-value">-</span>
                    </div>
                </div>
                <div class="calculation-note">
                    <small>💡 <strong>Note:</strong> Interest rates are calculated based on loan purpose, amount, and tenure. These are estimated values and final rates may vary based on approval.</small>
                </div>
            </div>

            <button type="submit" class="btn" id="submitBtn">🚀 Submit Loan Application</button>
        </form>

        <!-- ── NEW: AI Decision Result Panel ─────────────────────────────────── -->
        <div id="decisionPanel" style="display:none;margin-top:28px;">
            <div id="decisionHeader" style="padding:20px;border-radius:12px 12px 0 0;color:white;">
                <h3 id="decisionTitle" style="margin:0;"></h3>
                <p id="decisionSummary" style="margin:8px 0 0;opacity:0.9;"></p>
            </div>
            <div style="background:white;border:1px solid #e0e0e0;border-top:none;border-radius:0 0 12px 12px;padding:20px;">
                <!-- Score + Health row -->
                <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:16px;margin-bottom:20px;">
                    <div style="text-align:center;padding:16px;background:#f8f7ff;border-radius:8px;">
                        <div id="confidenceScore" style="font-size:36px;font-weight:700;color:#6366f1;"></div>
                        <div style="font-size:12px;color:#666;margin-top:4px;">AI Score</div>
                    </div>
                    <div style="text-align:center;padding:16px;background:#f0fdf4;border-radius:8px;">
                        <div id="healthScore" style="font-size:36px;font-weight:700;color:#22c55e;"></div>
                        <div style="font-size:12px;color:#666;margin-top:4px;">Health Score</div>
                    </div>
                    <div style="text-align:center;padding:16px;background:#fff7ed;border-radius:8px;">
                        <div id="riskBadge" style="font-size:16px;font-weight:700;color:#f59e0b;padding-top:8px;"></div>
                        <div style="font-size:12px;color:#666;margin-top:4px;">Risk Profile</div>
                    </div>
                </div>
                <!-- Rejection reasons -->
                <div id="reasonsSection" style="display:none;margin-bottom:16px;">
                    <h4 style="color:#dc2626;margin-bottom:10px;">❌ Why Your Application Was Not Approved</h4>
                    <ul id="reasonsList" style="padding-left:20px;color:#374151;line-height:1.8;"></ul>
                </div>
                <!-- Recommendations -->
                <div id="recsSection" style="display:none;margin-bottom:16px;">
                    <h4 style="color:#16a34a;margin-bottom:10px;">💡 Personalized Recommendations</h4>
                    <ul id="recsList" style="padding-left:20px;color:#374151;line-height:1.8;"></ul>
                </div>
                <!-- Score breakdown -->
                <div id="breakdownSection" style="display:none;">
                    <h4 style="color:#6366f1;margin-bottom:10px;">📊 Score Breakdown</h4>
                    <div id="breakdownList" style="display:grid;grid-template-columns:1fr 1fr;gap:8px;"></div>
                </div>
                <!-- Action buttons -->
                <div style="display:flex;gap:12px;margin-top:20px;">
                    <a href="/loans" style="flex:1;text-align:center;padding:12px;background:#6366f1;color:white;text-decoration:none;border-radius:8px;font-weight:600;">View My Loans</a>
                    <a href="/dashboard" style="flex:1;text-align:center;padding:12px;background:#f1f5f9;color:#374151;text-decoration:none;border-radius:8px;font-weight:600;">Dashboard</a>
                </div>
            </div>
        </div>
    </div>

    <!-- Success Modal -->
    <div id="successModal" class="success-modal">
        <div class="success-content">
            <div class="success-icon">✓</div>
            <h3 class="success-title">Loan Application Submitted!</h3>
            <div class="success-message">
                <p>Congratulations! Your loan application has been successfully submitted to our system.</p>
                <p><strong>Application ID: <span id="loanId"></span></strong></p>
                <p>You can track your application status in the loans section.</p>
            </div>
            <div class="success-buttons">
                <a href="/dashboard" class="success-btn btn-primary">Go to Dashboard</a>
                <a href="/loans" class="success-btn btn-secondary">View My Loans</a>
            </div>
        </div>
    </div>

    <script>
        function showAlert(message, type) {
            const alertContainer = document.getElementById('alertContainer');
            alertContainer.innerHTML = `<div class="alert alert-${type}">${message}</div>`;
            setTimeout(() => { alertContainer.innerHTML = ''; }, 5000);
        }

        // ── Bank Comparison ───────────────────────────────────────────────────
        function compareBanks() {
            const amount  = parseFloat(document.getElementById('amount').value);
            const tenure  = parseInt(document.getElementById('tenure').value);
            const purpose = document.getElementById('purpose').value;

            if (!amount || !tenure || !purpose) {
                showAlert('Please fill in Amount, Purpose, and Tenure before comparing banks.', 'warning');
                return;
            }
            const btn = document.getElementById('compareBanksBtn');
            btn.disabled = true;
            btn.textContent = '⏳ Fetching offers...';

            fetch(`/api/banks/compare?amount=${amount}&tenure=${tenure}&purpose=${encodeURIComponent(purpose)}`)
            .then(r => r.json())
            .then(data => {
                renderBankTable(data);
                document.getElementById('bankComparisonPanel').style.display = 'block';
                document.getElementById('bankComparisonPanel').scrollIntoView({behavior:'smooth'});
            })
            .catch(err => showAlert('Could not fetch bank offers: ' + err.message, 'danger'))
            .finally(() => { btn.disabled = false; btn.textContent = '🏦 Compare Bank Offers & Rates'; });
        }

        function renderBankTable(data) {
            const tbody = document.getElementById('bankTableBody');
            tbody.innerHTML = '';
            (data.offers || []).forEach((offer, idx) => {
                const isRec   = offer.recommended;
                const badge   = offer.riskBadge ? `<span style="background:#e0e7ff;color:#4338ca;padding:2px 8px;border-radius:12px;font-size:11px;font-weight:600;">${offer.riskBadge}</span>` : '';
                const probClr = offer.approvalProbability >= 75 ? '#16a34a' : offer.approvalProbability >= 50 ? '#d97706' : '#dc2626';
                tbody.innerHTML += `
                    <tr id="bankRow_${offer.bankCode}" style="border-bottom:1px solid #f0f0f0;${isRec?'background:#f5f3ff;':''}cursor:pointer;" onclick="selectBank('${offer.bankCode}','${offer.bankName}',${offer.interestRate})">
                        <td style="padding:12px;">
                            <div style="font-weight:600;">${offer.bankLogo} ${offer.bankName} ${badge}</div>
                            <div style="font-size:11px;color:#888;margin-top:2px;">${(offer.aiReason||'').substring(0,80)}...</div>
                        </td>
                        <td style="padding:12px;text-align:center;font-weight:700;color:#6366f1;">${offer.interestRate}%</td>
                        <td style="padding:12px;text-align:center;font-weight:700;color:${probClr};">${offer.approvalProbability}%</td>
                        <td style="padding:12px;text-align:center;">₹${Math.round(offer.emiAmount).toLocaleString('en-IN')}</td>
                        <td style="padding:12px;text-align:center;">
                            <button onclick="event.stopPropagation();selectBank('${offer.bankCode}','${offer.bankName}',${offer.interestRate})"
                                id="selBtn_${offer.bankCode}"
                                style="padding:6px 14px;border:2px solid #6366f1;border-radius:6px;background:white;color:#6366f1;font-weight:600;cursor:pointer;">
                                Select
                            </button>
                        </td>
                    </tr>`;
            });
            if (data.aiRecommendationText) {
                document.getElementById('aiRecommendationBox').innerHTML =
                    `<strong>🤖 AI Recommendation:</strong> ${data.aiRecommendationText}`;
            }
        }

        function selectBank(code, name, rate) {
            document.getElementById('selectedBankName').value = name;
            // Highlight selected row
            document.querySelectorAll('[id^="bankRow_"]').forEach(r => r.style.background = '');
            document.querySelectorAll('[id^="selBtn_"]').forEach(b => { b.style.background='white'; b.style.color='#6366f1'; b.textContent='Select'; });
            const row = document.getElementById('bankRow_' + code);
            const btn = document.getElementById('selBtn_' + code);
            if (row) row.style.background = '#ede9fe';
            if (btn) { btn.style.background='#6366f1'; btn.style.color='white'; btn.textContent='✓ Selected'; }
            showAlert(`✅ ${name} selected (${rate}% rate). Click Submit to apply.`, 'success');
        }

        // ── AI Decision Panel ─────────────────────────────────────────────────
        function showDecisionPanel(decision) {
            const panel  = document.getElementById('decisionPanel');
            const header = document.getElementById('decisionHeader');
            panel.style.display = 'block';

            const isApproved = decision.decisionType === 'AUTO_APPROVED';
            const isReview   = decision.decisionType === 'MANUAL_REVIEW';
            header.style.background = isApproved ? 'linear-gradient(135deg,#16a34a,#15803d)'
                                    : isReview   ? 'linear-gradient(135deg,#d97706,#b45309)'
                                    :              'linear-gradient(135deg,#dc2626,#b91c1c)';
            document.getElementById('decisionTitle').textContent =
                isApproved ? '✅ Loan Auto-Approved!' : isReview ? '⏳ Under Manual Review' : '❌ Application Not Approved';
            document.getElementById('decisionSummary').textContent = decision.decisionSummary || '';

            document.getElementById('confidenceScore').textContent = (decision.confidencePercent || 0) + '%';
            document.getElementById('healthScore').textContent     = (decision.financialHealthScore || 0) + '/100';
            document.getElementById('riskBadge').textContent       = (decision.riskProfile || '').replace('_',' ');

            const reasons = decision.rejectionReasons || [];
            if (reasons.length) {
                document.getElementById('reasonsSection').style.display = 'block';
                document.getElementById('reasonsList').innerHTML = reasons.map(r => `<li>${r}</li>`).join('');
            }
            const recs = decision.recommendations || [];
            if (recs.length) {
                document.getElementById('recsSection').style.display = 'block';
                document.getElementById('recsList').innerHTML = recs.map(r => `<li>${r}</li>`).join('');
            }
            const breakdown = decision.scoreBreakdown || [];
            if (breakdown.length) {
                document.getElementById('breakdownSection').style.display = 'block';
                document.getElementById('breakdownList').innerHTML = breakdown.map(b =>
                    `<div style="padding:8px;background:#f8f7ff;border-radius:6px;font-size:13px;">${b}</div>`).join('');
            }
            panel.scrollIntoView({behavior:'smooth'});
        }

        // ── Form Submission ───────────────────────────────────────────────────
        document.getElementById('loanForm').addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = {
                amount:             parseFloat(document.getElementById('amount').value),
                purpose:            document.getElementById('purpose').value,
                tenure:             parseInt(document.getElementById('tenure').value),
                employmentType:     document.getElementById('employmentType').value,
                employmentYears:    parseInt(document.getElementById('employmentYears').value),
                monthlyIncome:      parseFloat(document.getElementById('monthlyIncome').value) || null,
                selectedBankName:   document.getElementById('selectedBankName').value || null
            };

            if (formData.amount < 1000) { showAlert('Minimum loan amount is ₹1,000', 'danger'); return; }
            if (formData.amount > 10000000) { showAlert('Maximum loan amount is ₹1,00,00,000', 'danger'); return; }

            const submitBtn = document.getElementById('submitBtn');
            submitBtn.disabled = true;
            submitBtn.textContent = '⏳ Processing with AI...';

            fetch('/api/loans/apply', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(formData)
            })
            .then(r => r.json())
            .then(data => {
                document.getElementById('loanForm').style.display = 'none';
                document.getElementById('bankComparisonPanel').style.display = 'none';
                if (data.decision) {
                    showDecisionPanel(data.decision);
                } else if (data.id || data.loanId) {
                    showDecisionPanel({
                        decisionType: 'MANUAL_REVIEW',
                        decisionSummary: 'Application submitted successfully. ID: ' + (data.id || data.loanId),
                        confidencePercent: 0, financialHealthScore: 0, riskProfile: 'MEDIUM',
                        rejectionReasons: [], recommendations: [], scoreBreakdown: []
                    });
                } else {
                    showAlert('Application submitted!', 'success');
                }
            })
            .catch(err => { showAlert('Application failed: ' + err.message, 'danger'); })
            .finally(() => { submitBtn.disabled = false; submitBtn.textContent = '🚀 Submit Loan Application'; });
        });

        // Real-time loan calculation using the backend API
        function calculateLoanDetails() {
            const amount = parseFloat(document.getElementById('amount').value);
            const purpose = document.getElementById('purpose').value;
            const tenure = parseInt(document.getElementById('tenure').value);

            if (!isNaN(amount) && amount >= 1000 && purpose && !isNaN(tenure)) {
                const formData = {
                    amount: amount,
                    purpose: purpose,
                    tenure: tenure
                };

                // Call the backend calculation API
                fetch('/api/loans/calculate', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(formData)
                })
                .then(response => response.json())
                .then(data => {
                    if (data.error) {
                        console.error('Calculation error:', data.error);
                        document.getElementById('loanCalculation').style.display = 'none';
                    } else {
                        // Update the display with real calculated values
                        document.getElementById('interestRateDisplay').textContent = data.interestRate + '%';
                        document.getElementById('emiAmountDisplay').textContent = '₹' + data.emiAmount.toLocaleString('en-IN');
                        document.getElementById('totalAmountDisplay').textContent = '₹' + data.totalAmount.toLocaleString('en-IN');
                        document.getElementById('totalInterestDisplay').textContent = '₹' + data.totalInterest.toLocaleString('en-IN');

                        document.getElementById('loanCalculation').style.display = 'block';
                    }
                })
                .catch(error => {
                    console.error('Calculation API error:', error);
                    document.getElementById('loanCalculation').style.display = 'none';
                });
            } else {
                document.getElementById('loanCalculation').style.display = 'none';
            }
        }

        // Event listeners for real-time calculation
        document.getElementById('amount').addEventListener('input', calculateLoanDetails);
        document.getElementById('purpose').addEventListener('change', calculateLoanDetails);
        document.getElementById('tenure').addEventListener('change', calculateLoanDetails);

        // Check if user is logged in
        fetch('/api/auth/current')
            .then(response => {
                if (!response.ok) {
                    window.location.href = '/login';
                }
                return response.json();
            })
            .then(user => {
                if (user.role === 'ADMIN') {
                    showAlert('Admins cannot apply for loans. Please use the admin panel to manage loan applications.', 'danger');
                    setTimeout(() => {
                        window.location.href = '/dashboard';
                    }, 3000);
                } else if (user.role !== 'CUSTOMER') {
                    showAlert('Only customers can apply for loans. Admins can view loan applications in the Loans section.', 'danger');
                    setTimeout(() => {
                        window.location.href = '/loans';
                    }, 3000);
                }
            })
            .catch(() => {
                window.location.href = '/login';
            });
    </script>
    <%@ include file="fragments/hue-chatbot-widget.jspf" %>
</body>
</html>
