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
import com.example.devjobs.repository.IUserRepository;

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

    private final IJobRepository jobRepository;
    private final ICompanyRepository companyRepository;
    private final IUserRepository userRepository;
    private final SecurityService securityService;

    //createJob(request, email) — busca la empresa, verifica permisos,
    // construye el JobPosting con las tecnologías y lo guarda.
    @Transactional
    public JobDetailResponse createJob(CreateJobRequest request, String email){
        log.info("Creando trabajo por usuario: {}", email);
        Company company = companyRepository.findById(request.getCompanyId())
               .orElseThrow(()-> {
                   log.warn("Compañia no encontrada al crear trabajo - ID: {}", request.getCompanyId());
                   return new ResourceNotFoundException("Compañia con id no existe");
               });
       if(!securityService.isAdmin(email) && !company.getOwner().getEmail().equals(email)){
           log.warn("Usuario {} no autorizado para crear trabajo en compañia ID: {}", email, request.getCompanyId());
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
        JobPosting save = jobRepository.save(job);
        return getJobDetailResponse(save);
    }
    //getAllJobs(filtros, pageable) — consulta la BD con los filtros que llegaron,
    // los que vengan nulos los ignora. Devuelve paginado.
    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> getAllJobs(String technology, Modality modality,
                                   Seniority seniority, String location,
                                   Pageable pageable){
        log.info("Buscando trabajos con filtros - technology: {}, modality: {}, seniority: {}, location: {}",
                technology, modality, seniority, location);
        return jobRepository.findAllWithFilters(technology, modality, seniority, location, pageable)
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
        log.info("Buscando trabajo por ID: {}", id);
        JobPosting job = jobRepository.findById(id).orElseThrow(
                ()-> {
                    log.warn("Trabajo no encontrado - ID: {}", id);
                    return new ResourceNotFoundException("Trabajo no encontrado.");
                });
        return getJobDetailResponse(job);
    }


    //updateJob(id, request, email) — busca la oferta, verifica que el empleador autenticado
    // sea el dueño de la empresa que la publicó, actualiza campos.
    @Transactional
    public JobDetailResponse updateJob(Long id, UpdateJobRequest request, String email){
        log.info("Actualizando trabajo ID: {} por usuario: {}", id, email);
        JobPosting job = jobRepository.findById(id)
                .orElseThrow(()-> {
                    log.warn("Trabajo no encontrado al actualizar - ID: {}", id);
                    return new ResourceNotFoundException("Trabajo no encontrado");
                });
        if(!securityService.isAdmin(email) && !job.getCompany().getOwner().getEmail().equals(email)){
            log.warn("Usuario {} no autorizado para actualizar trabajo ID: {}", email, id);
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

        jobRepository.save(job);

        return getJobDetailResponse(job);
    }
    //deactivateJob(id, email) — busca la oferta, verifica dueño, pone isActive = false y guarda.
    // No elimina el registro.
    @Transactional
    public JobDetailResponse deactivateJob(Long id, String email){
        log.info("Desactivando trabajo ID: {} por usuario: {}", id, email);
        JobPosting job = jobRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Trabajo no encontrado al desactivar - ID: {}", id);
                    return new ResourceNotFoundException("Trabajo no encontrado");
                });
        if(!securityService.isAdmin(email) && !job.getCompany().getOwner().getEmail().equals(email)){
            log.warn("Usuario {} no autorizado para desactivar trabajo ID: {}", email, id);
            throw new UnauthorizedActionException("Solo el dueño de esta compañia puede desactivar los trabajos.");
        }
        job.setActive(false);
        jobRepository.save(job);
        return getJobDetailResponse(job);
    }
    @Transactional
    public JobDetailResponse activateJob(Long id, String email){
        log.info("Activando trabajo ID: {} por usuario: {}", id, email);
        JobPosting job = jobRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Trabajo no encontrado al activar - ID: {}", id);
                    return new ResourceNotFoundException("Trabajo no encontrado");
                });
        if(!securityService.isAdmin(email) && !job.getCompany().getOwner().getEmail().equals(email)){
            log.warn("Usuario {} no autorizado para activar trabajo ID: {}", email, id);
            throw new UnauthorizedActionException("Solo el dueño de esta compañia puede activar los trabajos.");
        }
        job.setActive(true);
        jobRepository.save(job);
        return getJobDetailResponse(job);
    }
    //getJobsByCompany(companyId) — busca todas las ofertas activas de una empresa específica.
    @Transactional(readOnly = true)
    public List<JobSummaryResponse> getJobsByCompany(Long companyId){
        log.info("Buscando trabajos activos de compañia ID: {}", companyId);
        return jobRepository.findByCompany_IdAndIsActiveTrue(companyId)
                .stream()
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
