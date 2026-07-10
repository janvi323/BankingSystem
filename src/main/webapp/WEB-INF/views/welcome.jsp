<%@ page contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>DebtHues — AI-Powered Banking &amp; Credit Intelligence Platform</title>
  <meta name="description" content="Monitor accounts, analyze creditworthiness, manage loans, and unlock financial insights through a secure intelligent banking platform.">
  <link rel="stylesheet" href="/css/welcome.css">
  <link rel="icon" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><text y='.9em' font-size='90'>💎</text></svg>">
</head>
<body>

<!-- Animated background orbs -->
<div class="bg-orbs">
  <div class="orb"></div>
  <div class="orb"></div>
  <div class="orb"></div>
</div>

<!-- ══════════════════════════════════════════════════════════
     NAVIGATION
     ══════════════════════════════════════════════════════════ -->
<nav class="nav" id="nav">
  <div class="container nav-inner">
    <a href="#" class="nav-logo">
      <div class="logo-icon">D</div>
      <span>DebtHues</span>
    </a>

    <ul class="nav-links" id="navLinks">
      <li><a href="#hero">Home</a></li>
      <li><a href="#features">Features</a></li>
      <li><a href="#security">Security</a></li>
      <li><a href="#trust">About</a></li>
      <li><a href="#footer">Contact</a></li>
    </ul>

    <div class="nav-actions">
      <a href="#hero" class="btn btn-ghost">Login</a>
      <a href="#hero" class="btn btn-primary" onclick="switchToRegister()">Create Account</a>
      <button class="hamburger" id="hamburger" aria-label="Toggle menu">
        <span></span><span></span><span></span>
      </button>
    </div>
  </div>
</nav>

<!-- ══════════════════════════════════════════════════════════
     HERO SECTION
     ══════════════════════════════════════════════════════════ -->
