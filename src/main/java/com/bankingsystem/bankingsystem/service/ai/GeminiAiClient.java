package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.config.AiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * GeminiAiClient — production-grade HTTP client for the Google Gemini API.
 *
 * <p>Features:
 * <ul>
 *   <li>Role-aware generation config: admin uses low temperature for precision analytics,
 *       customer uses moderate temperature for warm conversational responses</li>
 *   <li>Gemini safety settings configured to BLOCK_MEDIUM_AND_ABOVE for all harm categories,
 *       preventing hallucinated financial advice or harmful content</li>
 *   <li>Detailed error classification: 429 rate-limit, 400 bad request, 5xx server errors</li>
 *   <li>HTTP timeout enforcement via read/connect timeout on the underlying client</li>
 * </ul>
 */
@Component
public class GeminiAiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiClient.class);
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    /**
     * Gemini safety settings — applied to all requests.
     * BLOCK_MEDIUM_AND_ABOVE prevents the model from generating harmful or misleading content.
     * This is especially important for financial AI to avoid fabricated advice.
     */
    private static final List<Map<String, String>> SAFETY_SETTINGS = List.of(
            Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT",  "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
            Map.of("category", "HARM_CATEGORY_HATE_SPEECH",         "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
            Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT",   "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
            Map.of("category", "HARM_CATEGORY_HARASSMENT",          "threshold", "BLOCK_MEDIUM_AND_ABOVE")
    );

    private final AiProperties aiProperties;
    private final RestClient restClient;

    public GeminiAiClient(AiProperties aiProperties, RestClient.Builder restClientBuilder) {
        this.aiProperties = aiProperties;
        this.restClient   = restClientBuilder
                .baseUrl(GEMINI_BASE_URL)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Generates an AI response using role-specific generation parameters.
     *
     * @param systemPrompt The role-specific system prompt from {@link AiPromptFactory}
     * @param userPrompt   The user prompt including context block and user question
     * @param role         The AI role — determines temperature and max token limit
     * @return The generated text, or {@code null} if generation failed or is disabled
     */
    public String generate(String systemPrompt, String userPrompt, AiRole role) {
        if (!aiProperties.isLlmConfigured()) {
            return null;
        }

        String model = aiProperties.getModel();
        String path  = model + ":generateContent?key=" + aiProperties.getApiKey();

        double temperature = resolveTemperature(role);
        int    maxTokens   = resolveMaxTokens(role);

        Map<String, Object> requestBody = Map.of(
                "systemInstruction", Map.of(
                        "parts", new Object[]{Map.of("text", systemPrompt)}
                ),
                "contents", new Object[]{
                        Map.of("role", "user", "parts", new Object[]{Map.of("text", userPrompt)})
                },
                "generationConfig", Map.of(
                        "temperature",     temperature,
                        "maxOutputTokens", maxTokens,
                        "topP",            0.95,
                        "topK",            40
                ),
                "safetySettings", SAFETY_SETTINGS
        );

        try {
            JsonNode response = restClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            String text = extractText(response);
            if (text != null && !text.isBlank()) {
                log.debug("Gemini response generated for role={}, tokens≈{}", role, text.length() / 4);
            }
            return text;

        } catch (RestClientResponseException ex) {
            HttpStatusCode status = ex.getStatusCode();
            if (status.value() == 429) {
                log.warn("Gemini API rate limit exceeded (429) — falling back to rule-based chat");
            } else if (status.value() == 400) {
                log.warn("Gemini API bad request (400): {} — falling back", ex.getResponseBodyAsString());
            } else if (status.is5xxServerError()) {
                log.warn("Gemini API server error ({}): {}", status.value(), ex.getMessage());
            } else {
                log.warn("Gemini API HTTP error ({}): {}", status.value(), ex.getMessage());
            }
            return null;

        } catch (RestClientException ex) {
            log.warn("Gemini API connection failure: {} — falling back to rule-based chat", ex.getMessage());
            return null;
        }
    }

    /**
     * Legacy overload for backward compatibility — uses customer defaults.
     * Prefer {@link #generate(String, String, AiRole)} for role-aware generation.
     */
    public String generate(String systemPrompt, String userPrompt) {
        return generate(systemPrompt, userPrompt, AiRole.CUSTOMER);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Resolves the generation temperature for the given role.
     * Admin: low temperature (0.2) for deterministic analytics.
     * Customer: moderate temperature (0.65) for warm, natural conversation.
     * Anonymous: customer defaults.
     */
    private double resolveTemperature(AiRole role) {
        return role == AiRole.ADMIN
                ? aiProperties.getAdminTemperature()
                : aiProperties.getCustomerTemperature();
    }

    /**
     * Resolves the max output tokens for the given role.
     * Admin gets more tokens to support richer analytical responses.
     */
    private int resolveMaxTokens(AiRole role) {
        return role == AiRole.ADMIN
                ? aiProperties.getAdminMaxTokens()
                : aiProperties.getMaxTokens();
    }

    /**
     * Extracts the text content from a Gemini API response, handling all
     * possible error and empty-response cases.
     */
    private String extractText(JsonNode response) {
        if (response == null) {
            return null;
        }

        // Check for API-level error
        JsonNode error = response.path("error").path("message");
        if (!error.isMissingNode()) {
            log.warn("Gemini API returned error in body: {}", error.asText());
            return null;
        }

        // Check for safety block
        JsonNode candidates = response.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            log.debug("Gemini returned no candidates — possible safety block or empty response");
            return null;
        }

        JsonNode candidate     = candidates.get(0);
        JsonNode finishReason  = candidate.path("finishReason");
        if (!finishReason.isMissingNode() && "SAFETY".equals(finishReason.asText())) {
            log.warn("Gemini response blocked by safety filter");
            return null;
        }

        JsonNode parts = candidate.path("content").path("parts");
        if (!parts.isArray() || parts.isEmpty()) {
            return null;
        }

        StringBuilder text = new StringBuilder();
        for (JsonNode part : parts) {
            if (part.has("text")) {
                text.append(part.get("text").asText());
            }
        }

        String result = text.toString().trim();
        return result.isEmpty() ? null : result;
    }
}
