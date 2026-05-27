package com.bankingsystem.bankingsystem.dto;

import java.util.List;

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
