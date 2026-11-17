package tn.esprit.tpautoloc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.tpautoloc.domain.Equipement;
import tn.esprit.tpautoloc.domain.Maintenance;
import tn.esprit.tpautoloc.domain.enums.CategorieVehicule;
import tn.esprit.tpautoloc.domain.enums.RoleEmploye;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.service.IAgenceService;
import tn.esprit.tpautoloc.service.IClientService;
import tn.esprit.tpautoloc.service.IEmployeService;
import tn.esprit.tpautoloc.service.IEquipementService;
import tn.esprit.tpautoloc.service.IMaintenanceService;
import tn.esprit.tpautoloc.service.IVehiculeService;
import tn.esprit.tpautoloc.web.dto.AgenceDto;
import tn.esprit.tpautoloc.web.dto.ClientDto;
import tn.esprit.tpautoloc.web.dto.EmployeDto;
import tn.esprit.tpautoloc.web.dto.VehiculeDto;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Atelier 4 / 7 — Tests d'intégration de la couche Service.
 * Mis à jour pour utiliser les DTOs introduits à l'Atelier 7.
 */
@SpringBootTest
class ServiceCrudTests {

    private static final String LIGNE = "=".repeat(78);

    @Autowired private IAgenceService     agenceService;
    @Autowired private IClientService     clientService;
    @Autowired private IVehiculeService   vehiculeService;
    @Autowired private IEquipementService equipementService;
    @Autowired private IEmployeService    employeService;
    @Autowired private IMaintenanceService maintenanceService;

    // ------------------------------------------------------------------
    // Traces
    // ------------------------------------------------------------------
    private static void debutTest(String nom) {
        System.out.println();
        System.out.println(LIGNE);
        System.out.println(" TEST : " + nom);
        System.out.println(LIGNE);
    }
    private static void finTest(String nom) {
        System.out.println("--- FIN " + nom + " : tous les controles sont passes ---");
        System.out.println(LIGNE);
    }
    private static void etape(String e, int n, int t, String desc) {
        System.out.println("\n[" + e + "] ETAPE " + n + "/" + t + " - " + desc);
    }
    private static void info(String e, String msg)     { System.out.println("[" + e + "]     -> " + msg); }
    private static void ok(String e, String msg)       { System.out.println("[" + e + "]     OK  " + msg); }
    private static void nettoyage(String e, String msg){ System.out.println("[" + e + "] NETTOYAGE - " + msg); }

    // ------------------------------------------------------------------
    // Fabriques de DTOs
    // ------------------------------------------------------------------
    private AgenceDto dtoAgence() {
        return AgenceDto.builder()
                .nom("AutoLoc Tunis Centre")
                .ville("Tunis")
                .adresse("10 Avenue Habib Bourguiba")
                .telephone("71000000")
                .build();
    }

    private ClientDto dtoClient(String nom, String prenom, String email, String numPermis) {
        return ClientDto.builder()
                .nom(nom).prenom(prenom).email(email)
                .telephone("20123456").numPermis(numPermis)
                .build();
    }

    private VehiculeDto dtoVehicule(Long idAgence) {
        return VehiculeDto.builder()
                .immatriculation("123 TUN 4567")
                .marque("Peugeot").modele("208")
                .categorie(CategorieVehicule.CITADINE)
                .tarifJournalier(new BigDecimal("85.00"))
                .idAgence(idAgence)
                .build();
    }

    private EmployeDto dtoEmploye(Long idAgence) {
        return EmployeDto.builder()
                .nom("Ben Salah").prenom("Amine")
                .role(RoleEmploye.AGENT)
                .idAgence(idAgence)
                .build();
    }

    private Equipement creerEquipement() {
        Equipement e = new Equipement();
        e.setLibelle("GPS");
        return e;
    }

