// Rapport — GameOfThrones MAS (Concept Objet, M2 JUNIA ISEN Lille)
// Compilation : typst compile rapport.typ
// Le rapport doit faire exactement 6 pages, couverture comprise.

#set document(title: "GameOfThrones MAS — Rapport", author: ("César Dablemont", "Olivier Clavier", "James Lemoine"))
// Numéro « n / 6 » en pied de page, sauf sur la couverture.
#set page(paper: "a4", margin: (x: 2cm, y: 1.8cm), footer: context {
  if counter(page).get().first() > 1 {
    align(center, text(size: 8pt, counter(page).display("1 / 1", both: true)))
  }
})
#set text(lang: "fr", size: 10pt)
#set par(justify: true, leading: 0.6em)
#set heading(numbering: "1.")
#show heading.where(level: 1): it => block(above: 1.2em, below: 0.6em, text(size: 12pt, weight: "bold", it))
#show heading.where(level: 2): it => block(above: 0.9em, below: 0.4em, text(size: 10.5pt, weight: "bold", it))
#set table(stroke: 0.5pt, inset: 4pt)
#show table: set text(size: 9pt)
#show raw.where(block: true): it => block(width: 100%, inset: 6pt, stroke: (left: 1pt), text(size: 8.5pt, it))

// =========================== PAGE 1 : COUVERTURE ===========================
#align(center)[
  #v(1.2cm)
  #text(size: 10pt)[JUNIA ISEN Lille — M2 — Concept Objet — mini-projet]
  #v(1.6cm)
  #text(size: 28pt, weight: "bold")[GameOfThrones MAS]
  #v(0.3cm)
  #text(size: 14pt)[Simulation multi-agents polymorphe en Java]
  #v(1.2cm)
  #box(stroke: 0.5pt, inset: 8pt, align(left, text(size: 9pt, raw(read("img/console-pas60.txt")))))
  #v(0.2cm)
  #text(size: 9pt)[Affichage de la simulation dans le terminal (pas 60, graine 42)]
]
#v(1fr)
#grid(columns: (1fr, 1fr),
  [
    *Équipe* \
    César Dablemont \
    Olivier Clavier \
    James Lemoine
  ],
  align(right)[
    Enseignant : Samuel Deleplanque \
    Java 21, Maven — version 1.1.0 \
    Novembre 2026
  ],
)

#pagebreak()

// =========================== PAGE 2 : PROJET ET RÈGLES ===========================
= Le projet

Quatre maisons de l'univers _Game of Thrones_ évoluent sur une carte en deux dimensions. Chaque maison a un *Maître*
immobile dans sa SafeZone et six agents mobiles qui collectent des messages et les lui rapportent. Chaque maison reçoit
10 messages distincts au départ (40 au total). La maison gagnante est celle dont le Maître détient le plus de messages distincts.

#table(
  columns: (auto, auto, auto, auto, auto, auto),
  table.header([*Maison*], [*Alliance*], [*Maître*], [*Distance max.*], [*EP max.*], [*Attaque / Défense*]),
  [Stark], [Good], [Jon Snow], [2], [100], [2 / 3],
  [Targaryen], [Good], [Daenerys], [4], [100], [3 / 1],
  [Lannister], [Bad], [Cersei], [3], [100], [2 / 3],
  [Marcheurs Blancs], [Bad], [Roi de la Nuit], [2], [150], [2 / 2],
)

Les deux alliances reprennent le schéma du sujet (`Good` et `Bad`). Les profils sont contrastés pour que le
polymorphisme ait un effet visible : les Targaryen vont loin mais se défendent mal, les Marcheurs Blancs sont lents mais endurants.

= Règles de la simulation

