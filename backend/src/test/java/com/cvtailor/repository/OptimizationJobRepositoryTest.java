package com.cvtailor.repository;

import com.cvtailor.entity.OptimizationJobEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OptimizationJobRepositoryTest {

    @Autowired
    private OptimizationJobRepository repository;

    @Test
    void save_andFindById_shouldPersistEntity() {
        OptimizationJobEntity job = new OptimizationJobEntity();
        job.setModel("qwen2.5:7b");
        job.setCvText("Test CV");
        job.setCvTextHash("abc123");
        job.setJobDescriptionText("Test JD");

        OptimizationJobEntity saved = repository.save(job);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(OptimizationJobEntity.Status.PENDING);
        assertThat(saved.getCreatedAt()).isNotNull();

        OptimizationJobEntity found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found.getModel()).isEqualTo("qwen2.5:7b");
        assertThat(found.getCvTextHash()).isEqualTo("abc123");
    }

    @Test
    void findAllByOrderByCreatedAtDesc_shouldReturnPagedResults() {
        for (int i = 0; i < 3; i++) {
            OptimizationJobEntity job = new OptimizationJobEntity();
            job.setModel("llama3");
            job.setCvText("CV " + i);
            job.setCvTextHash("hash" + i);
            job.setJobDescriptionText("JD " + i);
            repository.save(job);
        }

        Page<OptimizationJobEntity> page =
                repository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(3);
    }

    @Test
    void findByStatus_shouldFilterCorrectly() {
        OptimizationJobEntity pending = new OptimizationJobEntity();
        pending.setModel("qwen2.5:7b");
        pending.setCvText("CV");
        pending.setCvTextHash("h1");
        pending.setJobDescriptionText("JD");
        repository.save(pending);

        OptimizationJobEntity completed = new OptimizationJobEntity();
        completed.setModel("qwen2.5:7b");
        completed.setCvText("CV2");
        completed.setCvTextHash("h2");
        completed.setJobDescriptionText("JD2");
        completed.setStatus(OptimizationJobEntity.Status.COMPLETED);
        repository.save(completed);

        Page<OptimizationJobEntity> completedPage = repository.findByStatusOrderByCreatedAtDesc(
                OptimizationJobEntity.Status.COMPLETED, PageRequest.of(0, 10));
        assertThat(completedPage.getContent()).hasSize(1);
        assertThat(completedPage.getContent().get(0).getStatus())
                .isEqualTo(OptimizationJobEntity.Status.COMPLETED);
    }
}
