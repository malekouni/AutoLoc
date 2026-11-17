package tn.esprit.tpautoloc.service;

import java.util.List;
import tn.esprit.tpautoloc.web.dto.EmployeDto;

public interface IEmployeService {

    EmployeDto create(EmployeDto dto);

    EmployeDto findById(Long id);

    List<EmployeDto> findAll();

    EmployeDto update(Long id, EmployeDto dto);

    void deleteById(Long id);

    void affecterEmployeAAgence(Long employeId, Long agenceId);

    void desaffecterEmployeDeAgence(Long employeId);
}