<section class="hero" id="hero">
  <div class="container hero-grid">

    <!-- Left: Content -->
    <div class="hero-content">
      <div class="hero-badge">
        <span class="pulse"></span>
        AI-Powered Financial Intelligence
      </div>

      <h1>
        Smarter Banking.<br>
        Better Credit.<br>
        <span class="gradient-text">Powered by Intelligence.</span>
      </h1>

      <p class="hero-subtitle">
        Monitor accounts, analyze creditworthiness, manage loans, and unlock financial insights through a secure intelligent banking platform.
      </p>

      <div class="hero-cta">
        <a href="#hero" class="btn btn-primary btn-lg" onclick="switchToRegister()">Get Started Free</a>
        <a href="#features" class="btn btn-outline btn-lg">Explore Features</a>
      </div>

      <div class="hero-stats">
        <div class="hero-stat">
          <span class="value">99.99%</span>
          <span class="label">Uptime SLA</span>
        </div>
        <div class="hero-stat">
          <span class="value">10K+</span>
          <span class="label">Records Managed</span>
        </div>
        <div class="hero-stat">
          <span class="value">&lt;100ms</span>
          <span class="label">Response Time</span>
        </div>
      </div>
    </div>

    <!-- Right: Auth Card -->
    <div style="position: relative;">
      <div class="auth-card">
        <div class="auth-tabs">
          <button class="auth-tab active" data-tab="login">Sign In</button>
          <button class="auth-tab" data-tab="register">Create Account</button>
        </div>

        <!-- Flash messages from server (login error / logout / registration success) -->
        <c:if test="${not empty loginError}">
          <div class="auth-error" style="display:block;">${loginError}</div>
        </c:if>
        <c:if test="${not empty logoutMessage}">
          <div class="auth-success" style="display:block;">${logoutMessage}</div>
        </c:if>
        <c:if test="${not empty message}">
          <div class="auth-success" style="display:block;">${message}</div>
        </c:if>
        <c:if test="${not empty error}">
          <div class="auth-error" style="display:block;">${error}</div>
        </c:if>

        <!-- JS-driven inline messages -->
        <div class="auth-success" id="jsAuthSuccess"></div>
        <div class="auth-error" id="jsAuthError"></div>

        <!-- ── Login Form ────────────────────────────── -->
        <form id="loginForm" action="/perform_login" method="POST">
          <div class="form-select-role">
            <input type="hidden" name="role" value="CUSTOMER" id="loginRole">
            <div class="role-option active" data-role="CUSTOMER" onclick="setLoginRole('CUSTOMER', this)">Customer</div>
            <div class="role-option" data-role="ADMIN" onclick="setLoginRole('ADMIN', this)">Admin</div>
          </div>

          <div class="form-group">
            <label class="form-label" for="loginEmail">Email Address</label>
            <input class="form-input" type="email" id="loginEmail" name="username" placeholder="you@example.com" required autocomplete="email">
          </div>

          <div class="form-group">
            <label class="form-label" for="loginPassword">Password</label>
            <input class="form-input" type="password" id="loginPassword" name="password" placeholder="••••••••" required autocomplete="current-password">
          </div>

          <div class="form-row">
            <label class="form-check">
              <input type="checkbox"> Remember me
            </label>
            <a href="#" class="form-link">Forgot Password?</a>
          </div>

          <button type="submit" class="btn btn-primary btn-lg btn-full">Sign In</button>

          <div class="divider"><span>or continue with</span></div>

          <a href="/oauth2/authorization/google" class="btn btn-google btn-full">
            <svg viewBox="0 0 24 24"><path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 0 1-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z"/><path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/><path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/><path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/></svg>
            Sign in with Google
          </a>
        </form>

        <!-- ── Register Form ─────────────────────────── -->
        <form id="registerForm" action="/perform_register" method="POST" style="display:none;">
          <div class="form-select-role">
            <input type="hidden" name="role" value="CUSTOMER" id="registerRole">
            <div class="role-option active" data-role="CUSTOMER" onclick="setRegisterRole('CUSTOMER', this)">Customer</div>
            <div class="role-option" data-role="ADMIN" onclick="setRegisterRole('ADMIN', this)">Admin</div>
          </div>

          <div class="form-group">
            <label class="form-label" for="regName">Full Name</label>
            <input class="form-input" type="text" id="regName" name="name" placeholder="John Doe" required>
          </div>

          <div class="form-group">
            <label class="form-label" for="regEmail">Email Address</label>
            <input class="form-input" type="email" id="regEmail" name="email" placeholder="you@example.com" required autocomplete="email">
          </div>

          <div class="form-group">
            <label class="form-label" for="regPassword">Password</label>
            <input class="form-input" type="password" id="regPassword" name="password" placeholder="••••••••" required autocomplete="new-password">
          </div>

          <div class="form-group">
            <label class="form-label" for="regPhone">Phone Number</label>
            <input class="form-input" type="tel" id="regPhone" name="phone" placeholder="+91 98765 43210" required>
          </div>

          <div class="form-group">
            <label class="form-label" for="regAddress">Address</label>
            <input class="form-input" type="text" id="regAddress" name="address" placeholder="City, State" required>
          </div>

          <button type="submit" class="btn btn-primary btn-lg btn-full">Create Account</button>

          <p style="text-align:center; margin-top:14px; font-size:0.8rem; color:var(--text-muted);">
            For detailed registration with loan info, <a href="/register" class="form-link">use the full form</a>
          </p>
        </form>
      </div>

      <!-- Floating decoration cards -->
      <div class="floating-cards">
        <div class="floating-card">
          <div class="fc-icon" style="background:var(--emerald-glow);">📊</div>
          <div class="fc-text"><span class="fc-label">Credit Score</span><span class="fc-value" style="color:var(--emerald);">812 Excellent</span></div>
        </div>
        <div class="floating-card">
          <div class="fc-icon" style="background:var(--blue-glow);">✅</div>
          <div class="fc-text"><span class="fc-label">Loan Status</span><span class="fc-value" style="color:var(--blue);">Approved</span></div>
        </div>
        <div class="floating-card">
          <div class="fc-icon" style="background:rgba(245,158,11,0.15);">💰</div>
          <div class="fc-text"><span class="fc-label">Balance</span><span class="fc-value" style="color:var(--amber);">₹4,82,500</span></div>
        </div>
        <div class="floating-card">
          <div class="fc-icon" style="background:rgba(139,92,246,0.15);">🛡️</div>
          <div class="fc-text"><span class="fc-label">Risk Analysis</span><span class="fc-value" style="color:var(--purple);">Low Risk</span></div>
        </div>
      </div>
    </div>
  </div>
