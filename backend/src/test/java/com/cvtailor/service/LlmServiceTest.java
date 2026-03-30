package com.cvtailor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class LlmServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LlmService service = new LlmService(WebClient.create(), objectMapper);

    @Test
    void llmServiceCreation_shouldSucceed() {
        assertNotNull(service);
    }

    @Test
    void extractJson_withWrappedJson_shouldReturnJsonOnly() throws Exception {
        Method extractJson = LlmService.class.getDeclaredMethod("extractJson", String.class);
        extractJson.setAccessible(true);

        String result = (String) extractJson.invoke(service, "Some text {\"key\": \"value\"} trailing");
        assertEquals("{\"key\": \"value\"}", result);
    }

    @Test
    void extractJson_withNullInput_shouldReturnEmptyObject() throws Exception {
        Method extractJson = LlmService.class.getDeclaredMethod("extractJson", String.class);
        extractJson.setAccessible(true);

        String result = (String) extractJson.invoke(service, (Object) null);
        assertEquals("{}", result);
    }

    @Test
    void extractJson_withJsonArray_shouldReturnArray() throws Exception {
        Method extractJson = LlmService.class.getDeclaredMethod("extractJson", String.class);
        extractJson.setAccessible(true);

        String result = (String) extractJson.invoke(service, "[\"a\", \"b\"]");
        assertEquals("[\"a\", \"b\"]", result);
    }
}
