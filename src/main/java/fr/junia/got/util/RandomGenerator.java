package fr.junia.got.util;

import fr.junia.got.simulation.Config;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Unique source de nombres pseudo-aléatoires de la simulation, comme l'exige le sujet.
 * C'est un singleton : toute la partie dépend d'une seule graine, donc une même graine
 * rejoue exactement la même partie.
 */
public final class RandomGenerator {

    private static RandomGenerator instance;

    private final Random random;

    private RandomGenerator(long seed) {
        this.random = new Random(seed);
    }

    /** Renvoie l'instance unique, créée avec la graine de {@link Config#SEED} si besoin. */
    public static RandomGenerator getInstance() {
        if (instance == null) {
            instance = new RandomGenerator(Config.SEED);
        }
        return instance;
    }

    /** Remplace le générateur par un nouveau, créé avec une autre graine. */
    public static void reseed(long seed) {
        instance = new RandomGenerator(seed);
    }

    /** Entier entre 0 (inclus) et bound (exclu). */
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    /** Entier entre min et max, bornes incluses. */
    public int between(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    /** Vrai avec la probabilité p, comprise entre 0 et 1. */
    public boolean chance(double p) {
        return random.nextDouble() < p;
    }

    /** Mélange une liste sur place. */
    public void shuffle(List<?> list) {
        Collections.shuffle(list, random);
    }

    /** Élément tiré au hasard dans une liste non vide. */
    public <T> T pick(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
