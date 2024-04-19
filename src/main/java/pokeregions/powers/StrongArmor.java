package pokeregions.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;

public class StrongArmor extends AbstractUnremovablePower {
    public static final String POWER_ID = PokemonRegions.makeID(StrongArmor.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public StrongArmor(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        this.priority = 99;
        this.loadRegion("juggernaut");
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
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
