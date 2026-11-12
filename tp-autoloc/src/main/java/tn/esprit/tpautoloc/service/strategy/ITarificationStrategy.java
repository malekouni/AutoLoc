package tn.esprit.tpautoloc.service.strategy;

import java.math.BigDecimal;
import tn.esprit.tpautoloc.domain.Reservation;
import tn.esprit.tpautoloc.domain.Vehicule;

/**
 * Patron GoF : STRATEGY.
 * Définit l'algorithme de calcul du tarif de location.
 * Chaque implémentation représente une politique tarifaire différente.
 * Principe OCP : ajouter un nouveau tarif = créer une nouvelle classe,
 * sans toucher au code existant.
 */
public interface ITarificationStrategy {

    /**
     * Calcule le montant total de la location.
     *
     * @param vehicule    le véhicule loué (contient le tarifJournalier de base)
     * @param reservation la réservation (contient les dates pour calculer la durée)
     * @return le montant total en BigDecimal
     */
    BigDecimal calculerTarif(Vehicule vehicule, Reservation reservation);

    /**
     * Code unique de la stratégie, utilisé par la factory pour la sélectionner
     * dynamiquement.
     */
    String getCode();
}
