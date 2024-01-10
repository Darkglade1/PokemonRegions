package code.events.act1;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import code.PokemonRegions;
import code.ui.PokemonTeamButton;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;

import static code.PokemonRegions.makeID;
import static code.util.Wiz.adp;

public class BerryBush extends PhasedEvent {
    public static final String ID = makeID("BerryBush");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private static final float HEAL = 0.25F;
    private static final float A15_HEAL = 0.20F;
    private final int heal;

    private final static int STAMINA_HEAL = 2;

    public BerryBush() {
        super(ID, title, PokemonRegions.makeEventPath("BerryBush.png"));
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.heal = (int)((float)adp().maxHealth * A15_HEAL);
        } else {
            this.heal = (int)((float)adp().maxHealth * HEAL);
        }
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + heal + OPTIONS[3], "g"), (i)->{
                    adp().heal(this.heal, true);
                    transitionKey("Yourself");
                }).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[4] + STAMINA_HEAL + OPTIONS[5], "g"), (i)->{
                    PokemonTeamButton.teamWideHeal(STAMINA_HEAL);
                    AbstractDungeon.topPanel.panelHealEffect();
                    transitionKey("Pokemon");
                }));

        registerPhase("Yourself", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[6], (t)->this.openMap()));
        registerPhase("Pokemon", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[6], (t)->this.openMap()));
        transitionKey(0);
    }
}