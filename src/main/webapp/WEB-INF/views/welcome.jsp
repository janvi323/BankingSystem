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
      <a href="/login" class="btn btn-ghost">Login</a>
      <a href="/login" class="btn btn-primary">Create Account</a>
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
        <a href="/login" class="btn btn-primary btn-lg">Get Started Free</a>
        <a href="/login" class="btn btn-outline btn-lg">Login</a>
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

    <!-- Right: Product Preview -->
    <div style="position: relative;">
      <div class="product-preview-card">
        <div class="preview-topline">
          <span>Financial Profile</span>
          <strong>85%</strong>
        </div>
        <div class="preview-progress"><span style="width:85%;"></span></div>
        <div class="preview-score-row">
          <div>
            <span class="preview-label">Health Score</span>
            <strong>78/100</strong>
          </div>
          <div>
            <span class="preview-label">Approval</span>
            <strong>82%</strong>
          </div>
        </div>
        <div class="preview-offer">
          <span>Pre-approved Personal Loan</span>
          <strong>Rs. 8,40,000</strong>
        </div>
        <a href="/login" class="btn btn-primary btn-lg btn-full">Open Dashboard</a>
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
          <li><a href="/login">Sign Up</a></li>
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

</body>
</html>
