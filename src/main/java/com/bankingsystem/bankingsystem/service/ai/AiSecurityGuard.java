package com.bankingsystem.bankingsystem.Service.ai;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * AiSecurityGuard — production-grade, multi-layer security for the AI chat pipeline.
 *
 * <p>Responsibilities:
 * <ol>
 *   <li>Input sanitization — strip control characters, normalise Unicode, truncate</li>
 *   <li>Prompt-injection detection — block attempts to override system instructions</li>
 *   <li>Privilege-escalation detection — block customers from requesting admin-only data</li>
 *   <li>Output sanitization — verify LLM responses don't leak restricted information</li>
 * </ol>
 *
 * <p>All pattern lists are intentionally verbose to cover known attack vectors while
 * minimising false positives for legitimate banking questions.
 */
@Component
public class AiSecurityGuard {

    // ─────────────────────────────────────────────────────────────────────────
    // Constants
    // ─────────────────────────────────────────────────────────────────────────

    private static final int MAX_MESSAGE_LENGTH = 2000;

    // ─────────────────────────────────────────────────────────────────────────
    // Layer 1 — Prompt Injection Patterns
    // Covers: direct instruction override, role impersonation, jailbreak attempts,
    // multi-turn injection, encoding tricks, and structured payload injection.
    // ─────────────────────────────────────────────────────────────────────────

    private static final List<Pattern> PROMPT_INJECTION_PATTERNS = List.of(
            // Classic instruction override
            Pattern.compile("(?i)ignore\\s+(all\\s+)?(previous|prior|above|earlier)\\s+(instructions?|prompts?|rules?|context)"),
            Pattern.compile("(?i)(disregard|forget|override|bypass|nullify)\\s+(your\\s+)?(system\\s+)?(instructions?|prompts?|rules?|training)"),
            Pattern.compile("(?i)(clear|reset|delete|erase)\\s+(your\\s+)?(memory|context|instructions?|session)"),

            // Role impersonation
            Pattern.compile("(?i)you\\s+are\\s+now\\s+(an?\\s+)?(admin|administrator|banker|analyst|root|superuser|god|unrestricted)"),
            Pattern.compile("(?i)(pretend|act|behave|respond)\\s+(to\\s+be|as|like)\\s+(an?\\s+)?(admin|administrator|system|developer|ceo|manager)"),
            Pattern.compile("(?i)(from\\s+now\\s+on|starting\\s+now)[,.]?\\s+(you\\s+are|act\\s+as|pretend)"),
            Pattern.compile("(?i)new\\s+(persona|identity|role|character)\\s*:"),

            // System prompt extraction
            Pattern.compile("(?i)(show|reveal|print|dump|expose|display|output|repeat|recite)\\s+(your\\s+)?(system\\s+)?(prompt|instructions?|rules?|training|configuration)"),
            Pattern.compile("(?i)(what\\s+are|tell\\s+me)\\s+(your\\s+)?(exact\\s+)?(instructions?|system\\s+prompt|rules?)"),

            // Jailbreak keywords
            Pattern.compile("(?i)(jailbreak|dan\\s+mode|developer\\s+mode|god\\s+mode|unrestricted\\s+mode|maintenance\\s+mode)"),
            Pattern.compile("(?i)(bypass|disable|turn\\s+off)\\s+(safety|filter|restriction|guardrail|security)"),
            Pattern.compile("(?i)do\\s+anything\\s+now"),

            // Structured payload injection
            Pattern.compile("(?i)new\\s+instructions?\\s*:"),
            Pattern.compile("(?i)role\\s*:\\s*system"),
            Pattern.compile("(?i)\\[\\s*system\\s*\\]"),
            Pattern.compile("(?i)<\\s*system\\s*>"),
            Pattern.compile("(?i)###\\s*(system|instruction|override)"),
            Pattern.compile("(?i)```\\s*(system|instruction|prompt)"),

            // Multi-turn / context manipulation
            Pattern.compile("(?i)(from\\s+the\\s+previous|based\\s+on\\s+earlier)\\s+(message|context|conversation).*ignore"),
            Pattern.compile("(?i)the\\s+above\\s+text\\s+was\\s+(a\\s+)?(joke|test|example)"),

            // Policy override
            Pattern.compile("(?i)do\\s+not\\s+follow\\s+(the\\s+)?(bank|debthues|security)\\s+(rules?|policies?)"),
            Pattern.compile("(?i)(your\\s+)?(real\\s+)?(true\\s+)?instructions?\\s+are\\s+to"),

            // Base64 / encoded injection hint
            Pattern.compile("(?i)(decode|base64|hex\\s+encoded|url\\s+encoded)\\s+(and\\s+)?(follow|execute|run|apply)")
    );

    // ─────────────────────────────────────────────────────────────────────────
    // Layer 2 — Customer Privilege Escalation Patterns
    // Customers must not be able to retrieve admin-level data through the AI.
    // ─────────────────────────────────────────────────────────────────────────

