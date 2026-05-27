package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.config.ChatbotAiConfig;
import com.bankingsystem.bankingsystem.dto.*;
import com.bankingsystem.bankingsystem.entity.ChatMessage;
import com.bankingsystem.bankingsystem.repository.ChatMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Service layer for chatbot functionality.
 * Handles AI API integration, message processing, and chat history management.
 */
@Service
public class ChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    
    @Autowired
    private ChatbotAiConfig chatbotAiConfig;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    // Constants for API endpoints
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    
    // API Key validation
    private static final String API_KEY_PATTERN = "^[a-zA-Z0-9\\-_]+$";
    
    /**
     * Process user message and get AI response
     */
    public ChatResponse processMessage(Long userId, String userMessage, String ipAddress) {
        try {
            // Validate input
            if (userMessage == null || userMessage.trim().isEmpty()) {
                return ChatResponse.error("Message cannot be empty");
            }
            
            if (userMessage.length() > 2000) {
                return ChatResponse.error("Message is too long (max 2000 characters)");
            }
            
            // Validate API configuration
            if (!isApiConfigured()) {
                logger.error("AI API is not properly configured");
                return ChatResponse.error("Chatbot service is not configured. Please contact administrator.");
            }
            
            // Get AI response
            String aiResponse = callAiApi(userMessage);
            
            // Determine message type
            String messageType = categorizeMessage(userMessage);
            
            // Save to database
            ChatMessage chatMessage = new ChatMessage(userId, userMessage, aiResponse, messageType);
            chatMessage.setIpAddress(ipAddress);
            ChatMessage saved = chatMessageRepository.save(chatMessage);
            
            logger.info("Chat message saved. UserId: {}, MessageType: {}", userId, messageType);
            
            return new ChatResponse(
                    saved.getId(),
                    userMessage,
                    aiResponse,
                    messageType,
                    saved.getCreatedAt()
            );
            
        } catch (Exception e) {
            logger.error("Error processing chat message for userId: {}", userId, e);
            return ChatResponse.error("Failed to process message: " + e.getMessage());
        }
    }
    
    /**
     * Get chat history for a user
     */
    public List<ChatMessage> getChatHistory(Long userId, int limit) {
        try {
            List<ChatMessage> history = chatMessageRepository.findByUserIdOrderByCreatedAtDesc(userId);
            return history.stream()
                    .limit(Math.min(limit, 100))
                    .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                    .toList();
        } catch (Exception e) {
            logger.error("Error retrieving chat history for userId: {}", userId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Clear chat history for a user
     */
    public boolean clearChatHistory(Long userId) {
        try {
            List<ChatMessage> messages = chatMessageRepository.findByUserIdOrderByCreatedAtDesc(userId);
            chatMessageRepository.deleteAll(messages);
            logger.info("Chat history cleared for userId: {}", userId);
            return true;
        } catch (Exception e) {
            logger.error("Error clearing chat history for userId: {}", userId, e);
            return false;
        }
    }
    
    /**
     * Call OpenAI API
     */
    private String callOpenAiApi(String userMessage) {
        try {
            OpenAIMessage systemMessage = new OpenAIMessage("system", chatbotAiConfig.getSystemPrompt());
            OpenAIMessage userMsg = new OpenAIMessage("user", userMessage);
            
            OpenAIRequest request = new OpenAIRequest(
                    chatbotAiConfig.getModel(),
                    Arrays.asList(systemMessage, userMsg)
            );
            request.setTemperature((int) chatbotAiConfig.getTemperature());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(chatbotAiConfig.getApiKey());
            
            HttpEntity<OpenAIRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<OpenAIResponse> response = restTemplate.postForEntity(
                    OPENAI_API_URL,
                    entity,
                    OpenAIResponse.class
            );
            
            if (response.getBody() != null && !response.getBody().getChoices().isEmpty()) {
                return response.getBody().getChoices().get(0).getMessage().getContent();
            } else {
                throw new RuntimeException("Empty response from OpenAI API");
            }
            
        } catch (Exception e) {
            logger.error("Error calling OpenAI API", e);
            throw new RuntimeException("Failed to get response from AI service: " + e.getMessage());
        }
    }
    
    /**
     * Call Google Gemini API
     */
    private String callGeminiApi(String userMessage) {
        try {
            GeminiRequest.Content userContent = new GeminiRequest.Content("user", userMessage);
            GeminiRequest request = new GeminiRequest(
                    List.of(userContent),
                    chatbotAiConfig.getSystemPrompt()
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);
            
            String url = GEMINI_API_URL + "?key=" + chatbotAiConfig.getApiKey();
            
            ResponseEntity<GeminiResponse> response = restTemplate.postForEntity(
                    url,
                    entity,
                    GeminiResponse.class
            );
            
            if (response.getBody() != null && !response.getBody().getCandidates().isEmpty()) {
                return response.getBody().getCandidates().get(0).getParts().get(0).getText();
            } else {
                throw new RuntimeException("Empty response from Gemini API");
            }
            
        } catch (Exception e) {
            logger.error("Error calling Gemini API", e);
            throw new RuntimeException("Failed to get response from AI service: " + e.getMessage());
        }
    }
    
    /**
     * Main method to call appropriate AI API based on configuration
     */
    private String callAiApi(String userMessage) {
        String provider = chatbotAiConfig.getProvider().toLowerCase();
        
        if ("gemini".equals(provider)) {
            return callGeminiApi(userMessage);
        } else {
            return callOpenAiApi(userMessage);
        }
    }
    
    /**
     * Categorize message type based on keywords
     */
    private String categorizeMessage(String message) {
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("emi") || lowerMessage.contains("installment")) {
            return "EMI_QUERY";
        } else if (lowerMessage.contains("interest") || lowerMessage.contains("rate")) {
            return "INTEREST_QUERY";
        } else if (lowerMessage.contains("eligibility") || lowerMessage.contains("eligible")) {
            return "ELIGIBILITY_QUERY";
        } else if (lowerMessage.contains("repay") || lowerMessage.contains("payment")) {
            return "REPAYMENT_QUERY";
        } else if (lowerMessage.contains("apply") || lowerMessage.contains("loan")) {
            return "LOAN_QUERY";
        } else {
            return "GENERAL_BANKING";
        }
    }
    
    /**
     * Check if API is properly configured
     */
    private boolean isApiConfigured() {
        String apiKey = chatbotAiConfig.getApiKey();
        String provider = chatbotAiConfig.getProvider();
        
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("API Key is not configured");
            return false;
        }
        
        if (provider == null || provider.isEmpty()) {
            logger.warn("AI Provider is not configured");
            return false;
        }
        
        // Additional validation to prevent injection
        if (!apiKey.matches(API_KEY_PATTERN)) {
            logger.warn("API Key format is invalid");
            return false;
        }
        
        return true;
    }
}
