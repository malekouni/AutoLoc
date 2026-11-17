package tn.esprit.tpautoloc.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.tpautoloc.domain.Paiement;
import tn.esprit.tpautoloc.web.dto.PaiementDto;

@Mapper(componentModel = "spring")
public interface PaiementMapper {

    @Mapping(target = "idContrat", source = "contrat.idContrat")
    PaiementDto toDto(Paiement paiement);

    @Mapping(target = "contrat", ignore = true)
    Paiement toEntity(PaiementDto dto);
}
