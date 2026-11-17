package tn.esprit.tpautoloc.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpautoloc.domain.Client;
import tn.esprit.tpautoloc.domain.Reservation;
import tn.esprit.tpautoloc.domain.enums.StatutReservation;
import tn.esprit.tpautoloc.repository.IClientRepository;
import tn.esprit.tpautoloc.repository.IReservationRepository;
import tn.esprit.tpautoloc.repository.IVehiculeRepository;
import tn.esprit.tpautoloc.service.impl.ReservationServiceImpl;
import tn.esprit.tpautoloc.service.observer.EmailNotificationObserver;
import tn.esprit.tpautoloc.service.observer.IReservationObserver;
import tn.esprit.tpautoloc.service.observer.ReservationSubject;
import tn.esprit.tpautoloc.service.observer.SmsNotificationObserver;
import tn.esprit.tpautoloc.web.dto.ReservationResponseDTO;
import tn.esprit.tpautoloc.web.mapper.ReservationMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Atelier 6 / 7 — Test du patron Observer.
 * Mis à jour pour tenir compte des nouveaux paramètres du constructeur (Atelier 7 : DTOs + Mapper).
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private EmailNotificationObserver  emailObserver;
    @Mock private SmsNotificationObserver    smsObserver;
    @Mock private IReservationRepository     reservationRepository;
    @Mock private IClientRepository          clientRepository;
    @Mock private IVehiculeRepository        vehiculeRepository;
    @Mock private ReservationMapper          reservationMapper;

    private ReservationSubject       reservationSubject;
    private ReservationServiceImpl   reservationService;
    private Client                   client;

    @BeforeEach
    void setUp() {
        List<IReservationObserver> observers = List.of(emailObserver, smsObserver);
        reservationSubject = new ReservationSubject(observers);

        // Constructeur complet : repository + clientRepo + vehiculeRepo + mapper + subject
        reservationService = new ReservationServiceImpl(
                reservationRepository,
                clientRepository,
                vehiculeRepository,
                reservationMapper,
                reservationSubject);

        client = new Client();
        client.setNom("Trabelsi");
        client.setPrenom("Sarra");
        client.setEmail("sarra.trabelsi@example.com");
        client.setTelephone("20123456");

        // Le mapper retourne un DTO avec statut CONFIRMEE par défaut
        when(reservationMapper.toDto(any(Reservation.class)))
                .thenAnswer(inv -> {
                    Reservation r = inv.getArgument(0);
                    return ReservationResponseDTO.builder()
                            .statut(r.getStatut())
                            .build();
                });
    }

    // =========================================================================
    // Test 1 : confirmation de réservation
    // =========================================================================

    @Test
    void creerReservation_doitNotifierEmailEtSms() {
        System.out.println("[TEST] creerReservation() → les deux observateurs doivent être notifiés");

        ReservationResponseDTO dto = reservationService.creerReservation(client);

        assertEquals(StatutReservation.CONFIRMEE, dto.getStatut(),
                "Le statut doit être CONFIRMEE après création");

        verify(emailObserver, times(1)).onReservationConfirmee(any(Reservation.class));
        verify(smsObserver,   times(1)).onReservationConfirmee(any(Reservation.class));

        System.out.println("   OK  [EMAIL] onReservationConfirmee appelé");
        System.out.println("   OK  [SMS]   onReservationConfirmee appelé");
    }

    // =========================================================================
    // Test 2 : annulation de réservation
    // =========================================================================

    @Test
    void annulerReservation_doitNotifierEmailEtSms() {
        System.out.println("[TEST] annulerReservation() → les deux observateurs doivent être notifiés");

        // Crée une réservation interne (via la méthode package-private qui travaille sur Reservation)
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setStatut(StatutReservation.CONFIRMEE);

        org.mockito.Mockito.clearInvocations(emailObserver, smsObserver);

        ReservationResponseDTO dto = reservationService.annulerReservation(reservation);

        assertEquals(StatutReservation.ANNULEE, dto.getStatut(),
                "Le statut doit être ANNULEE après annulation");

        verify(emailObserver, times(1)).onReservationAnnulee(reservation);
        verify(smsObserver,   times(1)).onReservationAnnulee(reservation);

        System.out.println("   OK  [EMAIL] onReservationAnnulee appelé");
        System.out.println("   OK  [SMS]   onReservationAnnulee appelé");
    }
}
