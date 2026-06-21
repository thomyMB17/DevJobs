package com.example.devjobs.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponse {
    private Long id;
    private String name;
    private String industry;
    private String website;
    private String description;
    private String location;
    private String ownerFullName;
    private LocalDateTime createdAt;
}