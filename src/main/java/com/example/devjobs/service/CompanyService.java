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
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final ICompanyRepository companyRepository;
    private final IUserRepository userRepository;
    private final SecurityService securityService;
    //crea una empresa asociada al usuario autenticado
    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> {
                    log.warn("Usuario no encontrado al crear compañia: {}", email);
                    return new ResourceNotFoundException("Usuario no encontrado.");
                });
        log.info("Creando compañia para el usuario: {}", email);
        Company company = Company.builder()
                .owner(user)
                .name(request.getName())
                .industry(request.getIndustry())
                .website(request.getWebsite())
                .description(request.getDescription())
                .location(request.getLocation())
                .build();
        Company save = companyRepository.save(company);
        log.info("Compañia creada con ID: {}", save.getId());
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
        log.info("Buscando compañia por ID: {}", id);
        Company company = companyRepository.findById(id)
                .orElseThrow(()-> {
                    log.warn("Compañia no encontrada - ID: {}", id);
                    return new ResourceNotFoundException("Compañia no encontrada");
                });
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
    public List<CompanyResponse> getAllCompanies(){
        log.info("Obteniendo todas las compañías");
        return companyRepository.findAll()
                .stream()
                .map(company -> CompanyResponse.builder()
                        .id(company.getId())
                        .name(company.getName())
                        .industry(company.getIndustry())
                        .website(company.getWebsite())
                        .description(company.getDescription())
                        .location(company.getLocation())
                        .ownerFullName(company.getOwner().getFullName())
                        .createdAt(company.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    // busca la empresa, verifica que el email del dueño coincida con
    // el autenticado, actualiza solo los campos que llegaron en el request.
    //PATCH MAPPING
    @Transactional
    public CompanyResponse updateCompany(Long id, UpdateCompanyRequest request, String email){
        log.info("Actualizando compañia ID: {} por usuario: {}", id, email);
        Company company = companyRepository.findById(id)
                .orElseThrow(()-> {
                    log.warn("Compañia no encontrada al actualizar - ID: {}", id);
                    return new ResourceNotFoundException("Compañia no encontrada");
                });
        if(!securityService.isAdmin(email) && !company.getOwner().getEmail().equals(email)){
            log.warn("Usuario {} no autorizado para actualizar compañia ID: {}", email, id);
            throw new UnauthorizedActionException("Solo el dueño de esta compañia puede actualizarla.");
        }
        if (request.getName() != null) company.setName(request.getName());
        if (request.getIndustry() != null) company.setIndustry(request.getIndustry());
        if (request.getWebsite() != null) company.setWebsite(request.getWebsite());
        if (request.getDescription() != null) company.setDescription(request.getDescription());
        if (request.getLocation() != null) company.setLocation(request.getLocation());

        companyRepository.save(company);

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

    @Transactional
    public DeleteResponse deleteCompany(Long id, String email){
        log.info("Eliminando compañia ID: {} por usuario: {}", id, email);
        Company company = companyRepository.findById(id)
                .orElseThrow(()-> {
                    log.warn("Compañia no encontrada al eliminar - ID: {}", id);
                    return new ResourceNotFoundException("Compañia no encontrada");
                });
        if(!securityService.isAdmin(email) && !company.getOwner().getEmail().equals(email)){
            log.warn("Usuario {} no autorizado para eliminar compañia ID: {}", email, id);
            throw new UnauthorizedActionException("Solo el dueño de esta compañia puede eliminarla.");
        }
        companyRepository.delete(company);
        log.info("Compañia ID: {} eliminada exitosamente", id);
        return DeleteResponse.builder().message("Compañia eliminada exitosamente.").build();
    }

}
