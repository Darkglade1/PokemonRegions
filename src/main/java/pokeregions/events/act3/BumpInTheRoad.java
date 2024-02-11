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
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import pokeregions.PokemonRegions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class BumpInTheRoad extends PhasedEvent {
    public static final String ID = makeID("BumpInTheRoad");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private final AbstractCard chosenCardAttack;
    private final AbstractCard chosenCardSkill;
    private boolean forUpgrade;
    private boolean forRemove;

    public BumpInTheRoad() {
        super(ID, title, PokemonRegions.makeEventPath("Kecleon.png"));
        chosenCardAttack = getRandomNonBasicAttack();
        chosenCardSkill = getRandomNonBasicSkill();
        TextPhase.OptionInfo attackOption = createCardPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[3] + chosenCardAttack.name + OPTIONS[4], "r") + " " + FontHelper.colorString(OPTIONS[5], "g"), chosenCardAttack, (i)->{
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(chosenCardAttack, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            AbstractDungeon.player.masterDeck.removeCard(chosenCardAttack);
            AbstractDungeon.gridSelectScreen.open(adp().masterDeck.getUpgradableCards(), 1, OPTIONS[5], true, false, false, false);
            forUpgrade = true;
            transitionKey("Attack");
        });
        TextPhase.OptionInfo skillOption = createCardPreviewOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[3] + chosenCardSkill.name + OPTIONS[4], "r") + " " + FontHelper.colorString(OPTIONS[6], "g"), chosenCardSkill, (i)->{
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(chosenCardSkill, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            AbstractDungeon.player.masterDeck.removeCard(chosenCardSkill);
            AbstractDungeon.gridSelectScreen.open(adp().masterDeck.getPurgeableCards(), 1, OPTIONS[6], false, false, false, true);
            forRemove = true;
            transitionKey("Skill");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(attackOption).
                addOption(skillOption).
                addOption(OPTIONS[2], (i)->{
                    transitionKey("Leave");
                }));

        registerPhase("Attack", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[7], (t)->this.openMap()));
        registerPhase("Skill", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[7], (t)->this.openMap()));
        registerPhase("Leave", new TextPhase(DESCRIPTIONS[3]).addOption(OPTIONS[7], (t)->this.openMap()));
        transitionKey(0);
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty() && forUpgrade) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                c.upgrade();
                adp().bottledCardUpgradeCheck(c);
                AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty() && forRemove) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (float)Settings.WIDTH / 2, (float)Settings.HEIGHT / 2));
                adp().masterDeck.removeCard(c);
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }

    public TextPhase.OptionInfo createCardPreviewOption(String optionText, AbstractCard previewCard, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard).setOptionResult(onClick);
    }

    private static AbstractCard getRandomNonBasicAttack() {
        ArrayList<AbstractCard> list = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity != AbstractCard.CardRarity.BASIC && c.type == AbstractCard.CardType.ATTACK) {
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

    private static AbstractCard getRandomNonBasicSkill() {
        ArrayList<AbstractCard> list = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity != AbstractCard.CardRarity.BASIC && c.type == AbstractCard.CardType.SKILL) {
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
        AbstractCard card = getRandomNonBasicAttack();
        AbstractCard card2 = getRandomNonBasicSkill();
        return card != null && card2 != null;
    }
}