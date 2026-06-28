package com.example.devjobs.controller;

import com.example.devjobs.dto.request.CreateCompanyRequest;
import com.example.devjobs.dto.request.UpdateCompanyRequest;
import com.example.devjobs.dto.response.CompanyResponse;
import com.example.devjobs.dto.response.DeleteResponse;
import com.example.devjobs.dto.response.ErrorResponse;
import com.example.devjobs.service.CompanyService;
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
@RequestMapping("/api/v1/company")
public class CompanyController extends BaseController {

    private final CompanyService companyService;

    @Operation(summary = "Listar compañías", description = "Obtiene todas las compañías con paginación")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de compañías")
    })
    @GetMapping("/getAll")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies(){
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @Operation(summary = "Obtener compañía por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compañía encontrada"),
            @ApiResponse(responseCode = "404", description = "Compañía no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/getById/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable Long id){
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @Operation(summary = "Crear compañía", description = "Crea una compañía asociada al usuario autenticado (solo EMPLOYER)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compañía creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permiso", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/createCompany")
    public ResponseEntity<CompanyResponse> createCompany(@RequestBody @Valid CreateCompanyRequest request){
        String email = getEmailFromToken();
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.createCompany(request, email));
    }

    @Operation(summary = "Actualizar compañía", description = "Actualiza datos de la compañía (solo el dueño o ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compañía actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No eres el dueño", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Compañía no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/updateCompany/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(@PathVariable Long id, @RequestBody @Valid UpdateCompanyRequest request){
        String email = getEmailFromToken();
        return ResponseEntity.ok(companyService.updateCompany(id, request, email));
    }

    @Operation(summary = "Eliminar compañía", description = "Elimina la compañía (solo el dueño o ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compañía eliminada"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No eres el dueño", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Compañía no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/deleteCompany/{id}")
    public ResponseEntity<DeleteResponse> deleteCompany(@PathVariable Long id){
        String email = getEmailFromToken();
        return ResponseEntity.ok(companyService.deleteCompany(id, email));
    }
}
