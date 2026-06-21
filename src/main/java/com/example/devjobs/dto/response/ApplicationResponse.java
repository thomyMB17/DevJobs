package com.example.devjobs.dto.response;

import com.example.devjobs.model.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationResponse {

    private Long id;
    private String candidateName;
    private String jobTitle;
    private ApplicationStatus status;
    private String coverLetter;
    private String cvUrl;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
