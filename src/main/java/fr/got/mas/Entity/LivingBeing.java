package fr.got.mas.Entity;

import fr.got.mas.utils.Energy;
import fr.got.mas.utils.Position;

public abstract class LivingBeing {
    Position position;
    int Energy;

    // Constructor
    public LivingBeing() {
        this.position = new Position();
        this.Energy = Energy.MAX_ENERGY.getValue();
    }

    



    
}
