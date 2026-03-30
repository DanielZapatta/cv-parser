package com.cvtailor.model.response;

import com.cvtailor.model.domain.CvData;

public record ParseResponse(CvData cvData, String rawText) {}
