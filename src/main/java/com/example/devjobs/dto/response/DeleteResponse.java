package com.example.devjobs.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta de eliminación")
public class DeleteResponse {
    @Schema(description = "Mensaje de confirmación", example = "Recurso eliminado exitosamente")
    private String message;
}
