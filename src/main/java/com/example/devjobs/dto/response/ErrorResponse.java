package com.example.devjobs.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Respuesta de error estándar")
public class ErrorResponse {
    @Schema(description = "Código HTTP", example = "404")
    private int status;

    @Schema(description = "Mensaje descriptivo del error", example = "Recurso no encontrado")
    private String message;

    @Schema(description = "Ruta del endpoint", example = "/api/v1/company/getById/99")
    private String path;

    @Schema(description = "Marca de tiempo", example = "2026-06-22T12:00:00")
    private LocalDateTime timestamp;
}
