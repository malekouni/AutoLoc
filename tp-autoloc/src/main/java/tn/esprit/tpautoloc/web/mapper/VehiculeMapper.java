package tn.esprit.tpautoloc.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.tpautoloc.domain.Vehicule;
import tn.esprit.tpautoloc.web.dto.VehiculeDto;

@Mapper(componentModel = "spring")
public interface VehiculeMapper {

    @Mapping(target = "idAgence", source = "agence.idAgence")
    @Mapping(target = "idEquipements", ignore = true)
    VehiculeDto toDto(Vehicule vehicule);

    @Mapping(target = "agence", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "maintenances", ignore = true)
    @Mapping(target = "equipements", ignore = true)
    Vehicule toEntity(VehiculeDto dto);
}
