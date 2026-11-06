package fr.got.mas.utils;
import fr.got.mas.utils.Direction;

public class Position {
    int x;
    int y;

    // Constructor

    public Position() {
        this.x = 0;
        this.y = 0;
    }

    public Position(int x, int y) {
        this.x = 0;
        this.y = 0;
    }

    // Move Method 
    
    public void move(Direction direction) {
        this.x += direction.getDx();
        this.y += direction.getDy();
    }

    // Getter Setters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
