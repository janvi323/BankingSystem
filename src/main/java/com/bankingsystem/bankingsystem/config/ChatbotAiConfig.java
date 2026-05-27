package com.bankingsystem.bankingsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for AI Chatbot settings
 * Loads properties from application.properties or environment variables
 */
@Configuration
@ConfigurationProperties(prefix = "chatbot.ai")
public class ChatbotAiConfig {
    
    private String provider; // "openai" or "gemini"
    private String apiKey;
    private String model;
    private String systemPrompt;
    private int maxTokens;
    private double temperature;
    
    public ChatbotAiConfig() {
        // Default values
        this.provider = "openai";
        this.model = "gpt-4o-mini";
        this.maxTokens = 500;
        this.temperature = 0.7;
        this.systemPrompt = "You are a professional AI banking assistant for a Loan Management System. " +
                "Only answer finance, loan, EMI, repayment, banking, and platform-related questions " +
                "in a professional and concise way. Be helpful and accurate.";
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getSystemPrompt() {
        return systemPrompt;
    }
    
    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
    
    public int getMaxTokens() {
        return maxTokens;
    }
    
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    
    @Override
    public String toString() {
        return "ChatbotAiConfig{" +
                "provider='" + provider + '\'' +
                ", model='" + model + '\'' +
                ", maxTokens=" + maxTokens +
                ", temperature=" + temperature +
                '}';
    }
}
