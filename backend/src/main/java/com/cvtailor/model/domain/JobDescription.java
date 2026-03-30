package com.cvtailor.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JobDescription(
    String title,
    String company,
    String description,
    List<String> requiredSkills,
    List<String> preferredSkills,
    List<String> responsibilities,
    List<String> requirements
) {}
