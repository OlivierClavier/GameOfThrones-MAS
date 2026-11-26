package fr.junia.got.entity.house;

import fr.junia.got.entity.Bad;
import fr.junia.got.entity.House;
import fr.junia.got.entity.Master;
import fr.junia.got.simulation.Config;

/** Les Marcheurs Blancs : une horde lente mais infatigable, avec bien plus d'EP que les vivants. */
public class WhiteWalker extends Bad {

    /** Nombre de Marcheurs Blancs créés, Maître compris (attribut statique demandé par le sujet). */
    private static int count = 0;

    public WhiteWalker(String name) {
        super(name);
        count++;
    }

    public static int getCount() {
        return count;
    }

    @Override
    public House getHouse() {
        return House.WHITE_WALKER;
    }

    @Override
    public Master getMaster() {
        return WhiteWalkerMaster.getInstance();
    }

    /** Infatigables : 50 % d'EP en plus. */
    @Override
    public int getMaxEp() {
        return Config.MAX_EP * 3 / 2;
    }

    /** Lents : des pas courts. */
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
        return 2;
    }
}
