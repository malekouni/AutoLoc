package tn.esprit.tpautoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tpautoloc.domain.Client;

public interface IClientRepository extends JpaRepository<Client, Long> {
}
