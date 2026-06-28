package com.example.devjobs.controller;

import com.example.devjobs.dto.request.CreateJobRequest;
import com.example.devjobs.dto.request.UpdateJobRequest;
import com.example.devjobs.dto.response.ErrorResponse;
import com.example.devjobs.dto.response.JobDetailResponse;
import com.example.devjobs.dto.response.JobSummaryResponse;
import com.example.devjobs.model.enums.Modality;
import com.example.devjobs.model.enums.Seniority;
import com.example.devjobs.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job")
public class JobController extends BaseController{

    private final JobService jobService;

    @Operation(summary = "Listar ofertas activas", description = "Obtiene ofertas activas con filtros y paginación")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de ofertas")
    })
    @GetMapping("/getAllJobs")
    public ResponseEntity<List<JobSummaryResponse>> getAllJobs(
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) Modality modality,
            @RequestParam(required = false) Seniority seniority,
            @RequestParam(required = false) String location) {

        return ResponseEntity.ok(jobService.getAllJobs(technology, modality, seniority, location));
    }

    @Operation(summary = "Obtener oferta por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Oferta encontrada"),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/getJobById/{id}")
    public ResponseEntity<JobDetailResponse> getJobById(@PathVariable Long id){
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @Operation(summary = "Ofertas activas de una empresa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de ofertas de la empresa"),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/getCompanyJobs/companyId/{id}/jobs")
    public ResponseEntity<List<JobSummaryResponse>> getJobsByCompany(@PathVariable Long id){
        return ResponseEntity.ok(jobService.getJobsByCompany(id));
    }

    @Operation(summary = "Crear oferta laboral", description = "Crea una oferta en una compañía (solo dueño o ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Oferta creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No eres el dueño", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Compañía no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/createJob")
    public ResponseEntity<JobDetailResponse> createJob(@RequestBody @Valid CreateJobRequest request){
        String email = getEmailFromToken();
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.createJob(request, email));
    }

    @Operation(summary = "Actualizar oferta", description = "Actualiza datos de la oferta (solo dueño o ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Oferta actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No eres el dueño", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/updateJob/{id}")
    public ResponseEntity<JobDetailResponse> updateJob(@PathVariable Long id, @RequestBody @Valid UpdateJobRequest request){
        String email = getEmailFromToken();
        return ResponseEntity.ok(jobService.updateJob(id, request, email));
    }

    @Operation(summary = "Desactivar oferta", description = "Desactiva una oferta (solo dueño o ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Oferta desactivada"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No eres el dueño", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/deactivateJob/{id}")
    public ResponseEntity<JobDetailResponse> deactivateJob(@PathVariable Long id){
        String email = getEmailFromToken();
        return ResponseEntity.ok(jobService.deactivateJob(id, email));
    }

    @Operation(summary = "Reactivar oferta", description = "Reactivar una oferta desactivada (solo dueño o ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Oferta reactivada"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No eres el dueño", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/activateJob/{id}")
    public ResponseEntity<JobDetailResponse> activateJob(@PathVariable Long id){
        String email = getEmailFromToken();
        return ResponseEntity.ok(jobService.activateJob(id, email));
    }
}
