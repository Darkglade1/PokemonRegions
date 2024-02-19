package pokeregions.events.act3;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import pokeregions.PokemonRegions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class FeatherCarnival extends PhasedEvent {
    public static final String ID = makeID("FeatherCarnival");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private static final int NUM_UPGRADES_1 = 2;
    private static final int NUM_UPGRADES_2 = 3;
    private static final int GOLD_COST_1 = 60;
    private static final int GOLD_COST_2 = 120;

    public FeatherCarnival() {
        super(ID, title, PokemonRegions.makeEventPath("FeatherCarnival.png"));
        TextPhase.OptionInfo goldOption1 = createOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[4] + GOLD_COST_1 + OPTIONS[5], "r") + " " + FontHelper.colorString(OPTIONS[6] + NUM_UPGRADES_1 + OPTIONS[7], "g"), (i)->{
            adp().loseGold(GOLD_COST_1);
            upgradeCards(NUM_UPGRADES_1);
            transitionKey("Play");
        });
        if (!hasEnoughGold1()) {
            goldOption1 = new TextPhase.OptionInfo(OPTIONS[8]).enabledCondition(this::hasEnoughGold1);
        }
        TextPhase.OptionInfo goldOption2 = createOption(OPTIONS[2] + FontHelper.colorString(OPTIONS[4] + GOLD_COST_2 + OPTIONS[5], "r") + " " + FontHelper.colorString(OPTIONS[6] + NUM_UPGRADES_2 + OPTIONS[7], "g"), (i)->{
            adp().loseGold(GOLD_COST_2);
            upgradeCards(NUM_UPGRADES_2);
            transitionKey("Play");
        });
        if (!hasEnoughGold2()) {
            goldOption2 = new TextPhase.OptionInfo(OPTIONS[8]).enabledCondition(this::hasEnoughGold2);
        }
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[3], "g"), (i)->{
                    upgradeCards(1);
                    transitionKey("Play");
                }).
                addOption(goldOption1).
                addOption(goldOption2));

        registerPhase("Play", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[9], (t)->this.openMap()));
        transitionKey(0);
    }

    private void upgradeCards(int num) {
        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
        ArrayList<AbstractCard> upgradableCards = new ArrayList<>();
        ArrayList<AbstractCard> cardsToUpgrade = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.canUpgrade()) {
                upgradableCards.add(c);
            }
        }
        Collections.shuffle(upgradableCards, new Random(AbstractDungeon.miscRng.randomLong()));
        for (int i = 0; i < num && i < upgradableCards.size(); i++) {
            cardsToUpgrade.add(upgradableCards.get(i));
        }
        for (AbstractCard card : cardsToUpgrade) {
            card.upgrade();
            adp().bottledCardUpgradeCheck(card);
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy()));
        }
    }

    public TextPhase.OptionInfo createOption(String optionText, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText).setOptionResult(onClick);
    }

    public boolean hasEnoughGold1() {
        return adp().gold >= GOLD_COST_1;
    }

    public boolean hasEnoughGold2() {
        return adp().gold >= GOLD_COST_2;
    }
}