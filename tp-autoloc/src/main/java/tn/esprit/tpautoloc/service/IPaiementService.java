package tn.esprit.tpautoloc.service;

import java.util.List;
import tn.esprit.tpautoloc.web.dto.PaiementDto;

public interface IPaiementService {

    PaiementDto findById(Long id);

    List<PaiementDto> findAll();
}
