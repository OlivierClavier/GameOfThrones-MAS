package fr.junia.got.simulation;

/**
 * Tous les réglages de la simulation, réunis au même endroit pour pouvoir l'équilibrer
 * sans chercher les chiffres dans le code.
 */
public final class Config {

    private Config() {
        // classe de constantes : aucune instance
    }

    /** Graine par défaut : la même graine rejoue exactement la même partie. */
    public static final long SEED = 42;

    /** Dimensions de la carte, en cases. */
    public static final int WIDTH = 30;
    public static final int HEIGHT = 20;

    /** Dimensions de chaque SafeZone : 4 x (7 x 5) = 140 cases, soit 23 % de la carte. */
    public static final int SAFE_ZONE_WIDTH = 7;
    public static final int SAFE_ZONE_HEIGHT = 5;

    /** Probabilité qu'une case hors SafeZone contienne un rocher au départ. */
    public static final double ROCK_RATE = 0.06;

    /** Nombre d'agents mobiles par maison (le Maître en plus). */
    public static final int AGENTS_PER_HOUSE = 6;

    /** Nombre de messages distincts donnés à chaque maison au départ. */
    public static final int MESSAGES_PER_HOUSE = 10;

    /** Points d'énergie maximum d'un agent (les Marcheurs Blancs en ont davantage). */
    public static final int MAX_EP = 100;

    /** EP récupérés à chaque pas passé dans sa propre SafeZone. */
    public static final int EP_RECHARGE_PER_STEP = 20;

    /** Sous ce ratio d'EP, l'agent rentre vers sa SafeZone (20 % imposé par le sujet). */
    public static final double LOW_EP_RATIO = 0.20;

    /** Un agent qui se recharge ne repart qu'au-dessus de ce ratio, pour éviter les allers-retours. */
    public static final double LEAVE_SAFE_ZONE_RATIO = 0.80;

    /** Nombre maximum de messages donnés dans chaque sens lors d'une rencontre entre alliés. */
    public static final int MAX_ALLY_EXCHANGE = 3;

    /** Nombre maximum de messages que le vainqueur d'une confrontation prend au perdant. */
    public static final int MAX_STOLEN_MESSAGES = 3;

    /** Nombre maximum de pas : garantit que la simulation se termine. */
    public static final int MAX_STEPS = 300;

    /** Pause entre deux affichages, en millisecondes. */
    public static final int DELAY_MS = 150;
}
