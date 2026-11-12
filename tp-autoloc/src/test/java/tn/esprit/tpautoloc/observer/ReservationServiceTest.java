package tn.esprit.tpautoloc.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpautoloc.domain.Client;
import tn.esprit.tpautoloc.domain.Reservation;
import tn.esprit.tpautoloc.domain.enums.StatutReservation;
import tn.esprit.tpautoloc.repository.IReservationRepository;
import tn.esprit.tpautoloc.service.impl.ReservationServiceImpl;
import tn.esprit.tpautoloc.service.observer.EmailNotificationObserver;
import tn.esprit.tpautoloc.service.observer.IReservationObserver;
import tn.esprit.tpautoloc.service.observer.ReservationSubject;
import tn.esprit.tpautoloc.service.observer.SmsNotificationObserver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Atelier 6 — Test du patron Observer.
 *
 * On teste que ReservationServiceImpl notifie bien TOUS les observateurs
 * (email + SMS) lors de la confirmation et de l'annulation d'une réservation.
 *
 * Pas de base de données, pas de Spring : tests unitaires purs avec Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    // ---- Mocks des observateurs (on vérifie qu'ils sont bien appelés) -------
    @Mock
    private EmailNotificationObserver emailObserver;

    @Mock
    private SmsNotificationObserver smsObserver;

    @Mock
    private IReservationRepository reservationRepository;

    // ---- Objets réels construits à la main ----------------------------------
    private ReservationSubject reservationSubject;
    private ReservationServiceImpl reservationService;

    // ---- Client de test -----------------------------------------------------
    private Client client;

    @BeforeEach
    void setUp() {
        // On passe la liste des observers mockés au sujet réel
        List<IReservationObserver> observers = List.of(emailObserver, smsObserver);
        reservationSubject = new ReservationSubject(observers);

        // Service réel avec dépendances injectées à la main
        reservationService = new ReservationServiceImpl(reservationRepository, reservationSubject);

        // Client de test
        client = new Client();
        client.setNom("Trabelsi");
        client.setPrenom("Sarra");
        client.setEmail("sarra.trabelsi@example.com");
        client.setTelephone("20123456");
    }

    // =========================================================================
    // Test 1 : confirmation de réservation
    // =========================================================================

    @Test
    void creerReservation_doitNotifierEmailEtSms() {
        System.out.println("[TEST] creerReservation() → les deux observateurs doivent être notifiés");

        Reservation reservation = reservationService.creerReservation(client);

        // Vérifie que le statut est bien CONFIRMEE
        assertEquals(StatutReservation.CONFIRMEE, reservation.getStatut(),
                "Le statut doit être CONFIRMEE après création");

        // Capture l'argument passé à chaque observer
        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);

        // onReservationConfirmee() appelé exactement 1 fois sur chaque observer
        verify(emailObserver, times(1)).onReservationConfirmee(captor.capture());
        verify(smsObserver,   times(1)).onReservationConfirmee(captor.capture());

        System.out.println("   OK  [EMAIL] onReservationConfirmee appelé");
        System.out.println("   OK  [SMS]   onReservationConfirmee appelé");
    }

    // =========================================================================
    // Test 2 : annulation de réservation
    // =========================================================================

    @Test
    void annulerReservation_doitNotifierEmailEtSms() {
        System.out.println("[TEST] annulerReservation() → les deux observateurs doivent être notifiés");

        // Crée d'abord une réservation confirmée
        Reservation reservation = reservationService.creerReservation(client);

        // Réinitialise les invocations précédentes pour ne tester que l'annulation
        org.mockito.Mockito.clearInvocations(emailObserver, smsObserver);

        // Annule la réservation
        Reservation annulee = reservationService.annulerReservation(reservation);

        // Vérifie que le statut est bien ANNULEE
        assertEquals(StatutReservation.ANNULEE, annulee.getStatut(),
                "Le statut doit être ANNULEE après annulation");

        // onReservationAnnulee() appelé exactement 1 fois sur chaque observer
        verify(emailObserver, times(1)).onReservationAnnulee(annulee);
        verify(smsObserver,   times(1)).onReservationAnnulee(annulee);

        System.out.println("   OK  [EMAIL] onReservationAnnulee appelé");
        System.out.println("   OK  [SMS]   onReservationAnnulee appelé");
    }
}
