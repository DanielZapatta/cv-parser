package com.cvtailor.model.response;

import com.cvtailor.model.domain.GeneratedCv;
import com.cvtailor.model.domain.MatchingResult;
import com.cvtailor.model.domain.OptimizationPlan;

public record OptimizationResponse(
    GeneratedCv optimizedCv,
    MatchingResult matchingResult,
    OptimizationPlan optimizationPlan,
    String rawCvText
) {}
