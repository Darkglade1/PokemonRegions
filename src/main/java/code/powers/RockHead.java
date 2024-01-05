package code.powers;

import code.PokemonRegions;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;

public class RockHead extends AbstractUnremovablePower  {
    public static final String POWER_ID = PokemonRegions.makeID(RockHead.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public RockHead(AbstractCreature owner) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, 0);
        this.loadRegion("juggernaut");
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        int bonus = 0;
        AbstractPower platedArmor = owner.getPower(PlatedArmorPower.POWER_ID);
        if (platedArmor != null) {
            bonus += platedArmor.amount;
        }
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage + bonus;
        } else {
            return damage;
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
