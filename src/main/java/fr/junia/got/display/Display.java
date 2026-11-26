package fr.junia.got.display;

import fr.junia.got.simulation.Simulation;

/**
 * Un affichage de la simulation. La simulation ne connaît que cette interface : on choisit
 * au lancement l'affichage console ou l'affichage graphique, sans changer le moteur
 * (patron de conception Stratégie).
 */
public interface Display {

    /** Appelée une fois, avant le premier pas. */
    void showIntro(Simulation simulation);

    /** Appelée après chaque pas de simulation. */
    void render(Simulation simulation);

    /** Appelée une fois, à la fin de la partie. */
    void showResult(Simulation simulation);
}
