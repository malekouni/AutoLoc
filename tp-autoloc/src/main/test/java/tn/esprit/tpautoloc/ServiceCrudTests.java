package tn.esprit.tpautoloc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.tpautoloc.domain.Agence;
import tn.esprit.tpautoloc.domain.Client;
import tn.esprit.tpautoloc.domain.Employe;
import tn.esprit.tpautoloc.domain.Equipement;
import tn.esprit.tpautoloc.domain.Maintenance;
import tn.esprit.tpautoloc.domain.Vehicule;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.service.IAgenceService;
import tn.esprit.tpautoloc.service.IClientService;
import tn.esprit.tpautoloc.service.IEmployeService;
import tn.esprit.tpautoloc.service.IEquipementService;
import tn.esprit.tpautoloc.service.IMaintenanceService;
import tn.esprit.tpautoloc.service.IVehiculeService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Atelier 4 - Partie 2 : tests des services AutoLoc (couche Service, pas Repository).
 *
 * Les interfaces I...Service n'exposent que create, findById, findAll, update et deleteById
 * (pas de count() ni de existsById()). Dans ces tests :
 * - count()      est remplace par findAll().size() ;
 * - existsById() est remplace par la recherche de l'id dans findAll() (present) ou par
 *                ResourceNotFoundException levee par findById() (absent) ;
 * - save()       de l'exercice autonome correspond a create().
 *
 * Suivi des traces dans la console :
 * - chaque test commence par une banniere "TEST : ..." ;
 * - chaque appel au service est annonce par "ETAPE n/N" juste AVANT son execution : le SQL Hibernate
 *   s'affiche donc entre deux lignes ETAPE ;
 * - "->" donne la valeur retournee, "OK" confirme qu'une assertion est passee.
 *
 * Les attributs simples suivent l'Atelier 1 ; les associations suivent l'Atelier 2. Les noms des champs
 * d'association (agence dans Vehicule et Employe, vehicule dans Maintenance) sont supposes :
 * corrigez-les dans les methodes creerXxx() si besoin.
 */
@SpringBootTest
class ServiceCrudTests {

    private static final String LIGNE = "=".repeat(78);

    @Autowired
    private IAgenceService agenceService;
    @Autowired
    private IClientService clientService;
    @Autowired
    private IVehiculeService vehiculeService;
    @Autowired
    private IEquipementService equipementService;
    @Autowired
    private IEmployeService employeService;
    @Autowired
    private IMaintenanceService maintenanceService;

    // ------------------------------------------------------------------
    // Aides pour les traces
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

    private static void etape(String entite, int numero, int total, String description) {
        System.out.println();
        System.out.println("[" + entite + "] ETAPE " + numero + "/" + total + " - " + description);
    }

    private static void info(String entite, String message) {
        System.out.println("[" + entite + "]     -> " + message);
    }

    private static void ok(String entite, String message) {
        System.out.println("[" + entite + "]     OK  " + message);
    }

    private static void nettoyage(String entite, String message) {
        System.out.println("[" + entite + "] NETTOYAGE - " + message);
    }

    // ------------------------------------------------------------------
    // Fabriques d'entites
    // ------------------------------------------------------------------

    private Agence creerAgence() {
        Agence agence = new Agence();
        agence.setNom("AutoLoc Tunis Centre");
        agence.setVille("Tunis");
        agence.setAdresse("10 Avenue Habib Bourguiba");
        agence.setTelephone("71000000");
        return agence;
    }

    private Client creerClient(String nom, String prenom, String email, String numPermis) {
        Client client = new Client();
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setEmail(email);
        client.setTelephone("20123456");
        client.setNumPermis(numPermis);
        return client;
    }

    private Vehicule creerVehicule(Agence agence) {
        Vehicule vehicule = new Vehicule();
        vehicule.setAgence(agence); // ManyToOne : un vehicule appartient a une agence
        vehicule.setImmatriculation("123 TUN 4567");
        vehicule.setMarque("Peugeot");
        vehicule.setModele("208");
        vehicule.setTarifJournalier(new BigDecimal("85.00"));
        return vehicule;
    }

