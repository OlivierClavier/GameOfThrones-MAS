package fr.junia.got.entity.house;

import fr.junia.got.entity.Good;
import fr.junia.got.entity.House;
import fr.junia.got.entity.Master;

/** Maison Stark : défensive et loyale. Petits déplacements, solide en défense. */
public class Stark extends Good {

    /** Nombre de Stark créés, Maître compris (attribut statique demandé par le sujet). */
    private static int count = 0;

    public Stark(String name) {
        super(name);
        count++;
    }

    public static int getCount() {
        return count;
    }

    @Override
    public House getHouse() {
        return House.STARK;
    }

    @Override
    public Master getMaster() {
        return StarkMaster.getInstance();
    }

    /** Comme le Roi aux échecs : des pas courts. */
    @Override
    protected int getMaxDistance() {
        return 2;
    }

    @Override
    protected int getCostPerTile() {
        return 1;
    }

    @Override
    public int getAttack() {
        return 2;
    }

    @Override
    public int getDefense() {
        return 3;
    }
}
