package pokeregions.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.*;

public class GlitteringWish extends AbstractEasyRelic {
    public static final String ID = makeID(GlitteringWish.class.getSimpleName());
    private static final int GOLD = 300;
    private static final int STR_LOSS = 1;

    public GlitteringWish() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onEquip() {
        CardCrawlGame.sound.play("GOLD_GAIN");
        adp().gainGold(GOLD);
    }

    @Override
    public void atBattleStart() {
        this.flash();
        atb(new RelicAboveCreatureAction(adp(), this));
        applyToTarget(adp(), adp(), new StrengthPower(adp(), -STR_LOSS));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + GOLD + DESCRIPTIONS[1] + STR_LOSS + DESCRIPTIONS[2];
    }
}
