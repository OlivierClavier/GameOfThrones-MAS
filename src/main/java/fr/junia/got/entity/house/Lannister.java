package fr.junia.got.entity.house;

import fr.junia.got.entity.Bad;
import fr.junia.got.entity.House;
import fr.junia.got.entity.Master;

/** Maison Lannister : ultra-riche. Leur or paie les meilleures armures, d'où une forte défense. */
public class Lannister extends Bad {

    /** Nombre de Lannister créés, Maître compris (attribut statique demandé par le sujet). */
    private static int count = 0;

    public Lannister(String name) {
        super(name);
        count++;
    }

    public static int getCount() {
        return count;
    }

    @Override
    public House getHouse() {
        return House.LANNISTER;
    }

    @Override
    public Master getMaster() {
        return LannisterMaster.getInstance();
    }

    /** Comme la Tour aux échecs : des déplacements moyens. */
    @Override
    protected int getMaxDistance() {
        return 3;
    }

    @Override
    protected int getCostPerTile() {
        return 1;
    }

    @Override
    public int getAttack() {
        return 2;
    }

    /** L'or des Lannister : armures et fortifications. */
    @Override
    public int getDefense() {
        return 3;
    }
}
