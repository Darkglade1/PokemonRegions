package pokeregions.events.act3;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Clumsy;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Shovel;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import pokeregions.PokemonRegions;
import pokeregions.relics.EnhancedShovel;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class LavaridgeGym extends PhasedEvent {
    public static final String ID = makeID("LavaridgeGym");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private final AbstractCard curse;
    private final AbstractRelic relic;
    private boolean alreadyHasShovel;

    public LavaridgeGym() {
        super(ID, title, PokemonRegions.makeEventPath("LavaridgeGym.png"));
        relic = RelicLibrary.getRelic(EnhancedShovel.ID).makeCopy();
        curse = CardLibrary.getCard(Clumsy.ID).makeCopy();
        if (adp().hasRelic(EnhancedShovel.ID) || adp().hasRelic(Shovel.ID)) {
            alreadyHasShovel = true;
        }
        if (alreadyHasShovel) {
            registerPhase(0, new TextPhase(DESCRIPTIONS[3]).
                    addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[6], "g"), (i)->{
                        AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                        transitionKey("HadShovel");
                    }).
                    addOption(OPTIONS[1], (i)->{
                        transitionKey("Decline");
                    }));
        } else {
            registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                    addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "g") + " " + FontHelper.colorString(OPTIONS[3] + curse.name + OPTIONS[4], "r"), relic, (i)->{
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), relic);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        AbstractDungeon.rareRelicPool.removeIf(relic -> relic.equals(Shovel.ID));
                        transitionKey("GetShovel");
                    }).
                    addOption(OPTIONS[1], (i)->{
                        transitionKey("Decline");
                    }));
        }


        registerPhase("GetShovel", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[5], (t)->this.openMap()));
        registerPhase("Decline", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[5], (t)->this.openMap()));
        registerPhase("HadShovel", new TextPhase(DESCRIPTIONS[4]).addOption(OPTIONS[5], (t)->this.openMap()));
        transitionKey(0);
    }
}