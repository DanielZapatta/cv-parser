package com.cvtailor.service;

import com.cvtailor.entity.OptimizationJobEntity;
import com.cvtailor.model.domain.CvData;
import com.cvtailor.model.domain.GeneratedCv;
import com.cvtailor.model.domain.JobDescription;
import com.cvtailor.model.domain.MatchingResult;
import com.cvtailor.model.domain.OptimizationPlan;
import com.cvtailor.model.response.OptimizationResponse;
import com.cvtailor.repository.OptimizationJobRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class PipelineService {

    private static final Logger log = LoggerFactory.getLogger(PipelineService.class);

    private final ParserService parserService;
    private final MatchingService matchingService;
    private final OptimizationService optimizationService;
    private final CvGeneratorService cvGeneratorService;
    private final ValidatorService validatorService;
    private final OptimizationJobRepository jobRepository;
    private final ObjectMapper objectMapper;

    public PipelineService(
            ParserService parserService,
            MatchingService matchingService,
            OptimizationService optimizationService,
            CvGeneratorService cvGeneratorService,
            ValidatorService validatorService,
            OptimizationJobRepository jobRepository,
            ObjectMapper objectMapper) {
        this.parserService = parserService;
        this.matchingService = matchingService;
        this.optimizationService = optimizationService;
        this.cvGeneratorService = cvGeneratorService;
        this.validatorService = validatorService;
        this.jobRepository = jobRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OptimizationResponse run(String cvText, String jobDescriptionText, String model) {
        log.info("Starting CV optimization pipeline with model={}", model);

        OptimizationJobEntity job = new OptimizationJobEntity();
        job.setModel(model);
        job.setCvText(cvText);
        job.setCvTextHash(sha256(cvText));
        job.setJobDescriptionText(jobDescriptionText);
        job.setStatus(OptimizationJobEntity.Status.PENDING);
        job = jobRepository.save(job);
        log.info("Created optimization job id={}", job.getId());

        try {
            log.info("Step 1: Parsing CV...");
            CvData cvData = parserService.parseCvText(cvText, model);

            log.info("Step 2: Parsing job description...");
            JobDescription jobDescription = matchingService.parseJobDescription(jobDescriptionText, model);

            log.info("Step 3: Matching CV against job...");
            MatchingResult matchingResult = matchingService.match(cvData, jobDescription, model);
            log.info("Match score: {}", matchingResult.score());

            log.info("Step 4: Creating optimization plan...");
            OptimizationPlan optimizationPlan = optimizationService.createOptimizationPlan(
                    cvData, jobDescription, matchingResult, model);

            log.info("Step 5: Generating optimized CV...");
            GeneratedCv rawGeneratedCv = cvGeneratorService.generateOptimizedCv(
                    cvData, jobDescription, optimizationPlan, model);

            log.info("Step 6: Validating and normalizing...");
            GeneratedCv finalCv = validatorService.validateAndNormalize(rawGeneratedCv, cvData);

            job.setStatus(OptimizationJobEntity.Status.COMPLETED);
            job.setCompletedAt(LocalDateTime.now());
            job.setMatchScore(matchingResult.score());
            job.setOptimizedCvJson(toJson(finalCv));
            job.setMatchingResultJson(toJson(matchingResult));
            job.setOptimizationPlanJson(toJson(optimizationPlan));
            jobRepository.save(job);

            log.info("Pipeline completed successfully for job id={}", job.getId());

            return new OptimizationResponse(
                    job.getId(),
                    OptimizationJobEntity.Status.COMPLETED,
                    finalCv,
                    matchingResult,
                    optimizationPlan,
                    cvText);

        } catch (Exception ex) {
            log.error("Pipeline failed for job id={}: {}", job.getId(), ex.getMessage(), ex);
            job.setStatus(OptimizationJobEntity.Status.FAILED);
            job.setErrorMessage(ex.getMessage());
            job.setCompletedAt(LocalDateTime.now());
            jobRepository.save(job);
            throw ex;
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.warn("Failed to serialize object to JSON: {}", e.getMessage());
            return "{}";
        }
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            return "unknown";
        }
    }
}
