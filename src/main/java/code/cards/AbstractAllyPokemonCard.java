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

import static code.PokemonRegions.makeID;


public abstract class AbstractAllyPokemonCard extends AbstractEasyCard {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("PokemonType"));
    public static final String TYPE = uiStrings.TEXT[0];
    private static final CardStrings staminaStrings = CardCrawlGame.languagePack.getCardStrings(makeID("StaminaKeyword"));
    public static final String STAMINA_KEYWORD = staminaStrings.DESCRIPTION;

    public int staminaCost1;
    public int staminaCost2;
    public int currentStamina;
    public int maxStamina;
    public String move1Name;
    public String move2Name;
    public String move1Description;
    public String move2Description;
    public boolean overrideWithDescription;

    public AbstractAllyPokemonCard(final String cardID, final CardRarity rarity) {
        super(cardID, -2, CardType.SKILL, rarity, CardTarget.NONE, PokemonRegions.Enums.Pokedex);
        this.move1Name = cardStrings.EXTENDED_DESCRIPTION[0];
        this.move2Name = cardStrings.EXTENDED_DESCRIPTION[1];
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
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
    }

    public void initializeDescriptionFromMoves() {
        if (overrideWithDescription) {
            this.rawDescription = cardStrings.DESCRIPTION;
        } else {
            this.rawDescription = move1Name + " (" + staminaCost1 + ") " + move1Description + " NL " + move2Name + " (" + staminaCost2 + ") " + move2Description;
        }
        this.rawDescription += " NL " + currentStamina + "/" + maxStamina + " " + STAMINA_KEYWORD;
        this.initializeDescription();
    }

    public void updateStamina (int newStamina) {
        if (newStamina > maxStamina) {
            newStamina = maxStamina;
        }
        if (newStamina < 0) {
            newStamina = 0;
        }
        this.currentStamina = misc = newStamina;
        initializeDescriptionFromMoves();
    }

    public ArrayList<TooltipInfo> getStarterKeyword() {
        ArrayList<TooltipInfo> info = new ArrayList<>();
        CardStrings starterInfo = CardCrawlGame.languagePack.getCardStrings(makeID("StarterKeyword"));
        TooltipInfo tip = new TooltipInfo(starterInfo.NAME, starterInfo.DESCRIPTION);
        info.add(tip);
        return info;
    }

    public abstract AbstractPokemonAlly getAssociatedPokemon(float x, float y);
}
