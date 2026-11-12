package tn.esprit.tpautoloc.service.strategy;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;
import tn.esprit.tpautoloc.domain.Reservation;
import tn.esprit.tpautoloc.domain.Vehicule;

/**
 * Stratégie tarifaire week-end : remise de 10 % sur le tarif journalier.
 * REMISE_WEEKEND = 0.90 → le client paie 90 % du tarif de base.
 */
@Component
public class TarifWeekendStrategy implements ITarificationStrategy {

    private static final BigDecimal REMISE_WEEKEND = new BigDecimal("0.90");

    @Override
    public BigDecimal calculerTarif(Vehicule vehicule, Reservation reservation) {
        long jours = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin());
        return vehicule.getTarifJournalier()
                .multiply(REMISE_WEEKEND)
                .multiply(BigDecimal.valueOf(jours));
    }

    @Override
    public String getCode() {
        return "WEEKEND";
    }
}
