package tn.esprit.tpautoloc.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.tpautoloc.domain.Reservation;
import tn.esprit.tpautoloc.web.dto.ReservationResponseDTO;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "id",                    source = "idReservation")
    @Mapping(target = "clientId",              source = "client.idClient")
    @Mapping(target = "clientNomComplet",
             expression = "java(reservation.getClient().getPrenom() + \" \" + reservation.getClient().getNom())")
    @Mapping(target = "vehiculeId",            source = "vehicule.idVehicule")
    @Mapping(target = "vehiculeImmatriculation", source = "vehicule.immatriculation")
    @Mapping(target = "tarifEstime",           ignore = true)
    ReservationResponseDTO toDto(Reservation reservation);
}
