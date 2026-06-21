package com.example.devjobs.repository;

import com.example.devjobs.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IApplicationRepository extends JpaRepository<Application, Long> {
}
