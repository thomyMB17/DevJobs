package com.example.devjobs.dto.response;

import com.example.devjobs.model.enums.ApplicationStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApplicationEventResponse {
    private ApplicationStatus fromStatus;
    private ApplicationStatus toStatus;
    private String note;
    private LocalDateTime changedAt;
}