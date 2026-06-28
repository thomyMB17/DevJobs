package com.example.devjobs.controller;

import com.example.devjobs.dto.request.ApplyRequest;
import com.example.devjobs.dto.request.ChangeStatusRequest;
import com.example.devjobs.dto.response.ApplicationEventResponse;
import com.example.devjobs.dto.response.ApplicationResponse;
import com.example.devjobs.dto.response.ErrorResponse;
import com.example.devjobs.service.ApplicationService;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/app")
public class ApplicationController extends BaseController{

    private final ApplicationService applicationService;

    @Operation(summary = "Mis postulaciones", description = "Obtiene las postulaciones del usuario autenticado (solo CANDIDATE)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de postulaciones"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/getApplications/me")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications(){
        String email = getEmailFromToken();
        return ResponseEntity.ok(applicationService.getMyApplications(email));
    }

    @Operation(summary = "Postulaciones por oferta", description = "Obtiene las postulaciones de una oferta (solo dueño o ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de postulaciones"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No eres el dueño", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/getApplicationsByJobId/{id}")
    public ResponseEntity<List<ApplicationResponse>> getAllApplicationsByJob(@PathVariable Long id){
        String email = getEmailFromToken();
        return ResponseEntity.ok(applicationService.getApplicationsByJob(id, email));
    }

    @Operation(summary = "Historial de cambios", description = "Obtiene el historial de cambios de estado de una postulación (solo dueño o ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial de eventos"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permiso", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Postulación no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/getApplicationsEventsId/{id}")
    public ResponseEntity<List<ApplicationEventResponse>> getAllApplicationsEvent(@PathVariable Long id){
        String email = getEmailFromToken();
        return ResponseEntity.ok(applicationService.getApplicationEvents(id, email));
    }

    @Operation(summary = "Postularse", description = "Postularse a una oferta (solo CANDIDATE)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Postulación creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No eres CANDIDATE o la oferta no está disponible", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Ya te postulaste a esta oferta", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/apply/{id}")
    public ResponseEntity<ApplicationResponse> apply(@PathVariable Long id, @RequestBody @Valid ApplyRequest request){
        String email = getEmailFromToken();
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.apply(id, request, email));
    }

    @Operation(summary = "Cambiar estado", description = "Cambia el estado de una postulación (solo dueño o ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No eres el dueño", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Postulación no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/changeStatusId/{id}")
    public ResponseEntity<ApplicationResponse> changeStatus(@PathVariable Long id, @RequestBody @Valid ChangeStatusRequest request){
        String email = getEmailFromToken();
        return ResponseEntity.ok(applicationService.changeStatus(id, request, email));
    }
}