    private Maintenance creerMaintenance(tn.esprit.tpautoloc.domain.Vehicule vehicule) {
        Maintenance m = new Maintenance();
        m.setVehicule(vehicule);
        m.setDescription("Vidange et controle des freins");
        return m;
    }

    // ------------------------------------------------------------------
    // AgenceService
    // ------------------------------------------------------------------
    @Test
    void testAgenceService() {
        final String e = "Agence";
        debutTest("AgenceService");

        etape(e, 1, 8, "findAll().size() AVANT");
        int avant = agenceService.findAll().size();
        info(e, "nombre = " + avant);

        etape(e, 2, 8, "create()");
        AgenceDto creee = agenceService.create(dtoAgence());
        info(e, "idAgence=" + creee.getIdAgence() + ", nom=" + creee.getNom());
        assertNotNull(creee.getIdAgence());
        ok(e, "id généré");

        etape(e, 3, 8, "findAll()");
        List<AgenceDto> toutes = agenceService.findAll();
        info(e, toutes.size() + " agence(s)");
        assertTrue(toutes.stream().anyMatch(a -> creee.getIdAgence().equals(a.getIdAgence())));
        ok(e, "agence présente dans la liste");

        etape(e, 4, 8, "findById(" + creee.getIdAgence() + ")");
        AgenceDto trouvee = agenceService.findById(creee.getIdAgence());
        assertEquals("AutoLoc Tunis Centre", trouvee.getNom());
        ok(e, "nom=" + trouvee.getNom());

        etape(e, 5, 8, "update() : ville Tunis -> Sfax");
        AgenceDto donnees = AgenceDto.builder().nom("AutoLoc Tunis Centre").ville("Sfax").build();
        AgenceDto modifiee = agenceService.update(creee.getIdAgence(), donnees);
        assertEquals("Sfax", modifiee.getVille());
        assertEquals(creee.getIdAgence(), modifiee.getIdAgence());
        ok(e, "même id, ville=Sfax");

        etape(e, 6, 8, "findAll().size() APRES");
        assertEquals(avant + 1, agenceService.findAll().size());
        ok(e, "compteur +1");

        etape(e, 7, 8, "deleteById(" + creee.getIdAgence() + ")");
        agenceService.deleteById(creee.getIdAgence());

        etape(e, 8, 8, "findById() -> ResourceNotFoundException");
        assertThrows(ResourceNotFoundException.class, () -> agenceService.findById(creee.getIdAgence()));
        assertEquals(avant, agenceService.findAll().size());
        ok(e, "supprimée, compteur revenu à " + avant);

        finTest("AgenceService");
    }

    // ------------------------------------------------------------------
    // ClientService
    // ------------------------------------------------------------------
    @Test
    void testClientService() {
        final String e = "Client";
        debutTest("ClientService");

        int avant = clientService.findAll().size();
        info(e, "nombre = " + avant);

        etape(e, 2, 7, "create() x2");
        ClientDto c1 = clientService.create(dtoClient("Trabelsi", "Sarra", "sarra.trabelsi@example.com", "P-001"));
        ClientDto c2 = clientService.create(dtoClient("Gharbi",   "Yassine", "y.gharbi@example.com",    "P-002"));
        assertNotNull(c1.getIdClient());
        assertNotNull(c2.getIdClient());
        assertNotEquals(c1.getIdClient(), c2.getIdClient());
        ok(e, "deux ids distincts");

        etape(e, 3, 7, "findAll()");
        assertTrue(clientService.findAll().size() >= 2);

        etape(e, 4, 7, "findById(" + c1.getIdClient() + ")");
        assertEquals("Trabelsi", clientService.findById(c1.getIdClient()).getNom());
        ok(e, "client retrouvé");

        etape(e, 5, 7, "findAll().size() APRES");
        assertEquals(avant + 2, clientService.findAll().size());

        etape(e, 6, 7, "deleteById(c1)");
        clientService.deleteById(c1.getIdClient());
        assertThrows(ResourceNotFoundException.class, () -> clientService.findById(c1.getIdClient()));
        ok(e, "c1 supprimé");

        etape(e, 7, 7, "NETTOYAGE c2");
        nettoyage(e, "deleteById(" + c2.getIdClient() + ")");
        clientService.deleteById(c2.getIdClient());
        assertEquals(avant, clientService.findAll().size());
        ok(e, "base revenue à l'état initial");

        finTest("ClientService");
    }

