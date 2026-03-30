package com.cvtailor.repository;

import com.cvtailor.entity.OptimizationJobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OptimizationJobRepository extends JpaRepository<OptimizationJobEntity, UUID> {

    Page<OptimizationJobEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<OptimizationJobEntity> findByStatusOrderByCreatedAtDesc(
            OptimizationJobEntity.Status status, Pageable pageable);
}
