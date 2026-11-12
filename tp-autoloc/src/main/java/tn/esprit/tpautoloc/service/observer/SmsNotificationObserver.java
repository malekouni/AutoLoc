package tn.esprit.tpautoloc.service.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tn.esprit.tpautoloc.domain.Reservation;

/**
 * Patron GoF : OBSERVER — observateur concret.
 * Notification par SMS (simulée via logs).
 */
@Component
public class SmsNotificationObserver implements IReservationObserver {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationObserver.class);

    @Override
    public void onReservationConfirmee(Reservation reservation) {
        log.info("[SMS] Confirmation envoyée au {} pour la réservation #{}",
                reservation.getClient().getTelephone(),
                reservation.getIdReservation());
    }

    @Override
    public void onReservationAnnulee(Reservation reservation) {
        log.info("[SMS] Annulation notifiée au {} pour la réservation #{}",
                reservation.getClient().getTelephone(),
                reservation.getIdReservation());
    }
}
