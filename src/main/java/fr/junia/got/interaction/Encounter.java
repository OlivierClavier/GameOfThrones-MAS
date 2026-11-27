package fr.junia.got.interaction;

import fr.junia.got.display.EventLog;
import fr.junia.got.entity.LivingBeing;
import fr.junia.got.entity.Master;
import fr.junia.got.map.Map;
import fr.junia.got.map.Tile;
import fr.junia.got.simulation.Config;
import fr.junia.got.util.RandomGenerator;

/** Les règles d'interaction entre êtres vivants : partage, échange, confrontation et livraison. */
public final class Encounter {

    private Encounter() {
        // classe utilitaire : aucune instance
    }

    /**
     * Résout la rencontre entre mover, qui se déplace, et other, qui se trouvait sur son chemin.
     * Même maison : union des messages. Même alliance : échange. Sinon : confrontation,
     * jamais dans une SafeZone.
     */
    public static void resolve(LivingBeing mover, LivingBeing other, Map map, EventLog log) {
        if (other instanceof Master master && master.getHouse() == mover.getHouse()) {
            deliver(mover, master, log);
        } else if (mover.getHouse() == other.getHouse()) {
            shareAll(mover, other, log);
        } else if (mover.isAlly(other)) {
            exchange(mover, other, log);
        } else if (!map.isInAnySafeZone(mover.getPosition()) && !map.isInAnySafeZone(other.getPosition())) {
            confront(mover, other, log);
        }
    }

    /** Livraison : le Maître récupère les messages d'un agent qui passe à une case de lui. */
    public static void deliverIfNearMaster(LivingBeing being, Map map, EventLog log) {
        Master master = being.getMaster();
        for (Tile tile : map.getNeighbours(being.getPosition())) {
            if (tile.getOccupant() == master) {
                deliver(being, master, log);
                return;
            }
        }
    }

    private static void deliver(LivingBeing courier, Master master, EventLog log) {
        int delivered = master.collect(courier);
        if (delivered > 0) {
            log.add(courier.getName() + " livre " + delivered + " message(s) à " + master.getName()
                    + " (score " + master.getScore() + ")");
        }
    }

    /** Deux membres de la même maison font l'union de tous leurs messages. */
    private static void shareAll(LivingBeing a, LivingBeing b, EventLog log) {
        int toA = a.getMessages().addAll(b.getMessages());
        int toB = b.getMessages().addAll(a.getMessages());
        if (toA + toB > 0) {
            log.add(a.getName() + " et " + b.getName() + " (" + a.getHouse().getDisplayName()
                    + ") mettent leurs messages en commun : +" + toA + " / +" + toB);
        }
    }

    /** Deux alliés de maisons différentes s'échangent un nombre aléatoire de messages. */
    private static void exchange(LivingBeing a, LivingBeing b, EventLog log) {
        RandomGenerator random = RandomGenerator.getInstance();
        int toA = a.getMessages().copyRandomMissing(b.getMessages(), random.between(1, Config.MAX_ALLY_EXCHANGE));
        int toB = b.getMessages().copyRandomMissing(a.getMessages(), random.between(1, Config.MAX_ALLY_EXCHANGE));
        if (toA + toB > 0) {
            log.add(a.getName() + " et " + b.getName() + " (alliés) échangent des messages : +" + toA + " / +" + toB);
        }
    }

    /**
     * Confrontation entre ennemis : chacun lance un dé à 6 faces, l'attaquant ajoute son attaque
     * et le défenseur sa défense. Le vainqueur prend au perdant des messages qu'il n'a pas encore.
     */
    private static void confront(LivingBeing attacker, LivingBeing defender, EventLog log) {
        RandomGenerator random = RandomGenerator.getInstance();
        int attack = random.between(1, 6) + attacker.getAttack();
        int defense = random.between(1, 6) + defender.getDefense();
        String duel = attacker.getName() + " (" + attacker.getHouse().getDisplayName() + ") attaque "
                + defender.getName() + " (" + defender.getHouse().getDisplayName() + ") " + attack + "-" + defense;
        if (attack == defense) {
            log.add(duel + " : égalité");
            return;
        }
        LivingBeing winner = attack > defense ? attacker : defender;
        LivingBeing loser = winner == attacker ? defender : attacker;
        int stolen = winner.getMessages().takeRandomMissing(loser.getMessages(),
                random.between(1, Config.MAX_STOLEN_MESSAGES));
        log.add(duel + " : " + winner.getName() + " gagne et prend " + stolen + " message(s)");
    }
}
