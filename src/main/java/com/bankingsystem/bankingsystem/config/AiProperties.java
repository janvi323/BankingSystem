package com.bankingsystem.bankingsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Externalized AI configuration — all values are driven by environment variables
 * with safe defaults. This allows Render deployment to tune behaviour without
 * a code change.
 */
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    /** Master switch — set ai.enabled=false to fully disable the AI layer */
    private boolean enabled = true;

    /** LLM provider — currently only "gemini" is supported */
    private String provider = "gemini";

    /** Gemini API key — must be set as AI_API_KEY on Render for LLM to activate */
    private String apiKey = "";

    /** Gemini model identifier */
    private String model = "gemini-1.5-flash";

    /** Max output tokens for customer-facing responses */
    private int maxTokens = 600;

    /** Max output tokens for admin-facing responses (richer analytics) */
    private int adminMaxTokens = 900;

    /**
     * Customer AI temperature — higher value = warmer, more conversational.
     * Range: 0.0–1.0. Default 0.65 balances empathy with accuracy.
     */
    private double customerTemperature = 0.65;

    /**
     * Admin AI temperature — lower value = more precise and deterministic.
     * Range: 0.0–1.0. Default 0.2 for analytical accuracy.
     */
    private double adminTemperature = 0.2;

    /** HTTP timeout in seconds for calls to the Gemini API */
    private int timeoutSeconds = 30;

    /**
     * Maximum number of conversation turns stored per session.
     * Older turns are dropped once this limit is exceeded.
     */
    private int maxConversationHistory = 6;

    /**
     * When true, every LLM response is run through the outbound response
     * sanitizer before being returned to the client.
     */
    private boolean responseOutputFilterEnabled = true;

    // ───────────────────────────────────────────────────
    // Computed helpers
    // ───────────────────────────────────────────────────

    /** Returns true when the LLM is usable (enabled + API key present) */
    public boolean isLlmConfigured() {
        return enabled && apiKey != null && !apiKey.isBlank();
    }

    // ───────────────────────────────────────────────────
    // Getters & Setters
    // ───────────────────────────────────────────────────

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }

    public int getAdminMaxTokens() { return adminMaxTokens; }
    public void setAdminMaxTokens(int adminMaxTokens) { this.adminMaxTokens = adminMaxTokens; }

    public double getCustomerTemperature() { return customerTemperature; }
    public void setCustomerTemperature(double customerTemperature) { this.customerTemperature = customerTemperature; }

    public double getAdminTemperature() { return adminTemperature; }
    public void setAdminTemperature(double adminTemperature) { this.adminTemperature = adminTemperature; }

    /** Legacy getter — returns customer temperature for backward compat */
    public double getTemperature() { return customerTemperature; }
    public void setTemperature(double temperature) { this.customerTemperature = temperature; }

    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }

    public int getMaxConversationHistory() { return maxConversationHistory; }
    public void setMaxConversationHistory(int maxConversationHistory) { this.maxConversationHistory = maxConversationHistory; }

    public boolean isResponseOutputFilterEnabled() { return responseOutputFilterEnabled; }
    public void setResponseOutputFilterEnabled(boolean responseOutputFilterEnabled) {
        this.responseOutputFilterEnabled = responseOutputFilterEnabled;
    }
}
