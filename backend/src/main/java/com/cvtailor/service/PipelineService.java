package com.cvtailor.service;

import com.cvtailor.model.domain.CvData;
import com.cvtailor.model.domain.GeneratedCv;
import com.cvtailor.model.domain.JobDescription;
import com.cvtailor.model.domain.MatchingResult;
import com.cvtailor.model.domain.OptimizationPlan;
import com.cvtailor.model.response.OptimizationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PipelineService {

    private static final Logger log = LoggerFactory.getLogger(PipelineService.class);

    private final ParserService parserService;
    private final MatchingService matchingService;
    private final OptimizationService optimizationService;
    private final CvGeneratorService cvGeneratorService;
    private final ValidatorService validatorService;

    public PipelineService(
            ParserService parserService,
            MatchingService matchingService,
            OptimizationService optimizationService,
            CvGeneratorService cvGeneratorService,
            ValidatorService validatorService) {
        this.parserService = parserService;
        this.matchingService = matchingService;
        this.optimizationService = optimizationService;
        this.cvGeneratorService = cvGeneratorService;
        this.validatorService = validatorService;
    }

    public OptimizationResponse run(String cvText, String jobDescriptionText, String model) {
        log.info("Starting CV optimization pipeline with model={}", model);

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

        log.info("Pipeline completed successfully");

        return new OptimizationResponse(finalCv, matchingResult, optimizationPlan, cvText);
    }
}
