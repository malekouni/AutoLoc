package tn.esprit.tpautoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tpautoloc.domain.Employe;

public interface IEmployeRepository extends JpaRepository<Employe, Long> {
}
