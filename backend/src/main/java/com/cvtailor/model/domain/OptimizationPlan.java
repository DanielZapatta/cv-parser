package com.cvtailor.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OptimizationPlan(
    String objective,
    List<String> keywordsToAdd,
    List<String> skillsToHighlight,
    List<String> experienceAdjustments,
    String summaryRewrite,
    List<String> generalRecommendations
) {}
