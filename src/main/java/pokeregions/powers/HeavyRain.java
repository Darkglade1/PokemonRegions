package pokeregions.powers;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;
import pokeregions.cards.cardMods.WaterLoggedMod;

public class HeavyRain extends AbstractUnremovablePower  {
    public static final String POWER_ID = PokemonRegions.makeID(HeavyRain.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int cardsAffected = 0;

    public HeavyRain(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        if (cardsAffected < amount) {
            if (!CardModifierManager.hasModifier(card, WaterLoggedMod.ID)) {
                CardModifierManager.addModifier(card, new WaterLoggedMod());
                card.flash();
                cardsAffected++;
            }
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
