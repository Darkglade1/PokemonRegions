package pokeregions.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.BarricadePower;

import static pokeregions.PokemonRegions.makeID;

public class VisibleBarricadePower extends AbstractUnremovablePower {
    public static final String POWER_ID = makeID(VisibleBarricadePower.class.getSimpleName());
    public static final String BARRICADE_POWER_ID = BarricadePower.POWER_ID;

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(BARRICADE_POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public VisibleBarricadePower(AbstractCreature owner) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, -1);
        this.loadRegion("barricade");
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
