package com.example.devjobs.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.devjobs.model.enums.Modality;
import com.example.devjobs.model.enums.Seniority;
import com.example.devjobs.model.JobPosting;
import org.springframework.stereotype.Repository;

@Repository
public interface IJobRepository extends JpaRepository<JobPosting, Long> {

    @Query("""
    SELECT j FROM JobPosting j
    WHERE j.isActive = true
    AND (:technology IS NULL OR :technology MEMBER OF j.technologies)
    AND (:modality IS NULL OR j.modality = :modality)
    AND (:seniority IS NULL OR j.seniority = :seniority)
    AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
    """)
    Page<JobPosting> findAllWithFilters(
            @Param("technology") String technology,
            @Param("modality") Modality modality,
            @Param("seniority") Seniority seniority,
            @Param("location") String location,
            Pageable pageable
    );

    List<JobPosting> findByCompany_IdAndIsActiveTrue(Long companyId);
}
