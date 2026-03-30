package com.cvtailor.model.response;

import com.cvtailor.entity.OptimizationJobEntity;
import com.cvtailor.model.domain.GeneratedCv;
import com.cvtailor.model.domain.MatchingResult;
import com.cvtailor.model.domain.OptimizationPlan;

import java.util.UUID;

public record OptimizationResponse(
    UUID jobId,
    OptimizationJobEntity.Status status,
    GeneratedCv optimizedCv,
    MatchingResult matchingResult,
    OptimizationPlan optimizationPlan,
    String rawCvText
) {}
