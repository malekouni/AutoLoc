package tn.esprit.tpautoloc.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpautoloc.domain.Client;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.repository.IClientRepository;
import tn.esprit.tpautoloc.service.IClientService;
import tn.esprit.tpautoloc.web.dto.ClientDto;
import tn.esprit.tpautoloc.web.mapper.ClientMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientDto create(ClientDto dto) {
        if (dto.getIdClient() != null) {
            throw new IllegalArgumentException("Un nouveau client ne doit pas avoir d'identifiant");
        }
        Client saved = clientRepository.save(clientMapper.toEntity(dto));
        return clientMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDto findById(Long id) {
        return clientMapper.toDto(
                clientRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Client", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> findAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .toList();
    }

    @Override
    public ClientDto update(Long id, ClientDto dto) {
        Client existant = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        existant.setNom(dto.getNom());
        existant.setPrenom(dto.getPrenom());
        existant.setEmail(dto.getEmail());
        existant.setTelephone(dto.getTelephone());
        existant.setNumPermis(dto.getNumPermis());
        existant.setDateInscription(dto.getDateInscription());
        return clientMapper.toDto(clientRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", id);
        }
        clientRepository.deleteById(id);
    }
}
