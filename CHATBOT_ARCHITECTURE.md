# Chatbot Architecture & Design

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                     BANKING SYSTEM FRONTEND                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                  JSP Pages (Dashboard, etc.)                 │   │
│  │  ┌────────────────────────────────────────────────────────┐ │   │
│  │  │         Floating Chatbot Button & Window               │ │   │
│  │  │  ┌──────────────────────────────────────────────────┐  │ │   │
│  │  │  │  Chatbot Header (Blue gradient)                  │  │ │   │
│  │  │  ├──────────────────────────────────────────────────┤  │ │   │
│  │  │  │  Messages Container                              │  │ │   │
│  │  │  │  - User bubbles (right, blue)                    │  │ │   │
│  │  │  │  - Bot bubbles (left, white)                     │  │ │   │
│  │  │  │  - Typing indicator                              │  │ │   │
│  │  │  ├──────────────────────────────────────────────────┤  │ │   │
│  │  │  │  Quick Questions (suggested buttons)             │  │ │   │
│  │  │  ├──────────────────────────────────────────────────┤  │ │   │
│  │  │  │  Input Area (textbox + send button)              │  │ │   │
│  │  │  └──────────────────────────────────────────────────┘  │ │   │
│  │  └────────────────────────────────────────────────────────┘ │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ HTTP/AJAX
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│               SPRING BOOT BACKEND (Port 8082)                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │  REST Controller Layer                                       │   │
│  │  ┌─────────────────────────────────────────────────────────┐│   │
│  │  │ ChatController                                          ││   │
│  │  │  ✓ POST /api/chat/send                                 ││   │
│  │  │  ✓ GET /api/chat/history                               ││   │
│  │  │  ✓ DELETE /api/chat/history                            ││   │
│  │  │  ✓ GET /api/chat/health                                ││   │
│  │  └─────────────────────────────────────────────────────────┘│   │
│  └───────────────────────────┬──────────────────────────────────┘   │
│                              │                                        │
│  ┌───────────────────────────▼──────────────────────────────────┐   │
│  │  Business Logic Layer                                        │   │
│  │  ┌─────────────────────────────────────────────────────────┐│   │
│  │  │ ChatService                                             ││   │
│  │  │  ✓ Process user messages                               ││   │
│  │  │  ✓ Validate inputs                                     ││   │
│  │  │  ✓ Categorize messages                                 ││   │
│  │  │  ✓ Call AI APIs (OpenAI/Gemini)                        ││   │
│  │  │  ✓ Manage chat history                                 ││   │
│  │  └─────────────────────────────────────────────────────────┘│   │
│  └───────────────────────────┬──────────────────────────────────┘   │
│                              │                                        │
│  ┌───────────────────────────▼──────────────────────────────────┐   │
│  │  Data Access Layer                                          │   │
│  │  ┌─────────────────────────────────────────────────────────┐│   │
│  │  │ ChatMessageRepository (JPA)                             ││   │
│  │  │  ✓ Save chat messages                                  ││   │
│  │  │  ✓ Retrieve history                                    ││   │
│  │  │  ✓ Delete messages                                     ││   │
│  │  │  ✓ Query by user/date/type                             ││   │
│  │  └─────────────────────────────────────────────────────────┘│   │
│  └───────────────────┬──────────────────────────────────────────┘   │
│                      │                                                │
└──────────────────────┼────────────────────────────────────────────────┘
                       │
        ┌──────────────┼──────────────┐
        │              │              │
        ▼              ▼              ▼
   ┌─────────┐  ┌──────────────┐  ┌──────────────┐
   │PostgreSQL│  │ OpenAI API   │  │ Gemini API   │
   │Database  │  │ (REST)       │  │ (REST)       │
   │          │  │              │  │              │
   │chat_     │  │gpt-4o-mini   │  │gemini-pro    │
   │messages  │  │gpt-4         │  │              │
   │          │  │gpt-3.5-turbo │  │              │
   └─────────┘  └──────────────┘  └──────────────┘
```

---

## Data Flow Sequence

```
User (Browser)
    │
    │ 1. Click Chatbot Button / Type Message
    ▼
