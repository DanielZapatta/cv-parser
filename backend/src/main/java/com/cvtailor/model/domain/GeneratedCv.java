package com.cvtailor.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeneratedCv(
    String fullName,
    String email,
    String phone,
    String location,
    String summary,
    List<CvData.Experience> experiences,
    List<CvData.Education> educations,
    List<String> skills,
    List<String> languages,
    List<String> certifications
) {}
