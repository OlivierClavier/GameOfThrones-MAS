package fr.junia.got.entity;

/** Alliance Libération : Stark et Targaryen, unis contre les Lannister. */
public abstract class Good extends LivingBeing {

    protected Good(String name) {
        super(name);
    }

    @Override
    public boolean isAlly(LivingBeing other) {
        return other instanceof Good;
    }
}
