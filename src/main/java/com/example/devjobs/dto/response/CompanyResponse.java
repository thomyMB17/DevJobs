package com.example.devjobs.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta con datos de una compañía")
public class CompanyResponse {
    @Schema(description = "ID de la compañía", example = "1")
    private Long id;

    @Schema(description = "Nombre de la compañía", example = "Tech Corp")
    private String name;

    @Schema(description = "Industria", example = "Software")
    private String industry;

    @Schema(description = "Sitio web", example = "https://techcorp.com")
    private String website;

    @Schema(description = "Descripción", example = "Empresa de software")
    private String description;

    @Schema(description = "Ubicación", example = "Santiago, Chile")
    private String location;

    @Schema(description = "Nombre del dueño", example = "Juan Pérez")
    private String ownerFullName;

    @Schema(description = "Fecha de creación", example = "2026-01-01T12:00:00")
    private LocalDateTime createdAt;
}