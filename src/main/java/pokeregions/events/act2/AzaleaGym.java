package pokeregions.events.act2;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import pokeregions.PokemonRegions;
import pokeregions.cards.SicEm;

import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class AzaleaGym extends PhasedEvent {
    public static final String ID = makeID("AzaleaGym");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;
    private static final float HP_LOSS_PERCENTAGE = 0.08F;
    private static final float HP_LOSS_PERCENTAGE_HIGH_ASCENSION = 0.12F;
    private final int hpLoss;
    private final AbstractCard card;

    public AzaleaGym() {
        super(ID, title, PokemonRegions.makeEventPath("AzaleaGym.png"));
        if (AbstractDungeon.ascensionLevel < 15) {
            hpLoss = (int) ((float) AbstractDungeon.player.maxHealth * HP_LOSS_PERCENTAGE);
        } else {
            hpLoss = (int) ((float) AbstractDungeon.player.maxHealth * HP_LOSS_PERCENTAGE_HIGH_ASCENSION);
        }
        card = CardLibrary.getCard(SicEm.ID).makeCopy();
        TextPhase.OptionInfo cardOption = createCardPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + hpLoss + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4] + card.name + OPTIONS[5], "g"), card, (i)->{
            CardCrawlGame.sound.play("BLUNT_FAST");
            adp().damage(new DamageInfo(null, hpLoss));
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, (float)Settings.WIDTH * 0.3F, (float)Settings.HEIGHT / 2.0F));
            transitionKey("Challenge");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(cardOption).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[6], "g"), (i)->{
                    AbstractDungeon.gridSelectScreen.open(adp().masterDeck.getUpgradableCards(), 1, OPTIONS[6], true, false, false, false);
                    transitionKey("Spectate");
                }));

        registerPhase("Challenge", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[7], (t)->this.openMap()));
        registerPhase("Spectate", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[7], (t)->this.openMap()));
        transitionKey(0);
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                c.upgrade();
                adp().bottledCardUpgradeCheck(c);
                AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }

    public TextPhase.OptionInfo createCardPreviewOption(String optionText, AbstractCard previewCard, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard).setOptionResult(onClick);
    }
}