┌─────────────────────────────┐
│   chatbot.js                │
│   - Validate input          │
│   - Show loading spinner    │
│   - Add user message to UI  │
└──────────────┬──────────────┘
               │
               │ 2. HTTP POST /api/chat/send
               ▼
        ┌──────────────────────┐
        │  ChatController      │
        │  - Extract user ID   │
        │  - Get IP address    │
        │  - Call ChatService  │
        └──────────┬───────────┘
                   │
                   │ 3. Process message
                   ▼
        ┌──────────────────────┐
        │  ChatService         │
        │  - Validate message  │
        │  - Categorize type   │
        │  - Call AI API       │
        └──────────┬───────────┘
                   │
                   │ 4a. Call AI API (OpenAI/Gemini)
                   ▼
        ┌──────────────────────┐
        │ OpenAI/Gemini API    │
        │ - Process request    │
        │ - Generate response  │
        │ - Return JSON        │
        └──────────┬───────────┘
                   │
                   │ 4b. Response
                   ▼
        ┌──────────────────────┐
        │  ChatService         │
        │  - Receive response  │
        │  - Create entity     │
        │  - Save to DB        │
        └──────────┬───────────┘
                   │
                   │ 5. Save ChatMessage
                   ▼
        ┌──────────────────────┐
        │ ChatMessageRepository│
        │ - Save to database   │
        │ - Return saved msg   │
        └──────────┬───────────┘
                   │
                   │ 6. Return ChatResponse
                   ▼
        ┌──────────────────────┐
        │  ChatController      │
        │  - Build response    │
        │  - Return JSON 200   │
        └──────────┬───────────┘
                   │
                   │ 7. HTTP Response JSON
                   ▼
        ┌──────────────────────┐
        │  chatbot.js          │
        │  - Parse response    │
        │  - Hide spinner      │
        │  - Add bot message   │
        │  - Scroll to bottom  │
        └──────────┬───────────┘
                   │
                   │ 8. Update DOM
                   ▼
              Browser Screen
                   │
                   ▼
         User sees bot response
```

---

## Data Model

```
┌──────────────────────────────────────────────────────┐
│              ChatMessage Entity                      │
├──────────────────────────────────────────────────────┤
│                                                      │
│  id: Long (Primary Key)                             │
│  ├─ Auto-incremented                                │
│  ├─ Unique identifier                               │
│                                                      │
│  userId: Long (Foreign Key) ★                        │
│  ├─ References: users.id                            │
│  ├─ User who sent the message                       │
│  ├─ Index: idx_chat_user_id                         │
│                                                      │
│  userMessage: String (TEXT)                         │
│  ├─ Content of user's message                       │
│  ├─ Max 2000 characters                             │
│  ├─ Stored as-is (no transformation)                │
│                                                      │
│  botResponse: String (TEXT)                         │
│  ├─ AI-generated response                           │
│  ├─ Content from OpenAI/Gemini API                  │
│  ├─ Variable length                                 │
│                                                      │
│  messageType: String (VARCHAR 50)                   │
│  ├─ Categorized message type                        │
│  ├─ Values: EMI_QUERY, INTEREST_QUERY, etc.         │
│  ├─ Used for analytics                              │
│                                                      │
│  createdAt: LocalDateTime (TIMESTAMP)               │
│  ├─ When message was created                        │
│  ├─ Default: NOW()                                  │
│  ├─ Immutable (updatable=false)                     │
│  ├─ Index: idx_chat_created_at (DESC)               │
│                                                      │
│  ipAddress: String (VARCHAR 45) [Optional]          │
│  ├─ Client IP address (IPv4/IPv6)                   │
│  ├─ Used for security auditing                      │
│  ├─ Extracted from HTTP request                     │
│                                                      │
│  Indexes:                                           │
│  ├─ idx_user_id (userId)                            │
│  ├─ idx_created_at (createdAt DESC)                 │
│  └─ idx_message_type (messageType)                  │
│                                                      │
└──────────────────────────────────────────────────────┘
        ★ Links to User entity (FK constraint)
```

---

## Request/Response Flow

### Request DTO: ChatRequest
```
{
  "message": "What is EMI?",
  "conversationId": "conv-1234567890-abc123"
}
```

### Response DTO: ChatResponse
```json
{
  "messageId": 42,
  "userMessage": "What is EMI?",
  "botResponse": "EMI stands for Equated Monthly Installment. It is...",
  "messageType": "EMI_QUERY",
  "timestamp": "2024-01-15T10:30:45.123456",
  "success": true,
  "errorMessage": null
}
```

### Error Response
```json
{
  "messageId": null,
  "userMessage": null,
  "botResponse": null,
  "messageType": null,
  "timestamp": "2024-01-15T10:30:45.123456",
  "success": false,
  "errorMessage": "Chatbot service is not configured"
}
```

---

## Configuration Hierarchy

```
┌────────────────────────────────────────────────────┐
│        Configuration Loading Order                 │
├────────────────────────────────────────────────────┤
│                                                    │
│  1. Environment Variables (.env file)              │
│     ├─ AI_API_KEY                                  │
│     ├─ AI_PROVIDER                                 │
│     ├─ AI_MODEL                                    │
│     └─ AI_TEMPERATURE                              │
│                                                    │
│  2. application.properties defaults                │
│     ├─ chatbot.ai.provider=${AI_PROVIDER:openai}   │
│     ├─ chatbot.ai.api-key=${AI_API_KEY:}           │
│     └─ ...other properties...                      │
│                                                    │
│  3. ChatbotAiConfig Java defaults                  │
│     ├─ provider = "openai"                         │
│     ├─ model = "gpt-4o-mini"                       │
│     ├─ maxTokens = 500                             │
│     └─ temperature = 0.7                           │
│                                                    │
└────────────────────────────────────────────────────┘
     Override at each level (env > props > code)