</section>

<!-- ══════════════════════════════════════════════════════════
     FEATURES SECTION
     ══════════════════════════════════════════════════════════ -->
<section class="section" id="features">
  <div class="container">
    <div class="section-header reveal">
      <div class="section-label">✦ Platform Features</div>
      <h2 class="section-title">Everything You Need for<br>Intelligent Banking</h2>
      <p class="section-desc">A comprehensive suite of AI-powered tools designed to transform how you manage finances, credit, and loans.</p>
    </div>

    <div class="features-grid">
      <div class="feature-card reveal reveal-delay-1">
        <div class="feature-icon emerald">📊</div>
        <h3>Credit Score Analysis</h3>
        <p>Real-time credit score monitoring with AI-driven insights. Understand the factors affecting your score and get personalized improvement recommendations.</p>
      </div>

      <div class="feature-card reveal reveal-delay-2">
        <div class="feature-icon blue">🏦</div>
        <h3>Loan Management</h3>
        <p>Apply, track, and manage loans with automated EMI calculations, approval predictions, and intelligent repayment scheduling powered by machine learning.</p>
      </div>

      <div class="feature-card reveal reveal-delay-3">
        <div class="feature-icon purple">🔐</div>
        <h3>Secure Banking</h3>
        <p>Enterprise-grade security with OAuth 2.0, end-to-end encryption, and multi-factor authentication protecting every transaction and data point.</p>
      </div>

      <div class="feature-card reveal reveal-delay-4">
        <div class="feature-icon amber">🤖</div>
        <h3>AI Financial Insights</h3>
        <p>Meet Hue — your AI banking assistant. Get instant answers about EMIs, credit scores, loan eligibility, and personalized financial guidance 24/7.</p>
      </div>
    </div>
  </div>
</section>

<!-- ══════════════════════════════════════════════════════════
     TRUST / STATS SECTION
     ══════════════════════════════════════════════════════════ -->
<section class="section trust-section" id="trust">
  <div class="container">
    <div class="section-header reveal">
      <div class="section-label">✦ Trusted Platform</div>
      <h2 class="section-title">Built for Scale.<br>Designed for Trust.</h2>
    </div>

    <div class="stats-grid">
      <div class="stat-card reveal reveal-delay-1">
        <div class="stat-value" data-target="99.99" data-suffix="%" data-decimals="2">0%</div>
        <div class="stat-label">Secure Transactions</div>
      </div>
      <div class="stat-card reveal reveal-delay-2">
        <div class="stat-value" data-target="10" data-suffix="K+" data-decimals="0">0K+</div>
        <div class="stat-label">Financial Records Managed</div>
      </div>
      <div class="stat-card reveal reveal-delay-3">
        <div class="stat-value" data-target="256" data-suffix="-bit" data-decimals="0">0-bit</div>
        <div class="stat-label">Enterprise Grade Encryption</div>
      </div>
      <div class="stat-card reveal reveal-delay-4">
        <div class="stat-value" data-target="24" data-suffix="/7" data-decimals="0">0/7</div>
        <div class="stat-label">Real-Time Analytics</div>
      </div>
    </div>
  </div>
</section>

<!-- ══════════════════════════════════════════════════════════
     SECURITY SECTION
     ══════════════════════════════════════════════════════════ -->
