package fr.got.mas.utils;

import fr.got.mas.utils.Position;

public enum House {

    STARK("Winterfell", new Position(1, 1)),
    LANNISTER("Casterly Rock", new Position(1, 9)),
    TARGARYEN("Dragonstone", new Position(9, 9)),
    WHITEWALKER("The Wall", new Position(9, 1)); 

    // Attributes
    private final String castle;
    private Position SafeZone;

    // Constructor
    House(String castle, Position SafeZone) {
        this.castle = castle;
        this.SafeZone = SafeZone;
    }

    // Getter Setters
    public String getCastle() {
        return castle;
    }

    public Position getPosition() {
        return SafeZone;
    }
}
