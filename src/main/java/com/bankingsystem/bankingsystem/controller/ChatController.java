package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.Service.ai.AiChatService;
import com.bankingsystem.bankingsystem.dto.ChatRequest;
import com.bankingsystem.bankingsystem.dto.ChatResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AiChatService aiChatService;

    public ChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatResponse> sendMessage(@Valid @RequestBody ChatRequest request, HttpSession session) {
        ChatResponse response = aiChatService.chat(session, request.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig(HttpSession session) {
        return ResponseEntity.ok(aiChatService.getWidgetConfig(session));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(aiChatService.health());
    }
}
