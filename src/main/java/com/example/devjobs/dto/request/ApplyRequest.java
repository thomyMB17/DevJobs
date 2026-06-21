package com.example.devjobs.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyRequest {

    private String coverLetter;
    private String cvUrl;
}
