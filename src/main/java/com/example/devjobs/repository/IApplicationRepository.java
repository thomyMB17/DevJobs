package com.example.devjobs.repository;

import com.example.devjobs.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByCandidateIdAndJobPostingId(Long userId, Long jobId);

    Page<Application> findByCandidate_Id(Long id, Pageable pageable);

    Page<Application> findByJobPosting_Id(Long id, Pageable pageable);
}
