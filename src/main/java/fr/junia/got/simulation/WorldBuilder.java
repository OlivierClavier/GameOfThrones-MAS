package fr.junia.got.simulation;

import fr.junia.got.display.ConsoleDisplay;
import fr.junia.got.entity.Entity;
import fr.junia.got.entity.House;
import fr.junia.got.entity.LivingBeing;
import fr.junia.got.entity.Master;
import fr.junia.got.entity.house.Lannister;
import fr.junia.got.entity.house.LannisterMaster;
import fr.junia.got.entity.house.Stark;
import fr.junia.got.entity.house.StarkMaster;
import fr.junia.got.entity.house.Targaryen;
import fr.junia.got.entity.house.TargaryenMaster;
import fr.junia.got.entity.house.WhiteWalker;
import fr.junia.got.entity.house.WhiteWalkerMaster;
import fr.junia.got.map.Map;
import fr.junia.got.map.Tile;
import fr.junia.got.util.RandomGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 * Prépare le monde au début de la partie : la carte, puis pour chaque maison son Maître au centre
 * de sa SafeZone, ses agents autour de lui et ses messages de départ.
 */
public final class WorldBuilder {

    private WorldBuilder() {
        // classe utilitaire : aucune instance
    }

    /** Construit une simulation prête à être lancée. */
    public static Simulation build(ConsoleDisplay display, int delayMs) {
        Map map = new Map(Config.WIDTH, Config.HEIGHT, Config.SAFE_ZONE_WIDTH, Config.SAFE_ZONE_HEIGHT,
                Config.ROCK_RATE);
        List<LivingBeing> agents = new ArrayList<>();
        List<Master> masters = new ArrayList<>();
        int totalMessages = 0;

        for (House house : House.values()) {
            Master master = createMaster(house);
            map.placeEntity((Entity) master, map.getSafeZoneCenter(house));
            masters.add(master);

            List<Tile> freeTiles = map.getFreeSafeZoneTiles(house);
            RandomGenerator.getInstance().shuffle(freeTiles);
            List<LivingBeing> team = new ArrayList<>();
            for (int i = 1; i <= Config.AGENTS_PER_HOUSE; i++) {
                LivingBeing agent = createAgent(house, house.getAgentSymbol() + String.valueOf(i));
                map.placeEntity(agent, freeTiles.get(i - 1).getPosition());
                team.add(agent);
            }

            // Messages de départ, répartis entre les agents de la maison (ex. STARK-01).
            for (int i = 1; i <= Config.MESSAGES_PER_HOUSE; i++) {
                String message = house.name() + "-" + String.format("%02d", i);
                team.get((i - 1) % team.size()).getMessages().add(message);
                totalMessages++;
            }
            agents.addAll(team);
        }
        return new Simulation(map, agents, masters, totalMessages, display, delayMs);
    }

    private static Master createMaster(House house) {
        return switch (house) {
            case STARK -> StarkMaster.getInstance();
            case TARGARYEN -> TargaryenMaster.getInstance();
            case LANNISTER -> LannisterMaster.getInstance();
            case WHITE_WALKER -> WhiteWalkerMaster.getInstance();
        };
    }

    private static LivingBeing createAgent(House house, String name) {
        return switch (house) {
            case STARK -> new Stark(name);
            case TARGARYEN -> new Targaryen(name);
            case LANNISTER -> new Lannister(name);
            case WHITE_WALKER -> new WhiteWalker(name);
        };
    }
}
