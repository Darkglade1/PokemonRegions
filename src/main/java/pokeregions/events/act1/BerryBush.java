package pokeregions.events.act1;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.ui.PokemonTeamButton;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class BerryBush extends PhasedEvent {
    public static final String ID = makeID("BerryBush");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private static final float HEAL = 0.25F;
    private static final float A15_HEAL = 0.20F;
    private final int heal;

    private static final float ALT_HEAL = 0.15F;
    private static final float A15_ALT_HEAL = 0.125F;
    private final int altHeal;

    private final static int STAMINA_HEAL = 2;

    public BerryBush() {
        super(ID, title, PokemonRegions.makeEventPath("BerryBush.png"));
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.heal = (int)((float)adp().maxHealth * A15_HEAL);
            this.altHeal = (int)((float)adp().maxHealth * A15_ALT_HEAL);
        } else {
            this.heal = (int)((float)adp().maxHealth * HEAL);
            this.altHeal = (int)((float)adp().maxHealth * ALT_HEAL);
        }
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + heal + OPTIONS[3], "g"), (i)->{
                    adp().heal(this.heal, true);
                    transitionKey("Yourself");
                }).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[2] + altHeal + OPTIONS[3] + " " + OPTIONS[4] + STAMINA_HEAL + OPTIONS[5], "g"), (i)->{
                    adp().heal(this.altHeal, true);
                    PokemonTeamButton.teamWideHeal(STAMINA_HEAL);
                    transitionKey("Pokemon");
                }));

        registerPhase("Yourself", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[6], (t)->this.openMap()));
        registerPhase("Pokemon", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[6], (t)->this.openMap()));
        transitionKey(0);
    }

    public static boolean canSpawn() {
        return adp().currentHealth < adp().maxHealth;
    }
}