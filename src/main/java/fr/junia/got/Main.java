package fr.junia.got;

import fr.junia.got.display.ConsoleDisplay;
import fr.junia.got.display.Display;
import fr.junia.got.display.SwingDisplay;
import fr.junia.got.simulation.Config;
import fr.junia.got.simulation.WorldBuilder;
import fr.junia.got.util.RandomGenerator;

/**
 * Point d'entrée de la simulation.
 * Options : --gui (fenêtre graphique), --fast (sans pause), --quiet (résultat final seulement),
 * --seed N (autre partie).
 */
public class Main {

    public static void main(String[] args) {
        int delayMs = Config.DELAY_MS;
        boolean quiet = false;
        boolean gui = false;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--gui" -> gui = true;
                case "--fast" -> delayMs = 0;
                case "--quiet" -> {
                    quiet = true;
                    delayMs = 0;
                }
                case "--seed" -> {
                    if (i + 1 < args.length) {
                        RandomGenerator.reseed(Long.parseLong(args[++i]));
                    }
                }
                default -> System.out.println("Option inconnue ignorée : " + args[i]);
            }
        }
        // Patron Stratégie : le moteur reçoit un Display, sans savoir lequel.
        Display display = gui ? new SwingDisplay() : new ConsoleDisplay(quiet);
        // En mode graphique, c'est la fenêtre qui règle la vitesse (curseur, pause, pas à pas).
        WorldBuilder.build(display, gui ? 0 : delayMs).run();
    }
}
