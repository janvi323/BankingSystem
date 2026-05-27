/**
 * Chatbot JavaScript - Frontend Logic
 * Handles UI interactions and API communication
 */

class ChatBot {
    constructor() {
        // DOM Elements
        this.toggleBtn = document.getElementById('chatbot-toggle');
        this.chatbotWindow = document.getElementById('chatbot-window');
        this.messagesContainer = document.getElementById('chatbot-messages');
        this.chatInput = document.getElementById('chat-input');
        this.sendBtn = document.getElementById('send-btn');
        this.closeBtn = document.getElementById('close-btn');
        this.minimizeBtn = document.getElementById('minimize-btn');
        this.clearBtn = document.getElementById('clear-btn');
        this.loadingIndicator = document.getElementById('loading-indicator');
        this.quickQuestions = document.getElementById('quick-questions');
        this.toast = document.getElementById('toast');
        this.unreadBadge = document.getElementById('unread-badge');
        this.charCount = document.getElementById('char-count');
        
        // State
        this.isOpen = false;
        this.isLoading = false;
        this.messages = [];
        this.apiUrl = '/api/chat';
        
        // Initialize
        this.init();
    }
    
    /**
     * Initialize event listeners
     */
    init() {
        // Toggle button
        this.toggleBtn.addEventListener('click', () => this.toggleChatbot());
        
        // Close and minimize buttons
        this.closeBtn.addEventListener('click', () => this.closeChatbot());
        this.minimizeBtn.addEventListener('click', () => this.minimizeChatbot());
        
        // Clear chat
        this.clearBtn.addEventListener('click', () => this.clearChat());
        
        // Input and send
        this.chatInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage();
            }
        });
        
        this.sendBtn.addEventListener('click', () => this.sendMessage());
        
        // Character count
        this.chatInput.addEventListener('input', (e) => {
            this.charCount.textContent = e.target.value.length;
        });
        
        // Quick question buttons
        const quickBtns = document.querySelectorAll('.quick-btn');
        quickBtns.forEach(btn => {
            btn.addEventListener('click', (e) => {
                const question = e.target.dataset.question;
                this.chatInput.value = question;
                this.charCount.textContent = question.length;
                this.sendMessage();
            });
        });
        
        // Load chat history
        this.loadChatHistory();
        
        // Check if localStorage is available
        if (this.isLocalStorageAvailable()) {
            const isOpen = localStorage.getItem('chatbot-open');
            if (isOpen === 'true') {
                this.openChatbot();
            }
        }
    }
    
    /**
     * Toggle chatbot window
     */
    toggleChatbot() {
        if (this.isOpen) {
            this.closeChatbot();
        } else {
            this.openChatbot();
        }
    }
    
    /**
     * Open chatbot window
     */
    openChatbot() {
        this.isOpen = true;
        this.chatbotWindow.classList.remove('hidden');
        this.chatInput.focus();
        this.unreadBadge.style.display = 'none';
        
        if (this.isLocalStorageAvailable()) {
            localStorage.setItem('chatbot-open', 'true');
        }
    }
    
    /**
     * Close chatbot window
     */
    closeChatbot() {
        this.isOpen = false;
        this.chatbotWindow.classList.add('hidden');
        
        if (this.isLocalStorageAvailable()) {
            localStorage.setItem('chatbot-open', 'false');
        }
    }
    
    /**
     * Minimize chatbot window
     */
    minimizeChatbot() {
        this.closeChatbot();
    }
    
    /**
     * Send message to chatbot
     */
    async sendMessage() {
        const message = this.chatInput.value.trim();
        
        // Validate input
        if (!message) {
            this.showToast('Please enter a message', 'warning');
            return;
        }
        
        if (message.length > 2000) {
            this.showToast('Message is too long (max 2000 characters)', 'error');
            return;
        }
        
        if (this.isLoading) {
            return;
        }
        
        // Clear input
        this.chatInput.value = '';
        this.charCount.textContent = '0';
        
        // Add user message to UI
        this.addMessage(message, 'user');
        
        // Show loading indicator
        this.showLoading(true);
        
        try {
            // Call API
            const response = await this.callChatApi(message);
            
            if (response.success) {
                // Add bot response to UI
                this.addMessage(response.botResponse, 'bot', response.messageType);
                
                // Hide quick questions after first message
                if (this.messages.length > 1 && this.quickQuestions) {
                    this.quickQuestions.style.display = 'none';
                }
            } else {
                this.addMessage(response.errorMessage || 'Sorry, I couldn\'t process your message. Please try again.', 'bot', 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            this.addMessage('Sorry, there was an error connecting to the chatbot service. Please try again later.', 'bot', 'error');
        } finally {
            this.showLoading(false);
            this.chatInput.focus();
        }
    }
    
    /**
     * Call chat API
     */
    async callChatApi(message) {
        const response = await fetch(this.apiUrl + '/send', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: JSON.stringify({
                message: message,
                conversationId: this.getOrCreateConversationId()
            })
        });
        
        if (!response.ok) {
            throw new Error(`API error: ${response.status} ${response.statusText}`);
        }
        
        return await response.json();
    }
    
    /**
     * Add message to chat
     */
    addMessage(text, sender, messageType = 'general') {
        const messageEl = document.createElement('div');
        messageEl.className = `message ${sender}`;
        
        let avatarHtml = '';
        if (sender === 'bot') {
            avatarHtml = `
                <div class="message-avatar">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm3.5-9c.83 0 1.5-.67 1.5-1.5S16.33 8 15.5 8 14 8.67 14 9.5s.67 1.5 1.5 1.5zm-7 0c.83 0 1.5-.67 1.5-1.5S9.33 8 8.5 8 7 8.67 7 9.5 7.67 11 8.5 11zm3.5 6.5c2.33 0 4.31-1.46 5.11-3.5H6.89c.8 2.04 2.78 3.5 5.11 3.5z"></path>
                    </svg>
                </div>
            `;
        } else {
            avatarHtml = `<div class="message-avatar">👤</div>`;
        }
        
        const timestamp = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        
        messageEl.innerHTML = `
            ${sender === 'bot' ? avatarHtml : ''}
            <div>
                <div class="message-content">${this.escapeHtml(text)}</div>
                <div class="message-time">${timestamp}</div>
            </div>
            ${sender === 'user' ? avatarHtml : ''}
        `;
        
        this.messagesContainer.appendChild(messageEl);
        this.messages.push({ text, sender, timestamp, messageType });
        
        // Auto scroll to latest message
        this.scrollToBottom();
    }
    
    /**
     * Load chat history from server
     */
    async loadChatHistory() {
        try {
            const response = await fetch(this.apiUrl + '/history?limit=10', {
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });
            
            if (response.ok) {
                const data = await response.json();
                if (data.success && data.messages && data.messages.length > 0) {
                    // Hide quick questions if there are messages
                    if (this.quickQuestions) {
                        this.quickQuestions.style.display = 'none';
                    }
                    
                    // Load messages in order
                    data.messages.reverse().forEach(msg => {
                        this.messages.push({
                            text: msg.userMessage,
                            sender: 'user',
                            timestamp: msg.createdAt,
                            messageType: 'history'
                        });
                        this.messages.push({
                            text: msg.botResponse,
                            sender: 'bot',
                            timestamp: msg.createdAt,
                            messageType: msg.messageType
                        });
                    });
                    
                    // Re-render messages
                    this.renderMessages();
                }
            }
        } catch (error) {
            console.error('Error loading chat history:', error);
        }
    }
    
    /**
     * Render all messages
     */
    renderMessages() {
        // Clear existing messages
        const welcome = document.querySelector('.welcome-message');
        if (welcome) {
            welcome.remove();
        }
        
        const existingMessages = document.querySelectorAll('.message');
        existingMessages.forEach(msg => msg.remove());
        
        // Render all messages
        this.messages.forEach(msg => {
            this.addMessage(msg.text, msg.sender, msg.messageType);
        });
    }
    
    /**
     * Clear chat history
     */
    async clearChat() {
        if (!confirm('Are you sure you want to clear the chat history?')) {
            return;
        }
        
        try {
            const response = await fetch(this.apiUrl + '/history', {
                method: 'DELETE',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });
            
            const data = await response.json();
            
            if (data.success) {
                this.messages = [];
                this.renderMessages();
                
                // Show quick questions again
                if (this.quickQuestions) {
                    this.quickQuestions.style.display = 'flex';
                }
                
                this.showToast('Chat history cleared', 'success');
            } else {
                this.showToast('Failed to clear chat history', 'error');
            }
        } catch (error) {
            console.error('Error clearing chat:', error);
            this.showToast('Error clearing chat history', 'error');
        }
    }
    
    /**
     * Show/hide loading indicator
     */
    showLoading(show) {
        this.isLoading = show;
        this.loadingIndicator.style.display = show ? 'flex' : 'none';
        this.sendBtn.disabled = show;
        this.chatInput.disabled = show;
    }
    
    /**
     * Scroll to bottom of messages
     */
    scrollToBottom() {
        setTimeout(() => {
            this.messagesContainer.scrollTop = this.messagesContainer.scrollHeight;
        }, 0);
    }
    
    /**
     * Show toast notification
     */
    showToast(message, type = 'info') {
        this.toast.textContent = message;
        this.toast.className = `toast ${type} show`;
        
        setTimeout(() => {
            this.toast.classList.remove('show');
        }, 3000);
    }
    
    /**
     * Escape HTML special characters
     */
    escapeHtml(text) {
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.replace(/[&<>"']/g, m => map[m]);
    }
    
    /**
     * Get or create conversation ID
     */
    getOrCreateConversationId() {
        const key = 'chatbot-conversation-id';
        let conversationId = null;
        
        if (this.isLocalStorageAvailable()) {
            conversationId = localStorage.getItem(key);
        }
        
        if (!conversationId) {
            conversationId = 'conv-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9);
            if (this.isLocalStorageAvailable()) {
                localStorage.setItem(key, conversationId);
            }
        }
        
        return conversationId;
    }
    
    /**
     * Check if localStorage is available
     */
    isLocalStorageAvailable() {
        try {
            const test = '__storage_test__';
            localStorage.setItem(test, test);
            localStorage.removeItem(test);
            return true;
        } catch (e) {
            return false;
        }
    }
}

// Initialize chatbot when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.chatbot = new ChatBot();
    console.log('Chatbot initialized successfully');
});

// Handle unload
window.addEventListener('beforeunload', () => {
    // Cleanup if needed
});
