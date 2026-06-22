package com.example.devjobs.controller;

import com.example.devjobs.dto.request.ApplyRequest;
import com.example.devjobs.dto.request.ChangeStatusRequest;
import com.example.devjobs.dto.response.ApplicationEventResponse;
import com.example.devjobs.dto.response.ApplicationResponse;
import com.example.devjobs.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/app")
public class ApplicationController extends BaseController{

    private final ApplicationService applicationService;

    //GET MAPPING
    @GetMapping("/getApplications/me")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications(){
        String email = getEmailFromToken();
        return ResponseEntity.ok(applicationService.getMyApplications(email));
    }
    @GetMapping("/getApplicationsByJobId/{id}")
    public ResponseEntity<List<ApplicationResponse>> getAllAplicationsByJob(@PathVariable Long id){
        String email = getEmailFromToken();
        return ResponseEntity.ok(applicationService.getApplicationsByJob(id, email));
    }
    @GetMapping("/getApplicationsEventsId/{id}")
    public ResponseEntity<List<ApplicationEventResponse>> getAllAplicationsEvent(@PathVariable Long id){
        String email = getEmailFromToken();
        return ResponseEntity.ok(applicationService.getApplicationEvents(id, email));
    }
    //POST MAPPING
    @PostMapping("/apply/{id}")
    public ResponseEntity<ApplicationResponse> apply(@PathVariable Long id, @RequestBody @Valid ApplyRequest request){
        String email = getEmailFromToken();
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.apply(id, request, email));
    }
    // PATCH MAPPING
    @PatchMapping("/changeStatusId/{id}")
    public ResponseEntity<ApplicationResponse> changeStatus(@PathVariable Long id, @RequestBody @Valid ChangeStatusRequest request){
        String email = getEmailFromToken();
        return ResponseEntity.ok(applicationService.changeStatus(id, request, email));
    }

}
