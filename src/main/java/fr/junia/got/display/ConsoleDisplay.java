package fr.junia.got.display;

import fr.junia.got.entity.House;
import fr.junia.got.entity.LivingBeing;
import fr.junia.got.entity.Master;
import fr.junia.got.entity.Rock;
import fr.junia.got.entity.house.Lannister;
import fr.junia.got.entity.house.Stark;
import fr.junia.got.entity.house.Targaryen;
import fr.junia.got.entity.house.WhiteWalker;
import fr.junia.got.map.Map;
import fr.junia.got.map.Position;
import fr.junia.got.map.Tile;
import fr.junia.got.simulation.Config;
import fr.junia.got.simulation.Simulation;
import java.util.List;

/** Affichage de la simulation dans la console, en caractères ASCII. */
public class ConsoleDisplay implements Display {

    /** Nombre maximum d'événements affichés par pas. */
    private static final int MAX_EVENTS_SHOWN = 8;

    /** En mode silencieux, seuls l'introduction et le résultat final sont affichés. */
    private final boolean quiet;

    public ConsoleDisplay(boolean quiet) {
        this.quiet = quiet;
    }

    /** Titre, légende et carte de départ. */
    public void showIntro(Simulation simulation) {
        StringBuilder out = new StringBuilder();
        out.append("=== GameOfThrones MAS ===\n");
        out.append("Légende : S/s Stark   T/t Targaryen   L/l Lannister   W/w Marcheurs Blancs\n");
        out.append("          majuscule = Maître, minuscule = agent, x = agent épuisé, # = rocher,\n");
        out.append("          , = case de SafeZone, . = case vide\n");
        out.append("Alliances : Stark + Targaryen contre Lannister + Marcheurs Blancs\n");
        out.append("Messages en jeu : ").append(simulation.getTotalMessages()).append('\n');
        if (!quiet) {
            appendMap(out, simulation.getMap());
        }
        System.out.print(out);
    }

    /** Affiche un pas : la carte, l'état de chaque maison, les instances et les événements. */
    public void render(Simulation simulation) {
        if (quiet) {
            return;
        }
        StringBuilder out = new StringBuilder();
        out.append("\n=== Pas ").append(simulation.getStep()).append(" / ").append(Config.MAX_STEPS).append(" ===\n");
        appendMap(out, simulation.getMap());
        appendHouses(out, simulation);
        appendInstances(out);
        appendEvents(out, simulation.getLog().getEvents());
        System.out.print(out);
    }

    /** Résultat final : raison de la fin, scores et vainqueur. */
    public void showResult(Simulation simulation) {
        StringBuilder out = new StringBuilder();
        out.append("\n=== Fin de la partie après ").append(simulation.getStep()).append(" pas ===\n");
        out.append(switch (simulation.getEndReason()) {
            case ALL_MESSAGES_COLLECTED -> "Un Maître a réuni tous les messages.\n";
            case ALL_AGENTS_EXHAUSTED -> "Tous les agents sont épuisés.\n";
            case MAX_STEPS_REACHED -> "Nombre maximum de pas atteint.\n";
        });
        appendHouses(out, simulation);
        appendInstances(out);
        Master winner = simulation.getWinner();
        if (winner == null) {
            out.append("Match nul : plusieurs Maîtres ont le meilleur score.\n");
        } else {
            out.append("Vainqueur : ").append(winner.getHouse().getDisplayName())
                    .append(" (").append(winner.getName()).append(", ").append(winner.getScore())
                    .append(" messages)\n");
        }
        System.out.print(out);
    }

    private void appendMap(StringBuilder out, Map map) {
        String border = "+" + "-".repeat(map.getWidth() * 2 + 1) + "+\n";
        out.append(border);
        for (int y = 0; y < map.getHeight(); y++) {
            out.append("| ");
            for (int x = 0; x < map.getWidth(); x++) {
                out.append(symbolOf(map.getTile(new Position(x, y)))).append(' ');
            }
            out.append("|\n");
        }
        out.append(border);
    }

    private char symbolOf(Tile tile) {
        if (tile.getOccupant() != null) {
            return tile.getOccupant().getSymbol();
        }
        return tile.isSafeZone() ? ',' : '.';
    }

    private void appendHouses(StringBuilder out, Simulation simulation) {
        out.append(String.format("%-17s %9s %7s %8s %8s%n", "Maison", "Score", "Actifs", "Épuisés", "EP moy."));
        for (Master master : simulation.getMasters()) {
            House house = master.getHouse();
            int active = 0;
            int exhausted = 0;
            int epSum = 0;
            for (LivingBeing being : simulation.getMovingBeings()) {
                if (being.getHouse() != house) {
                    continue;
                }
                if (being.isActive()) {
                    active++;
                    epSum += being.getEp();
                } else {
                    exhausted++;
                }
            }
            int averageEp = active == 0 ? 0 : epSum / active;
            String score = master.getScore() + "/" + simulation.getTotalMessages();
            out.append(String.format("%-17s %9s %7d %8d %8d%n", house.getDisplayName(), score, active, exhausted,
                    averageEp));
        }
    }

    /** Nombre d'instances de chaque classe, grâce aux attributs statiques. */
    private void appendInstances(StringBuilder out) {
        out.append("Instances (Maître compris) : Stark ").append(Stark.getCount())
                .append(" | Targaryen ").append(Targaryen.getCount())
                .append(" | Lannister ").append(Lannister.getCount())
                .append(" | Marcheurs ").append(WhiteWalker.getCount())
                .append(" | êtres vivants ").append(LivingBeing.getCount())
                .append(" | rochers ").append(Rock.getCount()).append('\n');
    }

    private void appendEvents(StringBuilder out, List<String> events) {
        if (events.isEmpty()) {
            out.append("Événements : aucun\n");
            return;
        }
        out.append("Événements :\n");
        int shown = Math.min(MAX_EVENTS_SHOWN, events.size());
        for (int i = 0; i < shown; i++) {
            out.append("  - ").append(events.get(i)).append('\n');
        }
        if (events.size() > shown) {
            out.append("  ... et ").append(events.size() - shown).append(" autre(s)\n");
        }
    }
}
