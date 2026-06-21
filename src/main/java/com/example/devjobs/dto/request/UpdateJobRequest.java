package com.example.devjobs.dto.request;

import com.example.devjobs.model.enums.Modality;
import com.example.devjobs.model.enums.Seniority;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateJobRequest {

    private String title;
    private String description;
    private String location;
    private Modality modality;
    private Seniority seniority;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private LocalDateTime expiresAt;
    private List<String> technologies;
}