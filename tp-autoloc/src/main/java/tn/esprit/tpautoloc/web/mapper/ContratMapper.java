package tn.esprit.tpautoloc.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.tpautoloc.domain.Contrat;
import tn.esprit.tpautoloc.web.dto.ContratDto;

@Mapper(componentModel = "spring", uses = PaiementMapper.class)
public interface ContratMapper {

    @Mapping(target = "paiements", source = "paiements")
    ContratDto toDto(Contrat contrat);

    @Mapping(target = "reservation", ignore = true)
    @Mapping(target = "paiements", source = "paiements")
    Contrat toEntity(ContratDto dto);
}
