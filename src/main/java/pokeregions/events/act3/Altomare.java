package pokeregions.events.act3;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.CombatPhase;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act1.Flareon;
import pokeregions.cards.pokemonAllyCards.act1.Jolteon;
import pokeregions.dungeons.EncounterIDs;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.util.PokemonReward;

import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class Altomare extends PhasedEvent {
    public static final String ID = makeID("Altomare");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;
    private static final int GOLD = 200;
    private final AbstractCard latias;
    private final AbstractCard latios;

    public Altomare() {
        super(ID, title, PokemonRegions.makeEventPath("Bianca.png"));
        this.noCardsInRewards = true;
        latias = CardLibrary.getCard(Jolteon.ID).makeCopy();
        latios = CardLibrary.getCard(Flareon.ID).makeCopy();

        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0], (i)->{
                    this.imageEventText.loadImage(PokemonRegions.makeEventPath("EonFlight.png"));
                    transitionKey("Agree");
                }).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[2], "g"), (i)->{
                    AbstractDungeon.gridSelectScreen.open(adp().masterDeck, 1, OPTIONS[2], false, false, false, false);
                    transitionKey("Decline");
                }));

        registerPhase("Agree", new TextPhase(DESCRIPTIONS[1]).
                addOption(OPTIONS[3] + FontHelper.colorString(OPTIONS[4], "r") + " " + FontHelper.colorString(OPTIONS[5], "g"), (i)->{
                    AbstractDungeon.getCurrRoom().eliteTrigger = true;
                    this.imageEventText.loadImage(PokemonRegions.makeEventPath("EonCelebrate.png"));
                    transitionKey("Fight");
                }).
                addOption(OPTIONS[8], (i)->{
                    transitionKey("Leave");
                }));

        registerPhase("Fight", new CombatPhase(EncounterIDs.TEAM_ROCKET)
                .addRewards(true, (room)-> {
                    room.addGoldToRewards(GOLD);
                })
                .setNextKey("Victory"));

        TextPhase.OptionInfo latiasOption = createCardPreviewOption(FontHelper.colorString(OPTIONS[6] + latias.name + OPTIONS[7], "g"), latias, (i)->{
            this.noCardsInRewards = true;
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(latias.cardID));
            AbstractDungeon.combatRewardScreen.open();
            transitionKey("LatiasChosen");
        });
        TextPhase.OptionInfo latiosOption = createCardPreviewOption(FontHelper.colorString(OPTIONS[6] + latios.name + OPTIONS[7], "g"), latios, (i)->{
            this.noCardsInRewards = true;
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(latios.cardID));
            AbstractDungeon.combatRewardScreen.open();
            transitionKey("LatiosChosen");
        });
        registerPhase("Victory", new TextPhase(DESCRIPTIONS[3]).
                addOption(latiasOption).
                addOption(latiosOption));

        registerPhase("LatiasChosen", new TextPhase(DESCRIPTIONS[4]).addOption(OPTIONS[8], (t)->this.openMap()));
        registerPhase("LatiosChosen", new TextPhase(DESCRIPTIONS[5]).addOption(OPTIONS[8], (t)->this.openMap()));
        registerPhase("Leave", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[8], (t)->this.openMap()));
        registerPhase("Decline", new TextPhase(DESCRIPTIONS[6]).addOption(OPTIONS[8], (t)->this.openMap()));
        transitionKey(0);
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
                AbstractCard dupe = card.makeStatEquivalentCopy();
                dupe.inBottleFlame = false;
                dupe.inBottleLightning = false;
                dupe.inBottleTornado = false;
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(dupe, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }

    public TextPhase.OptionInfo createCardPreviewOption(String optionText, AbstractCard previewCard, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard).setOptionResult(onClick);
    }
}