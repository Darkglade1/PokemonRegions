package pokeregions.events.act2;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import pokeregions.PokemonRegions;
import pokeregions.relics.PokeballBelt;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class Pokemart extends PhasedEvent {
    public static final String ID = makeID("Pokemart");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private static final int GOLD_COST = 30;
    private static final int NUM_POTIONS = 2;
    private static final int NUM_POKEBALLS = 3;

    public Pokemart() {
        super(ID, title, PokemonRegions.makeEventPath("Pokemart.png"));
        this.noCardsInRewards = true;
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + GOLD_COST + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4] + NUM_POTIONS + OPTIONS[5], "g"), (i)->{
                    adp().loseGold(GOLD_COST);
                    AbstractDungeon.getCurrRoom().rewards.clear();
                    AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
                    AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
                    AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                    AbstractDungeon.combatRewardScreen.open();
                    transitionKey("Potions");
                }).addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[2] + GOLD_COST + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4] + NUM_POKEBALLS + OPTIONS[6], "g"), (i)->{
                    adp().loseGold(GOLD_COST);
                    if (AbstractDungeon.player.hasRelic(PokeballBelt.ID)) {
                        PokeballBelt belt = (PokeballBelt) adp().getRelic(PokeballBelt.ID);
                        belt.increment(NUM_POKEBALLS);
                    }
                    transitionKey("Pokeballs");
                }).addOption(OPTIONS[7], (i)->{
                    transitionKey("Leave");
                }));

        registerPhase("Potions", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[7], (t)->this.openMap()));
        registerPhase("Pokeballs", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[7], (t)->this.openMap()));
        registerPhase("Leave", new TextPhase(DESCRIPTIONS[3]).addOption(OPTIONS[7], (t)->this.openMap()));
        transitionKey(0);
    }

    public static boolean canSpawn() {
        return adp().gold >= GOLD_COST;
    }
}