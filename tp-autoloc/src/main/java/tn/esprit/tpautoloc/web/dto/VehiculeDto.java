package tn.esprit.tpautoloc.web.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.tpautoloc.domain.enums.CategorieVehicule;
import tn.esprit.tpautoloc.domain.enums.StatutVehicule;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeDto {

    private Long idVehicule;
    private String immatriculation;
    private String marque;
    private String modele;
    private CategorieVehicule categorie;
    private BigDecimal tarifJournalier;
    private StatutVehicule statut;
    /** Identifiant de l'agence propriétaire. */
    private Long idAgence;
    /** Identifiants des équipements associés. */
    private List<Long> idEquipements;
}
