package tn.esprit.tpautoloc.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.repository.IPaiementRepository;
import tn.esprit.tpautoloc.service.IPaiementService;
import tn.esprit.tpautoloc.web.dto.PaiementDto;
import tn.esprit.tpautoloc.web.mapper.PaiementMapper;

/**
 * Lecture seule : un paiement est créé et supprimé uniquement via son Contrat.
 */
@Service
@RequiredArgsConstructor
public class PaiementServiceImpl implements IPaiementService {

    private final IPaiementRepository paiementRepository;
    private final PaiementMapper paiementMapper;

    @Override
    @Transactional(readOnly = true)
    public PaiementDto findById(Long id) {
        return paiementMapper.toDto(
                paiementRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Paiement", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementDto> findAll() {
        return paiementRepository.findAll().stream()
                .map(paiementMapper::toDto)
                .toList();
    }
}
