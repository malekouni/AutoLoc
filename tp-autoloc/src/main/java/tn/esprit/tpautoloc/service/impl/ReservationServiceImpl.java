package tn.esprit.tpautoloc.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpautoloc.domain.Client;
import tn.esprit.tpautoloc.domain.Reservation;
import tn.esprit.tpautoloc.domain.Vehicule;
import tn.esprit.tpautoloc.domain.enums.StatutReservation;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.repository.IClientRepository;
import tn.esprit.tpautoloc.repository.IReservationRepository;
import tn.esprit.tpautoloc.repository.IVehiculeRepository;
import tn.esprit.tpautoloc.service.IReservationService;
import tn.esprit.tpautoloc.service.observer.ReservationSubject;
import tn.esprit.tpautoloc.web.dto.ReservationRequestDTO;
import tn.esprit.tpautoloc.web.dto.ReservationResponseDTO;
import tn.esprit.tpautoloc.web.mapper.ReservationMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements IReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final IReservationRepository reservationRepository;
    private final IClientRepository clientRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationSubject reservationSubject;

    @Override
    public ReservationResponseDTO create(ReservationRequestDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", dto.getClientId()));
        Vehicule vehicule = vehiculeRepository.findById(dto.getVehiculeId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", dto.getVehiculeId()));

        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        reservation.setDateDebut(dto.getDateDebut());
        reservation.setDateFin(dto.getDateFin());
        reservation.setStatut(StatutReservation.CONFIRMEE);

        Reservation saved = reservationRepository.save(reservation);
        reservationSubject.notifierConfirmation(saved);
        return reservationMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponseDTO findById(Long id) {
        return reservationMapper.toDto(
                reservationRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Reservation", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .toList();
    }

    @Override
    public ReservationResponseDTO update(Long id, ReservationRequestDTO dto) {
        Reservation existant = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
        if (dto.getDateDebut() != null) existant.setDateDebut(dto.getDateDebut());
        if (dto.getDateFin() != null)   existant.setDateFin(dto.getDateFin());
        return reservationMapper.toDto(reservationRepository.save(existant));
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

    public ReservationResponseDTO creerReservation(Client client) {
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservationSubject.notifierConfirmation(reservation);
        return reservationMapper.toDto(reservation);
    }

    public ReservationResponseDTO annulerReservation(Reservation reservation) {
        reservation.setStatut(StatutReservation.ANNULEE);
        log.info("Réservation #{} annulée pour le client {}",
                reservation.getIdReservation(),
                reservation.getClient().getEmail());
        reservationSubject.notifierAnnulation(reservation);
        return reservationMapper.toDto(reservation);
    }
}
