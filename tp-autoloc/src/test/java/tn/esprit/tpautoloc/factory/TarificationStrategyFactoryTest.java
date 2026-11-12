package tn.esprit.tpautoloc.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.tpautoloc.domain.Reservation;
import tn.esprit.tpautoloc.service.factory.TarificationStrategyFactory;
import tn.esprit.tpautoloc.service.strategy.ITarificationStrategy;
import tn.esprit.tpautoloc.service.strategy.TarifLongueDureeStrategy;
import tn.esprit.tpautoloc.service.strategy.TarifStandardStrategy;
import tn.esprit.tpautoloc.service.strategy.TarifWeekendStrategy;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Atelier 6 — Test du patron Factory (TarificationStrategyFactory).
 *
 * Résumé des cas couverts (table du TP) :
 * ┌────┬────────────┬────────────┬──────────────────────┬─────────────────┐
 * │ N° │  Durée     │ Jour début │  Branche testée      │ Résultat attendu│
 * ├────┼────────────┼────────────┼──────────────────────┼─────────────────┤
 * │  1 │  7 j       │ mercredi   │  jours >= 7           │ LONGUE_DUREE   │
 * │  2 │  7 j (borne│ mercredi   │  jours >= 7 (limite)  │ LONGUE_DUREE   │
 * │  3 │  2 j       │ vendredi   │  jours <= 3 && weekend│ WEEKEND        │
 * │  4 │  4 j       │ lundi      │  fallback             │ STANDARD       │
 * └────┴────────────┴────────────┴──────────────────────┴─────────────────┘
 *
 * Tests purement unitaires : pas de Spring, pas de base de données.
 */
class TarificationStrategyFactoryTest {

    private TarificationStrategyFactory factory;
    private TarifStandardStrategy       standard;
    private TarifWeekendStrategy        weekend;
    private TarifLongueDureeStrategy    longueDuree;

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        standard    = new TarifStandardStrategy();
        weekend     = new TarifWeekendStrategy();
        longueDuree = new TarifLongueDureeStrategy();

        factory = new TarificationStrategyFactory(List.of(standard, weekend, longueDuree));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Construit une Reservation avec seulement dateDebut et dateFin. */
    private Reservation reservation(LocalDate debut, LocalDate fin) {
        Reservation r = new Reservation();
        r.setDateDebut(debut);
        r.setDateFin(fin);
        return r;
    }

    // =========================================================================
    // Test 1 — Longue durée (≥ 7 jours)
    // =========================================================================

    @Test
    void doitRetournerLongueDureePour7JoursOuPlus() {
        // 2024-01-01 (lundi) → 2024-01-08 : 7 jours
        Reservation r = reservation(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 8));

        ITarificationStrategy resultat = factory.resoudre(r);

        assertThat(resultat).isInstanceOf(TarifLongueDureeStrategy.class);
        assertThat(resultat.getCode()).isEqualTo("LONGUE_DUREE");
        System.out.println("[TEST 1] 7 jours → " + resultat.getCode() + "  OK");
    }

    // =========================================================================
    // Test 2 — Borne exacte à 7 jours
    // =========================================================================

    @Test
    void doitRetournerLongueDureePourExactement7Jours() {
        // 2024-05-01 (mercredi) → 2024-05-08 : exactement 7 jours
        Reservation r = reservation(
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 8));

        ITarificationStrategy resultat = factory.resoudre(r);

        assertThat(resultat.getCode()).isEqualTo("LONGUE_DUREE");
        System.out.println("[TEST 2] borne exacte 7 jours → " + resultat.getCode() + "  OK");
    }

    // =========================================================================
    // Test 3 — Court séjour commençant un vendredi
    // =========================================================================

    @Test
    void doitRetournerWeekendPourCourtSejourVendredi() {
        // 2024-03-08 (vendredi) → 2024-03-10 : 2 jours, début vendredi
        Reservation r = reservation(
                LocalDate.of(2024, 3, 8),
                LocalDate.of(2024, 3, 10));

        ITarificationStrategy resultat = factory.resoudre(r);

        assertThat(resultat).isInstanceOf(TarifWeekendStrategy.class);
        assertThat(resultat.getCode()).isEqualTo("WEEKEND");
        System.out.println("[TEST 3] 2 jours vendredi → " + resultat.getCode() + "  OK");
    }

    // =========================================================================
    // Test 4 — Durée moyenne (4 à 6 jours), pas un week-end
    // =========================================================================

    @Test
    void doitRetournerStandardPourMoyenneDuree() {
        // 2024-03-04 (lundi) → 2024-03-08 : 4 jours, début lundi
        Reservation r = reservation(
                LocalDate.of(2024, 3, 4),
                LocalDate.of(2024, 3, 8));

        ITarificationStrategy resultat = factory.resoudre(r);

        assertThat(resultat.getCode()).isEqualTo("STANDARD");
        System.out.println("[TEST 4] 4 jours lundi → " + resultat.getCode() + "  OK");
    }
}
