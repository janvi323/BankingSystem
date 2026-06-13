package com.bankingsystem.bankingsystem.dto;

public class ChatResponse {

    private boolean success;
    private String botResponse;
    private String messageType;
    private String aiRole;
    private boolean poweredByLlm;

    public ChatResponse() {
    }

    public ChatResponse(boolean success, String botResponse, String messageType, String aiRole, boolean poweredByLlm) {
        this.success = success;
        this.botResponse = botResponse;
        this.messageType = messageType;
        this.aiRole = aiRole;
        this.poweredByLlm = poweredByLlm;
    }

    public static ChatResponse ok(String botResponse, String messageType, String aiRole, boolean poweredByLlm) {
        return new ChatResponse(true, botResponse, messageType, aiRole, poweredByLlm);
    }

    public static ChatResponse blocked(String botResponse, String messageType, String aiRole) {
        return new ChatResponse(false, botResponse, messageType, aiRole, false);
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

    public String getAiRole() {
        return aiRole;
    }

    public void setAiRole(String aiRole) {
        this.aiRole = aiRole;
    }

    public boolean isPoweredByLlm() {
        return poweredByLlm;
    }

    public void setPoweredByLlm(boolean poweredByLlm) {
        this.poweredByLlm = poweredByLlm;
    }
}
