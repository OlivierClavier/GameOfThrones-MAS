package fr.junia.got.entity;

import fr.junia.got.map.Position;

/** Tout ce qui occupe une case de la carte : un rocher ou un être vivant. */
public abstract class Entity {

    private Position position;

    public Position getPosition() {
        return position;
    }

    /** À n'appeler que depuis la classe Map, pour que la case et l'entité restent d'accord. */
    public void setPosition(Position position) {
        this.position = position;
    }

    /** Caractère qui représente l'entité sur la carte. */
    public abstract char getSymbol();

    /** Vrai si l'entité bloque le passage comme un obstacle. */
    public abstract boolean isObstacle();
}
