package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.dto.ChatRequest;
import com.bankingsystem.bankingsystem.dto.ChatResponse;
import com.bankingsystem.bankingsystem.entity.ChatMessage;
import com.bankingsystem.bankingsystem.Service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for chatbot API endpoints
 * Handles chat message processing and history retrieval
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:8082") // Allow same-origin requests
public class ChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    
    @Autowired
    private ChatService chatService;
    
    /**
     * Send message to chatbot and get AI response
     * POST /api/chat/send
     */
    @PostMapping("/send")
    public ResponseEntity<ChatResponse> sendMessage(
            @Valid @RequestBody ChatRequest chatRequest,
            HttpServletRequest httpRequest) {
        try {
            // Get current user ID from authentication
            Long userId = getCurrentUserId();
            
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ChatResponse.error("User not authenticated"));
            }
            
            // Get client IP address
            String ipAddress = getClientIpAddress(httpRequest);
            
            logger.info("Chat message received from userId: {}, IP: {}", userId, ipAddress);
            
            // Process message
            ChatResponse response = chatService.processMessage(
                    userId,
                    chatRequest.getMessage(),
                    ipAddress
            );
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error in sendMessage endpoint", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ChatResponse.error("Failed to process message: " + e.getMessage()));
        }
    }
    
    /**
     * Get chat history for current user
     * GET /api/chat/history?limit=50
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getChatHistory(
            @RequestParam(defaultValue = "50") int limit) {
        try {
            Long userId = getCurrentUserId();
            
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }
            
            List<ChatMessage> history = chatService.getChatHistory(userId, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", history.size());
            response.put("messages", history);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving chat history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    /**
     * Clear chat history for current user
     * DELETE /api/chat/history
     */
    @DeleteMapping("/history")
    public ResponseEntity<Map<String, Object>> clearChatHistory() {
        try {
            Long userId = getCurrentUserId();
            
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "error", "User not authenticated"));
            }
            
            boolean cleared = chatService.clearChatHistory(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", cleared);
            response.put("message", cleared ? "Chat history cleared successfully" : "Failed to clear chat history");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error clearing chat history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    /**
     * Health check endpoint
     * GET /api/chat/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Chatbot service is running"
        ));
    }
    
    /**
     * Get current authenticated user ID
     * Returns null if user is not authenticated
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.isAuthenticated()) {
                // Get user from principal
                Object principal = authentication.getPrincipal();
                
                // If principal is a string (username), generate a user ID from it
                // For now, use a simplified approach - adapt based on your authentication implementation
                if (principal instanceof String) {
                    String username = principal.toString();
                    return hashString(username).longValue();
                }
                
                // If principal has a name, use it as the username
                String name = authentication.getName();
                if (name != null && !name.isEmpty()) {
                    return hashString(name).longValue();
                }
            }
        } catch (Exception e) {
            logger.debug("Could not extract user ID from authentication", e);
        }
        
        return null;
    }
    
    /**
     * Get client IP address from HTTP request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Simple hash function to convert username to long
     * For testing purposes - replace with actual user lookup
     */
    private Long hashString(String str) {
        if (str == null) {
            return 0L;
        }
        long hash = 0;
        for (char c : str.toCharArray()) {
            hash = ((hash << 5) - hash) + c;
        }
        return Math.abs(hash);
    }
}
