package pokeregions.events.act2;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Celebi;
import pokeregions.util.PokemonReward;

import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class VoiceOfTheForest extends PhasedEvent {
    public static final String ID = makeID("VoiceOfTheForest");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private static final float HP_LOSS_PERCENTAGE = 0.12F;
    private static final float HP_LOSS_PERCENTAGE_HIGH_ASCENSION = 0.16F;
    private final int hpLoss;

    private static final int MAX_HP_GAIN = 9;

    public VoiceOfTheForest() {
        super(ID, title, PokemonRegions.makeEventPath("Celebi.png"));
        this.noCardsInRewards = true;
        if (AbstractDungeon.ascensionLevel < 15) {
            hpLoss = (int) ((float) AbstractDungeon.player.maxHealth * HP_LOSS_PERCENTAGE);
        } else {
            hpLoss = (int) ((float) AbstractDungeon.player.maxHealth * HP_LOSS_PERCENTAGE_HIGH_ASCENSION);
        }
        AbstractCard rewardCard = CardLibrary.getCard(Celebi.ID).makeCopy();
        TextPhase.OptionInfo stopOption = createCardPreviewOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[4] + hpLoss + OPTIONS[5], "r") + " " + FontHelper.colorString(OPTIONS[6] + rewardCard.name + OPTIONS[7], "g"), rewardCard, (i)->{
            CardCrawlGame.sound.play("BLUNT_FAST");
            adp().damage(new DamageInfo(null, hpLoss));
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(rewardCard.cardID));
            AbstractDungeon.combatRewardScreen.open();
            transitionKey("Stop");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + MAX_HP_GAIN + OPTIONS[3], "g"), (i)->{
                    adp().increaseMaxHp(MAX_HP_GAIN, true);
                    transitionKey("Distract");
                }).
                addOption(stopOption));

        registerPhase("Distract", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[8], (t)->this.openMap()));
        registerPhase("Stop", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[8], (t)->this.openMap()));
        transitionKey(0);
    }

    public TextPhase.OptionInfo createCardPreviewOption(String optionText, AbstractCard previewCard, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard).setOptionResult(onClick);
    }
}