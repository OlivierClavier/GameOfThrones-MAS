package fr.junia.got.map;

import fr.junia.got.entity.Entity;
import fr.junia.got.entity.House;
import fr.junia.got.entity.Rock;
import fr.junia.got.util.RandomGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 * La carte : une grille de width x height cases, avec une SafeZone par maison dans chaque coin.
 * Stark en haut à gauche, Marcheurs Blancs en haut à droite, Lannister en bas à gauche,
 * Targaryen en bas à droite.
 */
public class Map {

    private final int width;
    private final int height;
    private final int safeZoneWidth;
    private final int safeZoneHeight;
    private final Tile[][] tiles;

    /**
     * Crée la carte, ses SafeZones et ses rochers.
     *
     * @param rockRate probabilité qu'une case hors SafeZone reçoive un rocher
     */
    public Map(int width, int height, int safeZoneWidth, int safeZoneHeight, double rockRate) {
        this.width = width;
        this.height = height;
        this.safeZoneWidth = safeZoneWidth;
        this.safeZoneHeight = safeZoneHeight;
        this.tiles = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x] = new Tile(new Position(x, y), findZoneOwner(x, y));
            }
        }
        placeRocks(rockRate);
    }

    /** Maison dont la SafeZone contient la case (x, y), ou null. */
    private House findZoneOwner(int x, int y) {
        boolean left = x < safeZoneWidth;
        boolean right = x >= width - safeZoneWidth;
        boolean upper = y < safeZoneHeight;
        boolean lower = y >= height - safeZoneHeight;
        if (left && upper) {
            return House.STARK;
        }
        if (right && upper) {
            return House.WHITE_WALKER;
        }
        if (left && lower) {
            return House.LANNISTER;
        }
        if (right && lower) {
            return House.TARGARYEN;
        }
        return null;
    }

    /** Place des rochers au hasard, jamais dans une SafeZone. */
    private void placeRocks(double rockRate) {
        RandomGenerator random = RandomGenerator.getInstance();
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (!tile.isSafeZone() && random.chance(rockRate)) {
                    placeEntity(new Rock(), tile.getPosition());
                }
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /** Vrai si la position est sur la carte. */
    public boolean isInside(Position p) {
        return p.x() >= 0 && p.x() < width && p.y() >= 0 && p.y() < height;
    }

    /** Case à une position (qui doit être sur la carte). */
    public Tile getTile(Position p) {
        return tiles[p.y()][p.x()];
    }

    /**
     * Cases voisines d'une position, diagonales comprises :
     * 3 dans un coin, 5 sur un bord, 8 ailleurs.
     */
    public List<Tile> getNeighbours(Position p) {
        List<Tile> neighbours = new ArrayList<>();
        for (Direction d : Direction.values()) {
            Position n = p.next(d);
            if (isInside(n)) {
                neighbours.add(getTile(n));
            }
        }
        return neighbours;
    }

    /** Case centrale de la SafeZone d'une maison : c'est là que se tient son Maître. */
    public Position getSafeZoneCenter(House house) {
        boolean left = house == House.STARK || house == House.LANNISTER;
        boolean upper = house == House.STARK || house == House.WHITE_WALKER;
        int x0 = left ? 0 : width - safeZoneWidth;
        int y0 = upper ? 0 : height - safeZoneHeight;
        return new Position(x0 + safeZoneWidth / 2, y0 + safeZoneHeight / 2);
    }

    /** Direction à prendre depuis p pour rejoindre la SafeZone de la maison (vers son centre). */
    public Direction getDirectionToSafeZone(Position p, House house) {
        Position target = getSafeZoneCenter(house);
        Direction direction = Direction.fromDelta(target.x() - p.x(), target.y() - p.y());
        return direction != null ? direction : Direction.random();
    }

    /** Vrai si la position est dans la SafeZone de la maison. */
    public boolean isInSafeZone(Position p, House house) {
        return getTile(p).getZoneOwner() == house;
    }

    /** Vrai si la position est dans une SafeZone, quelle qu'elle soit. */
    public boolean isInAnySafeZone(Position p) {
        return getTile(p).isSafeZone();
    }

    /**
     * Vrai si un membre de la maison peut entrer sur la position : elle doit être sur la carte,
     * et seule la maison propriétaire entre dans une SafeZone. Pour les autres,
     * le contour de la SafeZone agit comme un obstacle.
     */
    public boolean canEnter(Position p, House house) {
        if (!isInside(p)) {
            return false;
        }
        House owner = getTile(p).getZoneOwner();
        return owner == null || owner == house;
    }

    /** Pose une entité sur une case libre. */
    public void placeEntity(Entity entity, Position p) {
        getTile(p).setOccupant(entity);
        entity.setPosition(p);
    }

    /** Déplace une entité vers une case libre : seule méthode qui change une position en jeu. */
    public void moveEntity(Entity entity, Position to) {
        getTile(entity.getPosition()).setOccupant(null);
        getTile(to).setOccupant(entity);
        entity.setPosition(to);
    }

    /** Cases libres de la SafeZone d'une maison, pour y placer ses agents au départ. */
    public List<Tile> getFreeSafeZoneTiles(House house) {
        List<Tile> free = new ArrayList<>();
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (tile.getZoneOwner() == house && tile.isFree()) {
                    free.add(tile);
                }
            }
        }
        return free;
    }
}
