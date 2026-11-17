package tn.esprit.tpautoloc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpautoloc.domain.Contrat;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.repository.IContratRepository;
import tn.esprit.tpautoloc.service.impl.ContratServiceImpl;
import tn.esprit.tpautoloc.web.dto.ContratDto;
import tn.esprit.tpautoloc.web.mapper.ContratMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test unitaire du service Contrat : SANS Spring et SANS base de données.
 * Mis à jour pour utiliser les DTOs introduits à l'Atelier 7.
 */
@ExtendWith(MockitoExtension.class)
class ContratServiceImplTest {

    @Mock
    private IContratRepository contratRepository;

    @Mock
    private ContratMapper contratMapper;

    private ContratServiceImpl contratService;

    @BeforeEach
    void creerLeService() {
        contratService = new ContratServiceImpl(contratRepository, contratMapper);
    }

    /** Crée un ContratDto de test. */
    private ContratDto dto(Long id, String montant) {
        return ContratDto.builder()
                .idContrat(id)
                .montantTotal(montant == null ? null : new BigDecimal(montant))
                .build();
    }

    /** Crée un Contrat entité (retourné par le mock du repository). */
    private Contrat entite(Long id, String montant) {
        Contrat c = new Contrat();
        c.setIdContrat(id);
        c.setMontantTotal(montant == null ? null : new BigDecimal(montant));
        return c;
    }

    @Test
    void createRefuseUnIdentifiantNonNull() {
        System.out.println("[TEST] create() avec un id deja renseigne -> IllegalArgumentException attendue");
        ContratDto avecId = dto(5L, "500");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> contratService.create(avecId));

        System.out.println("   message : " + e.getMessage());
        verify(contratRepository, never()).save(any());
        System.out.println("   OK  save() n'a jamais ete appele");
    }

    @Test
    void createRefuseUnMontantNegatif() {
        System.out.println("[TEST] create() avec un montant negatif -> IllegalArgumentException attendue");
        ContratDto negatif = dto(null, "-1");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> contratService.create(negatif));

        System.out.println("   message : " + e.getMessage());
        verify(contratRepository, never()).save(any());
        System.out.println("   OK  save() n'a jamais ete appele");
    }

    @Test
    void createEnregistreUnContratValide() {
        System.out.println("[TEST] create() avec un contrat valide -> save() appele une fois");
        ContratDto valide = dto(null, "500");
        Contrat entite = entite(1L, "500");
        ContratDto retour = dto(1L, "500");

        when(contratMapper.toEntity(valide)).thenReturn(entite);
        when(contratRepository.save(entite)).thenReturn(entite);
        when(contratMapper.toDto(entite)).thenReturn(retour);

        ContratDto resultat = contratService.create(valide);

        assertNotNull(resultat);
        assertEquals(1L, resultat.getIdContrat());
        verify(contratRepository).save(entite);
        System.out.println("   OK  contrat enregistre");
    }

    @Test
    void findByIdLeveResourceNotFoundExceptionSiAbsent() {
        System.out.println("[TEST] findById(99) sur un id absent -> ResourceNotFoundException attendue");
        when(contratRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> contratService.findById(99L));

        assertEquals("Contrat introuvable avec l'identifiant 99", e.getMessage());
        System.out.println("   OK  message : " + e.getMessage());
    }

    @Test
    void updateChargeLEntiteExistantePuisRecopie() {
        System.out.println("[TEST] update() charge l'existant, recopie le montant, puis save()");
        Contrat existant = entite(1L, "100");
        ContratDto donnees = dto(null, "250");
        ContratDto retour = dto(1L, "250");

        when(contratRepository.findById(1L)).thenReturn(Optional.of(existant));
        when(contratRepository.save(existant)).thenReturn(existant);
        when(contratMapper.toDto(existant)).thenReturn(retour);

        ContratDto resultat = contratService.update(1L, donnees);

        assertEquals(1L, resultat.getIdContrat());
        assertEquals(0, new BigDecimal("250").compareTo(resultat.getMontantTotal()));
        verify(contratRepository).save(existant);
        System.out.println("   OK  l'entite chargee a ete mise a jour");
    }

    @Test
    void deleteByIdLeveExceptionSiAbsent() {
        System.out.println("[TEST] deleteById(7) sur un id absent -> ResourceNotFoundException, aucune suppression");
        when(contratRepository.existsById(7L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> contratService.deleteById(7L));

        verify(contratRepository, never()).deleteById(7L);
        System.out.println("   OK  deleteById() du repository n'a pas ete appele");
    }

    @Test
    void deleteByIdSupprimeSiPresent() {
        System.out.println("[TEST] deleteById(7) sur un id existant -> suppression deleguee au repository");
        when(contratRepository.existsById(7L)).thenReturn(true);

        contratService.deleteById(7L);

        verify(contratRepository).deleteById(7L);
        System.out.println("   OK  contrat supprime");
    }
}
