package com.cvtailor.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CvData(
    String fullName,
    String email,
    String phone,
    String location,
    String summary,
    List<Experience> experiences,
    List<Education> educations,
    List<String> skills,
    List<String> languages,
    List<String> certifications
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Experience(
        String company,
        String title,
        String startDate,
        String endDate,
        String description,
        List<String> achievements
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Education(
        String institution,
        String degree,
        String field,
        String startDate,
        String endDate
    ) {}
}
