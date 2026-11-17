package tn.esprit.tpautoloc.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpautoloc.domain.Agence;
import tn.esprit.tpautoloc.domain.Vehicule;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.repository.IAgenceRepository;
import tn.esprit.tpautoloc.repository.IVehiculeRepository;
import tn.esprit.tpautoloc.service.IVehiculeService;
import tn.esprit.tpautoloc.web.dto.VehiculeDto;
import tn.esprit.tpautoloc.web.mapper.VehiculeMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IAgenceRepository agenceRepository;
    private final VehiculeMapper vehiculeMapper;

    @Override
    public VehiculeDto create(VehiculeDto dto) {
        if (dto.getIdVehicule() != null) {
            throw new IllegalArgumentException("Un nouveau véhicule ne doit pas avoir d'identifiant");
        }
        Vehicule vehicule = vehiculeMapper.toEntity(dto);
        if (dto.getIdAgence() != null) {
            Agence agence = agenceRepository.findById(dto.getIdAgence())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence", dto.getIdAgence()));
            vehicule.setAgence(agence);
        }
        return vehiculeMapper.toDto(vehiculeRepository.save(vehicule));
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeDto findById(Long id) {
        return vehiculeMapper.toDto(
                vehiculeRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeDto> findAll() {
        return vehiculeRepository.findAll().stream()
                .map(vehiculeMapper::toDto)
                .toList();
    }

    @Override
    public VehiculeDto update(Long id, VehiculeDto dto) {
        Vehicule existant = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
        existant.setImmatriculation(dto.getImmatriculation());
        existant.setMarque(dto.getMarque());
        existant.setModele(dto.getModele());
        existant.setCategorie(dto.getCategorie());
        existant.setTarifJournalier(dto.getTarifJournalier());
        existant.setStatut(dto.getStatut());
        return vehiculeMapper.toDto(vehiculeRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicule", id);
        }
        vehiculeRepository.deleteById(id);
    }

    @Override
    public void affecterVehiculeAAgence(Long vehiculeId, Long agenceId) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", vehiculeId));
        Agence agence = agenceRepository.findById(agenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", agenceId));
        vehicule.setAgence(agence);
        vehiculeRepository.save(vehicule);
    }

    @Override
    public void desaffecterVehiculeDeAgence(Long vehiculeId) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", vehiculeId));
        if (vehicule.getAgence() == null) {
            throw new IllegalStateException("Le véhicule " + vehiculeId + " n'est rattaché à aucune agence");
        }
        vehicule.setAgence(null);
        vehiculeRepository.save(vehicule);
    }
}
