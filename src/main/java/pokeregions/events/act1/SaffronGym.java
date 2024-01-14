package pokeregions.events.act1;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import pokeregions.PokemonRegions;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class SaffronGym extends PhasedEvent {
    public static final String ID = makeID("SaffronGym");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;
    private boolean forTransform = false;
    private boolean forDuplicate = false;

    public SaffronGym() {
        super(ID, title, PokemonRegions.makeEventPath("SaffronGym.png"));
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "g"), (i)->{
                    forTransform = true;
                    AbstractDungeon.gridSelectScreen.open(adp().masterDeck.getPurgeableCards(), 1, OPTIONS[2], false, true, false, false);
                    transitionKey("Sabrina");
                }).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[3], "g"), (i)->{
                    forDuplicate = true;
                    AbstractDungeon.gridSelectScreen.open(adp().masterDeck, 1, OPTIONS[3], false, false, false, false);
                    transitionKey("Challenger");
                }));

        registerPhase("Sabrina", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[4], (t)->this.openMap()));
        registerPhase("Challenger", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[4], (t)->this.openMap()));
        transitionKey(0);
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
                if (forTransform) {
                    card.untip();
                    card.unhover();
                    AbstractDungeon.player.masterDeck.removeCard(card);
                    AbstractDungeon.transformCard(card, false, AbstractDungeon.miscRng);
                    AbstractCard c = AbstractDungeon.getTransformedCard();
                    if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.TRANSFORM && c != null) {
                        AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(c.makeStatEquivalentCopy(), (float)Settings.WIDTH / 3.0F, (float)Settings.HEIGHT / 2.0F, false));
                    }
                } else if (forDuplicate) {
                    AbstractCard dupe = card.makeStatEquivalentCopy();
                    dupe.inBottleFlame = false;
                    dupe.inBottleLightning = false;
                    dupe.inBottleTornado = false;
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(dupe, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                }
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }
}