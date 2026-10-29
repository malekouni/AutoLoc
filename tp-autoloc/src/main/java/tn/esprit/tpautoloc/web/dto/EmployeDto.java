package tn.esprit.tpautoloc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.tpautoloc.domain.enums.RoleEmploye;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeDto {

    private Long idEmploye;
    private String nom;
    private String prenom;
    private RoleEmploye role;
    /** Identifiant de l'agence de rattachement (lecture seule — affectation gérée séparément). */
    private Long idAgence;
}
