package code.events.act1;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import code.PokemonRegions;
import code.relics.RainbowBadge;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;

import static code.PokemonRegions.makeID;
import static code.util.Wiz.adp;

public class CeladonGym extends PhasedEvent {
    public static final String ID = makeID("CeladonGym");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private static final int MAX_HP = 8;
    private static final float HEALTH_LOSS_PERCENTAGE = 0.15F;
    private static final float HEALTH_LOSS_PERCENTAGE_HIGH_ASCENSION = 0.20F;
    private final int hpLoss;
    private final AbstractRelic relic;

    public CeladonGym() {
        super(ID, title, PokemonRegions.makeEventPath("CeladonGym.png"));
        if (AbstractDungeon.ascensionLevel < 15) {
            hpLoss = (int) ((float) AbstractDungeon.player.maxHealth * HEALTH_LOSS_PERCENTAGE);
        } else {
            hpLoss = (int) ((float) AbstractDungeon.player.maxHealth * HEALTH_LOSS_PERCENTAGE_HIGH_ASCENSION);
        }
        if (adp().hasRelic(RainbowBadge.ID)) {
            relic = RelicLibrary.getRelic(Circlet.ID).makeCopy();
        } else {
            relic = RelicLibrary.getRelic(RainbowBadge.ID).makeCopy();
        }
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + hpLoss + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4], "g"), relic, (i)->{
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    adp().damage(new DamageInfo(null, hpLoss));
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), relic);
                    this.imageEventText.loadImage(PokemonRegions.makeEventPath("Erika.png"));
                    transitionKey("Rescue");
                }).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[5] + MAX_HP + OPTIONS[6], "g"), (i)->{
                    adp().increaseMaxHp(MAX_HP, true);
                    transitionKey("Theft");
                }));

        registerPhase("Rescue", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[7], (t)->this.openMap()));
        registerPhase("Theft", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[7], (t)->this.openMap()));
        transitionKey(0);
    }
}