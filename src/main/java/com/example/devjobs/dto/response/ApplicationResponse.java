package com.example.devjobs.dto.response;

import com.example.devjobs.model.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApplicationResponse {

    private Long id;
    private String jobTitle;
    private String companyName;
    private ApplicationStatus status;
    private String coverLetter;
    private String cvUrl;
    private LocalDateTime appliedAt;
}
