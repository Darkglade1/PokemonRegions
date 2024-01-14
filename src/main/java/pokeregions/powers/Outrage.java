package pokeregions.powers;

import pokeregions.PokemonRegions;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class Outrage extends AbstractUnremovablePower {
    public static final String POWER_ID = PokemonRegions.makeID(Outrage.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int outrageDamageThreshold;

    public Outrage(AbstractCreature owner, int amount, int amount2) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        this.amount2 = amount2;
        this.outrageDamageThreshold = amount2;
        this.isTwoAmount = true;
        this.loadRegion("anger");
        updateDescription();
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        if (info.owner != null && info.type == DamageInfo.DamageType.NORMAL && info.owner != this.owner && damageAmount > 0) {
            this.amount2 -= damageAmount;
            if (this.amount2 <= 0) {
                this.amount++;
                this.amount2 = outrageDamageThreshold;
                this.flash();
                updateDescription();
            } else {
                this.flashWithoutSound();
                updateDescription();
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + amount2 + DESCRIPTIONS[2];
    }
}
