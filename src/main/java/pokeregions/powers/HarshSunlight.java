package pokeregions.powers;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;
import pokeregions.cards.cardMods.ScorchedMod;
import pokeregions.cards.cardMods.WaterLoggedMod;

public class HarshSunlight extends AbstractUnremovablePower  {
    public static final String POWER_ID = PokemonRegions.makeID(HarshSunlight.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int cardsAffected = 0;

    public HarshSunlight(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        if (cardsAffected < amount) {
            CardModifierManager.addModifier(card, new ScorchedMod());
            card.flash();
            cardsAffected++;
        }
    }

    @Override
    public void atEndOfRound() {
        cardsAffected = 0;
    }

    @Override
    public void updateDescription() {
        if (amount == 1) {
            this.description = DESCRIPTIONS[0];
        } else {
            this.description = DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];
        }
    }
}
