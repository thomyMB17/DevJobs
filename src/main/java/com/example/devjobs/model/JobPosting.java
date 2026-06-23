package com.example.devjobs.model;

import com.example.devjobs.model.enums.Modality;
import com.example.devjobs.model.enums.Seniority;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_postings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(length = 150)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Modality modality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Seniority seniority;

    @Column(name = "salary_min", precision = 12, scale = 2)
    private BigDecimal salaryMin;

    @Column(name = "salary_max", precision = 12, scale = 2)
    private BigDecimal salaryMax;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "published_at", updatable = false)
    private LocalDateTime publishedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @ElementCollection
    @CollectionTable(
            name = "job_technologies",
            joinColumns = @JoinColumn(name = "job_id")
    )
    @Column(name = "technology", length = 80)
    @Builder.Default
    private List<String> technologies = new ArrayList<>();

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    @PrePersist
    protected void onCreate() { publishedAt = LocalDateTime.now(); }
}
