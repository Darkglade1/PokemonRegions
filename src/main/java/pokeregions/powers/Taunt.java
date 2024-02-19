package pokeregions.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;

import static pokeregions.util.Wiz.atb;
import static pokeregions.util.Wiz.makePowerRemovable;

public class Taunt extends AbstractUnremovablePower  {
    public static final String POWER_ID = PokemonRegions.makeID(Taunt.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justApplied;

    public Taunt(AbstractCreature owner, boolean justApplied) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, 0);
        this.justApplied = justApplied;
    }

    public Taunt(AbstractCreature owner) {
        this(owner, true);
    }

    @Override
    public void atEndOfRound() {
        if (justApplied) {
            justApplied = false;
        } else {
            makePowerRemovable(this);
            atb(new RemoveSpecificPowerAction(owner, owner, this));
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
