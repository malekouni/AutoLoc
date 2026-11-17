package tn.esprit.tpautoloc.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.tpautoloc.domain.Agence;
import tn.esprit.tpautoloc.web.dto.AgenceDto;

@Mapper(componentModel = "spring")
public interface AgenceMapper {

    @Mapping(target = "idAgence", source = "idAgence")
    AgenceDto toDto(Agence agence);

    @Mapping(target = "employes", ignore = true)
    @Mapping(target = "vehicules", ignore = true)
    Agence toEntity(AgenceDto dto);
}
