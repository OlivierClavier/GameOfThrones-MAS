# GameOfThrones MAS

A multi-agent system (MAS) simulation in Java in the universe of Game Of Thrones.

Projet du cours « Concept Objet » (M2, JUNIA ISEN Lille) : quatre maisons se disputent les messages
du royaume sur une carte en 2D. Le Maître de chaque maison reste dans sa SafeZone, et ses agents
parcourent la carte pour lui rapporter des messages. La maison dont le Maître réunit le plus de messages
distincts gagne la partie.

![Interface graphique](docs/screenshot.png)

## Lancer le projet

Prérequis : JDK 21 et Maven (IntelliJ inclut Maven). Le `.jar` de chaque version est aussi disponible dans les Releases GitHub.

```bash
mvn package
java -jar target/got-mas.jar --gui
```

| Option | Effet |
| --- | --- |
| `--gui` | Fenêtre graphique, avec pause, pas à pas et réglage de la vitesse |
| `--fast` | Console sans pause entre deux affichages |
| `--quiet` | N'affiche que le résultat final |
| `--seed 7` | Joue une autre partie (même graine = même partie) |

Sans option, la simulation s'affiche dans la console. Les tests se lancent avec `mvn test`.

## Les maisons

| Maison | Alliance | Maître (singleton) | Coin | Particularité |
| --- | --- | --- | --- | --- |
| Stark `s` | Libération | Jon Snow `S` | Haut gauche | Pas courts (2 cases), défense forte |
| Targaryen `t` | Libération | Daenerys `T` | Bas droite | Dragons : grands déplacements (4 cases), attaque forte |
| Lannister `l` | Domination | Cersei `L` | Bas gauche | L'or : déplacements moyens (3 cases), défense forte |
| Marcheurs Blancs `w` | Domination | Roi de la Nuit `W` | Haut droite | Infatigables : 50 % d'EP en plus, pas courts |

L'alliance Domination est une alliance de circonstance : les Lannister ignorent la menace du Nord pour écraser les Stark.

## Les règles

- **Carte** : 30 x 20 cases, une SafeZone de 7 x 5 par maison dans chaque coin (23 % de la carte), des rochers placés au hasard. Seule la maison propriétaire entre dans sa SafeZone : pour les autres, son contour agit comme un obstacle.
- **Pas de temps** : à chaque pas, l'ordre des agents est tiré au hasard, puis chaque agent actif joue un tour avec `move()`.
- **Énergie (EP)** : chaque case parcourue hors de sa SafeZone coûte 1 EP. Sous 20 % d'EP, l'agent rentre vers sa SafeZone, s'y recharge (20 EP par pas) et n'en repart qu'au-dessus de 80 %. Bloqué par un obstacle, il perd les EP du trajet restant et part ensuite dans une autre direction. À 0 EP, il devient un obstacle et perd tous ses messages.
- **Rencontres** : elles n'ont lieu que si l'autre être se trouve sur le chemin prévu. Même maison : union des messages. Alliés : échange de 1 à 3 messages dans chaque sens. Ennemis : chacun lance un dé à 6 faces, plus son attaque ou sa défense ; le vainqueur prend jusqu'à 3 messages au perdant. Aucune confrontation dans une SafeZone.
- **Livraison** : un agent qui passe à une case de son Maître lui donne ses messages.
- **Fin** : quand un Maître a les 40 messages, quand plus aucun agent n'est actif, ou après 300 pas. Le Maître qui a le plus de messages gagne ; une égalité en tête donne un match nul.

Tous les réglages sont dans `Config.java`. Sur 60 parties (graines 1 à 60) : Stark 18 victoires, Lannister 17, Marcheurs Blancs 12, Targaryen 10, 3 matchs nuls.

## Conception

- **Encapsulation** : attributs privés, accessibles par des méthodes ; seule `Map` modifie la position d'une entité.
- **Héritage** : `Entity` → `LivingBeing` → alliance (`Good`, `Bad`) → maison → Maître.
- **Polymorphisme** : chaque maison redéfinit sa distance, son coût, son attaque et sa défense ; la boucle appelle `move()` sans savoir quelle maison joue.
- **Attributs statiques** : un compteur d'instances par classe, affiché à chaque pas.
- **Singleton** : les 4 Maîtres et le générateur aléatoire.
- **Stratégie** : `ConsoleDisplay` et `SwingDisplay` implémentent `Display` ; le moteur ne connaît que l'interface.

## Structure du code

```
src/main/java/fr/junia/got/
├── Main.java
├── simulation/   Simulation, WorldBuilder, Config
├── map/          Map, Tile, Position, Direction
├── util/         RandomGenerator
├── entity/       Entity, Rock, LivingBeing, Good, Bad, Master, House
│   └── house/    Stark, Targaryen, Lannister, WhiteWalker et leurs 4 Maîtres
├── interaction/  MessageBag, Encounter
└── display/      Display, ConsoleDisplay, SwingDisplay, WorldView, StepSnapshot, EventLog
```

## Diagrammes UML

Sources PlantUML et images dans `docs/uml/` :

- [Diagramme de classes](docs/uml/classDiagram.png)
- [Diagramme d'états d'un agent](docs/uml/stateDiagram.png)
- [Diagramme d'activité de l'algorithme global](docs/uml/activityDiagram.png)

## Authors

- [@CesarDablemont](https://github.com/CesarDablemont)
- [@OlivierClavier](https://github.com/OlivierClavier)
- [@jameslemoine](https://github.com/jameslemoine)
