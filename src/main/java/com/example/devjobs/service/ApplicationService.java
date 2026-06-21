package com.example.devjobs.service;

import com.example.devjobs.dto.request.ApplyRequest;
import com.example.devjobs.dto.request.ChangeStatusRequest;
import com.example.devjobs.dto.response.ApplicationResponse;
import com.example.devjobs.exception.DuplicateApplicationException;
import com.example.devjobs.exception.ResourceNotFoundException;
import com.example.devjobs.exception.UnauthorizedActionException;
import com.example.devjobs.model.Application;
import com.example.devjobs.model.JobPosting;
import com.example.devjobs.model.User;
import com.example.devjobs.model.enums.ApplicationStatus;
import com.example.devjobs.repository.IApplicationRepository;
import com.example.devjobs.repository.IJobRepository;
import com.example.devjobs.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final IApplicationRepository iApplicationRepository;
    private final IJobRepository iJobRepository;
    private final IUserRepository iUserRepository;


    public ApplicationResponse apply(Long jobId, ApplyRequest request, String email){
        User candidato = iUserRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado"));
        JobPosting job = iJobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajo no encontrado"));
        if(!job.isActive()){
            throw new UnauthorizedActionException("Oferta de trabajo no disponible.");
        }
        if(iApplicationRepository.existsByUserIdAndJobId(candidato.getId(), jobId)){
            throw new DuplicateApplicationException("No puedes postular 2 veces a la misma oferta.");
        }
        Application apply = Application.builder()
                .candidate(candidato)
                .jobPosting(job)
                .status(ApplicationStatus.PENDING)
                .coverLetter(request.getCoverLetter())
                .cvUrl(request.getCvUrl())
                .build();
        Application save = iApplicationRepository.save(apply);
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

    public List<ApplicationResponse> getMyApplications(String email){
        User candidato = iUserRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado"));
        return iApplicationRepository.findByCandidate_Id(candidato.getId())
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

    public List<ApplicationResponse> getApplicationsByJob(Long jobId, String email){
        JobPosting job = iJobRepository.findById(jobId)
                .orElseThrow(()-> new ResourceNotFoundException("No existe trabajo con este id"));
        if(!job.getCompany().getOwner().getEmail().equals(email)){
            throw new UnauthorizedActionException("Solo el dueño de la compañia puede ver las postulaciones");
        }
        return iApplicationRepository.findByJobPosting_Id(job.getId())
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

    public ApplicationResponse changeStatus(Long applicationId, ChangeStatusRequest request, String email){
        Application apply = iApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicacion no encontrada"));
        if(!apply.getJobPosting().getCompany().getOwner().getEmail().equals(email)){
            throw new UnauthorizedActionException("Solo el dueño de la compañia puede ver las postulaciones");
        }
        apply.changeStatus(request.getStatus(), request.getNote());
        Application save = iApplicationRepository.save(apply);
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
}
