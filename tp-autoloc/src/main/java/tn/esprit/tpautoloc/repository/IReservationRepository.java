package tn.esprit.tpautoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tpautoloc.domain.Reservation;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {
}
