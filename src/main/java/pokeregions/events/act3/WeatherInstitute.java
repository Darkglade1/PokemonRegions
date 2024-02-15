package pokeregions.events.act3;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import basemod.cardmods.RetainMod;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.purple.Wish;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import pokeregions.PokemonRegions;
import pokeregions.cards.cardMods.BlockUpMod;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class WeatherInstitute extends PhasedEvent {
    public static final String ID = makeID("WeatherInstitute");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private static final int BLOCK_BONUS = 2;
    private static final float HEALTH_LOSS_PERCENTAGE = 0.20F;
    private static final float HEALTH_LOSS_PERCENTAGE_HIGH_ASCENSION = 0.25F;
    private final int healthdamage;
    private final AbstractRelic relic1;
    private final AbstractRelic relic2;
    private final AbstractRelic relic3;

    public WeatherInstitute() {
        super(ID, title, PokemonRegions.makeEventPath("WeatherInstitute.png"));
        if (AbstractDungeon.ascensionLevel < 15) {
            healthdamage = (int) ((float) AbstractDungeon.player.maxHealth * HEALTH_LOSS_PERCENTAGE);
        } else {
            healthdamage = (int) ((float) AbstractDungeon.player.maxHealth * HEALTH_LOSS_PERCENTAGE_HIGH_ASCENSION);
        }
        relic1 = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
        relic2 = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
        relic3 = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + BLOCK_BONUS + OPTIONS[3], "g"), (i)->{
                    CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    for (AbstractCard card : adp().masterDeck.group) {
                        if (card.baseBlock > 0 && !card.cardID.equals(Wish.ID)) {
                            group.addToBottom(card);
                        }
                    }
                    group.sortAlphabetically(true);
                    group.sortByRarityPlusStatusCardType(false);
                    if (group.size() > 0) {
                        AbstractDungeon.gridSelectScreen.open(group, 1, OPTIONS[2] + BLOCK_BONUS + OPTIONS[3], false);
                    }
                    transitionKey("Wait");
                }).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[4] + healthdamage + OPTIONS[5], "r") + " " + FontHelper.colorString(OPTIONS[6], "g"), (i)->{
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    AbstractDungeon.player.damage(new DamageInfo(null, healthdamage));
                    transitionKey("Rush");
                }));
        registerPhase("Rush", new TextPhase(DESCRIPTIONS[2]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[8] + relic1.name + OPTIONS[9], "g"), relic1, (i)->{
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic1);
                    transitionKey("RushFinish");
                }).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[8] + relic2.name + OPTIONS[9], "g"), relic2, (i)->{
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic2);
                    transitionKey("RushFinish");
                }).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[8] + relic3.name + OPTIONS[9], "g"), relic3, (i)->{
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic3);
                    transitionKey("RushFinish");
                }));

        registerPhase("Wait", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[7], (t)->this.openMap()));
        registerPhase("RushFinish", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[7], (t)->this.openMap()));
        transitionKey(0);
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                CardModifierManager.addModifier(c, new RetainMod());
                CardModifierManager.addModifier(c, new BlockUpMod(BLOCK_BONUS));
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }
}