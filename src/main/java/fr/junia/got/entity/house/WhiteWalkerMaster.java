package fr.junia.got.entity.house;

import fr.junia.got.display.EventLog;
import fr.junia.got.entity.Master;
import fr.junia.got.map.Map;

/** Le Roi de la Nuit, Maître des Marcheurs Blancs. Singleton : il n'existe qu'une seule instance. */
public final class WhiteWalkerMaster extends WhiteWalker implements Master {

    private static WhiteWalkerMaster instance;

    /** Constructeur privé : impossible de créer un second Maître avec new. */
    private WhiteWalkerMaster() {
        super("Roi de la Nuit");
    }

    /** Renvoie l'unique instance, créée au premier appel. */
    public static WhiteWalkerMaster getInstance() {
        if (instance == null) {
            instance = new WhiteWalkerMaster();
        }
        return instance;
    }

    /** Le Maître reste immobile dans sa SafeZone. */
    @Override
    public void move(Map map, EventLog log) {
        // aucun déplacement
    }

    @Override
    public char getSymbol() {
        return getHouse().getMasterSymbol();
    }
}
