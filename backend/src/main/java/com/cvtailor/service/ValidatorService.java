package com.cvtailor.service;

import com.cvtailor.model.domain.CvData;
import com.cvtailor.model.domain.GeneratedCv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidatorService {

    private static final Logger log = LoggerFactory.getLogger(ValidatorService.class);

    public GeneratedCv validateAndNormalize(GeneratedCv generatedCv, CvData originalCv) {
        if (generatedCv == null) {
            log.warn("GeneratedCv is null, falling back to original");
            return fallbackToOriginal(originalCv);
        }

        String fullName = normalize(generatedCv.fullName(), originalCv.fullName());
        String email = normalize(generatedCv.email(), originalCv.email());
        String phone = normalize(generatedCv.phone(), originalCv.phone());
        String location = normalize(generatedCv.location(), originalCv.location());
        String summary = normalize(generatedCv.summary(), originalCv.summary());

        List<CvData.Experience> experiences = generatedCv.experiences() != null && !generatedCv.experiences().isEmpty()
                ? generatedCv.experiences()
                : (originalCv.experiences() != null ? originalCv.experiences() : List.of());

        List<CvData.Education> educations = generatedCv.educations() != null && !generatedCv.educations().isEmpty()
                ? generatedCv.educations()
                : (originalCv.educations() != null ? originalCv.educations() : List.of());

        List<String> skills = generatedCv.skills() != null && !generatedCv.skills().isEmpty()
                ? generatedCv.skills()
                : (originalCv.skills() != null ? originalCv.skills() : List.of());

        List<String> languages = generatedCv.languages() != null ? generatedCv.languages()
                : (originalCv.languages() != null ? originalCv.languages() : List.of());

        List<String> certifications = generatedCv.certifications() != null ? generatedCv.certifications()
                : (originalCv.certifications() != null ? originalCv.certifications() : List.of());

        log.info("CV validated and normalized for: {}", fullName);

        return new GeneratedCv(
                fullName, email, phone, location, summary,
                experiences, educations, skills, languages, certifications
        );
    }

    private String normalize(String generated, String original) {
        if (generated != null && !generated.isBlank()) {
            return generated.trim();
        }
        return original != null ? original.trim() : "";
    }

    private GeneratedCv fallbackToOriginal(CvData original) {
        return new GeneratedCv(
                original.fullName(),
                original.email(),
                original.phone(),
                original.location(),
                original.summary(),
                original.experiences() != null ? original.experiences() : List.of(),
                original.educations() != null ? original.educations() : List.of(),
                original.skills() != null ? original.skills() : List.of(),
                original.languages() != null ? original.languages() : List.of(),
                original.certifications() != null ? original.certifications() : List.of()
        );
    }
}
