package com.example.devjobs.repository;

import com.example.devjobs.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByCandidateIdAndJobPostingId(Long userId, Long jobId);

    List<Application> findByCandidate_Id(Long id);

    List<Application> findByJobPosting_Id(Long id);
}
