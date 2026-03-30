package com.cvtailor.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MatchingResult(
    double score,
    List<String> matchedSkills,
    List<String> missingSkills,
    List<String> partialMatches,
    String summary
) {}
