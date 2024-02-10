package pokeregions.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.*;

public class DeathWish extends AbstractEasyRelic {
    public static final String ID = makeID(DeathWish.class.getSimpleName());
    private static final int STR = 2;
    private static final int DEX_LOSS = 1;

    public DeathWish() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void atBattleStart() {
        this.flash();
        atb(new RelicAboveCreatureAction(adp(), this));
        applyToTarget(adp(), adp(), new StrengthPower(adp(), STR));
        applyToTarget(adp(), adp(), new DexterityPower(adp(), -DEX_LOSS));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + STR + DESCRIPTIONS[1] + DEX_LOSS + DESCRIPTIONS[2];
    }
}
