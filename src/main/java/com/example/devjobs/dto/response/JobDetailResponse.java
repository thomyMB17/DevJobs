package com.example.devjobs.dto.response;

import com.example.devjobs.model.enums.Modality;
import com.example.devjobs.model.enums.Seniority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobDetailResponse {

    private Long id;
    private String title;
    private String description;
    private String companyName;
    private String companyWebsite;
    private String location;
    private Modality modality;
    private Seniority seniority;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private boolean isActive;
    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;
    private List<String> technologies;
}
