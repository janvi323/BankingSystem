package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.config.AiProperties;
import com.bankingsystem.bankingsystem.dto.ChatResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

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
        this.sessionResolver = sessionResolver;
        this.securityGuard = securityGuard;
        this.contextBuilder = contextBuilder;
        this.promptFactory = promptFactory;
        this.geminiAiClient = geminiAiClient;
        this.ruleBasedChatService = ruleBasedChatService;
        this.aiProperties = aiProperties;
    }

    public ChatResponse chat(HttpSession session, String rawMessage) {
        AiRequestContext requestContext = sessionResolver.resolve(session, rawMessage, securityGuard);
        AiRole role = requestContext.getRole();
        String message = requestContext.getSanitizedMessage();

        AiSecurityGuard.AiSecurityResult securityResult = securityGuard.evaluate(role, message);
        if (!securityResult.allowed()) {
            return ChatResponse.blocked(securityResult.blockedMessage(), securityResult.reason(), role.name());
        }

        String contextBlock = contextBuilder.buildContext(role, requestContext.getCustomer());
        String systemPrompt = promptFactory.buildSystemPrompt(role);
        String userPrompt = promptFactory.buildUserPrompt(contextBlock, message);

        if (aiProperties.isLlmConfigured()) {
            try {
                String llmResponse = geminiAiClient.generate(systemPrompt, userPrompt);
                if (llmResponse != null && !llmResponse.isBlank()) {
                    String sanitizedResponse = sanitizeResponse(role, llmResponse);
                    return ChatResponse.ok(sanitizedResponse, "AI", role.name(), true);
                }
            } catch (Exception ex) {
                log.warn("LLM response failed, falling back to rule-based chat: {}", ex.getMessage());
            }
        }

        RuleBasedChatService.RuleBasedResult fallback =
                ruleBasedChatService.respond(role, requestContext.getCustomer(), message);
        return ChatResponse.ok(fallback.response(), fallback.messageType(), role.name(), false);
    }

    public Map<String, Object> getWidgetConfig(HttpSession session) {
        AiRequestContext context = sessionResolver.resolve(session, "", securityGuard);
        AiRole role = context.getRole();

        return switch (role) {
            case ADMIN -> Map.of(
                    "assistantName", "Hue Analyst",
                    "subtitle", "Banking Intelligence Analyst",
                    "role", "ADMIN",
                    "authenticated", true,
                    "llmEnabled", aiProperties.isLlmConfigured(),
                    "welcomeMessage", "Hello! I am Hue Analyst. Ask about pending loans, portfolio metrics, or overdue EMIs.",
                    "prompts", List.of(
                            "How many pending loan applications are there?",
                            "Summarize portfolio credit trends",
                            "Which loans need urgent review?",
                            "What is the overdue EMI count?"
                    )
            );
            case CUSTOMER -> Map.of(
                    "assistantName", "Hue",
                    "subtitle", "Your Financial Coach",
                    "role", "CUSTOMER",
                    "authenticated", true,
                    "llmEnabled", aiProperties.isLlmConfigured(),
                    "welcomeMessage", "Hi! I am Hue, your Financial Coach. Ask about your credit score, EMIs, or loans.",
                    "prompts", List.of(
                            "What is my credit score?",
                            "What will be my EMI for 100000 for 12 months?",
                            "Where do I apply for a loan?",
                            "Show my next EMI due date"
                    )
            );
            case ANONYMOUS -> Map.of(
                    "assistantName", "Hue",
                    "subtitle", "Banking assistant",
                    "role", "ANONYMOUS",
                    "authenticated", false,
                    "llmEnabled", aiProperties.isLlmConfigured(),
                    "welcomeMessage", "Hi! Log in to access your personal banking insights.",
                    "prompts", List.of(
                            "What features does DebtHues offer?",
                            "How do I register?",
                            "How does credit score work?",
                            "How do I apply for a loan?"
                    )
            );
        };
    }

    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "message", aiProperties.isLlmConfigured() ? "Hue AI is ready (LLM enabled)" : "Hue is ready (rule-based mode)",
                "llmEnabled", String.valueOf(aiProperties.isLlmConfigured()),
                "provider", aiProperties.getProvider()
        );
    }

    private String sanitizeResponse(AiRole role, String response) {
        if (role == AiRole.CUSTOMER || role == AiRole.ANONYMOUS) {
            String lower = response.toLowerCase();
            if (lower.contains("admin-only")
                    || (lower.contains("portfolio metrics") && lower.contains("all customers"))) {
                return "I can only help with your personal banking information. "
                        + "Please ask about your credit score, loans, or EMIs.";
            }
        }
        return response.trim();
    }
}
