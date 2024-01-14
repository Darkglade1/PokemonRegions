package pokeregions.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public abstract class AbstractUnremovablePower extends AbstractEasyPower {
    public boolean isUnremovable = true;

    public AbstractUnremovablePower(String ID, String NAME, AbstractPower.PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount) {
        super(ID, NAME, powerType, isTurnBased, owner, amount);
    }
}