package tn.esprit.tpautoloc.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratDto {

    private Long idContrat;
    private LocalDate dateSignature;

    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    private BigDecimal montantTotal;

    private boolean valide;
    @Valid
    private List<PaiementDto> paiements;
}
