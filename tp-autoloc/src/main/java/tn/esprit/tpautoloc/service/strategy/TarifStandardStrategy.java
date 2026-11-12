package tn.esprit.tpautoloc.service.strategy;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;
import tn.esprit.tpautoloc.domain.Reservation;
import tn.esprit.tpautoloc.domain.Vehicule;

/**
 * Stratégie tarifaire standard : tarifJournalier × nombre de jours, sans remise.
 */
@Component
public class TarifStandardStrategy implements ITarificationStrategy {

    @Override
    public BigDecimal calculerTarif(Vehicule vehicule, Reservation reservation) {
        long jours = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin());
        return vehicule.getTarifJournalier().multiply(BigDecimal.valueOf(jours));
    }

    @Override
    public String getCode() {
        return "STANDARD";
    }
}
