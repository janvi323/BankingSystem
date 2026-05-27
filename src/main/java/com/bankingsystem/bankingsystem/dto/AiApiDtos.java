package com.bankingsystem.bankingsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTOs for AI API integration (OpenAI and Google Gemini)
 */

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

/**
 * Message DTO for OpenAI
 */
public class OpenAIMessage {
    private String role;
    private String content;
    
    public OpenAIMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}

/**
 * Response DTO for OpenAI API
 */
public class OpenAIResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    
    public static class Choice {
        private int index;
        private OpenAIMessage message;
        private String finish_reason;
        
        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        
        public OpenAIMessage getMessage() { return message; }
        public void setMessage(OpenAIMessage message) { this.message = message; }
        
        public String getFinish_reason() { return finish_reason; }
        public void setFinish_reason(String finish_reason) { this.finish_reason = finish_reason; }
    }
    
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
        
        public int getPrompt_tokens() { return prompt_tokens; }
        public void setPrompt_tokens(int prompt_tokens) { this.prompt_tokens = prompt_tokens; }
        
        public int getCompletion_tokens() { return completion_tokens; }
        public void setCompletion_tokens(int completion_tokens) { this.completion_tokens = completion_tokens; }
        
        public int getTotal_tokens() { return total_tokens; }
        public void setTotal_tokens(int total_tokens) { this.total_tokens = total_tokens; }
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }
    
    public long getCreated() { return created; }
    public void setCreated(long created) { this.created = created; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public List<Choice> getChoices() { return choices; }
    public void setChoices(List<Choice> choices) { this.choices = choices; }
    
    public Usage getUsage() { return usage; }
    public void setUsage(Usage usage) { this.usage = usage; }
}

/**
 * Request DTO for Google Gemini API
 */
public class GeminiRequest {
    private List<Content> contents;
    private SystemInstruction system_instruction;
    
    public GeminiRequest(List<Content> contents, String systemPrompt) {
        this.contents = contents;
        this.system_instruction = new SystemInstruction(systemPrompt);
    }
    
    public List<Content> getContents() { return contents; }
    public void setContents(List<Content> contents) { this.contents = contents; }
    
    public SystemInstruction getSystem_instruction() { return system_instruction; }
    public void setSystem_instruction(SystemInstruction system_instruction) { this.system_instruction = system_instruction; }
    
    public static class Content {
        private String role;
        private List<Part> parts;
        
        public Content(String role, String text) {
            this.role = role;
            this.parts = List.of(new Part(text));
        }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public List<Part> getParts() { return parts; }
        public void setParts(List<Part> parts) { this.parts = parts; }
    }
    
    public static class Part {
        private String text;
        
        public Part(String text) { this.text = text; }
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
    
    public static class SystemInstruction {
        private List<Part> parts;
        
        public SystemInstruction(String text) {
            this.parts = List.of(new Part(text));
        }
        
        public List<Part> getParts() { return parts; }
        public void setParts(List<Part> parts) { this.parts = parts; }
    }
}

/**
 * Response DTO for Google Gemini API
 */
public class GeminiResponse {
    private List<Content> candidates;
    private String usageMetadata;
    
    public static class Content {
        private List<Part> parts;
        
        public List<Part> getParts() { return parts; }
        public void setParts(List<Part> parts) { this.parts = parts; }
    }
    
    public static class Part {
        private String text;
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
    
    public List<Content> getCandidates() { return candidates; }
    public void setCandidates(List<Content> candidates) { this.candidates = candidates; }
    
    public String getUsageMetadata() { return usageMetadata; }
    public void setUsageMetadata(String usageMetadata) { this.usageMetadata = usageMetadata; }
}
