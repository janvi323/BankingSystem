<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Loan Management System - Chatbot</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot-styles.css">
</head>
<body>
    <!-- Floating Chatbot Button -->
    <div id="chatbot-toggle" class="chatbot-toggle-btn">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
        </svg>
        <span class="unread-badge" id="unread-badge" style="display: none;">1</span>
    </div>

    <!-- Chatbot Window -->
    <div id="chatbot-window" class="chatbot-window">
        <!-- Header -->
        <div class="chatbot-header">
            <div class="chatbot-header-content">
                <h3>Banking Assistant</h3>
                <p class="status-text">Online</p>
            </div>
            <div class="chatbot-header-actions">
                <button id="minimize-btn" class="header-btn" title="Minimize">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="5" y1="12" x2="19" y2="12"></line>
                    </svg>
                </button>
                <button id="clear-btn" class="header-btn" title="Clear chat">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="3 6 5 4 21 4 23 6 23 20a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V6"></polyline>
                        <line x1="10" y1="11" x2="10" y2="17"></line>
                        <line x1="14" y1="11" x2="14" y2="17"></line>
                    </svg>
                </button>
                <button id="close-btn" class="header-btn" title="Close">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="18" y1="6" x2="6" y2="18"></line>
                        <line x1="6" y1="6" x2="18" y2="18"></line>
                    </svg>
                </button>
            </div>
        </div>

        <!-- Messages Container -->
        <div id="chatbot-messages" class="chatbot-messages">
            <div class="welcome-message">
                <div class="bot-avatar">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm3.5-9c.83 0 1.5-.67 1.5-1.5S16.33 8 15.5 8 14 8.67 14 9.5s.67 1.5 1.5 1.5zm-7 0c.83 0 1.5-.67 1.5-1.5S9.33 8 8.5 8 7 8.67 7 9.5 7.67 11 8.5 11zm3.5 6.5c2.33 0 4.31-1.46 5.11-3.5H6.89c.8 2.04 2.78 3.5 5.11 3.5z"></path>
                    </svg>
                </div>
                <div class="welcome-content">
                    <p><strong>Welcome to Banking Assistant!</strong></p>
                    <p>I can help you with:</p>
                    <ul>
                        <li>📊 Loan information and eligibility</li>
                        <li>💰 EMI calculations and payments</li>
                        <li>📈 Interest rates explanation</li>
                        <li>🏦 General banking queries</li>
                    </ul>
                </div>
            </div>
        </div>

        <!-- Quick Questions (Optional) -->
        <div id="quick-questions" class="quick-questions">
            <button class="quick-btn" data-question="What is EMI and how is it calculated?">EMI Calculation</button>
            <button class="quick-btn" data-question="What is the current loan interest rate?">Interest Rates</button>
            <button class="quick-btn" data-question="Am I eligible for a loan?">Loan Eligibility</button>
            <button class="quick-btn" data-question="How do I apply for a loan?">Apply for Loan</button>
        </div>

        <!-- Input Area -->
        <div class="chatbot-input-area">
            <div class="input-wrapper">
                <input 
                    type="text" 
                    id="chat-input" 
                    class="chat-input" 
                    placeholder="Type your question..."
                    maxlength="2000"
                >
                <button id="send-btn" class="send-btn" title="Send message (Enter)">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M16.6915026,12.4744748 L3.50612381,13.2599618 C3.19218622,13.2599618 3.03521743,13.4170592 3.03521743,13.5741566 L1.15159189,20.0151496 C0.8376543,20.8006365 0.99,21.89 1.77946707,22.52 C2.41,22.99 3.50612381,23.1 4.13399899,22.8429026 L21.714504,14.0454487 C22.6563168,13.5741566 23.1272231,12.6315722 22.9702544,11.6889879 L4.13399899,1.16398623 C3.34915502,0.9 2.40734225,1.00636533 1.77946707,1.4776575 C0.994623095,2.10604706 0.837654326,3.0486314 1.15159189,3.99701575 L3.03521743,10.4380088 C3.03521743,10.5951061 3.34915502,10.7522035 3.50612381,10.7522035 L16.6915026,11.5376904 C16.6915026,11.5376904 17.1624089,11.5376904 17.1624089,12.0089825 C17.1624089,12.4744748 16.6915026,12.4744748 16.6915026,12.4744748 Z"></path>
                    </svg>
                </button>
            </div>
            <div class="char-count">
                <span id="char-count">0</span>/2000
            </div>
        </div>

        <!-- Loading Indicator -->
        <div id="loading-indicator" class="loading-indicator" style="display: none;">
            <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
            </div>
        </div>
    </div>

    <!-- Toast Notification -->
    <div id="toast" class="toast"></div>

    <!-- Script -->
    <script src="${pageContext.request.contextPath}/js/chatbot.js"></script>
</body>
</html>
