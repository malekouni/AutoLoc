package tn.esprit.tpautoloc.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpautoloc.domain.Agence;
import tn.esprit.tpautoloc.domain.Employe;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.repository.IAgenceRepository;
import tn.esprit.tpautoloc.repository.IEmployeRepository;
import tn.esprit.tpautoloc.service.IEmployeService;
import tn.esprit.tpautoloc.web.dto.EmployeDto;
import tn.esprit.tpautoloc.web.mapper.EmployeMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeServiceImpl implements IEmployeService {

    private final IEmployeRepository employeRepository;
    private final IAgenceRepository agenceRepository;
    private final EmployeMapper employeMapper;

    @Override
    public EmployeDto create(EmployeDto dto) {
        if (dto.getIdEmploye() != null) {
            throw new IllegalArgumentException("Un nouvel employé ne doit pas avoir d'identifiant");
        }
        Employe employe = employeMapper.toEntity(dto);
        if (dto.getIdAgence() != null) {
            Agence agence = agenceRepository.findById(dto.getIdAgence())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence", dto.getIdAgence()));
            employe.setAgence(agence);
        }
        return employeMapper.toDto(employeRepository.save(employe));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeDto findById(Long id) {
        return employeMapper.toDto(
                employeRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Employe", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeDto> findAll() {
        return employeRepository.findAll().stream()
                .map(employeMapper::toDto)
                .toList();
    }

    @Override
    public EmployeDto update(Long id, EmployeDto dto) {
        Employe existant = employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
        existant.setNom(dto.getNom());
        existant.setPrenom(dto.getPrenom());
        existant.setRole(dto.getRole());
        return employeMapper.toDto(employeRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!employeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employe", id);
        }
        employeRepository.deleteById(id);
    }

    @Override
    public void affecterEmployeAAgence(Long employeId, Long agenceId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", employeId));
        Agence agence = agenceRepository.findById(agenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", agenceId));
        employe.setAgence(agence);
        employeRepository.save(employe);
    }

    @Override
    public void desaffecterEmployeDeAgence(Long employeId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", employeId));
        if (employe.getAgence() == null) {
            throw new IllegalStateException("L'employé " + employeId + " n'est rattaché à aucune agence");
        }
        employe.setAgence(null);
        employeRepository.save(employe);
    }
}
