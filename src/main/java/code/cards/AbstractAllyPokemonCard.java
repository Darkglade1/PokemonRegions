package code.cards;

import code.PokemonRegions;
import code.monsters.AbstractPokemonAlly;
import code.patches.TypeOverridePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

import static code.PokemonRegions.makeID;

public abstract class AbstractAllyPokemonCard extends AbstractEasyCard {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("PokemonType"));
    public static final String TYPE = uiStrings.TEXT[0];

    public int staminaCost1;
    public int staminaCost2;
    public int currentStamina;
    public int maxStamina;
    public String move1Name;
    public String move2Name;
    public String move1Description;
    public String move2Description;

    public AbstractAllyPokemonCard(final String cardID, final CardRarity rarity) {
        super(cardID, -2, CardType.SKILL, rarity, CardTarget.NONE, PokemonRegions.Enums.Pokedex);
        TypeOverridePatch.TypeOverrideField.typeOverride.set(this, TYPE);
    }

    public AbstractCard makeStatEquivalentCopy() {
        AbstractAllyPokemonCard c = (AbstractAllyPokemonCard)super.makeStatEquivalentCopy();
        c.staminaCost1 = staminaCost1;
        c.staminaCost2 = staminaCost2;
        c.currentStamina = currentStamina;
        c.maxStamina = maxStamina;
        return c;
    }

    public void initializeDescriptionFromMoves() {
        this.rawDescription = "*" + move1Name + " (" + staminaCost1 + ") " + move1Description + " NL " + "*" + move2Name + " (" + staminaCost2 + ") " + move2Description;
        this.rawDescription += " NL " + currentStamina + "/" + maxStamina;
        this.initializeDescription();
    }

    public void updateStamina (int newStamina) {
        this.currentStamina = misc = newStamina;
    }

    public abstract AbstractPokemonAlly getAssociatedPokemon(float x, float y);
}
