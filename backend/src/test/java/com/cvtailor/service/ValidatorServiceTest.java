package com.cvtailor.service;

import com.cvtailor.model.domain.CvData;
import com.cvtailor.model.domain.GeneratedCv;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorServiceTest {

    private final ValidatorService validatorService = new ValidatorService();

    @Test
    void validateAndNormalize_withNullGeneratedCv_shouldFallbackToOriginal() {
        CvData original = new CvData(
            "John Doe", "john@example.com", "+1234567890", "New York",
            "Experienced developer", List.of(), List.of(),
            List.of("Java", "Spring"), List.of("English"), List.of()
        );

        GeneratedCv result = validatorService.validateAndNormalize(null, original);

        assertNotNull(result);
        assertEquals("John Doe", result.fullName());
        assertEquals("john@example.com", result.email());
        assertEquals(List.of("Java", "Spring"), result.skills());
    }

    @Test
    void validateAndNormalize_withValidGeneratedCv_shouldUseGenerated() {
        CvData original = new CvData(
            "John Doe", "john@example.com", "+1234567890", "New York",
            "Old summary", List.of(), List.of(),
            List.of("Java"), List.of("English"), List.of()
        );

        GeneratedCv generated = new GeneratedCv(
            "John Doe", "john@example.com", "+1234567890", "New York",
            "Optimized summary for the job", List.of(), List.of(),
            List.of("Java", "Spring Boot", "Kubernetes"), List.of("English"), List.of()
        );

        GeneratedCv result = validatorService.validateAndNormalize(generated, original);

        assertNotNull(result);
        assertEquals("Optimized summary for the job", result.summary());
        assertEquals(3, result.skills().size());
        assertTrue(result.skills().contains("Kubernetes"));
    }

    @Test
    void validateAndNormalize_withBlankFields_shouldFallbackToOriginal() {
        CvData original = new CvData(
            "Jane Doe", "jane@example.com", null, null,
            "Backend engineer", List.of(), List.of(),
            List.of("Python"), List.of(), List.of()
        );

        GeneratedCv generated = new GeneratedCv(
            "", "", null, null,
            "New summary", List.of(), List.of(),
            List.of("Python", "Django"), List.of(), List.of()
        );

        GeneratedCv result = validatorService.validateAndNormalize(generated, original);

        assertNotNull(result);
        assertEquals("Jane Doe", result.fullName());
        assertEquals("jane@example.com", result.email());
        assertEquals("New summary", result.summary());
    }
}
