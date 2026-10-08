package tn.esprit.tpautoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tpautoloc.domain.Contrat;

public interface IContratRepository extends JpaRepository<Contrat, Long> {
}
