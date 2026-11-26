package fr.junia.got.entity;

import fr.junia.got.interaction.MessageBag;
import fr.junia.got.map.Position;

/**
 * Le Maître d'une maison. Il reste immobile au centre de sa SafeZone et collecte les messages
 * que lui apportent ses agents. Ses quatre implémentations sont des singletons.
 */
public interface Master {

    String getName();

    House getHouse();

    Position getPosition();

    MessageBag getMessages();

    /** Récupère les messages d'un agent de sa maison. Renvoie le nombre de messages nouveaux pour lui. */
    default int collect(LivingBeing courier) {
        return getMessages().addAll(courier.getMessages());
    }

    /** Score de la maison : le nombre de messages distincts détenus par le Maître. */
    default int getScore() {
        return getMessages().size();
    }
}
