package tn.esprit.tpautoloc.service.observer;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import tn.esprit.tpautoloc.domain.Reservation;

/**
 * Patron GoF : OBSERVER — sujet observable.
 * Maintient la liste des observateurs et les notifie lors des changements
 * d'état d'une réservation.
 *
 * Tous les beans IReservationObserver de Spring sont injectés automatiquement
 * via la liste passée au constructeur (injection par type).
 */
@Component
public class ReservationSubject {

    private final List<IReservationObserver> observers;

    public ReservationSubject(List<IReservationObserver> observers) {
        this.observers = new ArrayList<>(observers);
    }

    public void notifierConfirmation(Reservation reservation) {
        observers.forEach(o -> o.onReservationConfirmee(reservation));
    }

    public void notifierAnnulation(Reservation reservation) {
        observers.forEach(o -> o.onReservationAnnulee(reservation));
    }
}
