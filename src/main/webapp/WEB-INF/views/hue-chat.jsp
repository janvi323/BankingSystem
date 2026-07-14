<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hue Financial Coach - DebtHues</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hue-chatbot.css">
</head>
<body class="hue-full-page">
    <main class="hue-page-shell">
        <aside class="hue-page-sidebar">
            <a class="hue-page-back" href="${pageContext.request.contextPath}/dashboard">Back to dashboard</a>
            <h1>Hue Financial Coach</h1>
            <p>Discuss DTI, credit score, rejected loans, approval planning, EMIs, and everyday financial literacy.</p>

            <div class="hue-topic-list">
                <button type="button" data-question="How can I reduce my DTI?">Reduce DTI</button>
                <button type="button" data-question="How can I get my loan approved?">Loan approval plan</button>
                <button type="button" data-question="Why was my loan rejected?">Rejection reasons</button>
                <button type="button" data-question="How many loans are rejected?">Rejected loans</button>
                <button type="button" data-question="Teach me financial literacy">Financial literacy</button>
            </div>
        </aside>

        <section id="hue-chat-window" class="hue-chat-window hue-page-chat" aria-label="Hue assistant">
            <header class="hue-chat-header">
                <div>
                    <h3 id="hue-assistant-name">Hue</h3>
                    <p id="hue-assistant-subtitle">Your Financial Coach</p>
                </div>
            </header>

            <div id="hue-chat-messages" class="hue-chat-messages">
                <div class="hue-message hue-bot">
                    <div class="hue-bubble">Loading assistant...</div>
                </div>
            </div>

            <div id="hue-prompts" class="hue-prompts"></div>

            <form id="hue-chat-form" class="hue-chat-form">
                <input id="hue-chat-input" type="text" maxlength="2000" autocomplete="off"
                       placeholder="Ask Hue about DTI, credit score, loan rejection, EMI, or approval...">
                <button type="submit">Send</button>
            </form>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/js/hue-chatbot.js"></script>
    <script>
        document.querySelectorAll('.hue-topic-list button').forEach(function (button) {
            button.addEventListener('click', function () {
                const input = document.getElementById('hue-chat-input');
                const form = document.getElementById('hue-chat-form');
                input.value = button.dataset.question;
                form.requestSubmit();
            });
        });
    </script>
</body>
</html>
