package com.cvtailor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "optimization_jobs")
public class OptimizationJobEntity {

    public enum Status { PENDING, COMPLETED, FAILED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @Column(nullable = false, length = 64)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    private Double matchScore;

    @Column(columnDefinition = "TEXT")
    private String cvText;

    @Column(nullable = false, length = 64)
    private String cvTextHash;

    @Column(columnDefinition = "TEXT")
    private String jobDescriptionText;

    @Column(columnDefinition = "TEXT")
    private String optimizedCvJson;

    @Column(columnDefinition = "TEXT")
    private String matchingResultJson;

    @Column(columnDefinition = "TEXT")
    private String optimizationPlanJson;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = Status.PENDING;
        }
    }

    // Getters and setters

    public UUID getId() { return id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Double getMatchScore() { return matchScore; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }

    public String getCvText() { return cvText; }
    public void setCvText(String cvText) { this.cvText = cvText; }

    public String getCvTextHash() { return cvTextHash; }
    public void setCvTextHash(String cvTextHash) { this.cvTextHash = cvTextHash; }

    public String getJobDescriptionText() { return jobDescriptionText; }
    public void setJobDescriptionText(String jobDescriptionText) { this.jobDescriptionText = jobDescriptionText; }

    public String getOptimizedCvJson() { return optimizedCvJson; }
    public void setOptimizedCvJson(String optimizedCvJson) { this.optimizedCvJson = optimizedCvJson; }

    public String getMatchingResultJson() { return matchingResultJson; }
    public void setMatchingResultJson(String matchingResultJson) { this.matchingResultJson = matchingResultJson; }

    public String getOptimizationPlanJson() { return optimizationPlanJson; }
    public void setOptimizationPlanJson(String optimizationPlanJson) { this.optimizationPlanJson = optimizationPlanJson; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
