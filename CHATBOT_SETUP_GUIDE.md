# AI Chatbot Integration - Setup Guide

## Overview
This guide will help you set up and configure the AI-powered chatbot feature for your Loan Management System.

---

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [File Structure](#file-structure)
3. [Configuration Setup](#configuration-setup)
4. [API Key Setup](#api-key-setup)
5. [Database Setup](#database-setup)
6. [Integration Steps](#integration-steps)
7. [Testing](#testing)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software
- Java 21 or higher
- Spring Boot 3.3.4
- PostgreSQL 12+
- Maven 3.8+

### Required Dependencies
The following dependencies are already configured in `pom.xml`:
- Spring Boot Web
- Spring Data JPA (Hibernate)
- Spring WebFlux (WebClient)
- Spring Security
- PostgreSQL Driver

---

## File Structure

### Backend Files Created
```
src/main/java/com/bankingsystem/bankingsystem/
├── controller/
│   └── ChatController.java           # REST API endpoints
├── entity/
│   └── ChatMessage.java              # JPA Entity for chat history
├── repository/
│   └── ChatMessageRepository.java    # Database operations
├── dto/
│   ├── ChatRequest.java              # Request DTO
│   ├── ChatResponse.java             # Response DTO
│   └── AiApiDtos.java                # OpenAI & Gemini DTOs
├── Service/
│   └── ChatService.java              # Business logic for chatbot
└── config/
    ├── ChatbotAiConfig.java          # AI configuration
    └── RestClientConfig.java         # REST client configuration
```

### Frontend Files Created
```
src/main/resources/
├── static/
│   ├── js/
│   │   └── chatbot.js                # Frontend logic
│   └── css/
│       └── chatbot-styles.css        # UI styling
└── webapp/WEB-INF/views/
    └── chatbot.jsp                   # Chatbot JSP page (optional standalone)

src/main/resources/
└── application.properties             # Configuration (updated)
```

### Configuration Files
```
.env.example                          # Environment variables template
CHATBOT_SETUP_GUIDE.md               # This file
```

---

## Configuration Setup

### Step 1: Update application.properties

The chatbot configuration is already added to `application.properties`. 

Key properties:
```properties
# AI Provider: "openai" or "gemini"
chatbot.ai.provider=${AI_PROVIDER:openai}

# API Key (required)
chatbot.ai.api-key=${AI_API_KEY:}

# Model Name
chatbot.ai.model=${AI_MODEL:gpt-4o-mini}

# Maximum tokens in response
chatbot.ai.max-tokens=${AI_MAX_TOKENS:500}

# Temperature (0.0 = deterministic, 1.0 = creative)
chatbot.ai.temperature=${AI_TEMPERATURE:0.7}

# System prompt
chatbot.ai.system-prompt=${AI_SYSTEM_PROMPT:...}
```

### Step 2: Create .env File

Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```

Edit `.env` with your actual values:
```bash
# Database
DB_URL=jdbc:postgresql://localhost:5432/banking_system
DB_USER=postgres
DB_PASSWORD=your_password

# Google OAuth (if using)
GOOGLE_CLIENT_ID=your_id
GOOGLE_CLIENT_SECRET=your_secret

# AI Configuration
AI_PROVIDER=openai
AI_API_KEY=sk-your_openai_key_here
AI_MODEL=gpt-4o-mini
```

---

## API Key Setup

### Option 1: Using OpenAI API (Recommended)

1. **Create OpenAI Account**
   - Visit https://platform.openai.com
   - Sign up or log in

2. **Generate API Key**
   - Go to https://platform.openai.com/api-keys
   - Click "Create new secret key"
   - Copy the key (starts with `sk-`)
   - Save in `.env`: `AI_API_KEY=sk-...`

3. **Set Model**
   - For best results: `gpt-4o-mini` (fast and cost-effective)
   - Alternative: `gpt-4`, `gpt-3.5-turbo`

4. **Enable Billing**
   - Add payment method in OpenAI account
   - Set usage limits if desired

### Option 2: Using Google Gemini API

1. **Create Google AI Account**
   - Visit https://makersuite.google.com
   - Click "Get API Key"

2. **Generate API Key**
   - Click "Create API Key in new project"
   - Copy the key
   - Save in `.env`

3. **Configure**
   - Set `AI_PROVIDER=gemini`
   - Set `AI_API_KEY=your_gemini_key`
   - Set `AI_MODEL=gemini-pro`

---

## Database Setup

### Step 1: Enable Database Migrations

The JPA entity `ChatMessage` will automatically create the table when the application starts.

Configuration in `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
```

### Step 2: Create Migration Script (Optional)

If you want to create the table manually, execute:

```sql
CREATE TABLE IF NOT EXISTS chat_messages (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_message TEXT NOT NULL,
    bot_response TEXT NOT NULL,
    message_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    CONSTRAINT fk_chat_messages_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_chat_user_id ON chat_messages(user_id);
CREATE INDEX idx_chat_created_at ON chat_messages(created_at DESC);
CREATE INDEX idx_chat_message_type ON chat_messages(message_type);
```

### Step 3: Verify Database Connection

Before running the application, ensure PostgreSQL is running:

```bash
# Connect to PostgreSQL
psql -U postgres -d banking_system

# Verify table creation after app starts
\dt chat_messages
\d chat_messages
```

---

## Integration Steps

### Step 1: Add Chatbot to Existing JSP Pages

The chatbot widget loads globally. To include it in your existing JSP pages, add at the end of the body tag:

```jsp
<!-- Add before closing </body> tag -->
<script src="${pageContext.request.contextPath}/js/chatbot.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot-styles.css">
```

Or include the chatbot JSP:

```jsp
<jsp:include page="chatbot.jsp" />
```

### Step 2: Ensure Spring Security Allows Chat Endpoints

Add to your Security Configuration (if you have one):

```java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/chat/**").authenticated()  // Require authentication
    // ... other configurations
);
```

### Step 3: Build and Run

```bash
# Clean build
mvn clean package

# Run the application
mvn spring-boot:run
```

Or using your IDE:
1. Right-click project
2. Select "Run as" → "Maven Build"
3. Enter goal: `spring-boot:run`

### Step 4: Access the Chatbot

1. Open browser: http://localhost:8082
2. Look for the floating chatbot button (blue circle, bottom-right)
3. Click to open the chat window
4. Start asking questions!

---

## Testing

### Manual Testing

1. **Open Application**
   - http://localhost:8082

2. **Test Chatbot**
   - Click the floating button
   - Try quick questions or type custom messages
   - Test all message categories:
     - EMI queries
     - Interest rate questions
     - Loan eligibility
     - Repayment information

3. **Check Database**
   - View saved messages:
   ```sql
   SELECT * FROM chat_messages ORDER BY created_at DESC LIMIT 10;
   ```

### API Testing with curl

```bash
# Test API health
curl http://localhost:8082/api/chat/health

# Send a message (requires authentication)
curl -X POST http://localhost:8082/api/chat/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"message":"What is EMI?"}'

# Get chat history
curl http://localhost:8082/api/chat/history \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Unit Testing

Create a test class `ChatServiceTest.java`:

```java
@SpringBootTest
class ChatServiceTest {
    
    @Autowired
    private ChatService chatService;
    
    @Test
    void testProcessMessage() {
        ChatResponse response = chatService.processMessage(
            1L,
            "What is EMI?",
            "192.168.1.1"
        );
        
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getBotResponse());
    }
}
```

---

## Troubleshooting

### Common Issues

#### 1. API Key Not Configured Error

**Problem**: "Chatbot service is not configured"

**Solution**:
- Verify `.env` file exists
- Check `AI_API_KEY` is set correctly
- Ensure application.properties loads env variables
- Restart the application

#### 2. Database Connection Error

**Problem**: "Could not connect to database"

**Solution**:
```bash
# Check PostgreSQL is running
psql -U postgres

# Verify connection string in .env
DB_URL=jdbc:postgresql://localhost:5432/banking_system

# Check database exists
psql -U postgres -l | grep banking_system
```

#### 3. 401 Unauthorized Error

**Problem**: API returns 401 when sending messages

**Solution**:
- User must be authenticated
- Check Spring Security configuration
- Ensure authentication token is valid
- Check `getCurrentUserId()` method in ChatController

#### 4. Chatbot Widget Not Showing

**Problem**: Floating button doesn't appear

**Solution**:
- Check browser console for JavaScript errors
- Verify CSS file is loading: `chatbot-styles.css`
- Check JS file is loading: `chatbot.js`
- Clear browser cache and reload

#### 5. AI API Rate Limit Error

**Problem**: "Rate limit exceeded"

**Solution**:
- Check OpenAI/Gemini usage dashboard
- Upgrade API plan if needed
- Increase rate limit in account settings
- Implement request queuing (future enhancement)

#### 6. Chat History Not Saving

**Problem**: Messages don't persist in database

**Solution**:
- Verify PostgreSQL connection
- Check table exists: `SELECT * FROM chat_messages;`
- Check user ID is retrieved correctly
- Enable DEBUG logging: `logging.level.com.bankingsystem=DEBUG`

---

## Advanced Configuration

### Increase API Timeouts

If AI responses are slow, increase timeouts in `RestClientConfig.java`:

```java
factory.setReadTimeout(60000);  // 60 seconds
```

### Customize System Prompt

Edit `application.properties`:

```properties
chatbot.ai.system-prompt=Your custom system prompt here...
```

### Limit Message Length

Change in `ChatRequest.java`:

```java
@Size(min = 1, max = 5000, message = "...")
private String message;
```

### Add Request Logging

Add to `application.properties`:

```properties
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.com.bankingsystem.bankingsystem=DEBUG
```

---

## Performance Optimization

### Database Indexing
Indexes are already created. To manually verify:

```sql
SELECT * FROM pg_indexes WHERE tablename = 'chat_messages';
```

### Caching (Future Enhancement)
```java
@Cacheable("chatHistory")
public List<ChatMessage> getChatHistory(Long userId, int limit) {
    // ...
}
```

### Connection Pooling
Already configured in Spring Boot. To customize, add to `application.properties`:

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
```

---

## Security Best Practices

✅ **Implemented**:
- No hardcoded API keys
- API keys loaded from environment variables
- HTTPS-only in production
- CSRF protection
- Input validation
- SQL injection prevention (JPA)
- XSS prevention (escapeHtml in JS)

⚠️ **Additional Recommendations**:
1. Use HTTPS in production
2. Set `server.servlet.session.cookie.secure=true`
3. Implement rate limiting
4. Add request logging for audit
5. Encrypt sensitive data at rest
6. Use firewall rules

---

## Deployment

### Docker (Optional)

Create `Dockerfile`:

```dockerfile
FROM openjdk:21-slim
COPY target/banking-system.jar app.jar
ENV AI_API_KEY=${AI_API_KEY}
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:

```bash
docker build -t banking-system .
docker run -e AI_API_KEY=sk-... -p 8082:8082 banking-system
```

### Production Checklist

- [ ] Use HTTPS certificate
- [ ] Set environment variables securely
- [ ] Enable database backups
- [ ] Set up monitoring/alerts
- [ ] Configure logging aggregation
- [ ] Test API rate limiting
- [ ] Load test the application
- [ ] Implement CDN for static assets

---

## Support & Documentation

### OpenAI Documentation
- API Reference: https://platform.openai.com/docs/api-reference
- Chat Completions: https://platform.openai.com/docs/guides/gpt

### Google Gemini Documentation
- API Reference: https://ai.google.dev/api
- Quickstart: https://ai.google.dev/tutorials/rest_quickstart

### Spring Boot Documentation
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- Spring Data JPA: https://spring.io/projects/spring-data-jpa

---

## Next Steps

### Recommended Enhancements

1. **Multi-language Support**
   - Add language selection UI
   - Modify system prompt based on language

2. **Chat Memory**
   - Maintain conversation context
   - Send previous messages to API

3. **Suggested Responses**
   - Show quick reply options
   - Improve UX for mobile

4. **Analytics**
   - Track popular questions
   - Monitor chatbot performance
   - Generate usage reports

5. **Admin Dashboard**
   - View all chat conversations
   - Monitor API usage
   - Manage chatbot settings

6. **Dark Mode**
   - Add theme toggle
   - Store user preference

7. **Notification System**
   - Alert on important messages
   - Follow-up for unresolved queries

---

## FAQ

**Q: Can I use both OpenAI and Gemini?**
A: Currently, the application supports one provider at a time. Configure in `.env`.

**Q: What's the cost?**
A: OpenAI: ~$0.001-0.01 per message. Gemini: Free tier available. Check respective pricing pages.

**Q: Is the chatbot available to all users?**
A: Yes, any authenticated user can access it. Modify ChatController if you want role-based access.

**Q: How long are conversations stored?**
A: Forever, unless you implement a cleanup job. Add:

```java
@Scheduled(cron = "0 0 0 * * *") // Daily
public void deleteOldMessages() {
    LocalDateTime cutoff = LocalDateTime.now().minusDays(90);
    // Delete messages older than 90 days
}
```

**Q: Can I customize the chatbot's appearance?**
A: Yes! Modify `chatbot-styles.css` with your brand colors and styling.

---

## Version History

- **v1.0.0** (2024) - Initial release
  - OpenAI & Gemini API support
  - Chat history storage
  - JSP + JS frontend
  - Spring Boot backend

---

## License & Attribution

This chatbot implementation uses:
- Spring Boot (Apache 2.0)
- PostgreSQL (PostgreSQL License)
- OpenAI API
- Google Gemini API

---

**For questions or issues, please check the Troubleshooting section or contact support.**

Happy chatting! 🚀
