package fr.junia.got.entity;

/**
 * Alliance Domination : Lannister et Marcheurs Blancs. Une alliance de circonstance :
 * les Lannister ignorent la menace du Nord pour écraser les Stark.
 */
public abstract class Bad extends LivingBeing {

    protected Bad(String name) {
        super(name);
    }

    @Override
    public boolean isAlly(LivingBeing other) {
        return other instanceof Bad;
    }
}
