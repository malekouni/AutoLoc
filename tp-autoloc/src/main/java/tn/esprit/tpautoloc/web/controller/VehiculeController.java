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
import tn.esprit.tpautoloc.service.IVehiculeService;
import tn.esprit.tpautoloc.web.dto.VehiculeDto;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Tag(name = "Véhicules", description = "Gestion des véhicules")
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @Operation(summary = "Lister les véhicules")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules")
    @GetMapping
    public ResponseEntity<List<VehiculeDto>> findAll() {
        return ResponseEntity.ok(vehiculeService.findAll());
    }

    @Operation(summary = "Consulter un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehiculeDto> findById(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        return ResponseEntity.ok(vehiculeService.findById(id));
    }

    @Operation(summary = "Créer un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<VehiculeDto> create(@Valid @RequestBody VehiculeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculeService.create(dto));
    }

    @Operation(summary = "Modifier un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule modifié"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<VehiculeDto> update(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long id,
            @Valid @RequestBody VehiculeDto dto) {
        return ResponseEntity.ok(vehiculeService.update(id, dto));
    }

    @Operation(summary = "Supprimer un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule supprimé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        vehiculeService.deleteById(id);
    }

    @Operation(summary = "Affecter un véhicule à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule affecté"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou agence introuvable")
    })
    @PutMapping("/{vehiculeId}/affecter/{agenceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void affecterAAgence(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long vehiculeId,
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long agenceId) {
        vehiculeService.affecterVehiculeAAgence(vehiculeId, agenceId);
    }

    @Operation(summary = "Désaffecter un véhicule de son agence")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule désaffecté"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable"),
            @ApiResponse(responseCode = "400", description = "Véhicule non rattaché à une agence")
    })
    @PutMapping("/{vehiculeId}/desaffecter")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desaffecterDeAgence(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long vehiculeId) {
        vehiculeService.desaffecterVehiculeDeAgence(vehiculeId);
    }
}
