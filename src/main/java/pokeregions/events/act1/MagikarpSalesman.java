package pokeregions.events.act1;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.Magikarp;
import pokeregions.util.PokemonReward;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Shame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class MagikarpSalesman extends PhasedEvent {
    public static final String ID = makeID("MagikarpSalesman");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private static final int GOLD_COST = 50;

    public MagikarpSalesman() {
        super(ID, title, PokemonRegions.makeEventPath("MagikarpSalesman.png"));
        this.noCardsInRewards = true;
        AbstractCard magikarp = CardLibrary.getCard(Magikarp.ID).makeCopy();
        AbstractCard curse = CardLibrary.getCard(Shame.ID).makeCopy();

        TextPhase.OptionInfo cardOption = createCardPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + GOLD_COST + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4] + magikarp.name + OPTIONS[5], "g"), magikarp, (i)->{
            adp().loseGold(GOLD_COST);
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(magikarp.cardID));
            AbstractDungeon.combatRewardScreen.open();
            transitionKey("Buy");
        });
        if (!hasEnoughGold()) {
            cardOption = new TextPhase.OptionInfo(OPTIONS[8]).enabledCondition(this::hasEnoughGold);
        }
        TextPhase.OptionInfo curseOption = createCardPreviewOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[6], "g") + " " + FontHelper.colorString(OPTIONS[7] + curse.name + OPTIONS[5], "r"), curse, (i)->{
            AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
            transitionKey("Rob");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(cardOption).
                addOption(curseOption).
                addOption(OPTIONS[9], (i)->{
                    transitionKey("Leave");
                }));

        registerPhase("Buy", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[9], (t)->this.openMap()));
        registerPhase("Rob", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[9], (t)->this.openMap()));
        registerPhase("Leave", new TextPhase(DESCRIPTIONS[3]).addOption(OPTIONS[9], (t)->this.openMap()));
        transitionKey(0);
    }

    private boolean hasEnoughGold() {
        return adp().gold >= GOLD_COST;
    }

    public TextPhase.OptionInfo createCardPreviewOption(String optionText, AbstractCard previewCard, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard).setOptionResult(onClick);
    }

    public static boolean canSpawn() {
        return adp().gold >= GOLD_COST;
    }
}