package fr.junia.got.entity;

/** Les quatre maisons, avec leurs symboles sur la carte : minuscule pour un agent, majuscule pour le Maître. */
public enum House {
    STARK("Stark", 's', 'S'),
    TARGARYEN("Targaryen", 't', 'T'),
    LANNISTER("Lannister", 'l', 'L'),
    WHITE_WALKER("Marcheurs Blancs", 'w', 'W');

    private final String displayName;
    private final char agentSymbol;
    private final char masterSymbol;

    House(String displayName, char agentSymbol, char masterSymbol) {
        this.displayName = displayName;
        this.agentSymbol = agentSymbol;
        this.masterSymbol = masterSymbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public char getAgentSymbol() {
        return agentSymbol;
    }

    public char getMasterSymbol() {
        return masterSymbol;
    }
}
