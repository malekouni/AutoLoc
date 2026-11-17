package tn.esprit.tpautoloc.service;

import java.util.List;
import tn.esprit.tpautoloc.web.dto.ClientDto;

public interface IClientService {

    ClientDto create(ClientDto dto);

    ClientDto findById(Long id);

    List<ClientDto> findAll();

    ClientDto update(Long id, ClientDto dto);

    void deleteById(Long id);
}
