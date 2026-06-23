package com.example.devjobs.dto.request;

import com.example.devjobs.model.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeStatusRequest {

    @NotNull(message = "El estado es obligatorio")
    private ApplicationStatus status;

    @Size(max = 500, message = "La nota no puede exceder 500 caracteres")
    private String note;
}
