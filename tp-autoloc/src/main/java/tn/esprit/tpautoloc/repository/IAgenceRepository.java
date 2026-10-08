package tn.esprit.tpautoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tpautoloc.domain.Agence;

public interface IAgenceRepository extends JpaRepository<Agence, Long> {
}
