package fr.got.mas.utils;

public enum Direction {


    NORTH(0, 1),
    SOUTH(0, -1),
    EAST(1, 0),
    WEST(-1, 0);

    // Attributes
    private final int dx;
    private final int dy;

    // Constructor
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    // Getter Setters
    public int getDx() {
        return dx;
    }
    
    public int getDy() {
        return dy;
    }

}
