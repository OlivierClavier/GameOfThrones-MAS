package fr.junia.got;

import fr.junia.got.display.ConsoleDisplay;
import fr.junia.got.simulation.Config;
import fr.junia.got.simulation.WorldBuilder;
import fr.junia.got.util.RandomGenerator;

/**
 * Point d'entrée de la simulation, affichée dans le terminal.
 * Options : --fast (sans pause), --quiet (résultat final seulement), --seed N (autre partie).
 */
public class Main {

    public static void main(String[] args) {
        int delayMs = Config.DELAY_MS;
        boolean quiet = false;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
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
        WorldBuilder.build(new ConsoleDisplay(quiet), delayMs).run();
    }
}
