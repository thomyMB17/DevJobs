package com.example.devjobs.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyRequest {

    private String coverLetter;
    @Size(max = 500, message = "La URL del CV no puede exceder 500 caracteres")
    private String cvUrl;
}
