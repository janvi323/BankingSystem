package com.bankingsystem.bankingsystem.dto;

public class ChatResponse {

    private boolean success;
    private String botResponse;
    private String messageType;

    public ChatResponse() {
    }

    public ChatResponse(boolean success, String botResponse, String messageType) {
        this.success = success;
        this.botResponse = botResponse;
        this.messageType = messageType;
    }

    public static ChatResponse ok(String botResponse, String messageType) {
        return new ChatResponse(true, botResponse, messageType);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
}