== Carte
- Grille de 30 × 20 cases, au plus une entité par case. Une SafeZone de 7 × 5 cases dans chaque coin, soit 23 % de la carte.
- Rochers placés au hasard hors SafeZone. Seule la maison propriétaire entre dans sa SafeZone : pour les autres, son contour est un obstacle.
- `Map.getNeighbours()` renvoie 3, 5 ou 8 cases voisines ; `Map.getDirectionToSafeZone()` renvoie la direction de sa SafeZone.

== Pas de temps et déplacements
- À chaque pas, la liste des agents est mélangée, puis chaque agent actif joue un tour avec `move()`. Chaque action s'applique immédiatement.
- Au-dessus de 20 % d'EP : direction et distance tirées au hasard. Sous 20 % : retour vers la SafeZone, recharge de 20 EP par pas, départ au-dessus de 80 %.
- Chaque case hors de sa SafeZone coûte 1 EP. Bloqué par un obstacle, l'agent s'arrête et perd les EP du trajet restant, puis repart dans une autre direction.
- À 0 EP, l'agent devient un obstacle et perd tous ses messages.

== Rencontres et livraison
- Une rencontre n'a lieu que si l'autre être est sur le chemin prévu.
- Même maison : union des messages, sans doublon. Même alliance : échange de 1 à 3 messages dans chaque sens.
- Ennemis : chacun lance un dé à 6 faces plus son attaque ou sa défense ; le vainqueur prend jusqu'à 3 messages au perdant. Aucune confrontation dans une SafeZone.
- Un agent qui passe à une case de son Maître lui donne ses messages.

== Fin de partie
La partie s'arrête quand un Maître a les 40 messages, quand plus aucun agent n'est actif, ou après 300 pas.
Le meilleur score gagne ; une égalité en tête donne un match nul. Tous les réglages sont dans la classe `Config`.

#pagebreak()

// =========================== PAGE 3 : CONCEPTS OBJET ===========================
= Les trois concepts de l'objet

== Encapsulation
Tous les attributs sont privés. L'énergie et l'état d'un agent ne changent qu'à l'intérieur de `LivingBeing`.
Seule la classe `Map` déplace une entité, ce qui garde la case et son occupant cohérents. Les listes sont exposées en lecture seule.

== Héritage
La hiérarchie suit l'énoncé : `Entity` → `LivingBeing` → alliance (`Good`, `Bad`) → maison (`Stark`, `Targaryen`,
`Lannister`, `WhiteWalker`) → Maître. Le Maître est donc une spécialisation de son groupe. `Rock` hérite aussi d'`Entity`.
Les classes du haut de la hiérarchie sont abstraites, et l'interface `Master` regroupe la collecte et le score.

== Polymorphisme
Le moteur appelle `move()` sur des `LivingBeing` sans savoir quelle maison joue. Chaque maison redéfinit sa distance,
son coût, son attaque et sa défense ; chaque alliance redéfinit `isAlly()` ; les Maîtres redéfinissent `move()` pour rester immobiles.

```
RandomGenerator.getInstance().shuffle(movingBeings);
for (LivingBeing being : movingBeings) {
    if (being.isActive()) {
        being.move(map, log);   // liaison dynamique
    }
}
```

== Attributs statiques
Un compteur statique par classe (`Rock`, `LivingBeing` et les quatre maisons) compte les instances. Il est affiché à chaque pas.

= Patron de conception : Singleton

Chaque maison n'a qu'un Maître : constructeur privé et instance unique créée au premier appel de `getInstance()`.
Le patron intervient dans les quatre classes demandées (`StarkMaster`, `TargaryenMaster`, `LannisterMaster`, `WhiteWalkerMaster`).

```
private static StarkMaster instance;
private StarkMaster() { super("Jon Snow"); }
public static StarkMaster getInstance() {
    if (instance == null) { instance = new StarkMaster(); }
    return instance;
}
```

`RandomGenerator`, la classe dédiée aux nombres pseudo-aléatoires, est aussi un singleton : toute la partie dépend
d'une seule graine, et une même graine rejoue la même partie (option `--seed`).

