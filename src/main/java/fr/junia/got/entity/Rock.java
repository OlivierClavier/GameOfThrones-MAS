package fr.junia.got.entity;

/** Un rocher : un obstacle placé au hasard au début de la partie. */
public class Rock extends Entity {

    /** Nombre de rochers créés (attribut statique demandé par le sujet). */
    private static int count = 0;

    public Rock() {
        count++;
    }

    public static int getCount() {
        return count;
    }

    @Override
    public char getSymbol() {
        return '#';
    }

    @Override
    public boolean isObstacle() {
        return true;
    }
}
