package tn.esprit.tpautoloc.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.tpautoloc.domain.Employe;
import tn.esprit.tpautoloc.web.dto.EmployeDto;

@Mapper(componentModel = "spring")
public interface EmployeMapper {

    @Mapping(target = "idAgence", source = "agence.idAgence")
    EmployeDto toDto(Employe employe);

    @Mapping(target = "agence", ignore = true)
    Employe toEntity(EmployeDto dto);
}
