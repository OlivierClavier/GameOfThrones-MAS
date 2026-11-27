package fr.junia.got.entity;

import fr.junia.got.display.EventLog;
import fr.junia.got.interaction.Encounter;
import fr.junia.got.interaction.MessageBag;
import fr.junia.got.map.Direction;
import fr.junia.got.map.Map;
import fr.junia.got.map.Position;
import fr.junia.got.simulation.Config;
import fr.junia.got.util.RandomGenerator;

/**
 * Un être vivant de l'une des quatre maisons. Il porte des messages, dépense des EP
 * hors de sa SafeZone et les récupère dedans. Chaque maison redéfinit sa façon de se
 * déplacer et de combattre (polymorphisme).
 */
public abstract class LivingBeing extends Entity {

    /** Nombre d'êtres vivants créés, toutes maisons confondues. */
    private static int count = 0;

    private final String name;
    private final MessageBag messages = new MessageBag();
    private int ep;
    /** Dernière direction prise, gardée pour ne pas repartir droit dans un obstacle. */
    private Direction lastDirection;
    private boolean blockedLastTurn;
    private boolean returningHome;
    private boolean exhausted;

    protected LivingBeing(String name) {
        count++;
        this.name = name;
        // getMaxEp() est redéfinie par les Marcheurs Blancs : elle ne lit qu'une constante, donc l'appel est sûr ici.
        this.ep = getMaxEp();
    }

    public static int getCount() {
        return count;
    }

    // ----- Ce que chaque maison définit (polymorphisme) -----

    /** Maison de cet être. */
    public abstract House getHouse();

    /** Maître de sa maison (un singleton). */
    public abstract Master getMaster();

    /** Vrai si other fait partie de la même alliance (redéfinie dans Good et Bad). */
    public abstract boolean isAlly(LivingBeing other);

    /** Nombre maximum de cases parcourues en un tour. */
    protected abstract int getMaxDistance();

    /** EP perdus pour chaque case parcourue hors de sa SafeZone. */
    protected abstract int getCostPerTile();

    /** Bonus ajouté au dé quand cet être attaque. */
    public abstract int getAttack();

    /** Bonus ajouté au dé quand cet être se défend. */
    public abstract int getDefense();

    /** EP maximum : redéfini par les Marcheurs Blancs, plus endurants. */
    public int getMaxEp() {
        return Config.MAX_EP;
    }

    // ----- Déplacement -----

    /**
     * Un tour de l'être vivant, appelé une fois par pas de simulation.
     * Dans sa SafeZone, il récupère des EP. Sous 20 % d'EP, il rentre vers sa SafeZone
     * et n'en repart qu'une fois rechargé. Sinon, il part dans une direction au hasard.
     */
    public void move(Map map, EventLog log) {
        if (exhausted) {
            return;
        }
        boolean atHome = map.isInSafeZone(getPosition(), getHouse());
        if (atHome) {
            recharge();
        }
        if (ep < getMaxEp() * Config.LOW_EP_RATIO) {
            returningHome = true;
        }
        if (returningHome && atHome && ep >= getMaxEp() * Config.LEAVE_SAFE_ZONE_RATIO) {
            returningHome = false;
        }
        if (returningHome && atHome && isNextToMaster()) {
            return; // il se repose à côté de son Maître
        }

        Direction direction;
        int distance;
        if (blockedLastTurn) {
            // il ne repart pas dans la direction qui l'a bloqué
            direction = Direction.randomExcept(lastDirection);
            distance = 1;
        } else if (returningHome) {
            direction = map.getDirectionToSafeZone(getPosition(), getHouse());
            distance = getMaxDistance();
        } else {
            direction = Direction.random();
            distance = RandomGenerator.getInstance().between(1, getMaxDistance());
        }
        walk(map, direction, distance, log);
    }

    /** Avance case par case jusqu'à distance, ou jusqu'à ce qu'un obstacle ou un être l'arrête. */
    private void walk(Map map, Direction direction, int distance, EventLog log) {
        lastDirection = direction;
        blockedLastTurn = false;
        for (int done = 0; done < distance; done++) {
            Position next = getPosition().next(direction);
            int remaining = distance - done;
            if (!map.canEnter(next, getHouse())) {
                // bord de la carte ou contour de la SafeZone d'une autre maison
                hitObstacle(map, remaining, log);
                return;
            }
            Entity occupant = map.getTile(next).getOccupant();
            if (occupant instanceof LivingBeing other && other.isActive()) {
                // l'autre être était sur le chemin prévu : c'est une rencontre
                Encounter.resolve(this, other, map, log);
                return;
            }
            if (occupant != null) {
                hitObstacle(map, remaining, log);
                return;
            }
            map.moveEntity(this, next);
            if (!map.isInSafeZone(next, getHouse())) {
                spendEp(getCostPerTile());
            }
            Encounter.deliverIfNearMaster(this, map, log);
            if (ep == 0) {
                becomeExhausted(log);
                return;
            }
        }
    }

    /** Arrêt devant un obstacle : hors de sa SafeZone, il perd les EP du trajet qui lui restait. */
    private void hitObstacle(Map map, int remainingTiles, EventLog log) {
        blockedLastTurn = true;
        if (!map.isInSafeZone(getPosition(), getHouse())) {
            spendEp(remainingTiles * getCostPerTile());
            if (ep == 0) {
                becomeExhausted(log);
            }
        }
    }

    /** À 0 EP, l'être ne bouge plus, devient un obstacle et perd tous ses messages. */
    private void becomeExhausted(EventLog log) {
        exhausted = true;
        int lost = messages.size();
        messages.clear();
        log.add(name + " (" + getHouse().getDisplayName() + ") est épuisé : il devient un obstacle et perd "
                + lost + " message(s)");
    }

    private boolean isNextToMaster() {
        return getPosition().distanceTo(getMaster().getPosition()) <= 1;
    }

    private void spendEp(int amount) {
        ep = Math.max(0, ep - amount);
    }

    private void recharge() {
        ep = Math.min(getMaxEp(), ep + Config.EP_RECHARGE_PER_STEP);
    }

    // ----- Accesseurs -----

    public String getName() {
        return name;
    }

    public MessageBag getMessages() {
        return messages;
    }

    public int getEp() {
        return ep;
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public boolean isReturningHome() {
        return returningHome;
    }

    public boolean isExhausted() {
        return exhausted;
    }

    /** Vrai tant que l'être peut encore se déplacer. */
    public boolean isActive() {
        return !exhausted;
    }

    @Override
    public char getSymbol() {
        return exhausted ? 'x' : getHouse().getAgentSymbol();
    }

    @Override
    public boolean isObstacle() {
        return exhausted;
    }
}
