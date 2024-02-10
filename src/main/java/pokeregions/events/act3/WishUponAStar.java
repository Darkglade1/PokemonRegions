package pokeregions.events.act3;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Jirachi;
import pokeregions.relics.CunningWish;
import pokeregions.relics.DeathWish;
import pokeregions.relics.GlitteringWish;
import pokeregions.util.PokemonReward;

import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class WishUponAStar extends PhasedEvent {
    public static final String ID = makeID("WishUponAStar");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;
    private static final float MAX_HP_LOSS_PERCENTAGE = 0.06F;
    private static final float MAX_HP_LOSS_PERCENTAGE_HIGH_ASCENSION = 0.08F;
    private final int maxHPLoss;
    private final AbstractRelic deathWish;
    private final AbstractRelic glitteringWish;
    private final AbstractRelic cunningWish;

    public WishUponAStar() {
        super(ID, title, PokemonRegions.makeEventPath("Jirachi.png"));
        this.noCardsInRewards = true;
        if (AbstractDungeon.ascensionLevel < 15) {
            maxHPLoss = (int) ((float) AbstractDungeon.player.maxHealth * MAX_HP_LOSS_PERCENTAGE);
        } else {
            maxHPLoss = (int) ((float) AbstractDungeon.player.maxHealth * MAX_HP_LOSS_PERCENTAGE_HIGH_ASCENSION);
        }
        if (adp().hasRelic(DeathWish.ID)) {
            deathWish = RelicLibrary.getRelic(Circlet.ID).makeCopy();
        } else {
            deathWish = RelicLibrary.getRelic(DeathWish.ID).makeCopy();
        }
        if (adp().hasRelic(GlitteringWish.ID)) {
            glitteringWish = RelicLibrary.getRelic(Circlet.ID).makeCopy();
        } else {
            glitteringWish = RelicLibrary.getRelic(GlitteringWish.ID).makeCopy();
        }
        if (adp().hasRelic(CunningWish.ID)) {
            cunningWish = RelicLibrary.getRelic(Circlet.ID).makeCopy();
        } else {
            cunningWish = RelicLibrary.getRelic(CunningWish.ID).makeCopy();
        }
        AbstractCard jirachi = CardLibrary.getCard(Jirachi.ID).makeCopy();
        TextPhase.OptionInfo cardOption = createCardPreviewOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[4] + maxHPLoss + OPTIONS[5], "r") + " " + FontHelper.colorString(OPTIONS[6] + jirachi.name + OPTIONS[7], "g"), jirachi, (i)->{
            CardCrawlGame.sound.play("BLUNT_FAST");
            adp().decreaseMaxHealth(maxHPLoss);
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(jirachi.cardID));
            AbstractDungeon.combatRewardScreen.open();
            transitionKey("Food");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "g") + " " + FontHelper.colorString(OPTIONS[3], "r"), (i)->{
                    transitionKey("Wish");
                }).
                addOption(cardOption));

        registerPhase("Wish", new TextPhase(DESCRIPTIONS[1]).
                addOption(OPTIONS[9] + FontHelper.colorString(OPTIONS[6] + deathWish.name + OPTIONS[7], "g"), deathWish, (i)->{
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), deathWish);
                    transitionKey("WishChosen");
                }).
                addOption(OPTIONS[10] + FontHelper.colorString(OPTIONS[6] + glitteringWish.name + OPTIONS[7], "g"), glitteringWish, (i)->{
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), glitteringWish);
                    transitionKey("WishChosen");
                }).
                addOption(OPTIONS[11] + FontHelper.colorString(OPTIONS[6] + cunningWish.name + OPTIONS[7], "g"), cunningWish, (i)->{
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), cunningWish);
                    transitionKey("WishChosen");
                }));

        registerPhase("WishChosen", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[8], (t)->this.openMap()));
        registerPhase("Food", new TextPhase(DESCRIPTIONS[3]).addOption(OPTIONS[8], (t)->this.openMap()));
        transitionKey(0);
    }

    public TextPhase.OptionInfo createCardPreviewOption(String optionText, AbstractCard previewCard, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard).setOptionResult(onClick);
    }
}