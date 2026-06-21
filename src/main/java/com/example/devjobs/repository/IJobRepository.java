package com.example.devjobs.repository;

import com.example.devjobs.model.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IJobRepository extends JpaRepository<JobPosting, Long> {
}
