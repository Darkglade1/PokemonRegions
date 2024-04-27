package pokeregions.events.act2;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import pokeregions.PokemonRegions;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.util.Tags;

import java.util.ArrayList;
import java.util.Collections;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class RadioTower extends PhasedEvent {
    public static final String ID = makeID("RadioTower");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    public RadioTower() {
        super(ID, title, PokemonRegions.makeEventPath("RadioTower.png"));
        this.noCardsInRewards = true;
        ArrayList<AbstractCard> possibleOptions = getRandomNonStarterPokemonList();
        AbstractCard option1 = possibleOptions.get(0);
        AbstractCard option2 = possibleOptions.get(1);
        int gold1 = getGoldFromRarity(option1);
        int gold2 = getGoldFromRarity(option2);

        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + option1.name + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4] + gold1 + OPTIONS[5], "g"), (i)->{
                    AbstractDungeon.effectList.add(new PurgeCardEffect(option1));
                    PlayerSpireFields.pokemonTeam.get(adp()).removeCard(option1);
                    AbstractDungeon.effectList.add(new RainingGoldEffect(gold1));
                    adp().gainGold(gold1);
                    transitionKey("Offer");
                }).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + option2.name + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4] + gold2 + OPTIONS[5], "g"), (i)->{
                    AbstractDungeon.effectList.add(new PurgeCardEffect(option2));
                    PlayerSpireFields.pokemonTeam.get(adp()).removeCard(option2);
                    AbstractDungeon.effectList.add(new RainingGoldEffect(gold2));
                    adp().gainGold(gold2);
                    transitionKey("Offer");
                }).
                addOption(OPTIONS[1], (i)->{
                    transitionKey("Ignore");
                }));

        registerPhase("Offer", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[6], (t)->this.openMap()));
        registerPhase("Ignore", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[6], (t)->this.openMap()));
        transitionKey(0);
    }

    private ArrayList<AbstractCard> getRandomNonStarterPokemonList() {
        ArrayList<AbstractCard> validCards = new ArrayList<>();
        for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
            if (!card.hasTag(Tags.STARTER_POKEMON)) {
                validCards.add(card);
            }
        }
        Collections.shuffle(validCards, AbstractDungeon.eventRng.random);
        return validCards;
    }

    private int getGoldFromRarity(AbstractCard card) {
        if (card.rarity == AbstractCard.CardRarity.RARE) {
            return 240;
        } else if (card.rarity == AbstractCard.CardRarity.UNCOMMON) {
            return 180;
        } else {
            return 120;
        }
    }

    public static boolean canSpawn() {
        int nonStarterCount = 0;
        for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
            if (!card.hasTag(Tags.STARTER_POKEMON)) {
                nonStarterCount++;
            }
        }
        return nonStarterCount >= 2;
    }
}