package pokeregions.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public abstract class AbstractLambdaPower extends AbstractUnremovablePower {
    public AbstractLambdaPower(String id, String name, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount, String region) {
        super(id, name, powerType, isTurnBased, owner, amount);
        if (region != null) {
            this.loadRegion(region);
        }
    }

    public AbstractLambdaPower(String id, String name, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount) {
        this(id, name, powerType, isTurnBased, owner, amount, null);
    }

    public abstract void updateDescription();
}
