package pokeregions.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pokeregions.PokemonRegions;

public class InvisibleClosedOffHeart extends AbstractUnremovablePower implements InvisiblePower {
    public static final String POWER_ID = PokemonRegions.makeID(InvisibleClosedOffHeart.class.getSimpleName());

    public InvisibleClosedOffHeart(AbstractCreature owner, int amount) {
        super(POWER_ID, "", PowerType.BUFF, false, owner, amount);
        this.priority = 99;
    }

    private float calculateDamageTakenAmount(float damage) {
        return (int)(damage * (1 - ((float)amount / 100)));
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return calculateDamageTakenAmount(damage);
        } else {
            return damage;
        }
    }
}
