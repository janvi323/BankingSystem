package com.bankingsystem.bankingsystem.dto;

import java.time.LocalDateTime;

/**
 * DTO for outgoing chat responses to the frontend.
 */
public class ChatResponse {
    
    private Long messageId;
    private String userMessage;
    private String botResponse;
    private String messageType;
    private LocalDateTime timestamp;
    private boolean success;
    private String errorMessage;
    
    public ChatResponse() {}
    
    public ChatResponse(String userMessage, String botResponse, String messageType) {
        this.userMessage = userMessage;
        this.botResponse = botResponse;
        this.messageType = messageType;
        this.timestamp = LocalDateTime.now();
        this.success = true;
    }
    
    public ChatResponse(Long messageId, String userMessage, String botResponse, String messageType, LocalDateTime timestamp) {
        this.messageId = messageId;
        this.userMessage = userMessage;
        this.botResponse = botResponse;
        this.messageType = messageType;
        this.timestamp = timestamp;
        this.success = true;
    }
    
    // Error constructor
    public static ChatResponse error(String errorMessage) {
        ChatResponse response = new ChatResponse();
        response.success = false;
        response.errorMessage = errorMessage;
        response.timestamp = LocalDateTime.now();
        return response;
    }
    
    // Getters and Setters
    public Long getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
    
    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
    
    public String getBotResponse() {
        return botResponse;
    }
    
    public void setBotResponse(String botResponse) {
        this.botResponse = botResponse;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
