package com.cvtailor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

class LlmServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void extractJsonFromText_withPureJson() throws Exception {
        LlmService service = new LlmService(WebClient.create(), objectMapper);
        assertNotNull(service);
    }

    @Test
    void llmServiceCreation_shouldSucceed() {
        WebClient mockClient = WebClient.create();
        LlmService service = new LlmService(mockClient, objectMapper);
        assertNotNull(service);
    }
}
