package pokeregions.powers;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;

import static pokeregions.util.Wiz.atb;
import static pokeregions.util.Wiz.makePowerRemovable;

public class Fortitude extends AbstractUnremovablePower {
    public static final String POWER_ID = PokemonRegions.makeID(Fortitude.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final float DAMAGE_REDUCTION = 0.67F;
    private boolean justApplied;

    public Fortitude(AbstractCreature owner, int amount, boolean justApplied) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        this.justApplied = justApplied;
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * DAMAGE_REDUCTION;
        } else {
            return damage;
        }
    }

    @Override
    public void atEndOfRound() {
        if (this.justApplied) {
            this.justApplied = false;
        } else {
            if (this.amount == 0) {
                makePowerRemovable(this);
                atb(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            } else {
                atb(new ReducePowerAction(this.owner, this.owner, ID, 1));
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}
