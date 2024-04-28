package pokeregions.orbs;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Lightning;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FocusPower;

import static pokeregions.util.Wiz.adp;

public class RaikouLightning extends Lightning {
    private final int multiplier;
    public RaikouLightning(int multiplier) {
        super();
        this.multiplier = multiplier;
        this.passiveAmount = this.basePassiveAmount * multiplier;
        this.evokeAmount = this.baseEvokeAmount * multiplier;
        this.updateDescription();
    }

    @Override
    public void applyFocus() {
        AbstractPower power = adp().getPower(FocusPower.POWER_ID);
        if (power != null) {
            this.passiveAmount = Math.max(0, (this.basePassiveAmount + power.amount) * multiplier);
            this.evokeAmount = Math.max(0, (this.baseEvokeAmount + power.amount) * multiplier);
        } else {
            this.passiveAmount = this.basePassiveAmount * multiplier;
            this.evokeAmount = this.baseEvokeAmount * multiplier;
        }
    }

    @Override
    public AbstractOrb makeCopy() {
        return new RaikouLightning(multiplier);
    }
}
