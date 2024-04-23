package pokeregions.events.act2;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Steelix;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.ui.PokemonTeamButton;
import pokeregions.util.PokemonReward;
import pokeregions.util.Tags;

import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class OlivineLighthouse extends PhasedEvent {
    public static final String ID = makeID("OlivineLighthouse");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private final AbstractPotion potion;

    public OlivineLighthouse() {
        super(ID, title, PokemonRegions.makeEventPath("OlivineLighthouse.png"));
        this.noCardsInRewards = true;
        potion = adp().getRandomPotion();
        AbstractCard rewardCard = CardLibrary.getCard(Steelix.ID).makeCopy();
        TextPhase.OptionInfo tradeOption = createCardPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + potion.name + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4] + rewardCard.name + OPTIONS[3], "g"), rewardCard, (i)->{
            adp().removePotion(potion);
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(rewardCard.cardID));
            AbstractDungeon.combatRewardScreen.open();
            transitionKey("Trade");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(tradeOption).
                addOption(OPTIONS[1], (i)->{
                    transitionKey("Decline");
                }));

        registerPhase("Trade", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[5], (t)->this.openMap()));
        registerPhase("Decline", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[5], (t)->this.openMap()));
        transitionKey(0);
    }

    public TextPhase.OptionInfo createCardPreviewOption(String optionText, AbstractCard previewCard, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard).setOptionResult(onClick);
    }

    public static boolean canSpawn() {
        for (AbstractPotion potion : adp().potions) {
            if (!(potion instanceof PotionSlot)) {
                return true;
            }
        }
        return false;
    }
}