package com.example.devjobs.service;

import com.example.devjobs.dto.request.CreateCompanyRequest;
import com.example.devjobs.dto.request.UpdateCompanyRequest;
import com.example.devjobs.dto.response.CompanyResponse;
import com.example.devjobs.dto.response.DeleteResponse;
import com.example.devjobs.exception.ResourceNotFoundException;
import com.example.devjobs.exception.UnauthorizedActionException;
import com.example.devjobs.model.Company;
import com.example.devjobs.model.User;
import com.example.devjobs.repository.ICompanyRepository;
import com.example.devjobs.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final ICompanyRepository iCompanyRepository;
    private final IUserRepository iUserRepository;
    //crea una empresa asociada al usuario autenticado
    public CompanyResponse createCompany(CreateCompanyRequest request, String email){
        User user = iUserRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado."));
        Company company = Company.builder()
                .owner(user)
                .name(request.getName())
                .industry(request.getIndustry())
                .website(request.getWebsite())
                .description(request.getDescription())
                .location(request.getLocation())
                .build();
        Company save = iCompanyRepository.save(company);
        return CompanyResponse.builder()
                .id(save.getId())
                .name(save.getName())
                .industry(save.getIndustry())
                .website(save.getWebsite())
                .description(save.getDescription())
                .location(save.getLocation())
                .ownerFullName(save.getOwner().getFullName())
                .createdAt(save.getCreatedAt())
                .build();
    }
    //busca la empresa por id, si no existe lanza
    public CompanyResponse getCompanyById(Long id){
        Company company = iCompanyRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Compañia no encontrada"));
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .industry(company.getIndustry())
                .website(company.getWebsite())
                .description(company.getDescription())
                .location(company.getLocation())
                .ownerFullName(company.getOwner().getFullName())
                .createdAt(company.getCreatedAt())
                .build();
    }
    // busca la empresa, verifica que el email del dueño coincida con
    // el autenticado, actualiza solo los campos que llegaron en el request.
    //PATCH MAPPING
    public CompanyResponse updateCompany(Long id, UpdateCompanyRequest request, String email){
        Company company = iCompanyRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Compañia no encontrada"));
        if(!company.getOwner().getEmail().equals(email)){
            throw new UnauthorizedActionException("Solo el dueño de esta compañia puede actualizarla.");
        }
        if (request.getName() != null) company.setName(request.getName());
        if (request.getIndustry() != null) company.setIndustry(request.getIndustry());
        if (request.getWebsite() != null) company.setWebsite(request.getWebsite());
        if (request.getDescription() != null) company.setDescription(request.getDescription());
        if (request.getLocation() != null) company.setLocation(request.getLocation());

        iCompanyRepository.save(company);

        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .industry(company.getIndustry())
                .website(company.getWebsite())
                .description(company.getDescription())
                .location(company.getLocation())
                .ownerFullName(company.getOwner().getFullName())
                .createdAt(company.getCreatedAt())
                .build();
    }
    // busca la empresa, verifica que sea el dueño, llama a repository.delete().

    public DeleteResponse deleteCompany(Long id, String email){
        Company company = iCompanyRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Compañia no encontrada"));
        if(!company.getOwner().getEmail().equals(email)){
            throw new UnauthorizedActionException("Solo el dueño de esta compañia puede eliminarla.");
        }
        iCompanyRepository.delete(company);
        return DeleteResponse.builder().message("Compañia eliminada exitosamente.").build();
    }

}
