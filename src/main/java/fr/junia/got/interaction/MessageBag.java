package fr.junia.got.interaction;

import fr.junia.got.util.RandomGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Les messages connus par un être vivant. Le sac refuse les doublons :
 * chaque ajout vérifie d'abord que le message n'est pas déjà présent, comme le demande le sujet.
 */
public class MessageBag {

    private final List<String> messages = new ArrayList<>();

    /** Ajoute un message s'il n'est pas déjà présent. Renvoie vrai s'il a été ajouté. */
    public boolean add(String message) {
        if (messages.contains(message)) {
            return false;
        }
        messages.add(message);
        return true;
    }

    /** Union : ajoute tous les messages de other. Renvoie le nombre de messages nouveaux. */
    public int addAll(MessageBag other) {
        int added = 0;
        for (String message : other.messages) {
            if (add(message)) {
                added++;
            }
        }
        return added;
    }

    /**
     * Copie au plus max messages de source que ce sac ne connaît pas, tirés au hasard.
     * La source les garde : c'est un partage entre alliés. Renvoie le nombre de messages copiés.
     */
    public int copyRandomMissing(MessageBag source, int max) {
        List<String> chosen = pickMissing(source, max);
        chosen.forEach(this::add);
        return chosen.size();
    }

    /**
     * Prend au plus max messages de source que ce sac ne connaît pas, tirés au hasard.
     * La source les perd : c'est le butin d'une confrontation. Renvoie le nombre de messages pris.
     */
    public int takeRandomMissing(MessageBag source, int max) {
        List<String> chosen = pickMissing(source, max);
        for (String message : chosen) {
            add(message);
            source.messages.remove(message);
        }
        return chosen.size();
    }

    /** Au plus max messages de source absents de ce sac, tirés au hasard. */
    private List<String> pickMissing(MessageBag source, int max) {
        List<String> missing = new ArrayList<>();
        for (String message : source.messages) {
            if (!contains(message)) {
                missing.add(message);
            }
        }
        RandomGenerator.getInstance().shuffle(missing);
        return new ArrayList<>(missing.subList(0, Math.min(max, missing.size())));
    }

    public boolean contains(String message) {
        return messages.contains(message);
    }

    public int size() {
        return messages.size();
    }

    public void clear() {
        messages.clear();
    }

    /** Vue en lecture seule des messages. */
    public List<String> asList() {
        return Collections.unmodifiableList(messages);
    }
}
