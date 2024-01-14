package pokeregions.orbs;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Dark;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FocusPower;

import static pokeregions.util.Wiz.adp;
public class MewtwoDark extends Dark {
    private final int bonusGrowth;
    public MewtwoDark(int bonusGrowth) {
        super();
        this.bonusGrowth = bonusGrowth;
        this.passiveAmount = (int)(this.basePassiveAmount * (1 + ((float)this.bonusGrowth / 100)));
        this.updateDescription();
    }

    @Override
    public void applyFocus() {
        AbstractPower power = adp().getPower(FocusPower.POWER_ID);
        if (power != null) {
            this.passiveAmount = Math.max(0, (int)((this.basePassiveAmount + power.amount) * (1 + ((float)bonusGrowth / 100))));
        } else {
            this.passiveAmount = (int)(this.basePassiveAmount * (1 + ((float)this.bonusGrowth / 100)));
        }
    }

    @Override
    public AbstractOrb makeCopy() {
        return new MewtwoDark(bonusGrowth);
    }
}
