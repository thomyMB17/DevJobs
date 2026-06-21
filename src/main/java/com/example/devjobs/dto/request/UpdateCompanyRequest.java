package com.example.devjobs.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompanyRequest {

    private String name;
    private String industry;
    private String website;
    private String description;
    private String location;
}