    // ------------------------------------------------------------------
    // VehiculeService
    // ------------------------------------------------------------------
    @Test
    void testVehiculeService() {
        final String e = "Vehicule";
        debutTest("VehiculeService");

        int avant = vehiculeService.findAll().size();
        info(e, "nombre = " + avant);

        etape(e, 2, 8, "PREPARATION : agence parente");
        AgenceDto agence = agenceService.create(dtoAgence());
        info(e, "agence : id=" + agence.getIdAgence());

        etape(e, 3, 8, "create()");
        VehiculeDto cree = vehiculeService.create(dtoVehicule(agence.getIdAgence()));
        assertNotNull(cree.getIdVehicule());
        ok(e, "idVehicule=" + cree.getIdVehicule());

        etape(e, 4, 8, "findAll()");
        List<VehiculeDto> tous = vehiculeService.findAll();
        assertTrue(tous.stream().anyMatch(v -> cree.getIdVehicule().equals(v.getIdVehicule())));
        ok(e, "véhicule présent");

        etape(e, 5, 8, "findById(" + cree.getIdVehicule() + ")");
        assertEquals("208", vehiculeService.findById(cree.getIdVehicule()).getModele());
        ok(e, "modele=208");

        etape(e, 6, 8, "update() : modele 208 -> 308");
        VehiculeDto donnees = dtoVehicule(agence.getIdAgence());
        donnees.setModele("308");
        VehiculeDto modifie = vehiculeService.update(cree.getIdVehicule(), donnees);
        assertEquals("308", modifie.getModele());
        assertEquals(cree.getIdVehicule(), modifie.getIdVehicule());
        assertEquals(avant + 1, vehiculeService.findAll().size());
        ok(e, "même id, modele=308");

        etape(e, 7, 8, "deleteById(" + cree.getIdVehicule() + ")");
        vehiculeService.deleteById(cree.getIdVehicule());
        assertThrows(ResourceNotFoundException.class, () -> vehiculeService.findById(cree.getIdVehicule()));
        assertEquals(avant, vehiculeService.findAll().size());
        ok(e, "supprimé");

        etape(e, 8, 8, "NETTOYAGE agence");
        nettoyage(e, "deleteById agence " + agence.getIdAgence());
        agenceService.deleteById(agence.getIdAgence());

        finTest("VehiculeService");
    }

    // ------------------------------------------------------------------
    // EquipementService (entité non migrée en DTO)
    // ------------------------------------------------------------------
    @Test
    void testEquipementService() {
        final String e = "Equipement";
        debutTest("EquipementService");

        etape(e, 1, 5, "create()");
        Equipement cree = equipementService.create(creerEquipement());
        assertNotNull(cree.getIdEquipement());
        ok(e, "id=" + cree.getIdEquipement());

        etape(e, 2, 5, "findById()");
        assertEquals("GPS", equipementService.findById(cree.getIdEquipement()).getLibelle());
        ok(e, "libelle=GPS");

        etape(e, 3, 5, "findAll()");
        assertTrue(equipementService.findAll().stream()
                .anyMatch(x -> cree.getIdEquipement().equals(x.getIdEquipement())));
        ok(e, "présent");

        etape(e, 4, 5, "deleteById()");
        equipementService.deleteById(cree.getIdEquipement());

        etape(e, 5, 5, "findById() -> absent");
        assertThrows(ResourceNotFoundException.class, () -> equipementService.findById(cree.getIdEquipement()));
        assertFalse(equipementService.findAll().stream()
                .anyMatch(x -> cree.getIdEquipement().equals(x.getIdEquipement())));
        ok(e, "supprimé");

        finTest("EquipementService");
    }

