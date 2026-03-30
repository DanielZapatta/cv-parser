package com.cvtailor.service;

import com.cvtailor.model.domain.CvData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ParserService {

    private static final Logger log = LoggerFactory.getLogger(ParserService.class);

    private final LlmService llmService;
    private final ObjectMapper objectMapper;

    public ParserService(LlmService llmService, ObjectMapper objectMapper) {
        this.llmService = llmService;
        this.objectMapper = objectMapper;
    }

    public String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.info("Extracted {} characters from PDF", text.length());
            return text;
        }
    }

    public CvData parseCvText(String cvText, String model) {
        String prompt = buildParsePrompt(cvText);
        return llmService.generateAndParse(model, prompt, CvData.class);
    }

    private String buildParsePrompt(String cvText) {
        return """
            Extract structured information from the following CV/resume text.
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
            
            CV Text:
            %s
            """.formatted(cvText);
    }
}
