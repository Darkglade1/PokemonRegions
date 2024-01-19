package pokeregions.events.act1;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.CombatPhase;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import pokeregions.PokemonRegions;
import pokeregions.dungeons.EncounterIDs;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.util.Tags;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class TeamRocket extends PhasedEvent {
    public static final String ID = makeID("TeamRocket");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private static final int GOLD = 100;

    public TeamRocket() {
        super(ID, title, PokemonRegions.makeEventPath("TeamRocket.png"));
        this.noCardsInRewards = true;
        AbstractCard stolenPokemon = getRandomNonStarterPokemon();
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "r") + " " + FontHelper.colorString(OPTIONS[3], "g"), (i)->{
                    AbstractDungeon.getCurrRoom().eliteTrigger = true;
                    transitionKey("Fight");
                }).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[4] + stolenPokemon.name + OPTIONS[5], "r"), (i)->{
                    AbstractDungeon.effectList.add(new PurgeCardEffect(stolenPokemon));
                    PlayerSpireFields.pokemonTeam.get(adp()).removeCard(stolenPokemon);
                    transitionKey("Run");
                }));

        registerPhase("Fight", new CombatPhase(EncounterIDs.TEAM_ROCKET)
                .addRewards(true, (room)-> {
                    room.addRelicToRewards(AbstractDungeon.returnRandomRelicTier());
                    room.addGoldToRewards(GOLD);
                })
                .setNextKey("Victory"));

        registerPhase("Victory", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[6], (t)->this.openMap()));
        registerPhase("Run", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[6], (t)->this.openMap()));
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

    public static boolean canSpawn() {
        for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
            if (!card.hasTag(Tags.STARTER_POKEMON)) {
                return AbstractDungeon.floorNum > 6;
            }
        }
        return false;
    }
}