package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.config.AiProperties;
import com.bankingsystem.bankingsystem.dto.ChatResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * AiChatService — the main orchestrator for the DebtHues AI chat pipeline.
 *
 * <p>Request flow (per message):
 * <ol>
 *   <li>{@link AiSessionResolver}: resolve session → customer + role + sanitized message</li>
 *   <li>{@link AiSecurityGuard}: evaluate for prompt injection / privilege escalation</li>
 *   <li>{@link AiContextBuilder}: build the authoritative data context block</li>
 *   <li>{@link AiPromptFactory}: build system prompt and user prompt</li>
 *   <li>{@link GeminiAiClient}: call Gemini LLM (if configured)</li>
 *   <li>{@link AiSecurityGuard#sanitizeOutput}: scrub response for data leakage</li>
 *   <li>{@link RuleBasedChatService}: deterministic fallback when LLM is unavailable</li>
 * </ol>
 *
 * <p>Conversation history is stored in the HTTP session as a bounded queue of
 * {@link ConversationTurn} objects. This gives the AI multi-turn context without
 * requiring a database, keeping the implementation stateless at the DB layer.
 */
@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);
    private static final String SESSION_HISTORY_KEY = "aiConversationHistory";

    private final AiSessionResolver sessionResolver;
    private final AiSecurityGuard securityGuard;
    private final AiContextBuilder contextBuilder;
    private final AiPromptFactory promptFactory;
    private final GeminiAiClient geminiAiClient;
    private final RuleBasedChatService ruleBasedChatService;
    private final AiProperties aiProperties;

    public AiChatService(AiSessionResolver sessionResolver,
                         AiSecurityGuard securityGuard,
                         AiContextBuilder contextBuilder,
                         AiPromptFactory promptFactory,
                         GeminiAiClient geminiAiClient,
                         RuleBasedChatService ruleBasedChatService,
                         AiProperties aiProperties) {
        this.sessionResolver      = sessionResolver;
        this.securityGuard        = securityGuard;
        this.contextBuilder       = contextBuilder;
        this.promptFactory        = promptFactory;
        this.geminiAiClient       = geminiAiClient;
        this.ruleBasedChatService = ruleBasedChatService;
        this.aiProperties         = aiProperties;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Main chat entry point
    // ─────────────────────────────────────────────────────────────────────────

    public ChatResponse chat(HttpSession session, String rawMessage) {
        // Step 1: Resolve session → role + customer + sanitized message
        AiRequestContext requestContext = sessionResolver.resolve(session, rawMessage, securityGuard);
        AiRole role    = requestContext.getRole();
        String message = requestContext.getSanitizedMessage();

        // Step 2: Security evaluation (injection + privilege escalation)
        AiSecurityGuard.AiSecurityResult securityResult = securityGuard.evaluate(role, message);
        if (!securityResult.allowed()) {
            log.info("AI message blocked [role={}, reason={}]", role, securityResult.reason());
            return ChatResponse.blocked(securityResult.blockedMessage(), securityResult.reason(), role.name());
        }

        // Step 3: Build authoritative context block
        String contextBlock = contextBuilder.buildContext(role, requestContext.getCustomer());

        // Step 4: Build prompts, optionally including conversation history
        String historyBlock   = buildHistoryBlock(session, role);
        String systemPrompt   = promptFactory.buildSystemPrompt(role);
        String userPrompt     = promptFactory.buildUserPrompt(contextBlock + historyBlock, message);

        // Step 5: Attempt LLM generation
        if (aiProperties.isLlmConfigured()) {
            try {
                String llmResponse = geminiAiClient.generate(systemPrompt, userPrompt, role);
                if (llmResponse != null && !llmResponse.isBlank()) {
                    // Step 6: Outbound response sanitization
                    String sanitized = aiProperties.isResponseOutputFilterEnabled()
                            ? securityGuard.sanitizeOutput(role, llmResponse)
                            : llmResponse.trim();

                    // Record the turn in session history
                    recordConversationTurn(session, message, sanitized, role);

                    List<String> followUps = buildSuggestedFollowUps(role, sanitized);
                    return ChatResponse.ok(sanitized, "AI", role.name(), true, followUps);
                }
            } catch (Exception ex) {
                log.warn("LLM generation failed, falling back to rule-based chat: {}", ex.getMessage());
            }
        }

        // Step 7: Rule-based deterministic fallback
        RuleBasedChatService.RuleBasedResult fallback =
                ruleBasedChatService.respond(role, requestContext.getCustomer(), message);

        recordConversationTurn(session, message, fallback.response(), role);

        List<String> followUps = buildSuggestedFollowUps(role, fallback.response());
        return ChatResponse.ok(fallback.response(), fallback.messageType(), role.name(), false, followUps);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Widget configuration
    // ─────────────────────────────────────────────────────────────────────────

    public Map<String, Object> getWidgetConfig(HttpSession session) {
        AiRequestContext context = sessionResolver.resolve(session, "", securityGuard);
        AiRole role = context.getRole();
        boolean llmEnabled = aiProperties.isLlmConfigured();
        String today = LocalDate.now().toString();

        return switch (role) {
            case ADMIN -> Map.of(
                    "assistantName",  "Hue Analyst",
                    "subtitle",       "Banking Intelligence Analyst",
                    "role",           "ADMIN",
                    "authenticated",  true,
                    "llmEnabled",     llmEnabled,
                    "lastUpdated",    today,
                    "welcomeMessage", "Hello! I am Hue Analyst — your Banking Intelligence Analyst. " +
                                     "Ask me about pending loans, portfolio metrics, overdue EMIs, or credit trends.",
                    "prompts", List.of(
                            "How many pending loan applications are there?",
                            "What is the platform-wide approval rate?",
                            "Show the credit score distribution",
                            "Which customers have the most overdue EMIs?",
                            "Give me a portfolio overview"
                    )
            );
            case CUSTOMER -> Map.of(
                    "assistantName",  "Hue",
                    "subtitle",       "Your Financial Coach",
                    "role",           "CUSTOMER",
                    "authenticated",  true,
                    "llmEnabled",     llmEnabled,
                    "lastUpdated",    today,
                    "welcomeMessage", "Hi! I am Hue, your Financial Coach. " +
                                     "Ask me about your credit score, EMI schedule, or loan status!",
                    "prompts", List.of(
                            "What is my credit score?",
                            "When is my next EMI due?",
                            "What will my EMI be for ₹1,00,000 for 12 months?",
                            "How can I improve my credit score?",
                            "Show my loan status"
                    )
            );
            case ANONYMOUS -> Map.of(
                    "assistantName",  "Hue",
                    "subtitle",       "Banking assistant",
                    "role",           "ANONYMOUS",
                    "authenticated",  false,
                    "llmEnabled",     llmEnabled,
                    "lastUpdated",    today,
                    "welcomeMessage", "Hi! I am Hue. Log in to access your personal banking insights. " +
                                     "Or ask me what DebtHues can do for you!",
                    "prompts", List.of(
                            "What features does DebtHues offer?",
                            "How do I register?",
                            "How does credit score work?",
                            "What loan types are available?"
                    )
            );
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Health check
    // ─────────────────────────────────────────────────────────────────────────

    public Map<String, String> health() {
        return Map.of(
                "status",      "UP",
                "message",     aiProperties.isLlmConfigured()
                                   ? "Hue AI is ready (LLM + rule-based)"
                                   : "Hue is ready (rule-based mode — set AI_API_KEY on Render for LLM)",
                "llmEnabled",  String.valueOf(aiProperties.isLlmConfigured()),
                "provider",    aiProperties.getProvider(),
                "outputFilter", String.valueOf(aiProperties.isResponseOutputFilterEnabled()),
                "date",        LocalDate.now().toString()
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Conversation history management
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Stores the latest conversation turn in the HTTP session.
     * The history queue is bounded to {@link AiProperties#getMaxConversationHistory()} turns.
     * Older turns are evicted from the front of the queue.
     */
    @SuppressWarnings("unchecked")
    private void recordConversationTurn(HttpSession session, String userMessage,
                                        String botResponse, AiRole role) {
        if (session == null) return;

        try {
            LinkedList<ConversationTurn> history =
                    (LinkedList<ConversationTurn>) session.getAttribute(SESSION_HISTORY_KEY);
            if (history == null) {
                history = new LinkedList<>();
            }

            history.addLast(new ConversationTurn(userMessage, botResponse));

            int maxHistory = aiProperties.getMaxConversationHistory();
            while (history.size() > maxHistory) {
                history.removeFirst();
            }

            session.setAttribute(SESSION_HISTORY_KEY, history);
        } catch (Exception ex) {
            log.debug("Could not record conversation turn in session: {}", ex.getMessage());
        }
    }

    /**
     * Builds a conversation history block to append to the user prompt,
     * giving the LLM multi-turn awareness.
     * Only includes history for authenticated roles (CUSTOMER and ADMIN).
     */
    @SuppressWarnings("unchecked")
    private String buildHistoryBlock(HttpSession session, AiRole role) {
        if (session == null || role == AiRole.ANONYMOUS) {
            return "";
        }

        try {
            LinkedList<ConversationTurn> history =
                    (LinkedList<ConversationTurn>) session.getAttribute(SESSION_HISTORY_KEY);
            if (history == null || history.isEmpty()) {
                return "";
            }

            StringBuilder sb = new StringBuilder("\n=== RECENT CONVERSATION HISTORY (for context) ===\n");
            history.forEach(turn -> {
                sb.append("User: ").append(turn.userMessage()).append("\n");
                sb.append("Hue: ").append(
                        turn.botResponse().length() > 300
                                ? turn.botResponse().substring(0, 300) + "..."
                                : turn.botResponse()
                ).append("\n\n");
            });
            sb.append("=== END HISTORY ===\n");
            return sb.toString();

        } catch (Exception ex) {
            log.debug("Could not read conversation history from session: {}", ex.getMessage());
            return "";
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Suggested follow-up generation
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Generates role-specific quick-reply suggestions based on the message type
     * detected in the bot response. These are shown as clickable chips in the widget.
     */
    private List<String> buildSuggestedFollowUps(AiRole role, String response) {
        if (role == AiRole.ADMIN) {
            return buildAdminFollowUps(response);
        }
        if (role == AiRole.CUSTOMER) {
            return buildCustomerFollowUps(response);
        }
        return List.of("What features does DebtHues offer?", "How do I register?");
    }

    private List<String> buildAdminFollowUps(String response) {
        String lower = response.toLowerCase();
        if (lower.contains("pending")) {
            return List.of("Show the approval rate", "Which customers have overdue EMIs?", "Portfolio overview");
        }
        if (lower.contains("credit score") || lower.contains("distribution")) {
            return List.of("How many pending loans?", "Show overdue EMI report", "Portfolio overview");
        }
        if (lower.contains("overdue")) {
            return List.of("How many pending loans?", "Show credit score distribution", "Approval rate stats");
        }
        return List.of("How many pending loans?", "Show portfolio overview", "Credit score distribution");
    }

    private List<String> buildCustomerFollowUps(String response) {
        String lower = response.toLowerCase();
        if (lower.contains("credit score")) {
            return List.of("How can I improve my credit score?", "When is my next EMI due?", "Show my loans");
        }
        if (lower.contains("emi") || lower.contains("due")) {
            return List.of("What is my credit score?", "Show my loan status", "EMI estimate for ₹1,00,000 for 12 months");
        }
        if (lower.contains("loan")) {
            return List.of("What is my credit score?", "When is my next EMI due?", "How can I improve my score?");
        }
        if (lower.contains("improve") || lower.contains("tip")) {
            return List.of("What is my credit score?", "When is my next EMI due?", "Show my loans");
        }
        return List.of("What is my credit score?", "When is my next EMI due?", "Show my loans");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner records
    // ─────────────────────────────────────────────────────────────────────────

    private record ConversationTurn(String userMessage, String botResponse) {}
}
