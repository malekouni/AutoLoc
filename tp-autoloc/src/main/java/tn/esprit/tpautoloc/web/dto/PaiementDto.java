package tn.esprit.tpautoloc.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.tpautoloc.domain.enums.ModePaiement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaiementDto {

    private Long idPaiement;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être strictement positif")
    private BigDecimal montant;

    private LocalDate datePaiement;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;

    // idContrat non obligatoire en entrée : il est résolu côté serveur via la cascade du Contrat
    private Long idContrat;
}
