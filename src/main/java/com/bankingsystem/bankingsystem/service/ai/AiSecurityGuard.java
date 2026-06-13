package com.bankingsystem.bankingsystem.Service.ai;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class AiSecurityGuard {

    private static final int MAX_MESSAGE_LENGTH = 2000;

    private static final List<Pattern> PROMPT_INJECTION_PATTERNS = List.of(
            Pattern.compile("(?i)ignore\\s+(all\\s+)?(previous|prior|above|earlier)\\s+(instructions?|prompts?|rules?)"),
            Pattern.compile("(?i)(disregard|forget|override)\\s+(your\\s+)?(system\\s+)?(instructions?|prompts?|rules?)"),
            Pattern.compile("(?i)you\\s+are\\s+now\\s+(an?\\s+)?(admin|administrator|banker|analyst)"),
            Pattern.compile("(?i)(pretend|act|behave)\\s+(to\\s+be|as)\\s+(an?\\s+)?(admin|administrator|system)"),
            Pattern.compile("(?i)(show|reveal|print|dump|expose)\\s+(your\\s+)?(system\\s+)?(prompt|instructions?|rules?)"),
            Pattern.compile("(?i)(jailbreak|dan\\s+mode|developer\\s+mode|bypass\\s+safety)"),
            Pattern.compile("(?i)new\\s+instructions?\\s*:"),
            Pattern.compile("(?i)role\\s*:\\s*system"),
            Pattern.compile("(?i)\\[system\\]"),
            Pattern.compile("(?i)do\\s+not\\s+follow\\s+(the\\s+)?(bank|debt?hues|security)\\s+(rules?|policies?)")
    );

    private static final List<Pattern> CUSTOMER_PRIVILEGE_ESCALATION_PATTERNS = List.of(
            Pattern.compile("(?i)\\b(all|every|total)\\s+customers?\\b"),
            Pattern.compile("(?i)\\bcustomer\\s+(list|database|records?|directory)\\b"),
            Pattern.compile("(?i)\\b(portfolio|platform|system)[-\\s]wide\\s+(analytics?|insights?|metrics?|stats?)\\b"),
            Pattern.compile("(?i)\\b(pending|all)\\s+loan\\s+applications?\\b"),
            Pattern.compile("(?i)\\bapprove\\s+(or\\s+reject\\s+)?loans?\\b"),
            Pattern.compile("(?i)\\badmin\\s+(dashboard|panel|insights?|analytics?)\\b"),
            Pattern.compile("(?i)\\bother\\s+customers?'?\\s+(credit\\s+scores?|accounts?|loans?|em[iI]s?)\\b"),
            Pattern.compile("(?i)\\bdelete\\s+customers?\\b"),
            Pattern.compile("(?i)\\bbank[-\\s]wide\\b"),
            Pattern.compile("(?i)\\boperational\\s+(risk|metrics?|kpis?)\\b")
    );

    public String sanitizeInput(String rawMessage) {
        if (rawMessage == null) {
            return "";
        }

        String sanitized = rawMessage
                .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (sanitized.length() > MAX_MESSAGE_LENGTH) {
            sanitized = sanitized.substring(0, MAX_MESSAGE_LENGTH);
        }
        return sanitized;
    }

    public AiSecurityResult evaluate(AiRole role, String message) {
        if (message.isBlank()) {
            return AiSecurityResult.denied("Please enter a message.", "VALIDATION");
        }

        for (Pattern pattern : PROMPT_INJECTION_PATTERNS) {
            if (pattern.matcher(message).find()) {
                return AiSecurityResult.denied(
                        "I cannot process that request. Please ask a banking-related question in plain language.",
                        "PROMPT_INJECTION");
            }
        }

        if (role == AiRole.CUSTOMER || role == AiRole.ANONYMOUS) {
            for (Pattern pattern : CUSTOMER_PRIVILEGE_ESCALATION_PATTERNS) {
                if (pattern.matcher(message).find()) {
                    return AiSecurityResult.denied(
                            "That information is available only to administrators. "
                                    + "I can help with your own credit score, loans, EMIs, and financial guidance.",
                            "PRIVILEGE_ESCALATION");
                }
            }
        }

        return AiSecurityResult.permitted();
    }

    public record AiSecurityResult(boolean allowed, String blockedMessage, String reason) {
        public static AiSecurityResult permitted() {
            return new AiSecurityResult(true, null, null);
        }

        public static AiSecurityResult denied(String message, String reason) {
            return new AiSecurityResult(false, message, reason);
        }
    }
}