```

---

## API Integration Architecture

```
ChatService
│
├─ For OpenAI API:
│  ├─ Build: OpenAIRequest (system + user message)
│  ├─ Set: Authorization header with API key
│  ├─ Post: To https://api.openai.com/v1/chat/completions
│  └─ Parse: OpenAIResponse (extract message from choices[0])
│
└─ For Gemini API:
   ├─ Build: GeminiRequest (system instruction + content)
   ├─ Add: API key as query parameter
   ├─ Post: To generativelanguage.googleapis.com/v1beta/models/...
   └─ Parse: GeminiResponse (extract text from candidates[0].parts[0])
```

---

## Security Architecture

```
┌─────────────────────────────────────────────────────────┐
│              Security Layers                            │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Layer 1: Authentication (Spring Security)             │
│  ├─ Only authenticated users can access /api/chat/*   │
│  ├─ User ID extracted from SecurityContext            │
│  └─ Invalid users get 401 Unauthorized                │
│                                                         │
│  Layer 2: Input Validation                             │
│  ├─ Message length checked (max 2000 chars)           │
│  ├─ Non-null validation                                │
│  ├─ @Valid annotation on request DTOs                 │
│  └─ Backend validation in ChatService                  │
│                                                         │
│  Layer 3: API Key Protection                           │
│  ├─ Never stored in code (env variables only)         │
│  ├─ Validated before use                               │
│  ├─ Not returned in API responses                      │
│  └─ Only used in backend (never sent to frontend)     │
│                                                         │
│  Layer 4: Database Security                            │
│  ├─ JPA parameterized queries (no SQL injection)      │
│  ├─ Foreign key constraints (user_id)                  │
│  ├─ IP address logging for audit                       │
│  └─ Timestamps for activity tracking                   │
│                                                         │
│  Layer 5: Frontend Security                            │
│  ├─ HTML escape all user messages                      │
│  ├─ No sensitive data in localStorage                  │
│  ├─ HTTPS in production                                │
│  └─ CSRF token (Spring Security)                       │
│                                                         │
│  Layer 6: Transport Security                           │
│  ├─ HTTPS (in production)                              │
│  ├─ Secure cookies (HttpOnly, Secure flags)           │
│  ├─ CORS properly configured                           │
│  └─ X-Requested-With header check                      │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## Component Dependencies

```
ChatController
    ├─ depends on → ChatService
    ├─ depends on → ChatMessageRepository
    ├─ depends on → SecurityContextHolder
    └─ uses DTO ← ChatRequest, ChatResponse

ChatService
    ├─ depends on → ChatbotAiConfig
    ├─ depends on → ChatMessageRepository
    ├─ depends on → RestTemplate
    ├─ creates → ChatMessage entity
    ├─ uses DTO ← OpenAI/Gemini DTOs
    └─ returns DTO ← ChatResponse

ChatMessageRepository
    ├─ extends → JpaRepository<ChatMessage, Long>
    ├─ operates on → ChatMessage entity
    └─ uses → Database

ChatbotAiConfig
    ├─ loads from → application.properties
    ├─ loads from → environment variables
    ├─ provides → configuration values
    └─ used by → ChatService

RestTemplate (Spring Bean)
    ├─ created by → RestClientConfig
    ├─ used by → ChatService
    └─ calls → External AI APIs
```

---

## Performance Considerations

```
┌──────────────────────────────────────────────────┐
│      Performance Optimization Points             │
├──────────────────────────────────────────────────┤
│                                                  │
│  Database Queries:                               │
│  ├─ Index on user_id → Fast user lookups        │
│  ├─ Index on created_at DESC → Latest messages  │
│  └─ Pagination available → Limit result size    │
│                                                  │
│  API Calls:                                      │
│  ├─ 10s connection timeout → Fail fast          │
│  ├─ 30s read timeout → Allow processing time    │
│  └─ No retry logic → Avoid duplicate calls      │
│                                                  │
│  Frontend:                                       │
│  ├─ LocalStorage for state → No server calls    │
│  ├─ Auto-scroll efficient → DOM-level operation │
│  └─ Message batching → No per-char API calls    │
│                                                  │
│  Caching Ready (future):                         │
│  ├─ @Cacheable on getChatHistory               │
│  ├─ Cache invalidation on new message           │
│  └─ Redis-compatible setup                      │
│                                                  │
└──────────────────────────────────────────────────┘
```

---

## Scalability Architecture

```
Current Single-Instance:
┌─────────────────┐
│  Spring Boot    │──────────┐
│  Application    │          │
│  (Port 8082)    │          │
└─────────────────┘          │
                             ▼
                       ┌─────────────┐
                       │ PostgreSQL  │
                       │ Database    │
                       └─────────────┘

Future Multi-Instance (with load balancer):
┌──────────────────┐
│  Load Balancer   │
│  (nginx/HAProxy) │
└────────┬─────────┘
         │
     ┌───┴───┬───────┬────────┐
     │       │       │        │
     ▼       ▼       ▼        ▼
  ┌───────┐ ┌───────┐ ┌───────┐ ┌───────┐
  │ App 1 │ │ App 2 │ │ App 3 │ │ App 4 │
  └───────┘ └───────┘ └───────┘ └───────┘
        │       │       │        │
        └───────┬───────┬────────┘
                │
                ▼
        ┌─────────────────┐
        │  PostgreSQL     │
        │  (Replicated)   │
        └─────────────────┘

Future with Cache Layer:
     ┌─────────────┐
     │   Redis     │
     │   Cache     │
     └─────────────┘
              ▲
              │
          ┌───┴───┐
          │       │
          ▼       ▼
        App1    App2  (Each app instance)
```

---

## File Structure Overview

```
src/main/java/com/bankingsystem/bankingsystem/
│
├── controller/
│   └── ChatController.java (60 lines)
│       REST API endpoints
│
├── entity/
│   └── ChatMessage.java (100 lines)
│       Database model with annotations
│
├── repository/
│   └── ChatMessageRepository.java (30 lines)
│       Data access interface
│
├── dto/
│   ├── ChatRequest.java (20 lines)
│   ├── ChatResponse.java (80 lines)
│   └── AiApiDtos.java (250 lines)
│       Request/response models
│
├── Service/
│   └── ChatService.java (250 lines)
│       Business logic implementation
│
└── config/
    ├── ChatbotAiConfig.java (60 lines)
    │   Configuration properties
    │
    └── RestClientConfig.java (40 lines)
        HTTP client setup

src/main/resources/
├── application.properties (updated)
│   Configuration with env vars
│
└── static/
    ├── css/
    │   └── chatbot-styles.css (800+ lines)
    │       Professional UI styling
    │
    └── js/
        └── chatbot.js (400+ lines)
            Frontend logic

src/main/webapp/WEB-INF/views/
└── chatbot.jsp (120 lines)
    Chatbot HTML structure

Project Root/
├── .env.example
│   Environment variable template
│
├── CHATBOT_SETUP_GUIDE.md
│   Complete setup documentation
│
├── CHATBOT_IMPLEMENTATION_SUMMARY.md
│   Feature overview and checklist
│
└── CHATBOT_QUICK_REFERENCE.md
    Quick reference for developers
```

---

## Technology Stack

```
Frontend:
├─ Browser APIs
│  ├─ Fetch API (HTTP requests)
│  ├─ LocalStorage (persistence)
│  └─ DOM API (manipulation)
├─ CSS3
│  ├─ Flexbox layout
│  ├─ CSS Grid (optional)
│  └─ Media queries (responsive)
└─ JavaScript ES6+
   ├─ Classes
   ├─ Arrow functions
   └─ Async/await

Backend:
├─ Spring Boot 3.3.4
│  ├─ Spring Web (MVC, REST)
│  ├─ Spring Data JPA (ORM)
│  ├─ Spring Security (Authentication)
│  └─ Spring WebFlux (HTTP client)
├─ Java 21
├─ Hibernate (JPA provider)
└─ PostgreSQL 12+

External Services:
├─ OpenAI API
│  └─ Chat Completions endpoint
└─ Google Gemini API
   └─ Generate Content endpoint

DevOps (Optional):
├─ Docker
├─ Docker Compose
└─ CI/CD ready
```

---

## Error Handling Flow

```
User sends message
    │
    ▼
Input validation
    │
    ├─ Invalid? ──→ Show toast error (frontend)
    │
    ▼
API Authentication
    │
    ├─ Not authenticated? ──→ 401 Unauthorized
    │
    ▼
ChatService processing
    │
    ├─ API error? ──→ Log error, return user-friendly message
    │
    ├─ Database error? ──→ Log error, skip save, still return response
    │
    ├─ Timeout? ──→ Catch exception, return timeout message
    │
    ▼
Return response (success or error)
    │
    └─→ Display to user (message or error toast)
```

---

**For detailed implementation, see the individual code files and documentation!**

