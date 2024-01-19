package pokeregions.events.act1;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import pokeregions.PokemonRegions;
import pokeregions.cards.MomentOfCourage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class CinnabarGym extends PhasedEvent {
    public static final String ID = makeID("CinnabarGym");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private final AbstractCard chosenCard;
    private final AbstractCard card;

    public CinnabarGym() {
        super(ID, title, PokemonRegions.makeEventPath("CinnabarGym.png"));
        chosenCard = getRandomNonBasicCard();
        card = CardLibrary.getCard(MomentOfCourage.ID).makeCopy();
        TextPhase.OptionInfo cardOption = createCardPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + chosenCard.name + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4] + card.name + OPTIONS[3], "g"), card, (i)->{
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(chosenCard, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            AbstractDungeon.player.masterDeck.removeCard(chosenCard);
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, (float)Settings.WIDTH * 0.3F, (float)Settings.HEIGHT / 2.0F));
            transitionKey("Defect");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(cardOption).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[5], "g"), (i)->{
                    AbstractDungeon.gridSelectScreen.open(adp().masterDeck.getUpgradableCards(), 1, OPTIONS[5], true, false, false, false);
                    transitionKey("Research");
                }));

        registerPhase("Defect", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[6], (t)->this.openMap()));
        registerPhase("Research", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[6], (t)->this.openMap()));
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

    private static AbstractCard getRandomNonBasicCard() {
        ArrayList<AbstractCard> list = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity != AbstractCard.CardRarity.BASIC && c.type != AbstractCard.CardType.CURSE) {
                list.add(c);
            }
        }

        if (list.isEmpty()) {
            return null;
        } else {
            Collections.shuffle(list, new Random(AbstractDungeon.miscRng.randomLong()));
            return list.get(0);
        }
    }

    public static boolean canSpawn() {
        AbstractCard card = getRandomNonBasicCard();
        return card != null;
    }
}