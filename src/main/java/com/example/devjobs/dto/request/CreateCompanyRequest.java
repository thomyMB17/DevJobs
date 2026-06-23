package com.example.devjobs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCompanyRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String name;
    @Size(max = 100)
    private String industry;
    @Size(max = 255)
    private String website;
    private String description;
    @Size(max = 150)
    private String location;
}