// #page(...) commence et termine sa propre page : pas de pagebreak autour.

// =========================== PAGE 4 : DIAGRAMME DE CLASSES ===========================
// La grille donne à l'image toute la hauteur restante (ligne 1fr) : elle ne peut pas déborder sur une autre page.
#page(margin: (x: 1cm, top: 1cm, bottom: 1.4cm))[
  #grid(rows: (auto, 1fr), row-gutter: 0.3cm,
    [
      = Diagramme de classes
      Toutes les classes, avec leurs attributs, leurs méthodes et leur visibilité. Zoomer dans le PDF pour lire le détail.
    ],
    align(center, image("img/classDiagram.svg", height: 100%, fit: "contain")),
  )
]

// =========================== PAGE 5 : ÉTATS ET ACTIVITÉ ===========================
= Diagrammes d'états et d'activité

#grid(columns: (7.5cm, 1fr), gutter: 0.8cm,
  [
    == États d'un agent
    #image("img/stateDiagram.svg", width: 100%)

    Un agent démarre en *Exploration*. Sous 20 % d'EP il passe en *Retour*, puis en *Repos* à côté de son Maître.
    Il repart au-dessus de 80 %. À 0 EP, il passe définitivement à l'état *Épuisé*.

    == Algorithme global
    Le diagramme d'activité montre les deux boucles demandées : la boucle des pas de temps (`Simulation.run`)
    et la boucle de déplacement d'un agent (`LivingBeing.walk`). L'ordre des agents est tiré au hasard à chaque pas.
  ],
  [
    #align(center, image("img/activityDiagram.svg", height: 22cm))
  ],
)

#pagebreak()

// =========================== PAGE 6 : RÉSULTATS, CONFORMITÉ, RÔLES ===========================
= Affichage et résultats

Le terminal affiche à chaque pas la carte en ASCII (majuscule : Maître, minuscule : agent, `x` : agent épuisé,
`#` : rocher, virgule : SafeZone), le score de chaque maison, les compteurs d'instances et les événements
(échanges, confrontations, livraisons). Une pause de 150 ms sépare deux pas pour suivre la partie
(options `--fast` et `--quiet` pour aller plus vite). Sur 60 parties (graines 1 à 60) :

#table(
  columns: (1fr, auto, auto),
  table.header([*Maison*], [*Victoires*], [*Score moyen sur 40*]),
  [Stark], [18], [32,7],
  [Lannister], [17], [31,7],
  [Marcheurs Blancs], [12], [31,1],
  [Targaryen], [10], [29,3],
)
3 matchs nuls. Aucune maison ne domine : les Targaryen, très mobiles, s'épuisent le plus vite.

= Conformité au sujet

#table(
  columns: (1fr, auto),
  table.header([*Exigence*], [*Statut*]),
  [Simulation à pas constant, ordre aléatoire à chaque pas], [Fait],
  [Carte, SafeZones, obstacles, voisins, direction vers la SafeZone], [Fait],
  [Règles d'énergie, rencontres et livraison], [Fait],
  [Encapsulation, héritage, polymorphisme], [Fait],
  [Attributs statiques pour compter les instances], [Fait],
  [Singleton dans 4 classes, classe de nombres aléatoires], [Fait],
  [Affichage ASCII dans le terminal, avec pause entre les pas], [Fait],
  [Diagrammes de classes, d'états et d'activité], [Fait],
  [Bonus : statistiques sur plusieurs parties], [Partiel],
  [Bonus : second patron, événements, plus court chemin, 3D, agents coordonnés], [Non],
)

*Limites.* Le retour vers la SafeZone ne contourne pas les rochers, et les tests unitaires ne couvrent que la carte
et les messages.

= Rôles dans l'équipe

- *Membre A* : architecture et développement du code.
- *Membre B* : conception du diagramme de classes.
- *Membre C* : vérification de la conformité au sujet, tests sur 60 parties, rédaction du rapport.
