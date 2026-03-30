package com.cvtailor.controller;

import com.cvtailor.entity.OptimizationJobEntity;
import com.cvtailor.model.domain.CvData;
import com.cvtailor.model.request.OptimizationRequest;
import com.cvtailor.model.response.OptimizationResponse;
import com.cvtailor.model.response.ParseResponse;
import com.cvtailor.repository.OptimizationJobRepository;
import com.cvtailor.service.ParserService;
import com.cvtailor.service.PipelineService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CvOptimizationController {

    private static final Logger log = LoggerFactory.getLogger(CvOptimizationController.class);

    private final PipelineService pipelineService;
    private final ParserService parserService;
    private final OptimizationJobRepository jobRepository;

    public CvOptimizationController(
            PipelineService pipelineService,
            ParserService parserService,
            OptimizationJobRepository jobRepository) {
        this.pipelineService = pipelineService;
        this.parserService = parserService;
        this.jobRepository = jobRepository;
    }

    @PostMapping("/optimize")
    public ResponseEntity<OptimizationResponse> optimize(@Valid @RequestBody OptimizationRequest request) {
        log.info("Received optimization request");
        OptimizationResponse response = pipelineService.run(
                request.cvText(),
                request.jobDescription(),
                request.modelName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/parse-pdf")
    public ResponseEntity<ParseResponse> parsePdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "model", defaultValue = "qwen2.5:7b") String model) throws IOException {
        log.info("Received PDF parse request: {}", file.getOriginalFilename());
        String text = parserService.extractTextFromPdf(file);
        CvData cvData = parserService.parseCvText(text, model);
        return ResponseEntity.ok(new ParseResponse(cvData, text));
    }

    @GetMapping("/optimizations")
    public ResponseEntity<Page<OptimizationJobEntity>> listOptimizations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<OptimizationJobEntity> jobs =
                jobRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/optimizations/{id}")
    public ResponseEntity<OptimizationJobEntity> getOptimization(@PathVariable UUID id) {
        return jobRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("CV Tailor AI is running");
    }
}
