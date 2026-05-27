# Chatbot Integration - Testing Guide

## Testing Overview

This document provides comprehensive testing procedures for the AI-powered chatbot integration.

---

## Table of Contents

1. [Pre-Testing Setup](#pre-testing-setup)
2. [Unit Testing](#unit-testing)
3. [Integration Testing](#integration-testing)
4. [API Testing](#api-testing)
5. [Frontend Testing](#frontend-testing)
6. [Database Testing](#database-testing)
7. [Security Testing](#security-testing)
8. [Performance Testing](#performance-testing)
9. [UAT (User Acceptance Testing)](#uat)
10. [Test Checklist](#test-checklist)

---

## Pre-Testing Setup

### Prerequisites
- ✅ Java 21 installed
- ✅ Maven configured
- ✅ PostgreSQL running
- ✅ OpenAI/Gemini API key obtained
- ✅ `.env` file created with API key
- ✅ Application builds successfully

### Preparation Steps

```bash
# 1. Build project
mvn clean package

# 2. Verify database
psql -U postgres -d banking_system -c "\dt chat_messages"

# 3. Check logs directory exists
mkdir -p logs

# 4. Verify .env is set
cat .env | grep AI_

# 5. Start application
mvn spring-boot:run
```

---

## Unit Testing

### Test ChatService

Create file: `src/test/java/com/bankingsystem/bankingsystem/Service/ChatServiceTest.java`

```java
@SpringBootTest
class ChatServiceTest {
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private ChatMessageRepository repository;
    
    @BeforeEach
    void setup() {
        repository.deleteAll(); // Clean database before each test
    }
    
    @Test
    @DisplayName("Should process valid message successfully")
    void testProcessMessage_ValidMessage() {
        // Arrange
        Long userId = 1L;
        String message = "What is EMI?";
        String ipAddress = "192.168.1.1";
        
        // Act
        ChatResponse response = chatService.processMessage(userId, message, ipAddress);
        
        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getBotResponse());
        assertFalse(response.getBotResponse().isEmpty());
        assertEquals("EMI_QUERY", response.getMessageType());
    }
    
    @Test
    @DisplayName("Should reject empty message")
    void testProcessMessage_EmptyMessage() {
        // Act & Assert
        ChatResponse response = chatService.processMessage(1L, "", "192.168.1.1");
        assertFalse(response.isSuccess());
        assertNotNull(response.getErrorMessage());
    }
    
    @Test
    @DisplayName("Should reject message exceeding max length")
    void testProcessMessage_MessageTooLong() {
        // Arrange
        String longMessage = "a".repeat(2001);
        
        // Act
        ChatResponse response = chatService.processMessage(1L, longMessage, "192.168.1.1");
        
        // Assert
        assertFalse(response.isSuccess());
    }
    
    @Test
    @DisplayName("Should save message to database")
    void testProcessMessage_SaveToDatabase() {
        // Arrange
        Long userId = 1L;
        String message = "Explain interest rates";
        
        // Act
        ChatResponse response = chatService.processMessage(userId, message, "192.168.1.1");
        
        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.getMessageId());
        
        // Verify in database
        ChatMessage saved = repository.findById(response.getMessageId()).orElse(null);
        assertNotNull(saved);
        assertEquals(userId, saved.getUserId());
        assertEquals(message, saved.getUserMessage());
    }
    
    @Test
    @DisplayName("Should categorize message correctly")
    void testProcessMessage_MessageCategorization() {
        // Test EMI categorization
        ChatResponse emiResponse = chatService.processMessage(1L, "What is EMI?", "192.168.1.1");
        assertEquals("EMI_QUERY", emiResponse.getMessageType());
        
        // Test interest categorization
        ChatResponse interestResponse = chatService.processMessage(1L, "What is interest rate?", "192.168.1.1");
        assertEquals("INTEREST_QUERY", interestResponse.getMessageType());
        
        // Test loan categorization
        ChatResponse loanResponse = chatService.processMessage(1L, "How to apply for loan?", "192.168.1.1");
        assertEquals("LOAN_QUERY", loanResponse.getMessageType());
    }
    
    @Test
    @DisplayName("Should retrieve chat history")
    void testGetChatHistory() {
        // Arrange
        Long userId = 1L;
        chatService.processMessage(userId, "First message", "192.168.1.1");
        chatService.processMessage(userId, "Second message", "192.168.1.1");
        
        // Act
        List<ChatMessage> history = chatService.getChatHistory(userId, 10);
        
        // Assert
        assertEquals(2, history.size());
        assertEquals("First message", history.get(0).getUserMessage());
        assertEquals("Second message", history.get(1).getUserMessage());
    }
    
    @Test
    @DisplayName("Should clear chat history")
    void testClearChatHistory() {
        // Arrange
        Long userId = 1L;
        chatService.processMessage(userId, "Message 1", "192.168.1.1");
        chatService.processMessage(userId, "Message 2", "192.168.1.1");
        
        // Act
        boolean cleared = chatService.clearChatHistory(userId);
        List<ChatMessage> history = chatService.getChatHistory(userId, 10);
        
        // Assert
        assertTrue(cleared);
        assertEquals(0, history.size());
    }
}
```

### Test DTOs

Create file: `src/test/java/com/bankingsystem/bankingsystem/dto/ChatRequestTest.java`

```java
class ChatRequestTest {
    
    @Test
    @DisplayName("Should create ChatRequest with valid message")
    void testChatRequest_Valid() {
        // Arrange
        String message = "What is EMI?";
        
        // Act
        ChatRequest request = new ChatRequest(message);
        
        // Assert
        assertEquals(message, request.getMessage());
    }
    
    @Test
    @DisplayName("Should create ChatRequest with conversation ID")
    void testChatRequest_WithConversationId() {
        String message = "Test message";
        String convId = "conv-123";
        
        ChatRequest request = new ChatRequest(message, convId);
        
        assertEquals(message, request.getMessage());
        assertEquals(convId, request.getConversationId());
    }
}
```

### Run Unit Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ChatServiceTest

# Run specific test method
mvn test -Dtest=ChatServiceTest#testProcessMessage_ValidMessage

# Run with coverage report
mvn test jacoco:report
```

---

## Integration Testing

### Test ChatController

Create file: `src/test/java/com/bankingsystem/bankingsystem/controller/ChatControllerTest.java`

```java
@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ChatMessageRepository repository;
    
    @BeforeEach
    void setup() {
        repository.deleteAll();
    }
    
    @Test
    @DisplayName("Should send message and get response")
    @WithMockUser(username = "testuser")
    void testSendMessage_Success() throws Exception {
        // Arrange
        String jsonRequest = """
            {
                "message": "What is EMI?"
            }
            """;
        
        // Act & Assert
        mockMvc.perform(post("/api/chat/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.userMessage").value("What is EMI?"))
            .andExpect(jsonPath("$.botResponse").isNotEmpty());
    }
    
    @Test
    @DisplayName("Should return 401 without authentication")
    void testSendMessage_Unauthorized() throws Exception {
        String jsonRequest = """
            {
                "message": "What is EMI?"
            }
            """;
        
        mockMvc.perform(post("/api/chat/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("Should reject empty message")
    @WithMockUser(username = "testuser")
    void testSendMessage_EmptyMessage() throws Exception {
        String jsonRequest = """
            {
                "message": ""
            }
            """;
        
        mockMvc.perform(post("/api/chat/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should get chat history")
    @WithMockUser(username = "testuser")
    void testGetChatHistory() throws Exception {
        mockMvc.perform(get("/api/chat/history?limit=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.count").isNumber());
    }
    
    @Test
    @DisplayName("Should clear chat history")
    @WithMockUser(username = "testuser")
    void testClearChatHistory() throws Exception {
        mockMvc.perform(delete("/api/chat/history"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    @DisplayName("Should return health status")
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/chat/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }
}
```

### Run Integration Tests

```bash
# Run all integration tests
mvn test -Dgroups=integration

# Run with Spring context
mvn test -Dtest=ChatControllerTest
```

---

## API Testing

### Manual API Testing with curl

```bash
# 1. Health Check (No authentication required)
curl -X GET http://localhost:8082/api/chat/health
# Expected: {"status":"UP","message":"Chatbot service is running"}

# 2. Send Message (Requires authentication)
# First, get a JWT token or use session cookie from login

curl -X POST http://localhost:8082/api/chat/send \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=your_session_id" \
  -d '{
    "message": "What is EMI?",
    "conversationId": "conv-001"
  }'

# Expected response:
# {
#   "messageId": 123,
#   "userMessage": "What is EMI?",
#   "botResponse": "EMI stands for...",
#   "messageType": "EMI_QUERY",
#   "timestamp": "2024-01-15T10:30:00",
#   "success": true
# }

# 3. Get Chat History
curl -X GET "http://localhost:8082/api/chat/history?limit=50" \
  -H "Cookie: JSESSIONID=your_session_id"

# 4. Clear Chat History
curl -X DELETE http://localhost:8082/api/chat/history \
  -H "Cookie: JSESSIONID=your_session_id"

# 5. Test with invalid message (should fail)
curl -X POST http://localhost:8082/api/chat/send \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=your_session_id" \
  -d '{"message": ""}'

# Expected: Error response
```

### Postman Testing

1. **Create Postman Collection**
   - New Collection: "Banking System Chatbot"
   
2. **Add Requests**
   ```
   ├─ Health Check (GET /api/chat/health)
   ├─ Send Message (POST /api/chat/send)
   ├─ Get History (GET /api/chat/history)
   └─ Clear History (DELETE /api/chat/history)
   ```

3. **Configure Authentication**
   - Pre-request Script to extract session cookie
   - Or use OAuth2 if configured

4. **Test Scenarios**
   - Valid message
   - Empty message
   - Very long message (>2000 chars)
   - Special characters
   - No authentication
   - Rate limiting

---

## Frontend Testing

### Manual Browser Testing

#### Test 1: Chatbot Widget Appears
```
Steps:
1. Open http://localhost:8082
2. Look for blue circular button (bottom-right)
3. Button should show online status
4. No JavaScript errors in console

Expected: Button visible with "Banking Assistant" label
```

#### Test 2: Open/Close Chatbot
```
Steps:
1. Click the floating button
2. Chat window should expand smoothly
3. Click X button (top-right)
4. Window should collapse
5. Click button again to reopen

Expected: Smooth animations, no lag
```

#### Test 3: Send Message
```
Steps:
1. Click chatbot button
2. Type "What is EMI?" in input
3. Press Enter or click Send
4. Wait for response
5. Check message appears in chat

Expected:
- User message on right (blue)
- Loading indicator while waiting
- Bot response on left (white)
- Both messages visible and scrolled
```

#### Test 4: Message Categories
```
Test each category:
- "What is EMI?" → Should be EMI_QUERY
- "What is interest rate?" → Should be INTEREST_QUERY
- "Am I eligible?" → Should be ELIGIBILITY_QUERY
- "How to repay?" → Should be REPAYMENT_QUERY
- "How to apply?" → Should be LOAN_QUERY
- "Tell me a joke" → Should be GENERAL_BANKING

Expected: Messages categorized correctly
```

#### Test 5: Quick Questions
```
Steps:
1. Open chatbot
2. Click one of the quick question buttons
3. Message should auto-fill in input
4. Auto-send the message
5. Observe response

Expected: Buttons work, messages sent automatically
```

#### Test 6: Chat History
```
Steps:
1. Send multiple messages (5+)
2. Refresh page (F5)
3. Chat history should load
4. All previous messages visible

Expected: Persistence working, messages in order
```

#### Test 7: Clear Chat
```
Steps:
1. Send some messages
2. Click menu (3 dots) if present, or look for clear button
3. Confirm clear action
4. Chat window should show welcome message

Expected: All messages removed, quick questions visible again
```

#### Test 8: Responsive Design
```
Test on different screen sizes:
- Desktop (1920x1080)
- Tablet (768x1024)
- Mobile (375x667)

Expected:
- Desktop: Full window visible
- Tablet: Window adjusts width
- Mobile: Window full-screen (or near-full)
```

#### Test 9: Input Validation
```
Steps:
1. Try to send empty message
2. Try message with 2001+ characters
3. Type special characters: @#$%^&*
4. Type HTML: <script>alert('xss')</script>

Expected:
- Empty: Error toast
- Too long: Truncated or error
- Special chars: Accepted
- HTML: Escaped (shown as text)
```

#### Test 10: Keyboard Navigation
```
Steps:
1. Press Tab to focus input
2. Type message
3. Press Shift+Enter for new line
4. Press Enter to send
5. Use Tab to navigate buttons

Expected: Keyboard shortcuts work, no navigation issues
```

### Browser Console Testing

```javascript
// Check for JavaScript errors
// Open: F12 → Console tab
// Expected: No red errors

// Test chatbot API directly
window.chatbot.sendMessage(); // Check loading state
window.chatbot.openChatbot();
window.chatbot.closeChatbot();
window.chatbot.clearChat();

// Check localStorage
localStorage.getItem('chatbot-open');
localStorage.getItem('chatbot-conversation-id');

// Monitor network requests
// Open: F12 → Network tab
// Expected: POST /api/chat/send returns 200
//           Response time < 10 seconds
//           Valid JSON response
```

### Responsive Design Testing

```bash
# Chrome DevTools
F12 → Toggle device toolbar (Ctrl+Shift+M)

Test devices:
- iPhone 12 Pro (390×844)
- iPad Pro (1024×1366)
- Desktop (1920×1080)
- Samsung Galaxy S21 (360×800)

Expected:
- No horizontal scrolling
- Buttons easily tappable
- Text readable
- Animations smooth
```

---

## Database Testing

### Verify Table Structure

```sql
-- Connect to database
psql -U postgres -d banking_system

-- Show table structure
\d chat_messages

-- Expected output:
-- Column        |            Type             |
-- id            | bigint                      |
-- user_id       | bigint                      |
-- user_message  | text                        |
-- bot_response  | text                        |
-- message_type  | character varying(50)       |
-- created_at    | timestamp without time zone |
-- ip_address    | character varying(45)       |

-- Verify indexes
\di
-- Expected: idx_user_id, idx_created_at indexes exist
```

### Test Data Operations

```sql
-- Check table is empty initially
SELECT COUNT(*) FROM chat_messages;
-- Expected: 0

-- Send a message from application
-- Expected in browser: Response received

-- Verify data saved
SELECT * FROM chat_messages;
-- Expected: One row with all fields populated

-- Check latest messages
SELECT user_message, message_type, created_at 
FROM chat_messages 
ORDER BY created_at DESC 
LIMIT 5;

-- Check message by type
SELECT COUNT(*) FROM chat_messages WHERE message_type = 'EMI_QUERY';

-- Check IP tracking
SELECT DISTINCT ip_address FROM chat_messages;

-- Cleanup (optional)
DELETE FROM chat_messages WHERE user_id = 1;
```

---

## Security Testing

### Test 1: API Key Not Exposed

```bash
# Check no API key in response
curl http://localhost:8082/api/chat/send | grep -i "api"
# Expected: No "sk-" or API key strings

# Check no API key in logs
grep -r "sk-" logs/
# Expected: No matches (or only in .env which should be ignored)

# Check no API key in frontend code
grep -r "sk-" src/main/resources/static/
# Expected: No matches
```

### Test 2: Authentication Required

```bash
# Try to access without authentication
curl http://localhost:8082/api/chat/history
# Expected: 401 Unauthorized

# Try with invalid token
curl http://localhost:8082/api/chat/history \
  -H "Authorization: Bearer invalid_token"
# Expected: 401 Unauthorized
```

### Test 3: Input Sanitization

```bash
# Test XSS prevention
curl -X POST http://localhost:8082/api/chat/send \
  -H "Content-Type: application/json" \
  -d '{
    "message": "<script>alert(\"XSS\")</script>"
  }'
# Expected: Message saved as-is, but rendered safely

# In browser console:
// Check message is escaped
document.querySelector('.message-content').textContent
// Expected: HTML shown as text, not executed
```

### Test 4: SQL Injection Prevention

```bash
# Try SQL injection in message
curl -X POST http://localhost:8082/api/chat/send \
  -H "Content-Type: application/json" \
  -d '{
    "message": "'; DROP TABLE chat_messages; --"
  }'
# Expected: Message saved safely, table still exists
```

### Test 5: HTTPS Configuration

```bash
# Check if security headers are set (production)
curl -I https://your-production-server/
# Expected:
# Strict-Transport-Security
# X-Content-Type-Options: nosniff
# X-Frame-Options: DENY
# Content-Security-Policy
```

---

## Performance Testing

### Response Time Testing

```bash
# Test API response time
time curl -X POST http://localhost:8082/api/chat/send \
  -H "Content-Type: application/json" \
  -d '{"message":"What is EMI?"}'

# Expected: < 5 seconds (depends on API provider)
```

### Load Testing

```bash
# Using Apache Bench (if installed)
ab -n 100 -c 10 http://localhost:8082/api/chat/health

# Using wrk (if installed)
wrk -t4 -c100 -d30s http://localhost:8082/api/chat/health

# Expected:
# - Health endpoint: < 50ms
# - Chat endpoint: 1-10 seconds (due to AI API)
```

### Database Query Performance

```sql
-- Check query performance
EXPLAIN ANALYZE
SELECT * FROM chat_messages 
WHERE user_id = 1 
ORDER BY created_at DESC 
LIMIT 10;

-- Expected: Uses index, < 10ms

-- Check slow queries
SELECT query, mean_time FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;
```

---

## UAT (User Acceptance Testing)

### UAT Test Cases

#### Test Case 1: New User Flow
```
Precondition: User logged into system
Steps:
1. User navigates to dashboard
2. Sees chatbot button (bottom-right)
3. Clicks button
4. Chat window opens
5. User sees welcome message
6. User sees quick question buttons
7. User clicks one quick question
8. Message auto-fills and sends
9. User receives response
10. User clicks clear to reset

Expected Result: All steps work, responses are accurate
```

#### Test Case 2: Complex Query
```
Precondition: User logged in, chatbot open
Steps:
1. User types: "I want to apply for a 500000 loan with 5 year tenure"
2. User presses Enter
3. Wait for response
4. User asks follow-up: "What would be my EMI?"
5. Wait for response
6. Check both messages and responses in chat

Expected Result: Contextual responses, accurate information
```

#### Test Case 3: Multiple Queries in Sequence
```
Precondition: Chatbot ready
Steps:
1. Send: "What is EMI?"
2. Wait for response
3. Send: "How is interest calculated?"
4. Wait for response
5. Send: "Tell me about loan eligibility"
6. Wait for response
7. Close chatbot
8. Reopen chatbot
9. Verify history is loaded

Expected Result: All messages sent, history preserved
```

#### Test Case 4: Edge Cases
```
Test scenarios:
1. Very short message: "EMI?"
2. Very long message: Multiple paragraphs
3. Only numbers: "500000"
4. Special characters: "@#$%"
5. Multiple languages: "EMI क्या है?"
6. Typos: "wat is emi"
7. CAPS: "WHAT IS EMI?"
8. Mixed case: "WhAt Is EmI?"

Expected Result: All handled gracefully, responses provided
```

#### Test Case 5: Mobile User Experience
```
Device: iPhone or Android
Steps:
1. Open app on mobile
2. Scroll to see chatbot button
3. Click to open
4. Type message (test mobile keyboard)
5. Send message
6. Check layout adjusts to screen
7. Scroll in chat window
8. Close/minimize/reopen

Expected Result: Smooth mobile experience, readable text
```

### UAT Sign-off Template

```
UAT Report - AI Chatbot Feature
================================

Test Date: ___________
Tester Name: ___________
Test Environment: [Development/Staging/Production]

Test Results:
✓ Functional Testing (PASS/FAIL)
✓ UI/UX Testing (PASS/FAIL)
✓ Performance Testing (PASS/FAIL)
✓ Security Testing (PASS/FAIL)
✓ Compatibility Testing (PASS/FAIL)

Issues Found:
1. ___________
2. ___________

Issues Resolved:
1. ___________
2. ___________

Outstanding Issues:
1. ___________
2. ___________

Recommended Actions:
1. ___________
2. ___________

Sign-off:
By: ___________
Date: ___________
Status: [Ready for Production] / [Needs Fix]
```

---

## Test Checklist

### Pre-Deployment Checklist

- [ ] All unit tests pass (`mvn test`)
- [ ] All integration tests pass
- [ ] No compilation warnings
- [ ] No runtime errors in logs
- [ ] API responds correctly to all endpoints
- [ ] Database tables created successfully
- [ ] Chat messages persist correctly
- [ ] Authentication is required
- [ ] API key is not exposed
- [ ] Frontend loads without errors
- [ ] Chatbot button appears
- [ ] Messages can be sent and received
- [ ] Responses are received in < 30 seconds
- [ ] Responses are relevant and professional
- [ ] Chat history loads on page reload
- [ ] Clear history works correctly
- [ ] Mobile responsive works
- [ ] No console errors in browser
- [ ] No XSS vulnerabilities found
- [ ] No SQL injection vulnerabilities found
- [ ] Documentation is complete and clear
- [ ] Setup guide is accurate and tested
- [ ] Error messages are user-friendly
- [ ] Loading indicators appear during API calls
- [ ] Toast notifications work
- [ ] Quick questions buttons work
- [ ] Keyboard shortcuts work (Enter to send)
- [ ] Character counter works
- [ ] Message categorization is correct
- [ ] IP address is tracked
- [ ] Timestamps are correct
- [ ] Database indexes are created

### Production Deployment Checklist

- [ ] HTTPS is enabled
- [ ] Environment variables are set on production
- [ ] API key is not committed to repo
- [ ] Database backups are configured
- [ ] Error logging is enabled
- [ ] Performance monitoring is set up
- [ ] Security headers are configured
- [ ] CORS is properly configured
- [ ] Rate limiting is enabled (optional)
- [ ] Caching is configured (optional)
- [ ] Load balancer health checks configured
- [ ] SSL certificate is valid
- [ ] Database replication is working
- [ ] Monitoring and alerting is active

---

## Continuous Testing

### Automated Testing (CI/CD Pipeline)

```yaml
# Example GitHub Actions workflow
name: Test Chatbot

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: banking_system
          POSTGRES_PASSWORD: password
    steps:
      - uses: actions/checkout@v2
      - name: Set up Java
        uses: actions/setup-java@v2
        with:
          java-version: '21'
      - name: Run tests
        run: mvn clean test
      - name: Build package
        run: mvn clean package
      - name: Upload coverage
        uses: codecov/codecov-action@v2
```

### Manual Testing Cycle

**Weekly Testing:**
1. Run all unit tests
2. Run integration tests on staging
3. Test key user flows
4. Check logs for errors
5. Monitor API response times

**Monthly Testing:**
1. Full regression testing
2. Security audit
3. Performance testing
4. Database optimization review
5. User feedback review

---

## Debugging Tips

### Enable Debug Logging

```properties
# In application.properties
logging.level.com.bankingsystem=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Check Application Logs

```bash
# Real-time logs
tail -f target/logs/application.log | grep -i "chat"

# Search for errors
grep ERROR target/logs/application.log

# Search for specific message
grep "message content" target/logs/application.log
```

### Browser Developer Tools

```javascript
// Monitor network requests
// F12 → Network tab → Send message → Check request/response

// Monitor JavaScript console
// F12 → Console → Check for errors

// Check Local Storage
// F12 → Application → Local Storage → See chatbot data

// Monitor API calls
// F12 → Network → Filter XHR → Check /api/chat/* calls
```

---

## Test Reports

### Generate Test Coverage Report

```bash
# Add maven-jacoco-plugin to pom.xml
mvn clean test jacoco:report

# Report location: target/site/jacoco/index.html
# Open in browser: open target/site/jacoco/index.html
```

### Generate Javadoc

```bash
mvn javadoc:javadoc
# Report: target/site/apidocs/index.html
```

---

**Happy Testing! 🧪**

