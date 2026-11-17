package tn.esprit.tpautoloc.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpautoloc.domain.Agence;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.repository.IAgenceRepository;
import tn.esprit.tpautoloc.service.IAgenceService;
import tn.esprit.tpautoloc.web.dto.AgenceDto;
import tn.esprit.tpautoloc.web.mapper.AgenceMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;
    private final AgenceMapper agenceMapper;

    @Override
    public AgenceDto create(AgenceDto dto) {
        if (dto.getIdAgence() != null) {
            throw new IllegalArgumentException("Une nouvelle agence ne doit pas avoir d'identifiant");
        }
        Agence saved = agenceRepository.save(agenceMapper.toEntity(dto));
        return agenceMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AgenceDto findById(Long id) {
        return agenceMapper.toDto(
                agenceRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Agence", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgenceDto> findAll() {
        return agenceRepository.findAll().stream()
                .map(agenceMapper::toDto)
                .toList();
    }

    @Override
    public AgenceDto update(Long id, AgenceDto dto) {
        Agence existant = agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
        existant.setNom(dto.getNom());
        existant.setVille(dto.getVille());
        existant.setAdresse(dto.getAdresse());
        existant.setTelephone(dto.getTelephone());
        return agenceMapper.toDto(agenceRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agence", id);
        }
        agenceRepository.deleteById(id);
    }
}
