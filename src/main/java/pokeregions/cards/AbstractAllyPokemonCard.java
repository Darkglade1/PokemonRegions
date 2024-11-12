package pokeregions.cards;

import basemod.helpers.TooltipInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.PokemonRegions;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.patches.TypeOverridePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import pokeregions.util.Tags;

import java.util.ArrayList;
import java.util.List;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.monsters.AbstractPokemonAlly.MOVE_1;
import static pokeregions.monsters.AbstractPokemonAlly.MOVE_2;


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
    public boolean move1isLimited = false;
    public boolean move2isLimited = false;
    public boolean hasUsedMove1 = false;
    public boolean hasUsedMove2 = false;
    public boolean overrideWithDescription;

    public AbstractAllyPokemonCard(final String cardID, final CardRarity rarity) {
        super(cardID, -2, CardType.SKILL, rarity, CardTarget.NONE, PokemonRegions.Enums.Pokedex);
        this.move1Name = cardStrings.EXTENDED_DESCRIPTION[0];
        this.move2Name = cardStrings.EXTENDED_DESCRIPTION[1];
        TypeOverridePatch.TypeOverrideField.typeOverride.set(this, TYPE);
        this.tags.add(CardTags.HEALING); // Hopefully this stops people from randomly generating these.
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
            this.rawDescription = getMoveDescription(MOVE_1) + " NL " + getMoveDescription(MOVE_2);
        }
        this.rawDescription += " NL " + currentStamina + "/" + maxStamina + " " + STAMINA_KEYWORD;
        this.initializeDescription();
    }

    private String getMoveDescription(int move) {
        String greyColor = "[#808080]";
        String bracket = "[]";
        String moveName = "";
        int staminaCost = 0;
        String moveDescription = "";
        boolean moveIsLimited = false;
        boolean hasUsedMove = false;

        if (move == MOVE_1) {
            moveName = move1Name;
            staminaCost = staminaCost1;
            moveDescription = move1Description;
            moveIsLimited = move1isLimited;
            hasUsedMove = hasUsedMove1;
        }
        if (move == MOVE_2) {
            moveName = move2Name;
            staminaCost = staminaCost2;
            moveDescription = move2Description;
            moveIsLimited = move2isLimited;
            hasUsedMove = hasUsedMove2;
        }

        String description = moveName + " (" + staminaCost + ") " + moveDescription;
        if (moveIsLimited) {
            CardStrings keywordInfo = CardCrawlGame.languagePack.getCardStrings(makeID("LimitedKeyword"));
            String limited = keywordInfo.NAME + LocalizedStrings.PERIOD;
            if (hasUsedMove) {
                limited = greyColor + limited + bracket;
            } else {
                limited = "*" + limited;
            }
            description += " " + limited;
        }
        return description;
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

    @Override
    public List<TooltipInfo> getCustomTooltips() {
        ArrayList<TooltipInfo> info = new ArrayList<>();
        if (tags.contains(Tags.STARTER_POKEMON)) {
            CardStrings starterInfo = CardCrawlGame.languagePack.getCardStrings(makeID("StarterKeyword"));
            TooltipInfo tip = new TooltipInfo(starterInfo.NAME, starterInfo.DESCRIPTION);
            info.add(tip);
        }
        if (move1isLimited || move2isLimited) {
            CardStrings keywordInfo = CardCrawlGame.languagePack.getCardStrings(makeID("LimitedKeyword"));
            TooltipInfo tip = new TooltipInfo(keywordInfo.NAME, keywordInfo.DESCRIPTION);
            info.add(tip);
        }
        return info;
    }

    public abstract AbstractPokemonAlly getAssociatedPokemon(float x, float y);

    public void use(AbstractPlayer p, AbstractMonster m) {

    }
}
