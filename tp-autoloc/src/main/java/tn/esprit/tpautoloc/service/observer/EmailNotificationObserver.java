package tn.esprit.tpautoloc.service.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tn.esprit.tpautoloc.domain.Reservation;

/**
 * Patron GoF : OBSERVER — observateur concret.
 * Notification par email (simulée via logs).
 */
@Component
public class EmailNotificationObserver implements IReservationObserver {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationObserver.class);

    @Override
    public void onReservationConfirmee(Reservation reservation) {
        log.info("[EMAIL] Confirmation envoyée au client {} pour la réservation #{}",
                reservation.getClient().getEmail(),
                reservation.getIdReservation());
    }

    @Override
    public void onReservationAnnulee(Reservation reservation) {
        log.info("[EMAIL] Annulation notifiée au client {} pour la réservation #{}",
                reservation.getClient().getEmail(),
                reservation.getIdReservation());
    }
}
