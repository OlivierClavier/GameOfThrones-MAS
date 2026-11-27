package fr.junia.got.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.junia.got.entity.House;
import org.junit.jupiter.api.Test;

class MapTest {

    /** Carte 10 x 8 sans rocher, SafeZones de 3 x 2. */
    private final Map map = new Map(10, 8, 3, 2, 0.0);

    @Test
    void cornerHasThreeNeighbours() {
        assertEquals(3, map.getNeighbours(new Position(0, 0)).size());
    }

    @Test
    void edgeHasFiveNeighbours() {
        assertEquals(5, map.getNeighbours(new Position(4, 0)).size());
    }

    @Test
    void middleHasEightNeighbours() {
        assertEquals(8, map.getNeighbours(new Position(4, 4)).size());
    }

    @Test
    void directionPointsToOwnSafeZone() {
        // la SafeZone Stark est en haut à gauche
        assertEquals(Direction.NORTH_WEST, map.getDirectionToSafeZone(new Position(8, 6), House.STARK));
    }

    @Test
    void onlyTheOwnerCanEnterASafeZone() {
        assertTrue(map.canEnter(new Position(0, 0), House.STARK));
        assertFalse(map.canEnter(new Position(0, 0), House.LANNISTER));
    }
}
