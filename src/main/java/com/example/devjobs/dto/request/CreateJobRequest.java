package com.example.devjobs.dto.request;

import com.example.devjobs.model.enums.Modality;
import com.example.devjobs.model.enums.Seniority;
import jakarta.validation.constraints.AssertTrue;
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

    @NotNull(message = "La compañía es obligatoria")
    private Long companyId;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @Size(max = 150, message = "La ubicación no puede exceder 150 caracteres")
    private String location;

    @NotNull(message = "La modalidad es obligatoria")
    private Modality modality;

    @NotNull(message = "La seniority es obligatoria")
    private Seniority seniority;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private LocalDateTime expiresAt;

    private List<@Size(max = 80, message = "Cada tecnología no puede exceder 80 caracteres") String> technologies;

    @AssertTrue(message = "El salario máximo debe ser mayor o igual al salario mínimo")
    public boolean isSalaryValid() {
        if (salaryMin == null || salaryMax == null) return true;
        return salaryMax.compareTo(salaryMin) >= 0;
    }
}
