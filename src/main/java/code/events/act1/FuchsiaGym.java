package code.events.act1;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import code.PokemonRegions;
import code.cards.FightAsOne;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.function.Consumer;

import static code.PokemonRegions.makeID;
import static code.util.Wiz.adp;

public class FuchsiaGym extends PhasedEvent {
    public static final String ID = makeID("FuchsiaGym");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;
    private static final float MAX_HP_LOSS_PERCENTAGE = 0.06F;
    private static final float MAX_HP_LOSS_PERCENTAGE_HIGH_ASCENSION = 0.08F;
    private final int maxHPLoss;
    private final AbstractCard card;

    public FuchsiaGym() {
        super(ID, title, PokemonRegions.makeEventPath("FuchsiaGym.png"));
        if (AbstractDungeon.ascensionLevel < 15) {
            maxHPLoss = (int) ((float) AbstractDungeon.player.maxHealth * MAX_HP_LOSS_PERCENTAGE);
        } else {
            maxHPLoss = (int) ((float) AbstractDungeon.player.maxHealth * MAX_HP_LOSS_PERCENTAGE_HIGH_ASCENSION);
        }
        card = CardLibrary.getCard(FightAsOne.ID).makeCopy();
        TextPhase.OptionInfo cardOption = createCardPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + maxHPLoss + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4] + card.name + OPTIONS[5], "g"), card, (i)->{
            CardCrawlGame.sound.play("BLUNT_FAST");
            adp().decreaseMaxHealth(maxHPLoss);
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, (float)Settings.WIDTH * 0.3F, (float)Settings.HEIGHT / 2.0F));
            this.imageEventText.loadImage(PokemonRegions.makeEventPath("SmokeGym.png"));
            transitionKey("Challenge");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(cardOption).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[6], "g"), (i)->{
                    AbstractDungeon.gridSelectScreen.open(adp().masterDeck.getPurgeableCards(), 1, OPTIONS[6], false, false, false, true);
                    transitionKey("Retreat");
                }));

        registerPhase("Challenge", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[7], (t)->this.openMap()));
        registerPhase("Retreat", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[7], (t)->this.openMap()));
        transitionKey(0);
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
                adp().masterDeck.removeCard(c);
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }

    public TextPhase.OptionInfo createCardPreviewOption(String optionText, AbstractCard previewCard, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard).setOptionResult(onClick);
    }
}