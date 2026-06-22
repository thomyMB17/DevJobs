package com.example.devjobs.controller;

import com.example.devjobs.dto.request.CreateJobRequest;
import com.example.devjobs.dto.request.UpdateJobRequest;
import com.example.devjobs.dto.response.JobDetailResponse;
import com.example.devjobs.dto.response.JobSummaryResponse;
import com.example.devjobs.model.enums.Modality;
import com.example.devjobs.model.enums.Seniority;
import com.example.devjobs.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job")
public class JobController extends BaseController{

    private final JobService jobService;

    // GET MAPPING
    @GetMapping("/getAllJobs")
    public ResponseEntity<Page<JobSummaryResponse>> getAllJobs(
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) Modality modality,
            @RequestParam(required = false) Seniority seniority,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(jobService.getAllJobs(technology, modality, seniority, location, pageable));
    }
    @GetMapping("/getJobById/{id}")
    public ResponseEntity<JobDetailResponse> getJobById(@PathVariable Long id){
        return ResponseEntity.ok(jobService.getJobById(id));
    }
    @GetMapping("/getCompanyJobs/companyId/{id}/jobs")
    public ResponseEntity<List<JobSummaryResponse>> getJobsByCompany(@PathVariable Long id){
        return ResponseEntity.ok(jobService.getJobsByCompany(id));
    }
    //POST MAPPING
    @PostMapping("/createJob")
    public ResponseEntity<JobDetailResponse> createJob(@RequestBody @Valid CreateJobRequest request){
        String email = getEmailFromToken();
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.createJob(request, email));
    }
    //PATCH MAPPING
    @PatchMapping("/updateJob/{id}")
    public ResponseEntity<JobDetailResponse> updateJob(@PathVariable Long id, @RequestBody UpdateJobRequest request){
        String email = getEmailFromToken();
        return ResponseEntity.ok(jobService.updateJob(id, request, email));
    }
    //DELETE MAPPING
    @DeleteMapping("/deactivateJob/{id}")
    public ResponseEntity<JobDetailResponse> deactivateJob(@PathVariable Long id){
        String email = getEmailFromToken();
        return ResponseEntity.ok(jobService.deactivateJob(id, email));
    }
}
