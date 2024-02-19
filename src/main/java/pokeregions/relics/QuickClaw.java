package pokeregions.relics;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.atb;

public class QuickClaw extends AbstractEasyRelic {
    public static final String ID = makeID(QuickClaw.class.getSimpleName());
    private static final int BLOCK = 8;

    public QuickClaw() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        this.counter = 1;
    }

    @Override
    public int onPlayerGainedBlock(float blockAmount) {
        if (this.counter > 0) {
            this.flash();
            atb(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            counter = 0;
            return MathUtils.floor(blockAmount + BLOCK);
        }
        return MathUtils.floor(blockAmount);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + BLOCK + DESCRIPTIONS[1];
    }
}
