package tn.esprit.tpautoloc.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.tpautoloc.domain.Client;
import tn.esprit.tpautoloc.web.dto.ClientDto;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDto toDto(Client client);

    @Mapping(target = "reservations", ignore = true)
    Client toEntity(ClientDto dto);
}
