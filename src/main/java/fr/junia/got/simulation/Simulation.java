package fr.junia.got.simulation;

import fr.junia.got.display.ConsoleDisplay;
import fr.junia.got.display.EventLog;
import fr.junia.got.entity.LivingBeing;
import fr.junia.got.entity.Master;
import fr.junia.got.map.Map;
import fr.junia.got.util.RandomGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Le moteur de la simulation : une boucle à pas de temps constant.
 * À chaque pas, l'ordre des agents est tiré au hasard, puis chaque agent encore actif
 * joue un tour avec move(). Chaque action s'applique tout de suite, avant l'agent suivant.
 */
public class Simulation {

    /** Pourquoi la partie s'est terminée. */
    public enum EndReason {
        ALL_MESSAGES_COLLECTED, ALL_AGENTS_EXHAUSTED, MAX_STEPS_REACHED
    }

    private final Map map;
    private final List<LivingBeing> movingBeings;
    private final List<Master> masters;
    private final int totalMessages;
    private final ConsoleDisplay display;
    private final EventLog log = new EventLog();
    private final int delayMs;
    private int step;

    public Simulation(Map map, List<LivingBeing> movingBeings, List<Master> masters, int totalMessages,
                      ConsoleDisplay display, int delayMs) {
        this.map = map;
        this.movingBeings = new ArrayList<>(movingBeings);
        this.masters = new ArrayList<>(masters);
        this.totalMessages = totalMessages;
        this.display = display;
        this.delayMs = delayMs;
    }

    /** Boucle principale : joue les pas jusqu'à une condition de fin, puis affiche le résultat. */
    public void run() {
        display.showIntro(this);
        while (getEndReason() == null) {
            playStep();
            display.render(this);
            pause();
        }
        display.showResult(this);
    }

    /** Un pas de temps : nouvel ordre aléatoire, puis un tour pour chaque agent actif. */
    private void playStep() {
        step++;
        log.clear();
        RandomGenerator.getInstance().shuffle(movingBeings);
        for (LivingBeing being : movingBeings) {
            if (being.isActive()) {
                being.move(map, log); // polymorphisme : chaque maison se déplace à sa façon
            }
        }
    }

    /** Raison de la fin de partie, ou null si la partie continue. */
    public EndReason getEndReason() {
        for (Master master : masters) {
            if (master.getScore() == totalMessages) {
                return EndReason.ALL_MESSAGES_COLLECTED;
            }
        }
        if (countActive() == 0) {
            return EndReason.ALL_AGENTS_EXHAUSTED;
        }
        if (step >= Config.MAX_STEPS) {
            return EndReason.MAX_STEPS_REACHED;
        }
        return null;
    }

    /** Maître qui a le plus de messages, ou null en cas d'égalité en tête (match nul). */
    public Master getWinner() {
        Master best = null;
        boolean tie = false;
        for (Master master : masters) {
            if (best == null || master.getScore() > best.getScore()) {
                best = master;
                tie = false;
            } else if (master.getScore() == best.getScore()) {
                tie = true;
            }
        }
        return tie ? null : best;
    }

    /** Nombre d'agents mobiles encore actifs. */
    public int countActive() {
        int active = 0;
        for (LivingBeing being : movingBeings) {
            if (being.isActive()) {
                active++;
            }
        }
        return active;
    }

    private void pause() {
        if (delayMs <= 0) {
            return;
        }
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Map getMap() {
        return map;
    }

    public int getStep() {
        return step;
    }

    public List<LivingBeing> getMovingBeings() {
        return Collections.unmodifiableList(movingBeings);
    }

    public List<Master> getMasters() {
        return Collections.unmodifiableList(masters);
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public EventLog getLog() {
        return log;
    }
}
