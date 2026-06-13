(function () {
    function byId(id) {
        return document.getElementById(id);
    }

    function escapeHtml(value) {
        return String(value).replace(/[&<>"']/g, function (char) {
            return {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#039;'
            }[char];
        });
    }

    function addMessage(text, sender) {
        const messages = byId('hue-chat-messages');
        if (!messages) return;

        const row = document.createElement('div');
        row.className = 'hue-message hue-' + sender;
        row.innerHTML = '<div class="hue-bubble">' + escapeHtml(text) + '</div>';
        messages.appendChild(row);
        messages.scrollTop = messages.scrollHeight;
    }

    function setWelcomeMessage(text) {
        const messages = byId('hue-chat-messages');
        if (!messages) return;

        messages.innerHTML = '';
        addMessage(text, 'bot');
    }

    function renderPrompts(prompts) {
        const container = byId('hue-prompts');
        if (!container || !Array.isArray(prompts)) return;

        container.innerHTML = '';
        prompts.forEach(function (question) {
            const button = document.createElement('button');
            button.type = 'button';
            button.dataset.question = question;
            button.textContent = question.length > 42 ? question.substring(0, 39) + '...' : question;
            button.title = question;
            button.addEventListener('click', function () {
                sendMessage(question);
            });
            container.appendChild(button);
        });
    }

    async function loadConfig() {
        try {
            const response = await fetch('/api/chat/config', {
                credentials: 'include',
                headers: { 'X-Requested-With': 'XMLHttpRequest' }
            });

            if (!response.ok) return;

            const config = await response.json();
            const title = byId('hue-assistant-name');
            const subtitle = byId('hue-assistant-subtitle');

            if (title && config.assistantName) {
                title.textContent = config.assistantName;
            }
            if (subtitle && config.subtitle) {
                subtitle.textContent = config.subtitle;
            }
            if (config.welcomeMessage) {
                setWelcomeMessage(config.welcomeMessage);
            }
            renderPrompts(config.prompts);
        } catch (error) {
            console.warn('Hue config load failed:', error);
        }
    }

    async function sendMessage(text) {
        const message = text.trim();
        if (!message) return;

        addMessage(message, 'user');

        try {
            const response = await fetch('/api/chat/send', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                credentials: 'include',
                body: JSON.stringify({ message: message })
            });

            const data = await response.json();
            addMessage(data.botResponse || 'I could not answer that right now. Please try again.', 'bot');
        } catch (error) {
            console.warn('Hue request failed:', error);
            addMessage('I could not connect right now. Please try again in a moment.', 'bot');
        }
    }

    function initHueChat() {
        const toggle = byId('hue-chat-toggle');
        const windowEl = byId('hue-chat-window');
        const close = byId('hue-chat-close');
        const form = byId('hue-chat-form');
        const input = byId('hue-chat-input');

        if (!toggle || !windowEl || !form || !input) return;

        loadConfig();

        toggle.addEventListener('click', function () {
            windowEl.classList.toggle('hue-hidden');
            if (!windowEl.classList.contains('hue-hidden')) {
                input.focus();
            }
        });

        close?.addEventListener('click', function () {
            windowEl.classList.add('hue-hidden');
        });

        form.addEventListener('submit', function (event) {
            event.preventDefault();
            const message = input.value;
            input.value = '';
            sendMessage(message);
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initHueChat);
    } else {
        initHueChat();
    }
})();
