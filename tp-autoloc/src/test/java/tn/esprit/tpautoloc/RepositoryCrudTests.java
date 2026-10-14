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
import tn.esprit.tpautoloc.repository.IAgenceRepository;
import tn.esprit.tpautoloc.repository.IClientRepository;
import tn.esprit.tpautoloc.repository.IEmployeRepository;
import tn.esprit.tpautoloc.repository.IEquipementRepository;
import tn.esprit.tpautoloc.repository.IMaintenanceRepository;
import tn.esprit.tpautoloc.repository.IVehiculeRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Atelier 3 - Partie 2 : tests des JpaRepository.
 *
 * Suivi des traces dans la console :
 * - chaque test commence par une bannière "TEST : ..." ;
 * - chaque appel au repository est annoncé par une ligne "ETAPE n/N" juste AVANT son exécution :
 *   le SQL Hibernate (insert, select, update, delete, count) s'affiche donc entre deux lignes ETAPE ;
 * - "->" donne la valeur retournée, "OK" confirme qu'une assertion est passée ;
 * - si un test échoue, la dernière ligne ETAPE affichée indique l'appel fautif.
 *
 * Les attributs simples suivent l'Atelier 1 ; les associations suivent l'Atelier 2.
 * Les noms des champs d'association (agence dans Vehicule et Employe, vehicule dans Maintenance)
 * sont supposés : corrigez-les dans les méthodes creerXxx() si besoin.
 */
@SpringBootTest
class RepositoryCrudTests {

    private static final String LIGNE = "=".repeat(78);

