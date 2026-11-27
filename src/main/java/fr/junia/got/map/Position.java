package fr.junia.got.map;

/**
 * Coordonnées d'une case : x est l'indice horizontal, y l'indice vertical.
 * Immuable : un déplacement crée une nouvelle position.
 */
public record Position(int x, int y) {

    /** Position voisine dans la direction donnée. */
    public Position next(Direction direction) {
        return new Position(x + direction.getDx(), y + direction.getDy());
    }

    /** Distance en nombre de pas, diagonales comprises : 1 pour une case voisine. */
    public int distanceTo(Position other) {
        return Math.max(Math.abs(x - other.x), Math.abs(y - other.y));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
