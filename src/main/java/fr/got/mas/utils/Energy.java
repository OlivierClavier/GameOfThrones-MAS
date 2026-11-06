package fr.got.mas.utils;

public enum Energy {
    
    // Constants
    MAX_ENERGY(100),
    MIN_ENERGY(0),
    ENERGY_LOSS_PER_MOVE(1);

    // Attributes
    private final int value;

    // Constructor
    Energy(int value) {
        this.value = value;
    }

    // Getter Setters
    public int getValue() {
        return value;
    }
}
