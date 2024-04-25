package pokeregions.events.act2;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Injury;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import pokeregions.PokemonRegions;
import pokeregions.relics.DragonFang;

import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class BlackthornGym extends PhasedEvent {
    public static final String ID = makeID("BlackthornGym");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private final AbstractRelic relic;
    private static final int GOLD = 90;

    public BlackthornGym() {
        super(ID, title, PokemonRegions.makeEventPath("BlackthornGym.png"));
        if (adp().hasRelic(DragonFang.ID)) {
            relic = RelicLibrary.getRelic(Circlet.ID).makeCopy();
        } else {
            relic = RelicLibrary.getRelic(DragonFang.ID).makeCopy();
        }
        AbstractCard curse = CardLibrary.getCard(Injury.ID).makeCopy();

        TextPhase.OptionInfo relicOption = createPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "g") + " " + FontHelper.colorString(OPTIONS[3] + curse.name + OPTIONS[4], "r"), curse, relic, (i)->{
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
            transitionKey("Keep");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(relicOption).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[5] + GOLD + OPTIONS[6], "g"), (i)->{
                    AbstractDungeon.effectList.add(new RainingGoldEffect(GOLD));
                    adp().gainGold(GOLD);
                    transitionKey("Return");
                }));

        registerPhase("Keep", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[7], (t)->this.openMap()));
        registerPhase("Return", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[7], (t)->this.openMap()));
        transitionKey(0);
    }

    public TextPhase.OptionInfo createPreviewOption(String optionText, AbstractCard previewCard, AbstractRelic previewRelic, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard, previewRelic).setOptionResult(onClick);
    }

}