package com.example.devjobs.service;

import com.example.devjobs.dto.request.ApplyRequest;
import com.example.devjobs.dto.request.ChangeStatusRequest;
import com.example.devjobs.dto.response.ApplicationEventResponse;
import com.example.devjobs.dto.response.ApplicationResponse;
import com.example.devjobs.exception.DuplicateApplicationException;
import com.example.devjobs.exception.ResourceNotFoundException;
import com.example.devjobs.exception.UnauthorizedActionException;
import com.example.devjobs.model.Application;
import com.example.devjobs.model.JobPosting;
import com.example.devjobs.model.User;
import com.example.devjobs.model.enums.ApplicationStatus;
import com.example.devjobs.model.enums.Role;
import com.example.devjobs.repository.IApplicationRepository;
import com.example.devjobs.repository.IJobRepository;
import com.example.devjobs.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final IApplicationRepository applicationRepository;
    private final IJobRepository jobRepository;
    private final IUserRepository userRepository;
    private final SecurityService securityService;


    @Transactional
    public ApplicationResponse apply(Long jobId, ApplyRequest request, String email){
        log.info("Aplicando a trabajo ID: {} por usuario: {}", jobId, email);
        User candidato = userRepository.findByEmail(email)
                .orElseThrow(()-> {
                    log.warn("Usuario no encontrado al aplicar: {}", email);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });
        if(candidato.getRole() != Role.CANDIDATE && !securityService.isAdmin(email)){
            log.warn("Usuario {} con rol {} no puede postularse", email, candidato.getRole());
            throw new UnauthorizedActionException("Solo los candidatos pueden postularse.");
        }
        JobPosting job = jobRepository.findById(jobId)
                .orElseThrow(() -> {
                    log.warn("Trabajo no encontrado al aplicar - ID: {}", jobId);
                    return new ResourceNotFoundException("Trabajo no encontrado");
                });
        if(!job.isActive()){
            log.warn("Intento de aplicar a trabajo inactivo ID: {} por usuario: {}", jobId, email);
            throw new UnauthorizedActionException("Oferta de trabajo no disponible.");
        }
        if(applicationRepository.existsByCandidateIdAndJobPostingId(candidato.getId(), jobId)){
            log.warn("Postulación duplicada - usuario: {}, trabajo ID: {}", email, jobId);
            throw new DuplicateApplicationException("No puedes postular 2 veces a la misma oferta.");
        }
        Application apply = Application.builder()
                .candidate(candidato)
                .jobPosting(job)
                .status(ApplicationStatus.PENDING)
                .coverLetter(request.getCoverLetter())
                .cvUrl(request.getCvUrl())
                .build();
        Application save = applicationRepository.save(apply);
        return ApplicationResponse.builder()
                .id(save.getId())
                .jobTitle(save.getJobPosting().getTitle())
                .companyName(save.getJobPosting().getCompany().getName())
                .status(save.getStatus())
                .coverLetter(save.getCoverLetter())
                .cvUrl(save.getCvUrl())
                .appliedAt(save.getAppliedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications(String email){
        log.info("Obteniendo aplicaciones del usuario: {}", email);
        User candidato = userRepository.findByEmail(email)
                .orElseThrow(()-> {
                    log.warn("Usuario no encontrado al obtener aplicaciones: {}", email);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });
        return applicationRepository.findByCandidate_Id(candidato.getId())
                .stream()
                .map(app -> ApplicationResponse.builder()
                        .id(app.getId())
                        .jobTitle(app.getJobPosting().getTitle())
                        .companyName(app.getJobPosting().getCompany().getName())
                        .status(app.getStatus())
                        .coverLetter(app.getCoverLetter())
                        .cvUrl(app.getCvUrl())
                        .appliedAt(app.getAppliedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByJob(Long jobId, String email){
        log.info("Obteniendo aplicaciones del trabajo ID: {} por usuario: {}", jobId, email);
        JobPosting job = jobRepository.findById(jobId)
                .orElseThrow(()-> {
                    log.warn("Trabajo no encontrado al obtener postulaciones - ID: {}", jobId);
                    return new ResourceNotFoundException("No existe trabajo con este id");
                });
        if(!securityService.isAdmin(email) && !job.getCompany().getOwner().getEmail().equals(email)){
            log.warn("Usuario {} no autorizado para ver postulaciones del trabajo ID: {}", email, jobId);
            throw new UnauthorizedActionException("Solo el dueño de la compañia puede ver las postulaciones");
        }
        return applicationRepository.findByJobPosting_Id(job.getId())
                .stream()
                .map(app -> ApplicationResponse.builder()
                        .id(app.getId())
                        .jobTitle(app.getJobPosting().getTitle())
                        .companyName(app.getJobPosting().getCompany().getName())
                        .status(app.getStatus())
                        .coverLetter(app.getCoverLetter())
                        .cvUrl(app.getCvUrl())
                        .appliedAt(app.getAppliedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponse changeStatus(Long applicationId, ChangeStatusRequest request, String email){
        log.info("Cambiando estado de aplicación ID: {} por usuario: {}", applicationId, email);
        Application apply = applicationRepository.findById(applicationId)
                .orElseThrow(() -> {
                    log.warn("Aplicacion no encontrada al cambiar estado - ID: {}", applicationId);
                    return new ResourceNotFoundException("Aplicacion no encontrada");
                });
        if(!securityService.isAdmin(email) && !apply.getJobPosting().getCompany().getOwner().getEmail().equals(email)){
            log.warn("Usuario {} no autorizado para cambiar estado de aplicación ID: {}", email, applicationId);
            throw new UnauthorizedActionException("Solo el dueño de la compañia puede ver las postulaciones");
        }
        apply.changeStatus(request.getStatus(), request.getNote());
        Application save = applicationRepository.save(apply);
        return ApplicationResponse.builder()
                .id(save.getId())
                .jobTitle(save.getJobPosting().getTitle())
                .companyName(save.getJobPosting().getCompany().getName())
                .status(save.getStatus())
                .coverLetter(save.getCoverLetter())
                .cvUrl(save.getCvUrl())
                .appliedAt(save.getAppliedAt())
                .build();
    }
    @Transactional(readOnly = true)
    public List<ApplicationEventResponse> getApplicationEvents(Long applicationId, String email) {
        log.info("Obteniendo eventos de aplicación ID: {} por usuario: {}", applicationId, email);
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> {
                    log.warn("Aplicacion no encontrada al obtener eventos - ID: {}", applicationId);
                    return new ResourceNotFoundException("Aplicacion no encontrada");
                });
        if (!securityService.isAdmin(email) && !application.getJobPosting().getCompany().getOwner().getEmail().equals(email)) {
            log.warn("Usuario {} no autorizado para ver eventos de aplicación ID: {}", email, applicationId);
            throw new UnauthorizedActionException("No tienes permiso para ver este historial.");
        }
        return application.getEvents()
                .stream()
                .map(event -> ApplicationEventResponse.builder()
                        .fromStatus(event.getFromStatus())
                        .toStatus(event.getToStatus())
                        .note(event.getNote())
                        .changedAt(event.getChangedAt())
                        .build())
                .collect(Collectors.toList());
    }

}
