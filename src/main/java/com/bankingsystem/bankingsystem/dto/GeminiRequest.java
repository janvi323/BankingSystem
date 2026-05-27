package com.bankingsystem.bankingsystem.dto;

import java.util.List;

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
