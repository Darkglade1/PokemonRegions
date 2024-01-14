package pokeregions.powers;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

import static pokeregions.util.Wiz.atb;

public class MonsterIntangiblePower extends IntangiblePlayerPower {

    private boolean justApplied = true;

    public MonsterIntangiblePower(AbstractCreature owner, int turns) {
        super(owner, turns);
    }

    @Override
    public void atEndOfRound() {
        if (justApplied) {
            justApplied = false;
        } else {
            this.flash();
            atb(new ReducePowerAction(this.owner, this.owner, this, 1));
        }

    }
}
