package pokeregions.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.powers.Burn;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.*;

public class DragonFang extends AbstractEasyRelic {
    public static final String ID = makeID(DragonFang.class.getSimpleName());
    private static final int BURN = 2;

    public DragonFang() {
        super(ID, RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public void atBattleStart() {
        this.flash();
        for (AbstractMonster mo : Wiz.getEnemies()) {
            atb(new RelicAboveCreatureAction(mo, this));
            applyToTarget(mo, adp(), new Burn(mo, BURN));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + BURN + DESCRIPTIONS[1];
    }
}
