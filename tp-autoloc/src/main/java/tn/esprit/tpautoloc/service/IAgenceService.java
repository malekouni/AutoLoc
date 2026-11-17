package tn.esprit.tpautoloc.service;

import java.util.List;
import tn.esprit.tpautoloc.web.dto.AgenceDto;

public interface IAgenceService {

    AgenceDto create(AgenceDto dto);

    AgenceDto findById(Long id);

    List<AgenceDto> findAll();

    AgenceDto update(Long id, AgenceDto dto);

    void deleteById(Long id);
}
