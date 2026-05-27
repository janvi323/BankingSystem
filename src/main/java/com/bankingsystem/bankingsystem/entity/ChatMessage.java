package com.bankingsystem.bankingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a chat message exchanged between user and AI chatbot.
 * Stores conversation history in the database for audit and analytics.
 */
@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_created_at", columnList = "created_at DESC")
})
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "user_message", nullable = false, columnDefinition = "TEXT")
    private String userMessage;
    
    @Column(name = "bot_response", nullable = false, columnDefinition = "TEXT")
    private String botResponse;
    
    @Column(name = "message_type", nullable = false)
    private String messageType; // "LOAN_QUERY", "EMI_QUERY", "GENERAL_BANKING", etc.
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    // Constructors
    public ChatMessage() {
        this.createdAt = LocalDateTime.now();
    }
    
    public ChatMessage(Long userId, String userMessage, String botResponse, String messageType) {
        this.userId = userId;
        this.userMessage = userMessage;
        this.botResponse = botResponse;
        this.messageType = messageType;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", userId=" + userId +
                ", userMessage='" + userMessage + '\'' +
                ", messageType='" + messageType + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
