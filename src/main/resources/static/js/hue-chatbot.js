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

        document.querySelectorAll('#hue-prompts button').forEach(function (button) {
            button.addEventListener('click', function () {
                sendMessage(button.dataset.question || button.textContent);
            });
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initHueChat);
    } else {
        initHueChat();
    }
})();
