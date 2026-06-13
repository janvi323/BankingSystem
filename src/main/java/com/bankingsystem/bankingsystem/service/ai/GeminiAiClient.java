package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.config.AiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Component
public class GeminiAiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiClient.class);
    private static final String GEMINI_BASE_URL = "https://generativeai.googleapis.com/v1beta/models/";

    private final AiProperties aiProperties;
    private final RestClient restClient;

    public GeminiAiClient(AiProperties aiProperties, RestClient.Builder restClientBuilder) {
        this.aiProperties = aiProperties;
        this.restClient = restClientBuilder
                .baseUrl(GEMINI_BASE_URL)
                .build();
    }

    public String generate(String systemPrompt, String userPrompt) {
        if (!aiProperties.isLlmConfigured()) {
            return null;
        }

        String model = aiProperties.getModel();
        String path = model + ":generateContent?key=" + aiProperties.getApiKey();

        Map<String, Object> requestBody = Map.of(
                "systemInstruction", Map.of(
                        "parts", new Object[]{Map.of("text", systemPrompt)}
                ),
                "contents", new Object[]{
                        Map.of("role", "user", "parts", new Object[]{Map.of("text", userPrompt)})
                },
                "generationConfig", Map.of(
                        "temperature", aiProperties.getTemperature(),
                        "maxOutputTokens", aiProperties.getMaxTokens()
                )
        );

        try {
            JsonNode response = restClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            return extractText(response);
        } catch (RestClientException ex) {
            log.warn("Gemini API call failed: {}", ex.getMessage());
            return null;
        }
    }

    private String extractText(JsonNode response) {
        if (response == null) {
            return null;
        }

        JsonNode candidates = response.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            JsonNode error = response.path("error").path("message");
            if (!error.isMissingNode()) {
                log.warn("Gemini API error: {}", error.asText());
            }
            return null;
        }

        JsonNode parts = candidates.get(0).path("content").path("parts");
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
