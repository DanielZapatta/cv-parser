package com.cvtailor.controller;

import com.cvtailor.model.domain.CvData;
import com.cvtailor.model.request.OptimizationRequest;
import com.cvtailor.model.response.OptimizationResponse;
import com.cvtailor.model.response.ParseResponse;
import com.cvtailor.service.ParserService;
import com.cvtailor.service.PipelineService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class CvOptimizationController {

    private static final Logger log = LoggerFactory.getLogger(CvOptimizationController.class);

    private final PipelineService pipelineService;
    private final ParserService parserService;

    public CvOptimizationController(PipelineService pipelineService, ParserService parserService) {
        this.pipelineService = pipelineService;
        this.parserService = parserService;
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

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("CV Tailor AI is running");
    }
}
