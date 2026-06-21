package com.example.devjobs.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCompanyRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String industry;
    private String website;
    private String description;
    private String location;
}
