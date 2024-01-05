package code.powers;

import code.PokemonRegions;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class Sturdy extends AbstractUnremovablePower {
    public static final String POWER_ID = PokemonRegions.makeID(Sturdy.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Sturdy(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        this.priority = 99;
        this.loadRegion("juggernaut");
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL && !this.owner.hasPower(SuperEffective.POWER_ID)) {
            return calculateDamageTakenAmount(damage);
        } else {
            return damage;
        }
    }

    private float calculateDamageTakenAmount(float damage) {
        return damage * (1 - ((float)amount / 100));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}
