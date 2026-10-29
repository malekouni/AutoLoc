package tn.esprit.tpautoloc.web.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.tpautoloc.domain.enums.StatutReservation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {

    private Long idReservation;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private StatutReservation statut;
    /** Identifiant du client. */
    private Long idClient;
    /** Identifiant du véhicule. */
    private Long idVehicule;
    /** Contrat lié (inclus en lecture pour éviter un aller-retour réseau supplémentaire). */
    private ContratDto contrat;
}
