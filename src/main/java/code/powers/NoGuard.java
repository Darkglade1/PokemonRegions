package code.powers;

import code.PokemonRegions;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class NoGuard extends AbstractEasyPower {
    public static final String POWER_ID = PokemonRegions.makeID(NoGuard.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public NoGuard(AbstractCreature owner, int damageIncrease, int HPThreshold) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, damageIncrease);
        this.amount2 = HPThreshold;
        this.priority = 99;
        updateDescription();
        this.loadRegion("flex");
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL && underHPThreshold()) {
            return damage * (1 + ((float)amount / 100));
        } else {
            return damage;
        }
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL && underHPThreshold()) {
            return damage * (1 + (float)amount / 100);
        } else {
            return damage;
        }
    }

    private boolean underHPThreshold() {
        return (int)(((float)owner.currentHealth / owner.maxHealth) * 100) < this.amount2;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount2 + DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];
    }
}
