<!-- 
  CHATBOT INTEGRATION EXAMPLE
  
  This file shows different ways to integrate the chatbot into your existing pages.
  You can reference this when adding the chatbot to your dashboard or other pages.
-->

<!-- ====================================
     OPTION 1: MINIMAL INTEGRATION (Recommended)
     ===================================== -->

<!-- Add these 2 lines before closing </body> tag in ANY JSP page -->

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot-styles.css">
<script src="${pageContext.request.contextPath}/js/chatbot.js"></script>

<!-- Result: Chatbot will appear on every page that includes these lines -->


<!-- ====================================
     OPTION 2: INCLUDE FULL CHATBOT PAGE
     ===================================== -->

<!-- Include the entire chatbot JSP -->
<jsp:include page="chatbot.jsp" />


<!-- ====================================
     OPTION 3: GLOBAL INTEGRATION (Best for all pages)
     ===================================== -->

<!-- Add to your master/base layout JSP (if you have one) -->
<!-- This ensures chatbot appears on ALL pages -->

<!DOCTYPE html>
<html>
<head>
    <!-- Your existing head content -->
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Banking System</title>
    
    <!-- Include chatbot CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot-styles.css">
</head>
<body>
    <!-- Your existing page content -->
    
    <!-- Include chatbot JavaScript at end of body -->
    <script src="${pageContext.request.contextPath}/js/chatbot.js"></script>
</body>
</html>


<!-- ====================================
     OPTION 4: DYNAMIC INTEGRATION (For Single Page Apps)
     ===================================== -->

<script>
// Load chatbot dynamically when page is ready
document.addEventListener('DOMContentLoaded', function() {
    // Load CSS
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = '${pageContext.request.contextPath}/css/chatbot-styles.css';
    document.head.appendChild(link);
    
    // Load HTML (if not already in page)
    fetch('${pageContext.request.contextPath}/WEB-INF/views/chatbot.jsp')
        .then(response => response.text())
        .then(html => {
            // Append to body or target element
            document.body.insertAdjacentHTML('beforeend', html);
            
            // Load JavaScript
            const script = document.createElement('script');
            script.src = '${pageContext.request.contextPath}/js/chatbot.js';
            document.body.appendChild(script);
        });
});
</script>


<!-- ====================================
     OPTION 5: DASHBOARD INTEGRATION EXAMPLE
     ===================================== -->

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Banking Dashboard</title>
    
    <!-- Your existing stylesheets -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <!-- Chatbot CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot-styles.css">
</head>
<body>
    <!-- Header -->
    <header>
        <h1>Welcome to Banking Dashboard</h1>
    </header>
    
    <!-- Main Content -->
    <main>
        <div class="container">
            <!-- Your dashboard content here -->
            <div class="dashboard-content">
                <h2>Quick Links</h2>
                <!-- Dashboard content -->
            </div>
        </div>
    </main>
    
    <!-- Footer -->
    <footer>
        <p>&copy; 2024 Banking System. All rights reserved.</p>
    </footer>
    
    <!-- Your existing scripts -->
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
    
    <!-- Chatbot Script (must be after page content) -->
    <script src="${pageContext.request.contextPath}/js/chatbot.js"></script>
</body>
</html>


<!-- ====================================
     OPTION 6: PROGRAMMATIC CONTROL (Advanced)
     ===================================== -->

<script>
// Wait for chatbot to load
document.addEventListener('DOMContentLoaded', function() {
    // Access chatbot instance
    const chatbot = window.chatbot;
    
    if (chatbot) {
        // Programmatically open chatbot
        // chatbot.openChatbot();
        
        // Send message programmatically
        // chatbot.addMessage('What is EMI?', 'user');
        
        // Listen for custom events (if implemented)
        // document.addEventListener('chatbot:message-sent', function(event) {
        //     console.log('Message sent:', event.detail);
        // });
    }
});

