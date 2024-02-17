package pokeregions.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.monsters.act3.enemies.GroudonEnemy;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.applyToTarget;
import static pokeregions.util.Wiz.atb;

public class Ruby extends AbstractEasyRelic {
    public static final String ID = makeID(Ruby.class.getSimpleName());
    private static final int DAMAGE = 3;

    public Ruby() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void atBattleStart() {
        for (AbstractMonster mo : Wiz.getEnemies()) {
            if (mo instanceof GroudonEnemy) {
                this.flash();
                atb(new RelicAboveCreatureAction(mo, this));
                applyToTarget(mo, mo, new pokeregions.powers.Ruby(mo, DAMAGE));
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
