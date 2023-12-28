package code.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public abstract class AbstractUnremovablePower extends AbstractEasyPower {
    public boolean isUnremovable = true;

    public AbstractUnremovablePower(String NAME, String ID, AbstractPower.PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount) {
        super(NAME, ID, powerType, isTurnBased, owner, amount);
    }
}