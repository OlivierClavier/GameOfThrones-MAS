package fr.junia.got.map;

import fr.junia.got.entity.Entity;
import fr.junia.got.entity.House;

/** Une case de la carte : elle contient au plus une entité (un rocher ou un être vivant). */
public class Tile {

    private final Position position;
    /** Maison propriétaire de la SafeZone dont fait partie la case, null hors SafeZone. */
    private final House zoneOwner;
    private Entity occupant;

    public Tile(Position position, House zoneOwner) {
        this.position = position;
        this.zoneOwner = zoneOwner;
    }

    public Position getPosition() {
        return position;
    }

    public House getZoneOwner() {
        return zoneOwner;
    }

    public Entity getOccupant() {
        return occupant;
    }

    /** À n'appeler que depuis {@link Map}, pour que la case et l'entité restent d'accord. */
    void setOccupant(Entity occupant) {
        this.occupant = occupant;
    }

    public boolean isFree() {
        return occupant == null;
    }

    public boolean isSafeZone() {
        return zoneOwner != null;
    }
}
