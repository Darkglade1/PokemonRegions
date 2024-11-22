package pokeregions.events.act2;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Blissey;
import pokeregions.util.PokemonReward;

import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class HelpingHand extends PhasedEvent {
    public static final String ID = makeID("HelpingHand");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private static final float HEAL = 0.30F;
    private static final float A15_HEAL = 0.25F;
    private final int heal;

    private static final float ALT_HEAL = 0.70F;
    private static final float A15_ALT_HEAL = 0.60F;
    private final int altHeal;


    public HelpingHand() {
        super(ID, title, PokemonRegions.makeEventPath("Blissey.png"));
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.heal = (int)((float)(adp().maxHealth - adp().currentHealth) * A15_HEAL);
            this.altHeal = (int)((float)(adp().maxHealth - adp().currentHealth) * A15_ALT_HEAL);
        } else {
            this.heal = (int)((float)(adp().maxHealth - adp().currentHealth) * HEAL);
            this.altHeal = (int)((float)(adp().maxHealth - adp().currentHealth) * ALT_HEAL);
        }
        this.noCardsInRewards = true;
        AbstractCard pokemon = CardLibrary.getCard(Blissey.ID).makeCopy();
        TextPhase.OptionInfo littleHealOption = createCardPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + heal + OPTIONS[3] + " " + OPTIONS[4] + pokemon.name + OPTIONS[5], "g"), pokemon, (i)->{
            adp().heal(this.heal, true);
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(pokemon.cardID));
            AbstractDungeon.combatRewardScreen.open();
            transitionKey("LittleHeal");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(littleHealOption).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[2] + altHeal + OPTIONS[3], "g"), (i)->{
                    adp().heal(this.altHeal, true);
                    transitionKey("BigHeal");
                }));

        registerPhase("LittleHeal", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[6], (t)->this.openMap()));
        registerPhase("BigHeal", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[6], (t)->this.openMap()));
        transitionKey(0);
    }

    public TextPhase.OptionInfo createCardPreviewOption(String optionText, AbstractCard previewCard, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard).setOptionResult(onClick);
    }

    public static boolean canSpawn() {
        return adp().currentHealth <= (adp().maxHealth * 0.70f);
    }
}