    private Equipement creerEquipement() {
        Equipement equipement = new Equipement();
        equipement.setLibelle("GPS");
        return equipement;
    }

    private Employe creerEmploye(Agence agence) {
        Employe employe = new Employe();
        employe.setAgence(agence); // ManyToOne : un employe appartient a une agence
        employe.setNom("Ben Salah");
        employe.setPrenom("Amine");
        return employe;
    }

    private Maintenance creerMaintenance(Vehicule vehicule) {
        Maintenance maintenance = new Maintenance();
        maintenance.setVehicule(vehicule); // ManyToOne : une maintenance concerne un vehicule
        maintenance.setDescription("Vidange et controle des freins");
        return maintenance;
    }

    // ------------------------------------------------------------------
    // Partie 2 : AgenceService
    // ------------------------------------------------------------------

    @Test
    void testAgenceService() {
        final String e = "Agence";
        debutTest("AgenceService");

        etape(e, 1, 8, "findAll().size() : nombre d'agences AVANT le test");
        int avant = agenceService.findAll().size();
        info(e, "nombre d'agences = " + avant);

        etape(e, 2, 8, "create() : enregistrement d'une nouvelle agence (id null -> INSERT)");
        Agence creee = agenceService.create(creerAgence());
        info(e, "retour de create() : idAgence=" + creee.getIdAgence()
                + ", nom=" + creee.getNom() + ", ville=" + creee.getVille());
        assertNotNull(creee.getIdAgence());
        ok(e, "l'identifiant a ete genere par MySQL (IDENTITY)");

        etape(e, 3, 8, "findAll() : liste de toutes les agences");
        List<Agence> toutes = agenceService.findAll();
        info(e, toutes.size() + " agence(s) en base :");
        toutes.forEach(a -> info(e, "   idAgence=" + a.getIdAgence() + ", nom=" + a.getNom()));
        assertTrue(toutes.stream().anyMatch(a -> creee.getIdAgence().equals(a.getIdAgence())));
        ok(e, "l'agence creee figure dans la liste");

        etape(e, 4, 8, "findById(" + creee.getIdAgence() + ") : lecture par cle primaire");
        Agence trouvee = agenceService.findById(creee.getIdAgence());
        info(e, "idAgence=" + trouvee.getIdAgence() + ", nom=" + trouvee.getNom());
        assertEquals("AutoLoc Tunis Centre", trouvee.getNom());
        ok(e, "agence retrouvee");

        etape(e, 5, 8, "update() : ville Tunis -> Sfax (le service charge l'existant puis recopie)");
        Agence donnees = creerAgence();
        donnees.setVille("Sfax");
        Agence modifiee = agenceService.update(creee.getIdAgence(), donnees);
        info(e, "retour de update() : idAgence=" + modifiee.getIdAgence() + ", ville=" + modifiee.getVille());
        assertEquals("Sfax", modifiee.getVille());
        assertEquals(creee.getIdAgence(), modifiee.getIdAgence());
        ok(e, "meme identifiant, ville mise a jour");

        etape(e, 6, 8, "findAll().size() : nombre d'agences APRES l'ajout");
        int apres = agenceService.findAll().size();
        info(e, "nombre d'agences = " + apres + " (avant = " + avant + ")");
        assertEquals(avant + 1, apres);
        ok(e, "le compteur a augmente de 1");

        etape(e, 7, 8, "deleteById(" + creee.getIdAgence() + ") : suppression");
        agenceService.deleteById(creee.getIdAgence());
        info(e, "suppression demandee");

        etape(e, 8, 8, "findById(" + creee.getIdAgence() + ") : verification que l'agence n'existe plus");
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> agenceService.findById(creee.getIdAgence()));
        info(e, "ResourceNotFoundException : " + exception.getMessage());
        assertEquals(avant, agenceService.findAll().size());
        ok(e, "l'agence n'existe plus, nombre d'agences = " + avant);

