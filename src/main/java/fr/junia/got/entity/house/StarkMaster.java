package fr.junia.got.entity.house;

import fr.junia.got.display.EventLog;
import fr.junia.got.entity.Master;
import fr.junia.got.map.Map;

/** Jon Snow, Maître de la maison Stark. Singleton : il n'existe qu'une seule instance. */
public final class StarkMaster extends Stark implements Master {

    private static StarkMaster instance;

    /** Constructeur privé : impossible de créer un second Maître avec new. */
    private StarkMaster() {
        super("Jon Snow");
    }

    /** Renvoie l'unique instance, créée au premier appel. */
    public static StarkMaster getInstance() {
        if (instance == null) {
            instance = new StarkMaster();
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
