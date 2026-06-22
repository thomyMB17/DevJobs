package com.example.devjobs.controller;

import com.example.devjobs.dto.request.CreateCompanyRequest;
import com.example.devjobs.dto.request.UpdateCompanyRequest;
import com.example.devjobs.dto.response.CompanyResponse;
import com.example.devjobs.dto.response.DeleteResponse;
import com.example.devjobs.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/company")
public class CompanyController extends BaseController {

    private final CompanyService companyService;

    //GET MAPPINGS
    @GetMapping("/getById/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable Long id){
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }
    //POST MAPPING
    @PostMapping("/createCompany")
    public ResponseEntity<CompanyResponse> createCompany(@RequestBody @Valid CreateCompanyRequest request){
        String email = getEmailFromToken();
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.createCompany(request, email));
    }
    //PATCH MAPPING
    @PatchMapping("/updateCompany/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(@PathVariable Long id, @RequestBody UpdateCompanyRequest request){
        String email = getEmailFromToken();
        return ResponseEntity.ok(companyService.updateCompany(id, request, email));
    }
    //DELETE MAPPING
    @DeleteMapping("/deleteCompany/{id}")
    public ResponseEntity<DeleteResponse> deleteCompany(@PathVariable Long id){
        String email = getEmailFromToken();
        return ResponseEntity.ok(companyService.deleteCompany(id, email));
    }
}