// Custom button to open chatbot
document.getElementById('open-chat-btn').addEventListener('click', function() {
    if (window.chatbot) {
        window.chatbot.openChatbot();
    }
});
</script>


<!-- ====================================
     BEST PRACTICE: COMPLETE EXAMPLE
     ===================================== -->

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Banking System - Dashboard</title>
    
    <!-- CSS Files (in order) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <!-- Chatbot CSS (last so it can override) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot-styles.css">
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar">
        <div class="navbar-brand">
            <h1>Banking System</h1>
        </div>
        <ul class="nav-menu">
            <li><a href="/dashboard">Dashboard</a></li>
            <li><a href="/loans">Loans</a></li>
            <li><a href="/profile">Profile</a></li>
            <li><a href="/logout">Logout</a></li>
        </ul>
    </nav>
    
    <!-- Main Content -->
    <main class="main-content">
        <div class="container">
            <h2>Dashboard</h2>
            
            <!-- Dashboard Cards -->
            <div class="dashboard-grid">
                <div class="card">
                    <h3>Active Loans</h3>
                    <p class="card-value">${activeLoanCount}</p>
                </div>
                <div class="card">
                    <h3>Next EMI Due</h3>
                    <p class="card-value">${nextEmiDate}</p>
                </div>
                <div class="card">
                    <h3>Account Balance</h3>
                    <p class="card-value">₹${accountBalance}</p>
                </div>
            </div>
            
            <!-- Recent Transactions -->
            <div class="transactions">
                <h3>Recent Transactions</h3>
                <table class="table">
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Description</th>
                            <th>Amount</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="txn" items="${recentTransactions}">
                            <tr>
                                <td>${txn.date}</td>
                                <td>${txn.description}</td>
                                <td>₹${txn.amount}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
    
    <!-- Footer -->
    <footer class="footer">
        <p>&copy; 2024 Banking System. All rights reserved.</p>
    </footer>
    
    <!-- Scripts (in order) -->
    <script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
    
    <!-- Chatbot JavaScript (LAST - must be after body content) -->
    <script src="${pageContext.request.contextPath}/js/chatbot.js"></script>
    
    <script>
        // Optional: Initialize chatbot with custom settings after it loads
        window.addEventListener('load', function() {
            console.log('Page loaded - Chatbot should be visible');
            
            // Check if chatbot loaded
            if (window.chatbot) {
                console.log('Chatbot loaded successfully');
            } else {
                console.warn('Chatbot failed to load');
            }
        });
    </script>
</body>
</html>


<!-- ====================================
     IMPORTANT NOTES
     ===================================== -->

<!--
1. CSS MUST be included in <head> section
2. JavaScript MUST be included at the end of <body>
3. Order matters: CSS first, then page content, then JS
4. Works with jQuery, Bootstrap, or no framework
5. Chatbot will appear on ALL pages that include these files
6. Floating button will stick to bottom-right of screen
7. Chatbot persists across page navigation
8. Chat history loads automatically on page load

COMMON MISTAKES TO AVOID:
- Don't load JavaScript in <head> (it will fail to find DOM elements)
- Don't forget the context path: ${pageContext.request.contextPath}
- Don't load CSS multiple times on same page
- Don't modify chatbot code without backing up original
- Don't hardcode API key in frontend

TESTING:
- Open browser DevTools (F12)
- Check Console for errors (red messages)
- Check Network tab - CSS and JS should load (green 200 status)
- Click floating button - window should expand
- Type message and send - should work
- Check database - message should be saved

DEBUGGING:
- Enable logging: Add to application.properties
  logging.level.com.bankingsystem=DEBUG
- Check Spring logs for backend errors
- Check browser console for frontend errors
- Monitor Network tab for API calls
- Verify .env file has correct API key

CUSTOMIZATION:
- Edit chatbot-styles.css to change colors/fonts
- Edit chatbot.js to change behavior
- Edit ChatService.java to change AI responses
- Keep backups of original files
-->
