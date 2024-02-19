package pokeregions.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.monsters.act3.enemies.KyogreEnemy;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.applyToTarget;
import static pokeregions.util.Wiz.atb;

public class Sapphire extends AbstractEasyRelic {
    public static final String ID = makeID(Sapphire.class.getSimpleName());
    private static final int DAMAGE = 1;

    public Sapphire() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void atBattleStart() {
        for (AbstractMonster mo : Wiz.getEnemies()) {
            if (mo instanceof KyogreEnemy) {
                this.flash();
                atb(new RelicAboveCreatureAction(mo, this));
                applyToTarget(mo, mo, new pokeregions.powers.Sapphire(mo, DAMAGE));
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
