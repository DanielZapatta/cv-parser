package com.cvtailor.service;

import com.cvtailor.model.domain.CvData;
import com.cvtailor.model.domain.JobDescription;
import com.cvtailor.model.domain.MatchingResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchingService {

    private static final Logger log = LoggerFactory.getLogger(MatchingService.class);

    private final LlmService llmService;
    private final ObjectMapper objectMapper;

    public MatchingService(LlmService llmService, ObjectMapper objectMapper) {
        this.llmService = llmService;
        this.objectMapper = objectMapper;
    }

    public JobDescription parseJobDescription(String jobDescriptionText, String model) {
        String prompt = buildJobParsePrompt(jobDescriptionText);
        return llmService.generateAndParse(model, prompt, JobDescription.class);
    }

    public MatchingResult match(CvData cvData, JobDescription jobDescription, String model) {
        String prompt = buildMatchingPrompt(cvData, jobDescription);
        return llmService.generateAndParse(model, prompt, MatchingResult.class);
    }

    private String buildJobParsePrompt(String jobText) {
        return """
            Extract structured information from the following job description.
            Return ONLY a valid JSON object with this exact structure, no extra text:
            {
              "title": "string",
              "company": "string",
              "description": "string",
              "requiredSkills": ["string"],
              "preferredSkills": ["string"],
              "responsibilities": ["string"],
              "requirements": ["string"]
            }
            
            Job Description:
            %s
            """.formatted(jobText);
    }

    private String buildMatchingPrompt(CvData cvData, JobDescription jobDescription) {
        String cvSkills;
        String jobRequiredSkills;
        String jobPreferredSkills;
        try {
            cvSkills = String.join(", ", cvData.skills() != null ? cvData.skills() : List.of());
            jobRequiredSkills = String.join(", ", jobDescription.requiredSkills() != null ? jobDescription.requiredSkills() : List.of());
            jobPreferredSkills = String.join(", ", jobDescription.preferredSkills() != null ? jobDescription.preferredSkills() : List.of());
        } catch (Exception e) {
            cvSkills = "";
            jobRequiredSkills = "";
            jobPreferredSkills = "";
        }

        return """
            Compare the candidate's CV skills with the job requirements.
            Return ONLY a valid JSON object with this exact structure, no extra text:
            {
              "score": 0.0,
              "matchedSkills": ["string"],
              "missingSkills": ["string"],
              "partialMatches": ["string"],
              "summary": "string"
            }
            
            Where "score" is a number between 0.0 and 100.0 representing the match percentage.
            
            Candidate Skills: %s
            
            Required Skills: %s
            
            Preferred Skills: %s
            
            Job Title: %s
            Job Description: %s
            """.formatted(cvSkills, jobRequiredSkills, jobPreferredSkills,
                    jobDescription.title(), jobDescription.description());
    }
}
