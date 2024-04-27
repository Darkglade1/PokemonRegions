package pokeregions.events.act2;

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
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Phanpy;
import pokeregions.relics.PokemonEgg;

import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class MarineLab extends PhasedEvent {
    public static final String ID = makeID("MarineLab");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private final AbstractRelic relic;
    private static final int TRANSFORM_NUM = 2;

    public MarineLab() {
        super(ID, title, PokemonRegions.makeEventPath("MarineLab.png"));
        if (adp().hasRelic(PokemonEgg.ID)) {
            relic = RelicLibrary.getRelic(Circlet.ID).makeCopy();
        } else {
            relic = RelicLibrary.getRelic(PokemonEgg.ID).makeCopy();
        }
        AbstractCard card = CardLibrary.getCard(Phanpy.ID).makeCopy();

        TextPhase.OptionInfo relicOption = createPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "g"), card, relic, (i)->{
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
            transitionKey("Egg");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(relicOption).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[3] + TRANSFORM_NUM + OPTIONS[4], "g"), (i)->{
                    AbstractDungeon.gridSelectScreen.open(adp().masterDeck.getPurgeableCards(), TRANSFORM_NUM, OPTIONS[3] + TRANSFORM_NUM + OPTIONS[4], false, false, false, false);
                    transitionKey("Research");
                }));

        registerPhase("Egg", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[5], (t)->this.openMap()));
        registerPhase("Research", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[5], (t)->this.openMap()));
        transitionKey(0);
    }

    public TextPhase.OptionInfo createPreviewOption(String optionText, AbstractCard previewCard, AbstractRelic previewRelic, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard, previewRelic).setOptionResult(onClick);
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
                card.untip();
                card.unhover();
                AbstractDungeon.player.masterDeck.removeCard(card);
                AbstractDungeon.transformCard(card, false, AbstractDungeon.miscRng);
                AbstractCard c = AbstractDungeon.getTransformedCard();
                if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.TRANSFORM && c != null) {
                    AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(c.makeStatEquivalentCopy(), (float)Settings.WIDTH / 3.0F, (float)Settings.HEIGHT / 2.0F, false));
                }
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }

}