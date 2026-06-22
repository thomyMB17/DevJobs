package com.example.devjobs.service;

import com.example.devjobs.dto.request.CreateJobRequest;
import com.example.devjobs.dto.request.UpdateJobRequest;
import com.example.devjobs.dto.response.JobDetailResponse;
import com.example.devjobs.dto.response.JobSummaryResponse;
import com.example.devjobs.exception.ResourceNotFoundException;
import com.example.devjobs.exception.UnauthorizedActionException;
import com.example.devjobs.model.Company;
import com.example.devjobs.model.JobPosting;

import com.example.devjobs.model.enums.Modality;
import com.example.devjobs.model.enums.Seniority;
import com.example.devjobs.repository.ICompanyRepository;
import com.example.devjobs.repository.IJobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final IJobRepository iJobRepository;
    private final ICompanyRepository iCompanyRepository;

    //createJob(request, email) — busca el usuario, luego busca su empresa,
    // construye el JobPosting con las tecnologías y lo guarda.
    public JobDetailResponse createJob(CreateJobRequest request, String email){
       Company company = iCompanyRepository.findById(request.getCompanyId())
               .orElseThrow(()-> new ResourceNotFoundException("Compañia con id no existe"));
       if(!company.getOwner().getEmail().equals(email)){
           throw new UnauthorizedActionException("Solo el dueño de la compañia puede crear trabajos.");
       }
        JobPosting job = JobPosting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .company(company)
                .location(request.getLocation())
                .modality(request.getModality())
                .seniority(request.getSeniority())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .isActive(true)
                .publishedAt(LocalDateTime.now())
                .expiresAt(request.getExpiresAt())
                .technologies(request.getTechnologies())
                .build();
        JobPosting save = iJobRepository.save(job);
        return getJobDetailResponse(save);
    }
    //getAllJobs(filtros, pageable) — consulta la BD con los filtros que llegaron,
    // los que vengan nulos los ignora. Devuelve paginado.
    public Page<JobSummaryResponse> getAllJobs(String technology, Modality modality,
                                   Seniority seniority, String location,
                                   Pageable pageable){
        return iJobRepository.findAllWithFilters(technology, modality, seniority, location, pageable)
                .map(job -> JobSummaryResponse.builder()
                        .id(job.getId())
                        .title(job.getTitle())
                        .companyName(job.getCompany().getName())
                        .location(job.getLocation())
                        .modality(job.getModality())
                        .seniority(job.getSeniority())
                        .salaryMin(job.getSalaryMin())
                        .salaryMax(job.getSalaryMax())
                        .isActive(job.isActive())
                        .technologies(job.getTechnologies())
                        .publishedAt(job.getPublishedAt())
                        .build());
    }
    //getJobById(id) — busca por id, lanza ResourceNotFoundException si no existe.
    public JobDetailResponse getJobById(Long id){
        JobPosting job = iJobRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Trabajo no encontrado."));
        return getJobDetailResponse(job);
    }


    //updateJob(id, request, email) — busca la oferta, verifica que el empleador autenticado
    // sea el dueño de la empresa que la publicó, actualiza campos.
    public JobDetailResponse updateJob(Long id, UpdateJobRequest request, String email){
        JobPosting job = iJobRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Trabajo no encontrado"));
        if(!job.getCompany().getOwner().getEmail().equals(email)){
            throw new UnauthorizedActionException("Solo el dueño de esta compañia puede actualizar los trabajos.");
        }
        if (request.getTitle() != null) job.setTitle(request.getTitle());
        if (request.getDescription() != null) job.setDescription(request.getDescription());
        if (request.getLocation() != null) job.setLocation(request.getLocation());
        if (request.getModality() != null) job.setModality(request.getModality());
        if (request.getSeniority() != null) job.setSeniority(request.getSeniority());
        if (request.getSalaryMin() != null) job.setSalaryMin(request.getSalaryMin());
        if (request.getSalaryMax() != null) job.setSalaryMax(request.getSalaryMax());
        if (request.getExpiresAt() != null) job.setExpiresAt(request.getExpiresAt());
        if (request.getTechnologies() != null) job.setTechnologies(request.getTechnologies());

        iJobRepository.save(job);

        return getJobDetailResponse(job);
    }
    //deactivateJob(id, email) — busca la oferta, verifica dueño, pone isActive = false y guarda.
    // No elimina el registro.
    public JobDetailResponse deactivateJob(Long id, String email){
        JobPosting job = iJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajo no encontrado"));
        if(!job.getCompany().getOwner().getEmail().equals(email)){
            throw new UnauthorizedActionException("Solo el dueño de esta compañia puede desactivar los trabajos.");
        }
        job.setActive(false);
        iJobRepository.save(job);
        return getJobDetailResponse(job);
    }
    //getJobsByCompany(companyId) — busca todas las ofertas activas de una empresa específica.
    @Transactional(readOnly = true)
    public List<JobSummaryResponse> getJobsByCompany(Long companyId){
        Company company = iCompanyRepository.findById(companyId)
                .orElseThrow(()-> new ResourceNotFoundException("Compañia no encontrada"));
        return company.getJobPostings()
                .stream()
                .filter(job -> job.isActive())
                .map(job -> JobSummaryResponse.builder()
                        .id(job.getId())
                        .title(job.getTitle())
                        .companyName(job.getCompany().getName())
                        .location(job.getLocation())
                        .modality(job.getModality())
                        .seniority(job.getSeniority())
                        .salaryMin(job.getSalaryMin())
                        .salaryMax(job.getSalaryMax())
                        .publishedAt(job.getPublishedAt())
                        .build())
                .collect(Collectors.toList());
    }
    private JobDetailResponse getJobDetailResponse(JobPosting job) {
        return JobDetailResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .companyName(job.getCompany().getName())
                .companyWebsite(job.getCompany().getWebsite())
                .location(job.getLocation())
                .modality(job.getModality())
                .seniority(job.getSeniority())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .isActive(job.isActive())
                .publishedAt(job.getPublishedAt())
                .expiresAt(job.getExpiresAt())
                .technologies(job.getTechnologies())
                .build();
    }
}
