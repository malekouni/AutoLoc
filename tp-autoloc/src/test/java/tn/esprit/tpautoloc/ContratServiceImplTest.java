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

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test unitaire du service Contrat : SANS Spring et SANS base de données.
 *
 * Il illustre l'intérêt de l'injection par constructeur : on fabrique le service avec "new"
 * en lui passant un repository factice (mock), ce qui est impossible avec une injection par attribut.
 *
 * Hypothèses sur l'entité Contrat (à adapter si besoin) : setIdContrat(Long) et
 * setMontantTotal(BigDecimal) générés par Lombok.
 */
@ExtendWith(MockitoExtension.class)
class ContratServiceImplTest {

    @Mock
    private IContratRepository contratRepository;

    private ContratServiceImpl contratService;

    @BeforeEach
    void creerLeService() {
        // Aucun conteneur Spring : le "new" est écrit ici, et la dépendance est fournie à la main.
        contratService = new ContratServiceImpl(contratRepository);
    }

    private Contrat contrat(Long id, String montant) {
        Contrat contrat = new Contrat();
        contrat.setIdContrat(id);
        contrat.setMontantTotal(new BigDecimal(montant));
        return contrat;
    }

    @Test
    void createRefuseUnIdentifiantNonNull() {
        System.out.println("[TEST] create() avec un id deja renseigne -> IllegalArgumentException attendue");
        Contrat avecId = contrat(5L, "500");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> contratService.create(avecId));

        System.out.println("   message : " + e.getMessage());
        verify(contratRepository, never()).save(any());
        System.out.println("   OK  save() n'a jamais ete appele");
    }

    @Test
    void createRefuseUnMontantNegatif() {
        System.out.println("[TEST] create() avec un montant negatif -> IllegalArgumentException attendue");
        Contrat negatif = contrat(null, "-1");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> contratService.create(negatif));

        System.out.println("   message : " + e.getMessage());
        verify(contratRepository, never()).save(any());
        System.out.println("   OK  save() n'a jamais ete appele");
    }

    @Test
    void createEnregistreUnContratValide() {
        System.out.println("[TEST] create() avec un contrat valide -> save() appele une fois");
        Contrat valide = contrat(null, "500");
        when(contratRepository.save(valide)).thenReturn(valide);

        Contrat resultat = contratService.create(valide);

        assertSame(valide, resultat);
        verify(contratRepository).save(valide);
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
        Contrat existant = contrat(1L, "100");
        Contrat donnees = contrat(null, "250");
        when(contratRepository.findById(1L)).thenReturn(Optional.of(existant));
        when(contratRepository.save(existant)).thenReturn(existant);

        Contrat resultat = contratService.update(1L, donnees);

        assertSame(existant, resultat);
        assertEquals(1L, resultat.getIdContrat());
        assertEquals(0, new BigDecimal("250").compareTo(resultat.getMontantTotal()));
        verify(contratRepository).save(existant);
        verify(contratRepository, never()).save(donnees);
        System.out.println("   OK  l'entite chargee a ete mise a jour, l'objet recu n'a jamais ete sauvegarde");
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
