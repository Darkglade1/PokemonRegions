package pokeregions.events.act1;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act1.*;
import pokeregions.util.PokemonReward;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;

public class Yellow extends PhasedEvent {
    public static final String ID = makeID("Yellow");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    AbstractCard pokemon1;
    AbstractCard pokemon2;

    public Yellow() {
        super(ID, title, PokemonRegions.makeEventPath("Yellow.png"));
        this.noCardsInRewards = true;
        AbstractCard caterpie = CardLibrary.getCard(Caterpie.ID).makeCopy();
        AbstractCard pikachu = CardLibrary.getCard(Pikachu.ID).makeCopy();
        AbstractCard rat = CardLibrary.getCard(Rattata.ID).makeCopy();
        AbstractCard omastar = CardLibrary.getCard(Omastar.ID).makeCopy();
        AbstractCard golem = CardLibrary.getCard(Golem.ID).makeCopy();

        ArrayList<AbstractCard> list = new ArrayList<>();
        list.add(caterpie);
        list.add(pikachu);
        list.add(rat);
        list.add(omastar);
        if (AbstractDungeon.miscRng.randomBoolean()) {
            list.add(golem);
        }
        Collections.shuffle(list, new Random(AbstractDungeon.miscRng.randomLong()));

        pokemon1 = list.get(0);
        pokemon2 = list.get(1);

        TextPhase.OptionInfo pokemon1Option = createCardPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[1] + pokemon1.name + OPTIONS[2], "g"), pokemon1, (i)->{
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(pokemon1.cardID));
            AbstractDungeon.combatRewardScreen.open();
            transitionKey("Befriend");
        });
        TextPhase.OptionInfo pokemon2Option = createCardPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[1] + pokemon2.name + OPTIONS[2], "g"), pokemon2, (i)->{
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(pokemon2.cardID));
            AbstractDungeon.combatRewardScreen.open();
            transitionKey("Befriend");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(pokemon1Option).
                addOption(pokemon2Option).
                addOption(OPTIONS[3], (i)->{
                    transitionKey("Leave");
                }));

        registerPhase("Befriend", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[3], (t)->this.openMap()));
        registerPhase("Leave", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[3], (t)->this.openMap()));
        transitionKey(0);
    }

    public TextPhase.OptionInfo createCardPreviewOption(String optionText, AbstractCard previewCard, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard).setOptionResult(onClick);
    }
}