    // ------------------------------------------------------------------
    // EmployeService
    // ------------------------------------------------------------------
    @Test
    void testEmployeService() {
        final String e = "Employe";
        debutTest("EmployeService");

        etape(e, 1, 6, "PREPARATION agence");
        AgenceDto agence = agenceService.create(dtoAgence());
        info(e, "agence : id=" + agence.getIdAgence());

        etape(e, 2, 6, "create()");
        EmployeDto cree = employeService.create(dtoEmploye(agence.getIdAgence()));
        assertNotNull(cree.getIdEmploye());
        ok(e, "id=" + cree.getIdEmploye());

        etape(e, 3, 6, "findById() + findAll()");
        assertEquals("Ben Salah", employeService.findById(cree.getIdEmploye()).getNom());
        List<EmployeDto> tous = employeService.findAll();
        assertTrue(tous.stream().anyMatch(x -> cree.getIdEmploye().equals(x.getIdEmploye())));
        ok(e, "présent");

        etape(e, 4, 6, "deleteById()");
        employeService.deleteById(cree.getIdEmploye());

        etape(e, 5, 6, "findById() -> absent");
        assertThrows(ResourceNotFoundException.class, () -> employeService.findById(cree.getIdEmploye()));
        ok(e, "supprimé");

        etape(e, 6, 6, "NETTOYAGE agence");
        nettoyage(e, "deleteById " + agence.getIdAgence());
        agenceService.deleteById(agence.getIdAgence());

        finTest("EmployeService");
    }

    // ------------------------------------------------------------------
    // MaintenanceService (entité non migrée en DTO — dépend de Vehicule entité)
    // ------------------------------------------------------------------
    @Test
    void testMaintenanceService() {
        final String e = "Maintenance";
        debutTest("MaintenanceService");

        etape(e, 1, 6, "PREPARATION agence + vehicule");
        AgenceDto agence = agenceService.create(dtoAgence());
        VehiculeDto vDto = vehiculeService.create(dtoVehicule(agence.getIdAgence()));
        // MaintenanceService travaille encore avec l'entité Vehicule directement
        tn.esprit.tpautoloc.domain.Vehicule vehicule = new tn.esprit.tpautoloc.domain.Vehicule();
        vehicule.setIdVehicule(vDto.getIdVehicule());
        info(e, "vehicule : id=" + vehicule.getIdVehicule());

        etape(e, 2, 6, "create()");
        Maintenance cree = maintenanceService.create(creerMaintenance(vehicule));
        assertNotNull(cree.getIdMaintenance());
        ok(e, "id=" + cree.getIdMaintenance());

        etape(e, 3, 6, "findById() + findAll()");
        Maintenance trouvee = maintenanceService.findById(cree.getIdMaintenance());
        assertEquals("Vidange et controle des freins", trouvee.getDescription());
        assertTrue(maintenanceService.findAll().stream()
                .anyMatch(x -> cree.getIdMaintenance().equals(x.getIdMaintenance())));
        ok(e, "présente");

        etape(e, 4, 6, "deleteById()");
        maintenanceService.deleteById(cree.getIdMaintenance());

        etape(e, 5, 6, "findById() -> absent");
        assertThrows(ResourceNotFoundException.class, () -> maintenanceService.findById(cree.getIdMaintenance()));
        ok(e, "supprimée");

        etape(e, 6, 6, "NETTOYAGE vehicule + agence");
        nettoyage(e, "deleteById vehicule " + vDto.getIdVehicule());
        vehiculeService.deleteById(vDto.getIdVehicule());
        nettoyage(e, "deleteById agence " + agence.getIdAgence());
        agenceService.deleteById(agence.getIdAgence());

        finTest("MaintenanceService");
    }
}
