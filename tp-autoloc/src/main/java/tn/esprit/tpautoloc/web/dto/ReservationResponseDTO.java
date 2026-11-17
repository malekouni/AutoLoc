package tn.esprit.tpautoloc.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.tpautoloc.domain.enums.StatutReservation;

/**
 * DTO sortant : données retournées par l'API après traitement d'une réservation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {

    private Long id;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private StatutReservation statut;
    private Long clientId;
    private String clientNomComplet;
    private Long vehiculeId;
    private String vehiculeImmatriculation;
    /** Calculé et injecté par le service via la TarificationStrategyFactory. */
    private BigDecimal tarifEstime;
}