    @Autowired
    private IAgenceRepository agenceRepository;
    @Autowired
    private IClientRepository clientRepository;
    @Autowired
    private IVehiculeRepository vehiculeRepository;
    @Autowired
    private IEquipementRepository equipementRepository;
    @Autowired
    private IEmployeRepository employeRepository;
    @Autowired
    private IMaintenanceRepository maintenanceRepository;

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
    // Fabriques d'entités
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
        vehicule.setAgence(agence); // ManyToOne : un véhicule appartient à une agence
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
        employe.setAgence(agence); // ManyToOne : un employé appartient à une agence
        employe.setNom("Ben Salah");
        employe.setPrenom("Amine");
        return employe;
    }

    private Maintenance creerMaintenance(Vehicule vehicule) {
        Maintenance maintenance = new Maintenance();
        maintenance.setVehicule(vehicule); // ManyToOne : une maintenance concerne un véhicule
        maintenance.setDescription("Vidange et contrôle des freins");
        return maintenance;
    }

    // ------------------------------------------------------------------
    // Partie 2 : AgenceRepository
    // ------------------------------------------------------------------

    @Test
    void testAgenceRepository() {
        final String e = "Agence";
        debutTest("AgenceRepository");

        etape(e, 1, 8, "count() : nombre d'agences AVANT le test");
        long avant = agenceRepository.count();
        info(e, "count() = " + avant);

        etape(e, 2, 8, "save() : enregistrement d'une nouvelle agence (idAgence null -> INSERT)");
        Agence saved = agenceRepository.save(creerAgence());
        info(e, "retour de save() : idAgence=" + saved.getIdAgence()
                + ", nom=" + saved.getNom() + ", ville=" + saved.getVille());
        assertNotNull(saved.getIdAgence());
        ok(e, "l'identifiant a ete genere par MySQL (IDENTITY)");

        etape(e, 3, 8, "findAll() : liste de toutes les agences");
        List<Agence> toutes = agenceRepository.findAll();
        info(e, toutes.size() + " agence(s) en base :");
        toutes.forEach(a -> info(e, "   idAgence=" + a.getIdAgence() + ", nom=" + a.getNom()));
        assertFalse(toutes.isEmpty());
        ok(e, "la liste n'est pas vide");

        etape(e, 4, 8, "findById(" + saved.getIdAgence() + ") : lecture par cle primaire");
        Optional<Agence> trouvee = agenceRepository.findById(saved.getIdAgence());
        info(e, "Optional present = " + trouvee.isPresent());
        assertTrue(trouvee.isPresent());
        assertEquals("AutoLoc Tunis Centre", trouvee.get().getNom());
        ok(e, "agence retrouvee, nom = " + trouvee.get().getNom());

        etape(e, 5, 8, "existsById(" + saved.getIdAgence() + ") : test d'existence");
        boolean existe = agenceRepository.existsById(saved.getIdAgence());
        info(e, "existsById = " + existe);
        assertTrue(existe);
        ok(e, "l'agence existe");

        etape(e, 6, 8, "count() : nombre d'agences APRES l'ajout");
        long apres = agenceRepository.count();
        info(e, "count() = " + apres + " (avant = " + avant + ")");
        assertEquals(avant + 1, apres);
        ok(e, "le compteur a augmente de 1");

        etape(e, 7, 8, "deleteById(" + saved.getIdAgence() + ") : suppression");
        agenceRepository.deleteById(saved.getIdAgence());
        info(e, "suppression demandee");

        etape(e, 8, 8, "existsById(" + saved.getIdAgence() + ") : verification apres suppression");
        boolean existeEncore = agenceRepository.existsById(saved.getIdAgence());
        info(e, "existsById = " + existeEncore);
        assertFalse(existeEncore);
        assertEquals(avant, agenceRepository.count());
        ok(e, "l'agence n'existe plus, count() = " + avant);

        finTest("AgenceRepository");
    }

    // ------------------------------------------------------------------
    // Partie 3 : ClientRepository
    // ------------------------------------------------------------------

    @Test
    void testClientRepository() {
        final String e = "Client";
        debutTest("ClientRepository");

        etape(e, 1, 7, "count() : nombre de clients AVANT le test");
        long avant = clientRepository.count();
        info(e, "count() = " + avant);

        etape(e, 2, 7, "save() x2 : enregistrement de deux clients");
        Client c1 = clientRepository.save(creerClient("Trabelsi", "Sarra", "sarra.trabelsi@example.com", "P-001"));
        info(e, "client 1 : idClient=" + c1.getIdClient() + ", nom=" + c1.getNom());
        Client c2 = clientRepository.save(creerClient("Gharbi", "Yassine", "yassine.gharbi@example.com", "P-002"));
        info(e, "client 2 : idClient=" + c2.getIdClient() + ", nom=" + c2.getNom());
        assertNotNull(c1.getIdClient());
        assertNotNull(c2.getIdClient());
        assertNotEquals(c1.getIdClient(), c2.getIdClient());
        ok(e, "deux identifiants distincts ont ete generes");

        etape(e, 3, 7, "findAll() : liste de tous les clients");
        List<Client> tous = clientRepository.findAll();
        info(e, tous.size() + " client(s) en base :");
        tous.forEach(c -> info(e, "   idClient=" + c.getIdClient() + ", nom=" + c.getNom()));
        assertTrue(tous.size() >= 2);
        ok(e, "au moins 2 clients en base");

        etape(e, 4, 7, "findById(" + c1.getIdClient() + ") : lecture par cle primaire");
        Optional<Client> trouve = clientRepository.findById(c1.getIdClient());
        info(e, "Optional present = " + trouve.isPresent());
        assertTrue(trouve.isPresent());
        assertEquals("Trabelsi", trouve.get().getNom());
        ok(e, "client retrouve, nom = " + trouve.get().getNom());

        etape(e, 5, 7, "existsById() : identifiant existant puis identifiant inexistant");
        boolean existant = clientRepository.existsById(c1.getIdClient());
        info(e, "existsById(" + c1.getIdClient() + ") = " + existant);
        assertTrue(existant);
        boolean inexistant = clientRepository.existsById(Long.MAX_VALUE);
        info(e, "existsById(" + Long.MAX_VALUE + ") = " + inexistant);
        assertFalse(inexistant);
        ok(e, "true pour l'id existant, false pour l'id inexistant");

        etape(e, 6, 7, "count() : nombre de clients APRES l'ajout");
        long apres = clientRepository.count();
        info(e, "count() = " + apres + " (avant = " + avant + ")");
        assertEquals(avant + 2, apres);
        ok(e, "le compteur a augmente de 2");

        etape(e, 7, 7, "deleteById() x2 : nettoyage pour ne pas polluer la base");
        clientRepository.deleteById(c1.getIdClient());
        clientRepository.deleteById(c2.getIdClient());
        info(e, "count() = " + clientRepository.count());
        assertEquals(avant, clientRepository.count());
        ok(e, "la base est revenue a son etat initial");

        finTest("ClientRepository");
    }

    // ------------------------------------------------------------------
    // Partie 4 : VehiculeRepository
    // ------------------------------------------------------------------

    @Test
    void testVehiculeRepository() {
        final String e = "Vehicule";
        debutTest("VehiculeRepository");

        etape(e, 1, 7, "count() : nombre de vehicules AVANT le test");
        long avant = vehiculeRepository.count();
        info(e, "count() = " + avant);

        etape(e, 2, 7, "PREPARATION : save() d'une agence parente (un vehicule appartient a une agence)");
        Agence agence = agenceRepository.save(creerAgence());
        info(e, "agence parente : idAgence=" + agence.getIdAgence());

        etape(e, 3, 7, "save() : enregistrement d'un nouveau vehicule (INSERT)");
        Vehicule saved = vehiculeRepository.save(creerVehicule(agence));
        info(e, "retour de save() : idVehicule=" + saved.getIdVehicule()
                + ", marque=" + saved.getMarque() + ", modele=" + saved.getModele());
        assertNotNull(saved.getIdVehicule());
        ok(e, "l'identifiant a ete genere par MySQL (IDENTITY)");

        etape(e, 4, 7, "findAll() puis count() : lecture de tous les vehicules");
        List<Vehicule> tous = vehiculeRepository.findAll();
        info(e, tous.size() + " vehicule(s) en base :");
        tous.forEach(v -> info(e, "   idVehicule=" + v.getIdVehicule()
                + ", " + v.getMarque() + " " + v.getModele()));
        assertFalse(tous.isEmpty());
        long apres = vehiculeRepository.count();
        info(e, "count() = " + apres + " (avant = " + avant + ")");
        assertEquals(avant + 1, apres);
        ok(e, "le compteur a augmente de 1");

        etape(e, 5, 7, "save() apres modification du modele : 208 -> 308 (id renseigne -> merge : SELECT + UPDATE)");
        saved.setModele("308");
        Vehicule modifie = vehiculeRepository.save(saved);
        info(e, "retour de save() : idVehicule=" + modifie.getIdVehicule() + ", modele=" + modifie.getModele());
        assertEquals("308", modifie.getModele());
        assertEquals(saved.getIdVehicule(), modifie.getIdVehicule());
        assertEquals(apres, vehiculeRepository.count());
        ok(e, "meme identifiant, modele mis a jour, pas de nouvelle ligne");

        etape(e, 6, 7, "deleteById(" + saved.getIdVehicule() + ") : suppression du vehicule");
        vehiculeRepository.deleteById(saved.getIdVehicule());
        boolean existe = vehiculeRepository.existsById(saved.getIdVehicule());
        info(e, "existsById = " + existe);
        assertFalse(existe);
        assertEquals(avant, vehiculeRepository.count());
        ok(e, "le vehicule n'existe plus, count() = " + avant);

        etape(e, 7, 7, "NETTOYAGE : suppression de l'agence parente");
        nettoyage(e, "deleteById(" + agence.getIdAgence() + ") sur l'agence");
        agenceRepository.deleteById(agence.getIdAgence());

        finTest("VehiculeRepository");
    }

    // ------------------------------------------------------------------
    // Partie 5 : exercice autonome (Equipement, Employe, Maintenance)
    // ------------------------------------------------------------------

    @Test
    void testEquipementRepository() {
        final String e = "Equipement";
        debutTest("EquipementRepository");

        etape(e, 1, 5, "save() : enregistrement d'un equipement");
        Equipement saved = equipementRepository.save(creerEquipement());
        info(e, "retour de save() : idEquipement=" + saved.getIdEquipement()
                + ", libelle=" + saved.getLibelle());
        assertNotNull(saved.getIdEquipement());
        ok(e, "l'identifiant a ete genere");

        etape(e, 2, 5, "findById(" + saved.getIdEquipement() + ") : lecture par cle primaire");
        boolean present = equipementRepository.findById(saved.getIdEquipement()).isPresent();
        info(e, "Optional present = " + present);
        assertTrue(present);
        ok(e, "equipement retrouve");

        etape(e, 3, 5, "findAll() : liste des equipements");
        List<Equipement> tous = equipementRepository.findAll();
        info(e, tous.size() + " equipement(s) en base");
        assertFalse(tous.isEmpty());
        ok(e, "la liste n'est pas vide");

        etape(e, 4, 5, "existsById(" + saved.getIdEquipement() + ") : test d'existence");
        boolean existe = equipementRepository.existsById(saved.getIdEquipement());
        info(e, "existsById = " + existe);
        assertTrue(existe);
        ok(e, "l'equipement existe");

        etape(e, 5, 5, "deleteById(" + saved.getIdEquipement() + ") puis existsById()");
        equipementRepository.deleteById(saved.getIdEquipement());
        boolean existeEncore = equipementRepository.existsById(saved.getIdEquipement());
        info(e, "existsById = " + existeEncore);
        assertFalse(existeEncore);
        ok(e, "l'equipement a ete supprime");

        finTest("EquipementRepository");
    }

    @Test
    void testEmployeRepository() {
        final String e = "Employe";
        debutTest("EmployeRepository");

        etape(e, 1, 6, "PREPARATION : save() d'une agence parente (un employe appartient a une agence)");
        Agence agence = agenceRepository.save(creerAgence());
        info(e, "agence parente : idAgence=" + agence.getIdAgence());

        etape(e, 2, 6, "save() : enregistrement d'un employe");
        Employe saved = employeRepository.save(creerEmploye(agence));
        info(e, "retour de save() : idEmploye=" + saved.getIdEmploye() + ", nom=" + saved.getNom());
        assertNotNull(saved.getIdEmploye());
        ok(e, "l'identifiant a ete genere");

        etape(e, 3, 6, "findById(" + saved.getIdEmploye() + ") : lecture par cle primaire");
        boolean present = employeRepository.findById(saved.getIdEmploye()).isPresent();
        info(e, "Optional present = " + present);
        assertTrue(present);
        ok(e, "employe retrouve");

        etape(e, 4, 6, "findAll() puis existsById()");
        List<Employe> tous = employeRepository.findAll();
        info(e, tous.size() + " employe(s) en base");
        assertFalse(tous.isEmpty());
        boolean existe = employeRepository.existsById(saved.getIdEmploye());
        info(e, "existsById = " + existe);
        assertTrue(existe);
        ok(e, "liste non vide et employe existant");

        etape(e, 5, 6, "deleteById(" + saved.getIdEmploye() + ") puis existsById()");
        employeRepository.deleteById(saved.getIdEmploye());
        boolean existeEncore = employeRepository.existsById(saved.getIdEmploye());
        info(e, "existsById = " + existeEncore);
        assertFalse(existeEncore);
        ok(e, "l'employe a ete supprime");

        etape(e, 6, 6, "NETTOYAGE : suppression de l'agence parente");
        nettoyage(e, "deleteById(" + agence.getIdAgence() + ") sur l'agence");
        agenceRepository.deleteById(agence.getIdAgence());

        finTest("EmployeRepository");
    }

    @Test
    void testMaintenanceRepository() {
        final String e = "Maintenance";
        debutTest("MaintenanceRepository");

        etape(e, 1, 6, "PREPARATION : save() d'une agence puis d'un vehicule parents");
        Agence agence = agenceRepository.save(creerAgence());
        info(e, "agence parente : idAgence=" + agence.getIdAgence());
        Vehicule vehicule = vehiculeRepository.save(creerVehicule(agence));
        info(e, "vehicule parent : idVehicule=" + vehicule.getIdVehicule());

        etape(e, 2, 6, "save() : enregistrement d'une maintenance liee au vehicule");
        Maintenance saved = maintenanceRepository.save(creerMaintenance(vehicule));
        info(e, "retour de save() : idMaintenance=" + saved.getIdMaintenance()
                + ", description=" + saved.getDescription());
        assertNotNull(saved.getIdMaintenance());
        ok(e, "l'identifiant a ete genere");

        etape(e, 3, 6, "findById(" + saved.getIdMaintenance() + ") : lecture par cle primaire");
        boolean present = maintenanceRepository.findById(saved.getIdMaintenance()).isPresent();
        info(e, "Optional present = " + present);
        assertTrue(present);
        ok(e, "maintenance retrouvee");

        etape(e, 4, 6, "findAll() puis existsById()");
        List<Maintenance> toutes = maintenanceRepository.findAll();
        info(e, toutes.size() + " maintenance(s) en base");
        assertFalse(toutes.isEmpty());
        boolean existe = maintenanceRepository.existsById(saved.getIdMaintenance());
        info(e, "existsById = " + existe);
        assertTrue(existe);
        ok(e, "liste non vide et maintenance existante");

        etape(e, 5, 6, "deleteById(" + saved.getIdMaintenance() + ") puis existsById()");
        maintenanceRepository.deleteById(saved.getIdMaintenance());
        boolean existeEncore = maintenanceRepository.existsById(saved.getIdMaintenance());
        info(e, "existsById = " + existeEncore);
        assertFalse(existeEncore);
        ok(e, "la maintenance a ete supprimee");

        etape(e, 6, 6, "NETTOYAGE : suppression des parents (enfant d'abord, puis parent : cles etrangeres)");
        nettoyage(e, "deleteById(" + vehicule.getIdVehicule() + ") sur le vehicule");
        vehiculeRepository.deleteById(vehicule.getIdVehicule());
        nettoyage(e, "deleteById(" + agence.getIdAgence() + ") sur l'agence");
        agenceRepository.deleteById(agence.getIdAgence());

        finTest("MaintenanceRepository");
    }
}
