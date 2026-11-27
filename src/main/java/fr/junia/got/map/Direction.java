package fr.junia.got.map;

import fr.junia.got.util.RandomGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 * Les 8 directions de déplacement. La case (0, 0) est en haut à gauche :
 * aller vers le Nord, c'est diminuer y.
 */
public enum Direction {
    NORTH(0, -1),
    NORTH_EAST(1, -1),
    EAST(1, 0),
    SOUTH_EAST(1, 1),
    SOUTH(0, 1),
    SOUTH_WEST(-1, 1),
    WEST(-1, 0),
    NORTH_WEST(-1, -1);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    /** Direction qui correspond au déplacement (dx, dy), ou null si dx et dy valent 0. */
    public static Direction fromDelta(int dx, int dy) {
        int sx = Integer.signum(dx);
        int sy = Integer.signum(dy);
        for (Direction d : values()) {
            if (d.dx == sx && d.dy == sy) {
                return d;
            }
        }
        return null;
    }

    /** Direction tirée au hasard. */
    public static Direction random() {
        Direction[] all = values();
        return all[RandomGenerator.getInstance().nextInt(all.length)];
    }

    /** Direction tirée au hasard, différente de excluded : utile après un blocage. */
    public static Direction randomExcept(Direction excluded) {
        List<Direction> choices = new ArrayList<>(List.of(values()));
        choices.remove(excluded);
        return RandomGenerator.getInstance().pick(choices);
    }
}
