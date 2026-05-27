package com.bankingsystem.bankingsystem.dto;

import java.util.List;

/**
 * Request DTO for OpenAI API
 */
public class OpenAIRequest {
    private String model;
    private List<OpenAIMessage> messages;
    private int temperature;
    
    public OpenAIRequest(String model, List<OpenAIMessage> messages) {
        this.model = model;
        this.messages = messages;
        this.temperature = 0;
    }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public List<OpenAIMessage> getMessages() { return messages; }
    public void setMessages(List<OpenAIMessage> messages) { this.messages = messages; }
    
    public int getTemperature() { return temperature; }
    public void setTemperature(int temperature) { this.temperature = temperature; }
}
