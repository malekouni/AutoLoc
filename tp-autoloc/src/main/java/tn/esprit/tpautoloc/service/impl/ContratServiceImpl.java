package tn.esprit.tpautoloc.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpautoloc.domain.Contrat;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.repository.IContratRepository;
import tn.esprit.tpautoloc.service.IContratService;
import tn.esprit.tpautoloc.web.dto.ContratDto;
import tn.esprit.tpautoloc.web.mapper.ContratMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final ContratMapper contratMapper;

    @Override
    public ContratDto create(ContratDto dto) {
        if (dto.getIdContrat() != null) {
            throw new IllegalArgumentException("Un nouveau contrat ne doit pas avoir d'identifiant");
        }
        if (dto.getMontantTotal() != null && dto.getMontantTotal().signum() < 0) {
            throw new IllegalArgumentException("Le montant total ne peut pas être négatif");
        }
        Contrat contrat = contratMapper.toEntity(dto);
        // Rétablir la relation bidirectionnelle si des paiements sont fournis
        if (dto.getPaiements() != null) {
            contrat.getPaiements().forEach(p -> p.setContrat(contrat));
        }
        return contratMapper.toDto(contratRepository.save(contrat));
    }

    @Override
    @Transactional(readOnly = true)
    public ContratDto findById(Long id) {
        return contratMapper.toDto(
                contratRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Contrat", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratDto> findAll() {
        return contratRepository.findAll().stream()
                .map(contratMapper::toDto)
                .toList();
    }

    @Override
    public ContratDto update(Long id, ContratDto dto) {
        Contrat existant = contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
        if (dto.getMontantTotal() != null && dto.getMontantTotal().signum() < 0) {
            throw new IllegalArgumentException("Le montant total ne peut pas être négatif");
        }
        existant.setDateSignature(dto.getDateSignature());
        existant.setMontantTotal(dto.getMontantTotal());
        existant.setValide(dto.isValide());
        return contratMapper.toDto(contratRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!contratRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contrat", id);
        }
        contratRepository.deleteById(id);
    }
}