        finTest("AgenceService");
    }

    // ------------------------------------------------------------------
    // Partie 3 : ClientService
    // ------------------------------------------------------------------

    @Test
    void testClientService() {
        final String e = "Client";
        debutTest("ClientService");

        etape(e, 1, 7, "findAll().size() : nombre de clients AVANT le test");
        int avant = clientService.findAll().size();
        info(e, "nombre de clients = " + avant);

        etape(e, 2, 7, "create() x2 : enregistrement de deux clients");
        Client c1 = clientService.create(creerClient("Trabelsi", "Sarra", "sarra.trabelsi@example.com", "P-001"));
        info(e, "client 1 : idClient=" + c1.getIdClient() + ", nom=" + c1.getNom());
        Client c2 = clientService.create(creerClient("Gharbi", "Yassine", "yassine.gharbi@example.com", "P-002"));
        info(e, "client 2 : idClient=" + c2.getIdClient() + ", nom=" + c2.getNom());
        assertNotNull(c1.getIdClient());
        assertNotNull(c2.getIdClient());
        assertNotEquals(c1.getIdClient(), c2.getIdClient());
        ok(e, "deux identifiants distincts ont ete generes");

        etape(e, 3, 7, "findAll() : liste de tous les clients");
        List<Client> tous = clientService.findAll();
        info(e, tous.size() + " client(s) en base :");
        tous.forEach(c -> info(e, "   idClient=" + c.getIdClient() + ", nom=" + c.getNom()));
        assertTrue(tous.size() >= 2);
        ok(e, "au moins 2 clients en base");

        etape(e, 4, 7, "findById(" + c1.getIdClient() + ") : lecture par cle primaire");
        Client trouve = clientService.findById(c1.getIdClient());
        info(e, "idClient=" + trouve.getIdClient() + ", nom=" + trouve.getNom());
        assertEquals("Trabelsi", trouve.getNom());
        ok(e, "client retrouve");

        etape(e, 5, 7, "findAll().size() : nombre de clients APRES l'ajout");
        int apres = clientService.findAll().size();
        info(e, "nombre de clients = " + apres + " (avant = " + avant + ")");
        assertEquals(avant + 2, apres);
        ok(e, "le compteur a augmente de 2");

        etape(e, 6, 7, "deleteById(" + c1.getIdClient() + ") puis findById() : le client n'existe plus");
        clientService.deleteById(c1.getIdClient());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> clientService.findById(c1.getIdClient()));
        info(e, "ResourceNotFoundException : " + exception.getMessage());
        ok(e, "le client 1 a ete supprime");

        etape(e, 7, 7, "NETTOYAGE : suppression du second client");
        nettoyage(e, "deleteById(" + c2.getIdClient() + ")");
        clientService.deleteById(c2.getIdClient());
        assertEquals(avant, clientService.findAll().size());
        ok(e, "la base est revenue a son etat initial");

        finTest("ClientService");
    }

    // ------------------------------------------------------------------
    // Partie 4 : VehiculeService
    // ------------------------------------------------------------------

    @Test
    void testVehiculeService() {
        final String e = "Vehicule";
        debutTest("VehiculeService");

        etape(e, 1, 8, "findAll().size() : nombre de vehicules AVANT le test");
        int avant = vehiculeService.findAll().size();
        info(e, "nombre de vehicules = " + avant);

        etape(e, 2, 8, "PREPARATION : create() d'une agence parente (un vehicule appartient a une agence)");
        Agence agence = agenceService.create(creerAgence());
        info(e, "agence parente : idAgence=" + agence.getIdAgence());

        etape(e, 3, 8, "create() : enregistrement d'un vehicule");
        Vehicule cree = vehiculeService.create(creerVehicule(agence));
        info(e, "retour de create() : idVehicule=" + cree.getIdVehicule()
                + ", marque=" + cree.getMarque() + ", modele=" + cree.getModele());
        assertNotNull(cree.getIdVehicule());
        ok(e, "l'identifiant a ete genere par MySQL (IDENTITY)");

        etape(e, 4, 8, "findAll() : liste de tous les vehicules");
        List<Vehicule> tous = vehiculeService.findAll();
        info(e, tous.size() + " vehicule(s) en base :");
        tous.forEach(v -> info(e, "   idVehicule=" + v.getIdVehicule() + ", " + v.getMarque() + " " + v.getModele()));
        assertTrue(tous.stream().anyMatch(v -> cree.getIdVehicule().equals(v.getIdVehicule())));
        ok(e, "le vehicule cree figure dans la liste");

        etape(e, 5, 8, "findById(" + cree.getIdVehicule() + ") : lecture par cle primaire");
        Vehicule trouve = vehiculeService.findById(cree.getIdVehicule());
        info(e, "idVehicule=" + trouve.getIdVehicule() + ", modele=" + trouve.getModele());
        assertEquals("208", trouve.getModele());
        ok(e, "vehicule retrouve");

        etape(e, 6, 8, "update() : modele 208 -> 308 (le service charge l'existant puis recopie)");
        Vehicule donnees = creerVehicule(agence);
        donnees.setModele("308");
        Vehicule modifie = vehiculeService.update(cree.getIdVehicule(), donnees);
        info(e, "retour de update() : idVehicule=" + modifie.getIdVehicule() + ", modele=" + modifie.getModele());
        assertEquals("308", modifie.getModele());
        assertEquals(cree.getIdVehicule(), modifie.getIdVehicule());
        assertEquals(avant + 1, vehiculeService.findAll().size());
        ok(e, "meme identifiant, modele mis a jour, pas de nouvelle ligne");

        etape(e, 7, 8, "deleteById(" + cree.getIdVehicule() + ") puis findById() : le vehicule n'existe plus");
        vehiculeService.deleteById(cree.getIdVehicule());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> vehiculeService.findById(cree.getIdVehicule()));
        info(e, "ResourceNotFoundException : " + exception.getMessage());
        assertEquals(avant, vehiculeService.findAll().size());
        ok(e, "le vehicule n'existe plus, nombre de vehicules = " + avant);

        etape(e, 8, 8, "NETTOYAGE : suppression de l'agence parente");
        nettoyage(e, "deleteById(" + agence.getIdAgence() + ") sur l'agence");
        agenceService.deleteById(agence.getIdAgence());

        finTest("VehiculeService");
    }

    // ------------------------------------------------------------------
    // Partie 5 : exercice autonome (Equipement, Employe, Maintenance)
    // ------------------------------------------------------------------

    @Test
    void testEquipementService() {
        final String e = "Equipement";
        debutTest("EquipementService");

        etape(e, 1, 5, "create() : enregistrement d'un equipement");
        Equipement cree = equipementService.create(creerEquipement());
        info(e, "retour de create() : idEquipement=" + cree.getIdEquipement() + ", libelle=" + cree.getLibelle());
        assertNotNull(cree.getIdEquipement());
        ok(e, "l'identifiant a ete genere");

        etape(e, 2, 5, "findById(" + cree.getIdEquipement() + ") : lecture par cle primaire");
        Equipement trouve = equipementService.findById(cree.getIdEquipement());
        info(e, "libelle=" + trouve.getLibelle());
        assertEquals("GPS", trouve.getLibelle());
        ok(e, "equipement retrouve");

        etape(e, 3, 5, "findAll() : verification de l'existence (id present dans la liste)");
        List<Equipement> tous = equipementService.findAll();
        info(e, tous.size() + " equipement(s) en base");
        assertTrue(tous.stream().anyMatch(x -> cree.getIdEquipement().equals(x.getIdEquipement())));
        ok(e, "l'equipement existe");

        etape(e, 4, 5, "deleteById(" + cree.getIdEquipement() + ") : suppression");
        equipementService.deleteById(cree.getIdEquipement());
        info(e, "suppression demandee");

        etape(e, 5, 5, "findById() : verification que l'equipement n'existe plus");
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> equipementService.findById(cree.getIdEquipement()));
        info(e, "ResourceNotFoundException : " + exception.getMessage());
        assertFalse(equipementService.findAll().stream()
                .anyMatch(x -> cree.getIdEquipement().equals(x.getIdEquipement())));
        ok(e, "l'equipement a ete supprime");

        finTest("EquipementService");
    }

    @Test
    void testEmployeService() {
        final String e = "Employe";
        debutTest("EmployeService");

        etape(e, 1, 6, "PREPARATION : create() d'une agence parente (un employe appartient a une agence)");
        Agence agence = agenceService.create(creerAgence());
        info(e, "agence parente : idAgence=" + agence.getIdAgence());

        etape(e, 2, 6, "create() : enregistrement d'un employe");
        Employe cree = employeService.create(creerEmploye(agence));
        info(e, "retour de create() : idEmploye=" + cree.getIdEmploye() + ", nom=" + cree.getNom());
        assertNotNull(cree.getIdEmploye());
        ok(e, "l'identifiant a ete genere");

        etape(e, 3, 6, "findById(" + cree.getIdEmploye() + ") puis findAll() : lecture et existence");
        Employe trouve = employeService.findById(cree.getIdEmploye());
        info(e, "findById -> nom=" + trouve.getNom() + ", prenom=" + trouve.getPrenom());
        assertEquals("Ben Salah", trouve.getNom());
        List<Employe> tous = employeService.findAll();
        info(e, "findAll -> " + tous.size() + " employe(s) en base");
        assertTrue(tous.stream().anyMatch(x -> cree.getIdEmploye().equals(x.getIdEmploye())));
        ok(e, "l'employe existe");

        etape(e, 4, 6, "deleteById(" + cree.getIdEmploye() + ") : suppression");
        employeService.deleteById(cree.getIdEmploye());
        info(e, "suppression demandee");

        etape(e, 5, 6, "findById() : verification que l'employe n'existe plus");
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> employeService.findById(cree.getIdEmploye()));
        info(e, "ResourceNotFoundException : " + exception.getMessage());
        ok(e, "l'employe a ete supprime");

        etape(e, 6, 6, "NETTOYAGE : suppression de l'agence parente");
        nettoyage(e, "deleteById(" + agence.getIdAgence() + ") sur l'agence");
        agenceService.deleteById(agence.getIdAgence());

        finTest("EmployeService");
    }

    @Test
    void testMaintenanceService() {
        final String e = "Maintenance";
        debutTest("MaintenanceService");

        etape(e, 1, 6, "PREPARATION : create() d'une agence puis d'un vehicule parents");
        Agence agence = agenceService.create(creerAgence());
        info(e, "agence parente : idAgence=" + agence.getIdAgence());
        Vehicule vehicule = vehiculeService.create(creerVehicule(agence));
        info(e, "vehicule parent : idVehicule=" + vehicule.getIdVehicule());

        etape(e, 2, 6, "create() : enregistrement d'une maintenance liee au vehicule");
        Maintenance cree = maintenanceService.create(creerMaintenance(vehicule));
        info(e, "retour de create() : idMaintenance=" + cree.getIdMaintenance()
                + ", description=" + cree.getDescription());
        assertNotNull(cree.getIdMaintenance());
        ok(e, "l'identifiant a ete genere");

        etape(e, 3, 6, "findById(" + cree.getIdMaintenance() + ") puis findAll() : lecture et existence");
        Maintenance trouvee = maintenanceService.findById(cree.getIdMaintenance());
        info(e, "findById -> description=" + trouvee.getDescription());
        List<Maintenance> toutes = maintenanceService.findAll();
        info(e, "findAll -> " + toutes.size() + " maintenance(s) en base");
        assertTrue(toutes.stream().anyMatch(x -> cree.getIdMaintenance().equals(x.getIdMaintenance())));
        ok(e, "la maintenance existe");

        etape(e, 4, 6, "deleteById(" + cree.getIdMaintenance() + ") : suppression");
        maintenanceService.deleteById(cree.getIdMaintenance());
        info(e, "suppression demandee");

        etape(e, 5, 6, "findById() : verification que la maintenance n'existe plus");
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> maintenanceService.findById(cree.getIdMaintenance()));
        info(e, "ResourceNotFoundException : " + exception.getMessage());
        ok(e, "la maintenance a ete supprimee");

        etape(e, 6, 6, "NETTOYAGE : suppression des parents (enfant d'abord, puis parent : cles etrangeres)");
        nettoyage(e, "deleteById(" + vehicule.getIdVehicule() + ") sur le vehicule");
        vehiculeService.deleteById(vehicule.getIdVehicule());
        nettoyage(e, "deleteById(" + agence.getIdAgence() + ") sur l'agence");
        agenceService.deleteById(agence.getIdAgence());

        finTest("MaintenanceService");
    }
}
