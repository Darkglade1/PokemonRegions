package pokeregions.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;

public class CunningWish extends AbstractEasyRelic {
    public static final String ID = makeID(CunningWish.class.getSimpleName());
    private static final int DRAW = 4;
    private boolean firstTurn;

    public CunningWish() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void atTurnStart() {
        if (this.firstTurn) {
            atb(new RelicAboveCreatureAction(adp(), this));
            atb(new DrawCardAction(DRAW));
            atb(new LoseEnergyAction(1));
            this.firstTurn = false;
        }
    }

    @Override
    public void atPreBattle() {
        this.firstTurn = true;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + DRAW + DESCRIPTIONS[1];
    }
}
