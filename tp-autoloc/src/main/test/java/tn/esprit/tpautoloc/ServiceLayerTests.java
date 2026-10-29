package tn.esprit.tpautoloc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import tn.esprit.tpautoloc.domain.Agence;
import tn.esprit.tpautoloc.domain.Client;
import tn.esprit.tpautoloc.domain.Equipement;
import tn.esprit.tpautoloc.domain.Vehicule;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.service.IAgenceService;
import tn.esprit.tpautoloc.service.IClientService;
import tn.esprit.tpautoloc.service.IContratService;
import tn.esprit.tpautoloc.service.IEmployeService;
import tn.esprit.tpautoloc.service.IEquipementService;
import tn.esprit.tpautoloc.service.IMaintenanceService;
import tn.esprit.tpautoloc.service.IPaiementService;
import tn.esprit.tpautoloc.service.IReservationService;
import tn.esprit.tpautoloc.service.IVehiculeService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Atelier 4 : test d'intégration de la couche Service (démarre Spring et utilise MySQL).
 *
 * Premier test : le conteneur a bien créé les neuf services (IoC / DI).
 * Les suivants : le service applique ses règles de gestion et traduit l'absence en exception métier.
 */
@SpringBootTest
class ServiceLayerTests {

    @Autowired
    private ApplicationContext contexte;
    @Autowired
    private IAgenceService agenceService;
    @Autowired
    private IClientService clientService;
    @Autowired
    private IEquipementService equipementService;
    @Autowired
    private IVehiculeService vehiculeService;
    @Autowired
    private IPaiementService paiementService;

    // ------------------------------------------------------------------
    // IoC / DI : les neuf services sont des beans fournis par le conteneur
    // ------------------------------------------------------------------

    @Test
    void lesNeufServicesSontDesBeans() {
        System.out.println("[TEST] le conteneur Spring fournit les neuf services");
        List<Class<?>> interfaces = List.of(
                IAgenceService.class, IEmployeService.class, IVehiculeService.class,
                IEquipementService.class, IClientService.class, IReservationService.class,
                IContratService.class, IPaiementService.class, IMaintenanceService.class);

        for (Class<?> type : interfaces) {
            Object bean = contexte.getBean(type);
            assertNotNull(bean);
            System.out.println("   " + type.getSimpleName() + " -> " + bean.getClass().getSimpleName());
        }
        assertEquals(9, interfaces.size());
        System.out.println("   OK  9 services detectes par le scan et injectes");
    }

    // ------------------------------------------------------------------
    // Agence : cycle CRUD complet par le service
    // ------------------------------------------------------------------

    @Test
    void agenceCycleCompletParLeService() {
        System.out.println("[TEST] AgenceService : create, findById, update, findAll, deleteById");

        Agence nouvelle = new Agence();
        nouvelle.setNom("Agence Test Service");
        nouvelle.setVille("Tunis");
        Agence creee = agenceService.create(nouvelle);
        System.out.println("   create()     -> idAgence=" + creee.getIdAgence());
        assertNotNull(creee.getIdAgence());

        Agence lue = agenceService.findById(creee.getIdAgence());
        assertEquals("Agence Test Service", lue.getNom());
        System.out.println("   findById()   -> nom=" + lue.getNom());

        Agence donnees = new Agence();
        donnees.setNom("Agence Test Service");
        donnees.setVille("Sfax");
        Agence modifiee = agenceService.update(creee.getIdAgence(), donnees);
        assertEquals("Sfax", modifiee.getVille());
        assertEquals(creee.getIdAgence(), modifiee.getIdAgence());
        System.out.println("   update()     -> ville=" + modifiee.getVille() + " (meme id)");

        assertTrue(agenceService.findAll().stream()
                .anyMatch(a -> creee.getIdAgence().equals(a.getIdAgence())));
        System.out.println("   findAll()    -> l'agence est dans la liste");

        agenceService.deleteById(creee.getIdAgence());
        assertThrows(ResourceNotFoundException.class, () -> agenceService.findById(creee.getIdAgence()));
        System.out.println("   deleteById() -> findById leve ResourceNotFoundException : OK");
    }

    @Test
    void agenceCreateRefuseUnIdentifiant() {
        System.out.println("[TEST] AgenceService.create() avec un id renseigne -> IllegalArgumentException");
        Agence avecId = new Agence();
        avecId.setIdAgence(1L);
        avecId.setNom("Ne doit pas etre creee");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> agenceService.create(avecId));
        System.out.println("   OK  message : " + e.getMessage());
    }

    @Test
    void agenceUpdateEtDeleteSurUnIdAbsent() {
        System.out.println("[TEST] AgenceService : update et deleteById sur un id absent -> ResourceNotFoundException");
        Long idAbsent = Long.MAX_VALUE;

        assertThrows(ResourceNotFoundException.class, () -> agenceService.update(idAbsent, new Agence()));
        assertThrows(ResourceNotFoundException.class, () -> agenceService.deleteById(idAbsent));
        System.out.println("   OK  les deux appels ont leve ResourceNotFoundException");
    }

    // ------------------------------------------------------------------
    // Règles de gestion
    // ------------------------------------------------------------------

    @Test
    void agenceSansNomEstRefusee() {
        System.out.println("[TEST] Agence sans nom -> IllegalArgumentException");
        Agence sansNom = new Agence();
        sansNom.setNom("  ");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> agenceService.create(sansNom));
        System.out.println("   OK  message : " + e.getMessage());
    }

    @Test
    void equipementSansLibelleEstRefuse() {
        System.out.println("[TEST] Equipement sans libelle -> IllegalArgumentException");
        Equipement sansLibelle = new Equipement();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> equipementService.create(sansLibelle));
        System.out.println("   OK  message : " + e.getMessage());
    }

    @Test
    void clientSansEmailEstRefuse() {
        System.out.println("[TEST] Client sans e-mail -> IllegalArgumentException");
        Client sansEmail = new Client();
        sansEmail.setNom("Trabelsi");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> clientService.create(sansEmail));
        System.out.println("   OK  message : " + e.getMessage());
    }

    @Test
    void vehiculeAvecTarifNegatifEstRefuse() {
        System.out.println("[TEST] Vehicule avec un tarif journalier negatif -> IllegalArgumentException");
        Vehicule vehicule = new Vehicule();
        vehicule.setTarifJournalier(new BigDecimal("-10"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> vehiculeService.create(vehicule));
        System.out.println("   OK  message : " + e.getMessage());
    }

    // ------------------------------------------------------------------
    // Paiement : lecture seule
    // ------------------------------------------------------------------

    @Test
    void paiementEstEnLectureSeule() {
        System.out.println("[TEST] PaiementService : findAll() fonctionne, findById() d'un id absent leve l'exception");
        assertNotNull(paiementService.findAll());
        assertThrows(ResourceNotFoundException.class, () -> paiementService.findById(Long.MAX_VALUE));
        System.out.println("   OK  lecture seule : l'interface n'expose ni create, ni update, ni deleteById");
    }
}
