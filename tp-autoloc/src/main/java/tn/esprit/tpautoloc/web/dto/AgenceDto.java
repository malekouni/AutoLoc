package tn.esprit.tpautoloc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgenceDto {

    private Long idAgence;
    private String nom;
    private String ville;
    private String adresse;
    private String telephone;
}
