package fr.junia.got.display;

import fr.junia.got.entity.House;
import fr.junia.got.entity.LivingBeing;
import fr.junia.got.entity.Master;
import fr.junia.got.map.Map;
import fr.junia.got.map.Position;
import fr.junia.got.map.Tile;
import fr.junia.got.simulation.Config;
import fr.junia.got.simulation.Simulation;
import java.util.ArrayList;
import java.util.List;

/**
 * Copie figée de l'état de la simulation à un pas donné. La fenêtre dessine cette copie dans
 * le thread de Swing pendant que la simulation continue, sans jamais lire un état en cours de modification.
 *
 * @param cells  symbole de chaque case (comme dans l'affichage console), indexé [y][x]
 * @param zones  maison propriétaire de chaque case de SafeZone, null ailleurs
 * @param result texte du résultat final, null tant que la partie continue
 */
public record StepSnapshot(int step, int maxSteps, int totalMessages, int activeAgents,
                           char[][] cells, House[][] zones, List<HouseStats> houses,
                           List<String> events, String result) {

    /** État d'une maison à ce pas. */
    public record HouseStats(House house, String masterName, int score, int active, int exhausted,
                             int energyPercent) {
    }

    /** Photographie la simulation. */
    public static StepSnapshot of(Simulation simulation, String result) {
        Map map = simulation.getMap();
        char[][] cells = new char[map.getHeight()][map.getWidth()];
        House[][] zones = new House[map.getHeight()][map.getWidth()];
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Tile tile = map.getTile(new Position(x, y));
                cells[y][x] = tile.getOccupant() == null ? '.' : tile.getOccupant().getSymbol();
                zones[y][x] = tile.getZoneOwner();
            }
        }

        List<HouseStats> houses = new ArrayList<>();
        for (Master master : simulation.getMasters()) {
            int active = 0;
            int exhausted = 0;
            int energySum = 0;
            for (LivingBeing being : simulation.getMovingBeings()) {
                if (being.getHouse() != master.getHouse()) {
                    continue;
                }
                if (being.isActive()) {
                    active++;
                    energySum += being.getEp() * 100 / being.getMaxEp();
                } else {
                    exhausted++;
                }
            }
            houses.add(new HouseStats(master.getHouse(), master.getName(), master.getScore(), active, exhausted,
                    active == 0 ? 0 : energySum / active));
        }

        return new StepSnapshot(simulation.getStep(), Config.MAX_STEPS, simulation.getTotalMessages(),
                simulation.countActive(), cells, zones, List.copyOf(houses),
                List.copyOf(simulation.getLog().getEvents()), result);
    }
}
