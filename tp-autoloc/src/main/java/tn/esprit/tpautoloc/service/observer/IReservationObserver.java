package tn.esprit.tpautoloc.service.observer;

import tn.esprit.tpautoloc.domain.Reservation;

/**
 * Patron GoF : OBSERVER.
 * Interface commune à tous les observateurs notifiés lors des événements
 * du cycle de vie d'une réservation (confirmation, annulation...).
 */
public interface IReservationObserver {

    void onReservationConfirmee(Reservation reservation);

    void onReservationAnnulee(Reservation reservation);
}