    private static final List<Pattern> CUSTOMER_PRIVILEGE_ESCALATION_PATTERNS = List.of(
            // Bulk customer data
            Pattern.compile("(?i)\\b(all|every|each|list\\s+of)\\s+customers?\\b"),
            Pattern.compile("(?i)\\bcustomer\\s+(list|database|records?|directory|registry|dump)\\b"),
            Pattern.compile("(?i)\\bhow\\s+many\\s+customers?\\b"),

            // Platform-wide analytics
            Pattern.compile("(?i)\\b(portfolio|platform|system|bank)[-\\s]wide\\s*(analytics?|insights?|metrics?|stats?|data)?\\b"),
            Pattern.compile("(?i)\\b(total|overall)\\s+(loan|emi|credit)\\s+(portfolio|exposure|book)\\b"),
            Pattern.compile("(?i)\\boperational\\s+(risk|metrics?|kpis?|dashboard)\\b"),

            // Other customers' data
            Pattern.compile("(?i)\\bother\\s+customers?'?\\s*(credit\\s+scores?|accounts?|loans?|emis?|data|details?)\\b"),
            Pattern.compile("(?i)\\bsomeone\\s+else'?s?\\s*(loan|emi|credit|account)\\b"),

            // Admin-only loan operations
            Pattern.compile("(?i)\\b(pending|all)\\s+loan\\s+applications?\\b"),
            Pattern.compile("(?i)\\bapprove\\s+(or\\s+reject\\s+)?loans?\\b"),
            Pattern.compile("(?i)\\breject\\s+loans?\\b"),
            Pattern.compile("(?i)\\badmin\\s+(dashboard|panel|insights?|analytics?|actions?)\\b"),

            // Customer management
            Pattern.compile("(?i)\\b(delete|remove|ban|suspend)\\s+customers?\\b"),
            Pattern.compile("(?i)\\bbank[-\\s]wide\\b"),

            // SQL / raw data requests
            Pattern.compile("(?i)\\b(sql|select\\s+from|insert\\s+into|drop\\s+table|database\\s+query)\\b"),
            Pattern.compile("(?i)\\b(raw\\s+data|export\\s+data|download\\s+data)\\b")
    );

    // ─────────────────────────────────────────────────────────────────────────
    // Layer 3 — Output Filter (admin-only keywords in customer responses)
    // These strings must never appear in a response sent to a CUSTOMER or ANONYMOUS user.
    // ─────────────────────────────────────────────────────────────────────────

    private static final Set<String> ADMIN_ONLY_OUTPUT_KEYWORDS = Set.of(
            "portfolio metrics",
            "all customers",
            "pending applications",
            "approval rate",
            "admin dashboard",
            "admin panel",
            "operational risk",
            "bank-wide",
            "platform overdue",
            "credit risk portfolio",
            "customer database"
    );

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sanitizes raw user input before it enters the AI pipeline.
     * <ol>
     *   <li>Normalizes Unicode to NFC form to prevent homoglyph attacks</li>
     *   <li>Strips control characters</li>
     *   <li>Collapses whitespace</li>
     *   <li>Truncates to {@link #MAX_MESSAGE_LENGTH}</li>
     * </ol>
     */
    public String sanitizeInput(String rawMessage) {
        if (rawMessage == null) {
            return "";
        }

        // Normalize Unicode (prevents look-alike character attacks)
        String normalized = Normalizer.normalize(rawMessage, Normalizer.Form.NFC);

        String sanitized = normalized
                .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", " ") // control chars
                .replaceAll("\\s+", " ")
                .trim();

        if (sanitized.length() > MAX_MESSAGE_LENGTH) {
            sanitized = sanitized.substring(0, MAX_MESSAGE_LENGTH);
        }
        return sanitized;
    }

    /**
     * Evaluates a sanitized message for security violations.
     *
     * @param role    The resolved AI role for the session
     * @param message The sanitized user message
     * @return {@link AiSecurityResult#permitted()} if safe, otherwise a denial result
     */
    public AiSecurityResult evaluate(AiRole role, String message) {
        if (message == null || message.isBlank()) {
            return AiSecurityResult.denied("Please enter a message.", "VALIDATION");
        }

        // Check prompt injection (applies to all roles)
        for (Pattern pattern : PROMPT_INJECTION_PATTERNS) {
            if (pattern.matcher(message).find()) {
                return AiSecurityResult.denied(
                        "I can only help with DebtHues banking questions. " +
                        "Please ask about your account, credit score, loans, or EMIs.",
                        "PROMPT_INJECTION");
            }
        }

        // Check privilege escalation (customers and anonymous users only)
        if (role == AiRole.CUSTOMER || role == AiRole.ANONYMOUS) {
            for (Pattern pattern : CUSTOMER_PRIVILEGE_ESCALATION_PATTERNS) {
                if (pattern.matcher(message).find()) {
                    return AiSecurityResult.denied(
                            "That information is restricted to bank administrators. " +
                            "I can help with your own credit score, loans, EMIs, and financial guidance.",
                            "PRIVILEGE_ESCALATION");
                }
            }
        }

        return AiSecurityResult.permitted();
    }

    /**
     * Sanitizes an outbound LLM response before it is sent to a non-admin user.
     * If the response contains admin-only keywords, a safe fallback is returned instead.
     *
     * @param role     The AI role of the requesting user
     * @param response The raw LLM response
     * @return The safe response string
     */
    public String sanitizeOutput(AiRole role, String response) {
        if (response == null || response.isBlank()) {
            return "I'm sorry, I couldn't generate a response. Please try again.";
        }

        if (role == AiRole.CUSTOMER || role == AiRole.ANONYMOUS) {
            String lower = response.toLowerCase();
            for (String keyword : ADMIN_ONLY_OUTPUT_KEYWORDS) {
                if (lower.contains(keyword)) {
                    return "I can only help with your personal banking information — " +
                           "your credit score, loans, EMIs, and financial guidance. " +
                           "Is there something specific about your account I can help with?";
                }
            }
        }

        return response.trim();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Result record
    // ─────────────────────────────────────────────────────────────────────────

    public record AiSecurityResult(boolean allowed, String blockedMessage, String reason) {

        public static AiSecurityResult permitted() {
            return new AiSecurityResult(true, null, null);
        }

        public static AiSecurityResult denied(String message, String reason) {
            return new AiSecurityResult(false, message, reason);
        }
    }
}
