package pokeregions.events.act1;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act1.Flareon;
import pokeregions.cards.pokemonAllyCards.act1.Jolteon;
import pokeregions.cards.pokemonAllyCards.act1.Vaporeon;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.util.PokemonReward;
import pokeregions.util.Tags;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class TradeOffer extends PhasedEvent {
    public static final String ID = makeID("TradeOffer");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    public TradeOffer() {
        super(ID, title, PokemonRegions.makeEventPath("TradeOffer.png"));
        this.noCardsInRewards = true;
        AbstractCard jolteon = CardLibrary.getCard(Jolteon.ID).makeCopy();
        AbstractCard flareon = CardLibrary.getCard(Flareon.ID).makeCopy();
        AbstractCard vaporeon = CardLibrary.getCard(Vaporeon.ID).makeCopy();
        ArrayList<AbstractCard> possiblePokemon = new ArrayList<>();
        possiblePokemon.add(jolteon);
        possiblePokemon.add(flareon);
        possiblePokemon.add(vaporeon);
        Collections.shuffle(possiblePokemon, AbstractDungeon.eventRng.random);
        AbstractCard option1Pokemon = possiblePokemon.get(0);
        AbstractCard option2Pokemon = possiblePokemon.get(1);
        AbstractCard tradedPokemon = getRandomNonStarterPokemon();

        TextPhase.OptionInfo option1 = createCardPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + tradedPokemon.name + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4] + option1Pokemon.name + OPTIONS[3], "g"), option1Pokemon, (i)->{
            AbstractDungeon.effectList.add(new PurgeCardEffect(tradedPokemon));
            PlayerSpireFields.pokemonTeam.get(adp()).removeCard(tradedPokemon);
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(option1Pokemon.cardID));
            AbstractDungeon.combatRewardScreen.open();
            transitionKey("Trade");
        });
        TextPhase.OptionInfo option2 = createCardPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + tradedPokemon.name + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4] + option2Pokemon.name + OPTIONS[3], "g"), option2Pokemon, (i)->{
            AbstractDungeon.effectList.add(new PurgeCardEffect(tradedPokemon));
            PlayerSpireFields.pokemonTeam.get(adp()).removeCard(tradedPokemon);
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(option2Pokemon.cardID));
            AbstractDungeon.combatRewardScreen.open();
            transitionKey("Trade");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(option1).
                addOption(option2).
                addOption(OPTIONS[1], (i)->{
                    transitionKey("Decline");
                }));

        registerPhase("Trade", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[5], (t)->this.openMap()));
        registerPhase("Decline", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[5], (t)->this.openMap()));
        transitionKey(0);
    }

    private AbstractCard getRandomNonStarterPokemon() {
        ArrayList<AbstractCard> validCards = new ArrayList<>();
        for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
            if (!card.hasTag(Tags.STARTER_POKEMON)) {
                validCards.add(card);
            }
        }
       return validCards.get(AbstractDungeon.eventRng.random(validCards.size() - 1));
    }

    public TextPhase.OptionInfo createCardPreviewOption(String optionText, AbstractCard previewCard, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard).setOptionResult(onClick);
    }

    public static boolean canSpawn() {
        for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
            if (!card.hasTag(Tags.STARTER_POKEMON)) {
                return true;
            }
        }
        return false;
    }
}