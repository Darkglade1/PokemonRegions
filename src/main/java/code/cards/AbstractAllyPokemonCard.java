package code.cards;

import basemod.helpers.TooltipInfo;
import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import code.PokemonRegions;
import code.monsters.AbstractPokemonAlly;
import code.patches.TypeOverridePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.List;

import static code.PokemonRegions.makeID;

@NoPools
public abstract class AbstractAllyPokemonCard extends AbstractEasyCard {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("PokemonType"));
    public static final String TYPE = uiStrings.TEXT[0];
    private static final CardStrings staminaStrings = CardCrawlGame.languagePack.getCardStrings(makeID("StaminaKeyword"));
    public static final String STAMINA_NAME = staminaStrings.NAME;
    public static final String STAMINA_DESCRIPTION = staminaStrings.DESCRIPTION;

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

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractAllyPokemonCard c = (AbstractAllyPokemonCard)super.makeStatEquivalentCopy();
        c.staminaCost1 = staminaCost1;
        c.staminaCost2 = staminaCost2;
        c.currentStamina = currentStamina;
        c.maxStamina = maxStamina;
        return c;
    }

    @Override
    public List<TooltipInfo> getCustomTooltips() {
        ArrayList<TooltipInfo> info = new ArrayList<>();
        info.add(new TooltipInfo(STAMINA_NAME, STAMINA_DESCRIPTION));
        return info;
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
    }

    public void initializeDescriptionFromMoves() {
        this.rawDescription = "*" + move1Name + " (" + staminaCost1 + ") " + move1Description + " NL " + "*" + move2Name + " (" + staminaCost2 + ") " + move2Description;
        this.rawDescription += " NL " + currentStamina + "/" + maxStamina + " *" + STAMINA_NAME;
        this.initializeDescription();
    }

    public void updateStamina (int newStamina) {
        this.currentStamina = misc = newStamina;
    }

    public abstract AbstractPokemonAlly getAssociatedPokemon(float x, float y);
}
