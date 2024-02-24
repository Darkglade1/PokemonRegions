package pokeregions.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;
import pokeregions.monsters.act4.GiratinaEnemy;

public class DistortionWorld extends AbstractUnremovablePower {
    public static final String POWER_ID = PokemonRegions.makeID(DistortionWorld.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    GiratinaEnemy giratina;

    public DistortionWorld(GiratinaEnemy owner) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, 0);
        this.giratina = owner;
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        return 0;
    }

    @Override
    public void atEndOfRound() {
        if (giratina.leyline1.isDeadOrEscaped() && giratina.leyline2.isDeadOrEscaped()) {
            giratina.exitDistortionWorld();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
