package tn.esprit.tpautoloc.service.factory;

import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import tn.esprit.tpautoloc.domain.Reservation;
import tn.esprit.tpautoloc.service.strategy.ITarificationStrategy;

/**
 * Patron GoF : FACTORY METHOD appliqué aux stratégies de tarification.
 *
 * Sélectionne dynamiquement la ITarificationStrategy adaptée à une réservation
 * (durée du séjour + jour de début).
 *
 * Toutes les implémentations Spring de ITarificationStrategy sont injectées
 * automatiquement via la liste → construites dans une Map<code, strategy>
 * pour une résolution O(1).
 *
 * Règles de sélection :
 *   - jours >= 7                        → LONGUE_DUREE
 *   - jours <= 3 ET début vendredi/sam  → WEEKEND
 *   - sinon                             → STANDARD  (fallback)
 */
@Component
public class TarificationStrategyFactory {

    private final Map<String, ITarificationStrategy> strategies;

    public TarificationStrategyFactory(List<ITarificationStrategy> strategyList) {
        // On indexe chaque stratégie par son code pour une résolution rapide
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(ITarificationStrategy::getCode, s -> s));
    }

    /**
     * Résout la bonne stratégie en fonction de la durée et du jour de début de
     * la réservation.
     */
    public ITarificationStrategy resoudre(Reservation reservation) {
        long jours = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin());

        if (jours >= 7) {
            return strategies.get("LONGUE_DUREE");
        }
        if (jours <= 3 && estWeekend(reservation)) {
            return strategies.get("WEEKEND");
        }
        return strategies.get("STANDARD");
    }

    /**
     * Retourne true si la réservation commence un vendredi ou un samedi.
     */
    private boolean estWeekend(Reservation reservation) {
        DayOfWeek jour = reservation.getDateDebut().getDayOfWeek();
        return jour == DayOfWeek.FRIDAY || jour == DayOfWeek.SATURDAY;
    }
}
