package com.example.devjobs.dto.request;

import com.example.devjobs.model.enums.Modality;
import com.example.devjobs.model.enums.Seniority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CreateJobRequest {

    @NotNull
    private Long companyId;

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    private String description;

    @Size(max = 150)
    private String location;

    @NotNull
    private Modality modality;

    @NotNull
    private Seniority seniority;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private LocalDateTime expiresAt;

    private List<@Size(max = 80) String> technologies;
}
