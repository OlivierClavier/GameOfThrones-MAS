package fr.junia.got.display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Journal des événements d'un pas de simulation : échanges, confrontations, livraisons, épuisements. */
public class EventLog {

    private final List<String> events = new ArrayList<>();
    private int total;

    public void add(String event) {
        events.add(event);
        total++;
    }

    /** Événements du pas en cours. */
    public List<String> getEvents() {
        return Collections.unmodifiableList(events);
    }

    /** Vide le journal au début de chaque pas. */
    public void clear() {
        events.clear();
    }

    /** Nombre total d'événements depuis le début de la partie. */
    public int getTotal() {
        return total;
    }
}
