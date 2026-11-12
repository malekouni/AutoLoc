package tn.esprit.tpautoloc.service.strategy;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;
import tn.esprit.tpautoloc.domain.Reservation;
import tn.esprit.tpautoloc.domain.Vehicule;

/**
 * Stratégie tarifaire longue durée (≥ 7 jours) : remise de 20 % sur le tarif journalier.
 * REMISE_LONGUE_DUREE = 0.80 → le client paie 80 % du tarif de base.
 */
@Component
public class TarifLongueDureeStrategy implements ITarificationStrategy {

    private static final BigDecimal REMISE_LONGUE_DUREE = new BigDecimal("0.80");

    @Override
    public BigDecimal calculerTarif(Vehicule vehicule, Reservation reservation) {
        long jours = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin());
        return vehicule.getTarifJournalier()
                .multiply(REMISE_LONGUE_DUREE)
                .multiply(BigDecimal.valueOf(jours));
    }

    @Override
    public String getCode() {
        return "LONGUE_DUREE";
    }
}
