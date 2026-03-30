package com.cvtailor.service;

import com.cvtailor.model.domain.CvData;
import com.cvtailor.model.domain.GeneratedCv;
import com.cvtailor.model.domain.JobDescription;
import com.cvtailor.model.domain.OptimizationPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CvGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(CvGeneratorService.class);

    private final LlmService llmService;

    public CvGeneratorService(LlmService llmService) {
        this.llmService = llmService;
    }

    public GeneratedCv generateOptimizedCv(
            CvData cvData,
            JobDescription jobDescription,
            OptimizationPlan plan,
            String model) {

        String prompt = buildGenerationPrompt(cvData, jobDescription, plan);
        return llmService.generateAndParse(model, prompt, GeneratedCv.class);
    }

    private String buildGenerationPrompt(CvData cvData, JobDescription jobDescription, OptimizationPlan plan) {
        String keywordsToAdd = plan.keywordsToAdd() != null ? String.join(", ", plan.keywordsToAdd()) : "";
        String skillsToHighlight = plan.skillsToHighlight() != null ? String.join(", ", plan.skillsToHighlight()) : "";

        return """
            Generate an optimized CV based on the original data and optimization plan.
            Return ONLY a valid JSON object with this exact structure, no extra text:
            {
              "fullName": "string",
              "email": "string",
              "phone": "string",
              "location": "string",
              "summary": "string",
              "experiences": [
                {
                  "company": "string",
                  "title": "string",
                  "startDate": "string",
                  "endDate": "string",
                  "description": "string",
                  "achievements": ["string"]
                }
              ],
              "educations": [
                {
                  "institution": "string",
                  "degree": "string",
                  "field": "string",
                  "startDate": "string",
                  "endDate": "string"
                }
              ],
              "skills": ["string"],
              "languages": ["string"],
              "certifications": ["string"]
            }
            
            Original CV Data:
            - Name: %s
            - Email: %s
            - Phone: %s
            - Location: %s
            - Current Summary: %s
            
            Optimization Plan:
            - Objective: %s
            - Keywords to Add: %s
            - Skills to Highlight: %s
            - New Summary: %s
            
            Target Job: %s
            
            Instructions:
            1. Keep all factual information (dates, companies, education) accurate
            2. Rewrite descriptions to emphasize relevant experience
            3. Add the keywords naturally to achieve ATS optimization
            4. Highlight the requested skills prominently
            5. Use the provided new summary
            """.formatted(
                cvData.fullName(), cvData.email(), cvData.phone(), cvData.location(),
                cvData.summary() != null ? cvData.summary() : "",
                plan.objective() != null ? plan.objective() : "",
                keywordsToAdd,
                skillsToHighlight,
                plan.summaryRewrite() != null ? plan.summaryRewrite() : "",
                jobDescription.title() != null ? jobDescription.title() : "");
    }
}
