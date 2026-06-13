package com.bankingsystem.bankingsystem.dto;

import java.time.Instant;
import java.util.List;

/**
 * ChatResponse — outbound payload returned by the chat API.
 *
 * <p>Fields:
 * <ul>
 *   <li>{@code success} — false when the message was blocked by security</li>
 *   <li>{@code botResponse} — the AI-generated or rule-based response text</li>
 *   <li>{@code messageType} — classification tag (e.g. CREDIT_SCORE, EMI, ADMIN_LOANS)</li>
 *   <li>{@code aiRole} — the resolved role: CUSTOMER | ADMIN | ANONYMOUS</li>
 *   <li>{@code poweredByLlm} — true when the response came from Gemini (vs rule-based)</li>
 *   <li>{@code timestamp} — ISO instant for the client to display message timestamps</li>
 *   <li>{@code suggestedFollowUps} — role-aware quick-reply suggestions for the widget</li>
 * </ul>
 */
public class ChatResponse {

    private boolean success;
    private String botResponse;
    private String messageType;
    private String aiRole;
    private boolean poweredByLlm;
    private String timestamp;
    private List<String> suggestedFollowUps;

    public ChatResponse() {}

    public ChatResponse(boolean success, String botResponse, String messageType,
                        String aiRole, boolean poweredByLlm,
                        String timestamp, List<String> suggestedFollowUps) {
        this.success           = success;
        this.botResponse       = botResponse;
        this.messageType       = messageType;
        this.aiRole            = aiRole;
        this.poweredByLlm      = poweredByLlm;
        this.timestamp         = timestamp;
        this.suggestedFollowUps = suggestedFollowUps;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Factory methods
    // ─────────────────────────────────────────────────────────────────────────

    public static ChatResponse ok(String botResponse, String messageType,
                                  String aiRole, boolean poweredByLlm) {
        return new ChatResponse(true, botResponse, messageType, aiRole,
                poweredByLlm, Instant.now().toString(), null);
    }

    public static ChatResponse ok(String botResponse, String messageType,
                                  String aiRole, boolean poweredByLlm,
                                  List<String> suggestedFollowUps) {
        return new ChatResponse(true, botResponse, messageType, aiRole,
                poweredByLlm, Instant.now().toString(), suggestedFollowUps);
    }

    public static ChatResponse blocked(String botResponse, String reason, String aiRole) {
        return new ChatResponse(false, botResponse, reason, aiRole,
                false, Instant.now().toString(), null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Getters & Setters
    // ─────────────────────────────────────────────────────────────────────────

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getBotResponse() { return botResponse; }
    public void setBotResponse(String botResponse) { this.botResponse = botResponse; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getAiRole() { return aiRole; }
    public void setAiRole(String aiRole) { this.aiRole = aiRole; }

    public boolean isPoweredByLlm() { return poweredByLlm; }
    public void setPoweredByLlm(boolean poweredByLlm) { this.poweredByLlm = poweredByLlm; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public List<String> getSuggestedFollowUps() { return suggestedFollowUps; }
    public void setSuggestedFollowUps(List<String> suggestedFollowUps) {
        this.suggestedFollowUps = suggestedFollowUps;
    }
}
