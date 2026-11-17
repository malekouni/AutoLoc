package tn.esprit.tpautoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.tpautoloc.service.IEmployeService;
import tn.esprit.tpautoloc.web.dto.EmployeDto;

@RestController
@RequestMapping("/employes")
@RequiredArgsConstructor
@Tag(name = "Employés", description = "Gestion des employés")
public class EmployeController {

    private final IEmployeService employeService;

    @Operation(summary = "Lister les employés")
    @ApiResponse(responseCode = "200", description = "Liste des employés")
    @GetMapping
    public ResponseEntity<List<EmployeDto>> findAll() {
        return ResponseEntity.ok(employeService.findAll());
    }

    @Operation(summary = "Consulter un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé trouvé"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeDto> findById(
            @Parameter(description = "Identifiant de l'employé") @PathVariable Long id) {
        return ResponseEntity.ok(employeService.findById(id));
    }

    @Operation(summary = "Créer un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employé créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<EmployeDto> create(@Valid @RequestBody EmployeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeService.create(dto));
    }

    @Operation(summary = "Modifier un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé modifié"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeDto> update(
            @Parameter(description = "Identifiant de l'employé") @PathVariable Long id,
            @Valid @RequestBody EmployeDto dto) {
        return ResponseEntity.ok(employeService.update(id, dto));
    }

    @Operation(summary = "Supprimer un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employé supprimé"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
            @Parameter(description = "Identifiant de l'employé") @PathVariable Long id) {
        employeService.deleteById(id);
    }

    @Operation(summary = "Affecter un employé à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employé affecté"),
            @ApiResponse(responseCode = "404", description = "Employé ou agence introuvable")
    })
    @PutMapping("/{employeId}/affecter/{agenceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void affecterAAgence(
            @Parameter(description = "Identifiant de l'employé") @PathVariable Long employeId,
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long agenceId) {
        employeService.affecterEmployeAAgence(employeId, agenceId);
    }

    @Operation(summary = "Désaffecter un employé de son agence")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employé désaffecté"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable"),
            @ApiResponse(responseCode = "400", description = "Employé non rattaché à une agence")
    })
    @PutMapping("/{employeId}/desaffecter")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desaffecterDeAgence(
            @Parameter(description = "Identifiant de l'employé") @PathVariable Long employeId) {
        employeService.desaffecterEmployeDeAgence(employeId);
    }
}