<section class="section" id="security">
  <div class="container">
    <div class="section-header reveal">
      <div class="section-label">✦ Security First</div>
      <h2 class="section-title">Banking-Grade Protection<br>at Every Layer</h2>
      <p class="section-desc">Your financial data is protected with the same security standards used by the world's leading financial institutions.</p>
    </div>

    <div class="security-grid">
      <div class="security-card reveal reveal-delay-1">
        <div class="security-icon">🔑</div>
        <h3>OAuth 2.0 Authentication</h3>
        <p>Industry-standard authentication with Google Sign-In integration and secure token management.</p>
      </div>

      <div class="security-card reveal reveal-delay-2">
        <div class="security-icon">🔒</div>
        <h3>End-to-End Encryption</h3>
        <p>256-bit AES encryption protects all data in transit and at rest across every interaction.</p>
      </div>

      <div class="security-card reveal reveal-delay-3">
        <div class="security-icon">📋</div>
        <h3>Secure Credit Evaluation</h3>
        <p>Isolated microservice architecture ensures credit data processing is compartmentalized and secure.</p>
      </div>

      <div class="security-card reveal reveal-delay-4">
        <div class="security-icon">🏛️</div>
        <h3>Banking Grade Protection</h3>
        <p>Spring Security framework with CSRF protection, session management, and role-based access control.</p>
      </div>
    </div>
  </div>
</section>

<!-- ══════════════════════════════════════════════════════════
     FOOTER
     ══════════════════════════════════════════════════════════ -->
<footer class="footer" id="footer">
  <div class="container">
    <div class="footer-grid">
      <div class="footer-brand">
        <a href="#" class="nav-logo">
          <div class="logo-icon">D</div>
          <span>DebtHues</span>
        </a>
        <p>AI-powered banking and credit intelligence platform. Smarter financial decisions through technology.</p>
        <div class="footer-social">
          <a href="#" aria-label="Twitter">𝕏</a>
          <a href="#" aria-label="LinkedIn">in</a>
          <a href="#" aria-label="GitHub">⌘</a>
        </div>
      </div>

      <div class="footer-col">
        <h4>Product</h4>
        <ul>
          <li><a href="#features">Features</a></li>
          <li><a href="#security">Security</a></li>
          <li><a href="#trust">About</a></li>
          <li><a href="/register">Sign Up</a></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>Resources</h4>
        <ul>
          <li><a href="#">Documentation</a></li>
          <li><a href="#">API Reference</a></li>
          <li><a href="#">System Status</a></li>
          <li><a href="#">Changelog</a></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>Legal</h4>
        <ul>
          <li><a href="#">Privacy Policy</a></li>
          <li><a href="#">Terms of Service</a></li>
          <li><a href="#">Cookie Policy</a></li>
          <li><a href="#">Contact Us</a></li>
        </ul>
      </div>
    </div>

    <div class="footer-bottom">
      <span>&copy; 2026 DebtHues. All rights reserved.</span>
      <span>Built with ☕ Spring Boot &amp; 💎 Intelligence</span>
    </div>
  </div>
</footer>

<script src="/js/landing.js"></script>
<script>
  /* ── Tab switching ── */
  function switchToRegister() {
    document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
    document.querySelector('.auth-tab[data-tab="register"]').classList.add('active');
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'block';
    document.getElementById('hero').scrollIntoView({ behavior: 'smooth' });
  }

  /* ── Role selectors ── */
  function setLoginRole(role, el) {
    document.getElementById('loginRole').value = role;
    document.querySelectorAll('#loginForm .role-option').forEach(o => o.classList.remove('active'));
    el.classList.add('active');
  }

  function setRegisterRole(role, el) {
    document.getElementById('registerRole').value = role;
    document.querySelectorAll('#registerForm .role-option').forEach(o => o.classList.remove('active'));
    el.classList.add('active');
  }

  /* ── Auth tab clicks ── */
  document.querySelectorAll('.auth-tab').forEach(tab => {
    tab.addEventListener('click', () => {
      document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      const isLogin = tab.dataset.tab === 'login';
      document.getElementById('loginForm').style.display = isLogin ? 'block' : 'none';
      document.getElementById('registerForm').style.display = isLogin ? 'none' : 'block';
    });
  });
</script>

</body>
</html>
