package pokeregions.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public abstract class AbstractLambdaPower extends AbstractUnremovablePower {
    public AbstractLambdaPower(String id, String name, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount, String region, int priority) {
        super(id, name, powerType, isTurnBased, owner, amount);
        this.priority = priority;
        if (region != null) {
            this.loadRegion(region);
        }
    }

    public AbstractLambdaPower(String id, String name, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount) {
        this(id, name, powerType, isTurnBased, owner, amount, null, 5);
    }

    public AbstractLambdaPower(String id, String name, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount, String region) {
        this(id, name, powerType, isTurnBased, owner, amount, region, 5);
    }

    public AbstractLambdaPower(String id, String name, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount, int priority) {
        this(id, name, powerType, isTurnBased, owner, amount, null, priority);
    }

    public abstract void updateDescription();
}
