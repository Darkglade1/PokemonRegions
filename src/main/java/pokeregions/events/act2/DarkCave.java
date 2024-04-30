package pokeregions.events.act2;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import pokeregions.PokemonRegions;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class DarkCave extends PhasedEvent {
    public static final String ID = makeID("DarkCave");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;
    private static final float HP_LOSS_PERCENTAGE = 0.25F;
    private static final float HP_LOSS_PERCENTAGE_HIGH_ASCENSION = 0.30F;
    private final int hpLoss;
    private static final float MAX_HP_LOSS_PERCENTAGE = 0.10F;
    private static final float MAX_HP_LOSS_PERCENTAGE_HIGH_ASCENSION = 0.12F;
    private final int maxHPLoss;
    private int NUM_UPGRADE = 2;
    private int NUM_REMOVE = 2;
    private boolean forUpgrade = false;
    private boolean forRemove = false;

    public DarkCave() {
        super(ID, title, PokemonRegions.makeEventPath("DarkCave.png"));
        if (AbstractDungeon.ascensionLevel < 15) {
            hpLoss = (int) ((float) AbstractDungeon.player.maxHealth * HP_LOSS_PERCENTAGE);
            maxHPLoss = (int) ((float) AbstractDungeon.player.maxHealth * MAX_HP_LOSS_PERCENTAGE);
        } else {
            hpLoss = (int) ((float) AbstractDungeon.player.maxHealth * HP_LOSS_PERCENTAGE_HIGH_ASCENSION);
            maxHPLoss = (int) ((float) AbstractDungeon.player.maxHealth * MAX_HP_LOSS_PERCENTAGE_HIGH_ASCENSION);
        }
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + hpLoss + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[5] + NUM_UPGRADE + OPTIONS[7], "g"), (i)->{
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    adp().damage(new DamageInfo(null, hpLoss));
                    forUpgrade = true;
                    AbstractDungeon.gridSelectScreen.open(adp().masterDeck.getUpgradableCards(), NUM_UPGRADE, OPTIONS[5] + NUM_UPGRADE + OPTIONS[7], false, false, false, false);
                    transitionKey("Stand");
                }).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[2] + maxHPLoss + OPTIONS[4], "r") + " " + FontHelper.colorString(OPTIONS[6] + NUM_REMOVE + OPTIONS[7], "g"), (i)->{
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    adp().decreaseMaxHealth(maxHPLoss);
                    forRemove = true;
                    AbstractDungeon.gridSelectScreen.open(adp().masterDeck.getPurgeableCards(), NUM_REMOVE, OPTIONS[6] + NUM_REMOVE + OPTIONS[7], false, false, false, false);
                    transitionKey("Run");
                }));

        registerPhase("Stand", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[8], (t)->this.openMap()));
        registerPhase("Run", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[8], (t)->this.openMap()));
        transitionKey(0);
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                if (forUpgrade) {
                    c.upgrade();
                    adp().bottledCardUpgradeCheck(c);
                    AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                    AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                } else if (forRemove) {
                    AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
                    adp().masterDeck.removeCard(c);
                }
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }
}