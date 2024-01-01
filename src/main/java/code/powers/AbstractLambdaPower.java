package code.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public abstract class AbstractLambdaPower extends AbstractUnremovablePower {
    public AbstractLambdaPower(String id, String name, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount) {
        super(id, name, powerType, isTurnBased, owner, amount);
    }

    public abstract void updateDescription();
}
