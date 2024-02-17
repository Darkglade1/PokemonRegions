package pokeregions.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.monsters.act3.enemies.rayquaza.RayquazaEnemy;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.*;

public class Emerald extends AbstractEasyRelic {
    public static final String ID = makeID(Emerald.class.getSimpleName());
    private static final int STR = 2;
    private static final int HP_LOSS = 10;

    public Emerald() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void atBattleStart() {
        for (AbstractMonster mo : Wiz.getEnemies()) {
            if (mo instanceof RayquazaEnemy) {
                this.flash();
                atb(new RelicAboveCreatureAction(mo, this));
                applyToTarget(mo, mo, new pokeregions.powers.Emerald(mo, HP_LOSS, STR));
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
