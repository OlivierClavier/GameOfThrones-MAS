package fr.junia.got.entity.house;

import fr.junia.got.display.EventLog;
import fr.junia.got.entity.Master;
import fr.junia.got.map.Map;

/** Daenerys, Maîtresse de la maison Targaryen. Singleton : il n'existe qu'une seule instance. */
public final class TargaryenMaster extends Targaryen implements Master {

    private static TargaryenMaster instance;

    /** Constructeur privé : impossible de créer un second Maître avec new. */
    private TargaryenMaster() {
        super("Daenerys");
    }

    /** Renvoie l'unique instance, créée au premier appel. */
    public static TargaryenMaster getInstance() {
        if (instance == null) {
            instance = new TargaryenMaster();
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
