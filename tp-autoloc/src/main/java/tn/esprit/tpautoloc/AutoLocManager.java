package tn.esprit.tpautoloc;

/**
 * Partie 1 — AutoLoc v0 : classe problématique intentionnelle.
 *
 * PROBLÈMES (réponses aux questions du TP) :
 *
 * Q1 — Responsabilités présentes dans cette seule classe :
 *   1. Calcul du prix de location (logique tarifaire)
 *   2. Création et sauvegarde du contrat (persistance)
 *   3. Envoi de notifications email ET SMS (communication)
 *   4. Enregistrement du paiement (comptabilité)
 *   → Violation du SRP : une classe = une seule raison de changer.
 *
 * Q2 — Principes SOLID non respectés :
 *   • SRP (Single Responsibility) : 4 responsabilités dans une seule classe.
 *   • OCP (Open/Closed)          : ajouter un tarif impose de modifier cette classe.
 *   • DIP (Dependency Inversion) : dépendances sur des implémentations concrètes
 *                                   (System.out) et non sur des abstractions.
 *
 * Q3 — Ajout d'un nouveau type "PleinesAison" :
 *   • Il faut modifier directement le code existant (nouveau else-if).
 *   • Risque de régression sur les autres types.
 *   • Violation de l'OCP : une entité logicielle doit être ouverte à l'extension
 *     mais fermée à la modification.
 */
public class AutoLocManager {

    public void louerVehicule(String typeReservation, int nombreJours, String clientEmail) {
        double prix = 0;
        if (typeReservation.equals("LongDuree")) {
            prix = nombreJours * 120;
        } else if (typeReservation.equals("Weekend")) {
            prix = nombreJours * 180;
        } else if (typeReservation.equals("Standard")) {
            prix = nombreJours * 150;
        }
        System.out.println("Contrat créé");
        System.out.println("Prix total : " + prix);
        envoyerEmail(clientEmail);
        enregistrerPaiement();
        sauvegarderContrat();
    }

    private void envoyerEmail(String email) {
        System.out.println("Email envoyé à " + email);
    }

    private void envoyerSMS(String sms) {
        System.out.println("SMS envoyé à " + sms);
    }

    private void sauvegarderContrat() {
        System.out.println("Contrat sauvegardé");
    }

    private void enregistrerPaiement() {
        System.out.println("Paiement enregistré");
    }
}
