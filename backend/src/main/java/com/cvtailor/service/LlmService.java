package com.cvtailor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class LlmService {

    private static final Logger log = LoggerFactory.getLogger(LlmService.class);

    private final WebClient ollamaWebClient;
    private final ObjectMapper objectMapper;

    public LlmService(WebClient ollamaWebClient, ObjectMapper objectMapper) {
        this.ollamaWebClient = ollamaWebClient;
        this.objectMapper = objectMapper;
    }

    public String generate(String model, String prompt) {
        log.debug("Calling Ollama model={} prompt_length={}", model, prompt.length());

        Map<String, Object> requestBody = Map.of(
            "model", model,
            "prompt", prompt,
            "stream", false,
            "format", "json"
        );

        try {
            String responseBody = ollamaWebClient.post()
                    .uri("/api/generate")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(java.time.Duration.ofMinutes(5))
                    .block();

            if (responseBody == null) {
                throw new RuntimeException("Empty response from Ollama");
            }

            JsonNode root = objectMapper.readTree(responseBody);
            String response = root.path("response").asText();

            log.debug("Ollama raw response length={}", response.length());
            return response;

        } catch (Exception e) {
            log.error("Error calling Ollama: {}", e.getMessage(), e);
            throw new RuntimeException("LLM service error: " + e.getMessage(), e);
        }
    }

    public <T> T generateAndParse(String model, String prompt, Class<T> targetClass) {
        String rawResponse = generate(model, prompt);
        try {
            String jsonStr = extractJson(rawResponse);
            return objectMapper.readValue(jsonStr, targetClass);
        } catch (Exception e) {
            log.error("Failed to parse LLM response as {}: {}", targetClass.getSimpleName(), rawResponse, e);
            throw new RuntimeException("Failed to parse LLM response: " + e.getMessage(), e);
        }
    }

    private String extractJson(String text) {
        if (text == null || text.isBlank()) {
            return "{}";
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        start = text.indexOf('[');
        end = text.lastIndexOf(']');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }
}
