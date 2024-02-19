package pokeregions.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;

public class MagicGuard extends AbstractUnremovablePower {
    public static final String POWER_ID = PokemonRegions.makeID(MagicGuard.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public MagicGuard(AbstractCreature owner) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, 1);
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if (this.amount >= 1) {
            flash();
            this.amount = 0;
        }
    }

    @Override
    public void atEndOfRound() {
        amount = 1;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
