package tn.esprit.tpautoloc.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.tpautoloc.domain.enums.ModePaiement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaiementDto {

    private Long idPaiement;
    private BigDecimal montant;
    private LocalDate datePaiement;
    private ModePaiement modePaiement;
    /** Identifiant du contrat associé. */
    private Long idContrat;
}
