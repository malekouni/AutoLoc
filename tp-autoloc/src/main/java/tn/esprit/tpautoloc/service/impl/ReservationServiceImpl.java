package tn.esprit.tpautoloc.service.impl;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tn.esprit.tpautoloc.domain.Client;
import tn.esprit.tpautoloc.domain.Reservation;
import tn.esprit.tpautoloc.domain.enums.StatutReservation;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.repository.IReservationRepository;
import tn.esprit.tpautoloc.service.IReservationService;
import tn.esprit.tpautoloc.service.observer.ReservationSubject;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final IReservationRepository reservationRepository;
    private final ReservationSubject reservationSubject;

    @Override
    public Reservation create(Reservation reservation) {
        if (reservation.getIdReservation() != null) {
            throw new IllegalArgumentException(
                    "Une nouvelle réservation ne doit pas avoir d'identifiant");
        }
        verifierRegles(reservation);
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation update(Long id, Reservation reservation) {
        Reservation existant = findById(id);
        verifierRegles(reservation);
        // Recopie des attributs modifiables uniquement : ni l'identifiant ni les associations
        // (leur gestion est l'objet du chapitre 9).
        existant.setDateDebut(reservation.getDateDebut());
        existant.setDateFin(reservation.getDateFin());
        existant.setStatut(reservation.getStatut());
        return reservationRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation", id);
        }
        reservationRepository.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // Méthodes métier Observer (Atelier 6)
    // -------------------------------------------------------------------------

    /**
     * Crée une réservation pour un client et notifie tous les observateurs
     * (email, SMS, statistiques…) via le patron Observer.
     */
    public Reservation creerReservation(Client client) {
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setDateDebut(LocalDate.now());
        reservation.setStatut(StatutReservation.CONFIRMEE);
        // Déclenche le process métier via le pattern Observer
        reservationSubject.notifierConfirmation(reservation);
        return reservation;
    }

    /**
     * Annule une réservation existante et notifie tous les observateurs.
     */
    public Reservation annulerReservation(Reservation reservation) {
        reservation.setStatut(StatutReservation.ANNULEE);
        log.info("Réservation #{} annulée pour le client {}",
                reservation.getIdReservation(),
                reservation.getClient().getEmail());
        reservationSubject.notifierAnnulation(reservation);
        return reservation;
    }

    // -------------------------------------------------------------------------

    private void verifierRegles(Reservation reservation) {
        if (reservation.getDateDebut() != null && reservation.getDateFin() != null
                && reservation.getDateFin().isBefore(reservation.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
}
