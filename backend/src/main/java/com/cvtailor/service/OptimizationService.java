package com.cvtailor.service;

import com.cvtailor.model.domain.CvData;
import com.cvtailor.model.domain.JobDescription;
import com.cvtailor.model.domain.MatchingResult;
import com.cvtailor.model.domain.OptimizationPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OptimizationService {

    private static final Logger log = LoggerFactory.getLogger(OptimizationService.class);

    private final LlmService llmService;

    public OptimizationService(LlmService llmService) {
        this.llmService = llmService;
    }

    public OptimizationPlan createOptimizationPlan(
            CvData cvData,
            JobDescription jobDescription,
            MatchingResult matchingResult,
            String model) {

        String prompt = buildOptimizationPrompt(cvData, jobDescription, matchingResult);
        return llmService.generateAndParse(model, prompt, OptimizationPlan.class);
    }

    private String buildOptimizationPrompt(CvData cvData, JobDescription jobDescription, MatchingResult matchingResult) {
        String missingSkills = matchingResult.missingSkills() != null
                ? String.join(", ", matchingResult.missingSkills()) : "";
        String matchedSkills = matchingResult.matchedSkills() != null
                ? String.join(", ", matchingResult.matchedSkills()) : "";

        return """
            Create a strategic optimization plan for improving this CV for the target job.
            Return ONLY a valid JSON object with this exact structure, no extra text:
            {
              "objective": "string",
              "keywordsToAdd": ["string"],
              "skillsToHighlight": ["string"],
              "experienceAdjustments": ["string"],
              "summaryRewrite": "string",
              "generalRecommendations": ["string"]
            }
            
            Current CV Summary: %s
            Matched Skills: %s
            Missing Skills: %s
            Current Match Score: %.1f%%
            
            Target Job: %s at %s
            Job Description: %s
            
            Create a plan to maximize ATS compatibility and recruiter appeal.
            """.formatted(
                cvData.summary() != null ? cvData.summary() : "",
                matchedSkills,
                missingSkills,
                matchingResult.score(),
                jobDescription.title() != null ? jobDescription.title() : "",
                jobDescription.company() != null ? jobDescription.company() : "",
                jobDescription.description() != null ? jobDescription.description() : "");
    }
}
