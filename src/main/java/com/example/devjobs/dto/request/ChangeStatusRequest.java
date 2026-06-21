package com.example.devjobs.dto.request;

import com.example.devjobs.model.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeStatusRequest {

    @NotNull
    private ApplicationStatus status;

    private String note;
}
