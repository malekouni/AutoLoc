package tn.esprit.tpautoloc.service;

import java.util.List;
import tn.esprit.tpautoloc.web.dto.VehiculeDto;

public interface IVehiculeService {

    VehiculeDto create(VehiculeDto dto);

    VehiculeDto findById(Long id);

    List<VehiculeDto> findAll();

    VehiculeDto update(Long id, VehiculeDto dto);

    void deleteById(Long id);

    void affecterVehiculeAAgence(Long vehiculeId, Long agenceId);

    void desaffecterVehiculeDeAgence(Long vehiculeId);
}
