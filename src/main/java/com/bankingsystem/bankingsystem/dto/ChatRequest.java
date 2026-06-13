package com.bankingsystem.bankingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ChatRequest — inbound payload from the chat widget.
 * The optional {@code conversationId} field allows the frontend to
 * track and display multi-turn conversation continuity.
 */
public class ChatRequest {

    @NotBlank(message = "Message cannot be empty")
    @Size(max = 2000, message = "Message must be 2000 characters or fewer")
    private String message;

    /** Optional client-side conversation identifier for UI continuity display */
    private String conversationId;

    public ChatRequest() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
}
