package fr.junia.got.entity.house;

import fr.junia.got.entity.Good;
import fr.junia.got.entity.House;
import fr.junia.got.entity.Master;

/** Maison Targaryen : les dragons. Grands déplacements et forte attaque, mais une défense faible. */
public class Targaryen extends Good {

    /** Nombre de Targaryen créés, Maître compris (attribut statique demandé par le sujet). */
    private static int count = 0;

    public Targaryen(String name) {
        super(name);
        count++;
    }

    public static int getCount() {
        return count;
    }

    @Override
    public House getHouse() {
        return House.TARGARYEN;
    }

    @Override
    public Master getMaster() {
        return TargaryenMaster.getInstance();
    }

    /** Comme la Reine aux échecs : de longues distances. */
    @Override
    protected int getMaxDistance() {
        return 4;
    }

    @Override
    protected int getCostPerTile() {
        return 1;
    }

    @Override
    public int getAttack() {
        return 3;
    }

    @Override
    public int getDefense() {
        return 1;
    }
}
