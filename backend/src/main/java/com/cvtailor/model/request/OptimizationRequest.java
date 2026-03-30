package com.cvtailor.model.request;

import jakarta.validation.constraints.NotBlank;

public record OptimizationRequest(
    @NotBlank(message = "CV text is required") String cvText,
    @NotBlank(message = "Job description is required") String jobDescription,
    String modelName
) {
    public OptimizationRequest {
        if (modelName == null || modelName.isBlank()) {
            modelName = "qwen2.5:7b";
        }
    }
}
