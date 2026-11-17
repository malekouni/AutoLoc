package tn.esprit.tpautoloc.service;

import java.util.List;
import tn.esprit.tpautoloc.web.dto.ContratDto;

public interface IContratService {

    ContratDto create(ContratDto dto);

    ContratDto findById(Long id);

    List<ContratDto> findAll();

    ContratDto update(Long id, ContratDto dto);

    void deleteById(Long id);
}
