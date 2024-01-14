package pokeregions.events.act1;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Regret;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.relics.ThunderBadge;
import pokeregions.util.Tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.Consumer;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class VermilionGym extends PhasedEvent {
    public static final String ID = makeID("VermilionGym");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private static final int UPGRADE_AMT = 3;
    private static final int STAMINA_LOSS = 3;
    private static final float HEALTH_LOSS_PERCENTAGE = 0.20F;
    private static final float HEALTH_LOSS_PERCENTAGE_HIGH_ASCENSION = 0.25F;
    private final int hpLoss;
    private final AbstractRelic relic;
    private final AbstractCard curse;
    private final AbstractPotion potion;

    public VermilionGym() {
        super(ID, title, PokemonRegions.makeEventPath("VermilionGym.png"));
        if (AbstractDungeon.ascensionLevel < 15) {
            hpLoss = (int) ((float) AbstractDungeon.player.maxHealth * HEALTH_LOSS_PERCENTAGE);
        } else {
            hpLoss = (int) ((float) AbstractDungeon.player.maxHealth * HEALTH_LOSS_PERCENTAGE_HIGH_ASCENSION);
        }
        if (adp().hasRelic(ThunderBadge.ID)) {
            relic = RelicLibrary.getRelic(Circlet.ID).makeCopy();
        } else {
            relic = RelicLibrary.getRelic(ThunderBadge.ID).makeCopy();
        }
        curse = CardLibrary.getCard(Regret.ID);
        potion = adp().getRandomPotion();

        TextPhase.OptionInfo agreeOption = createCardPreviewOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + UPGRADE_AMT + OPTIONS[3], "g") + " " + FontHelper.colorString(OPTIONS[4] + curse.name + OPTIONS[5], "r"), curse, (i)->{
            upgradeCards();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
            transitionKey("Agree");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(agreeOption).
                addOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[6], "g") + " " + FontHelper.colorString(OPTIONS[7], "r"), relic, (i)->{
                    transitionKey("Escape");
                }));
        registerPhase("Agree", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[8], (t)->this.openMap()));
        registerPhase("Escape", new TextPhase(DESCRIPTIONS[2]).
                addOption(new TextPhase.OptionInfo(hasPotion() ? (OPTIONS[9] + FontHelper.colorString(OPTIONS[10] + potion.name + OPTIONS[5], "r")) : OPTIONS[16]).enabledCondition(this::hasPotion), (i)->{
                    adp().removePotion(potion);
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), relic);
                    transitionKey("PotionEscape");
                }).
                addOption(new TextPhase.OptionInfo(hasPokemonWithEnoughStamina() ? (OPTIONS[11] + FontHelper.colorString(OPTIONS[12] + STAMINA_LOSS + OPTIONS[13], "r")) : OPTIONS[17]).enabledCondition(this::hasPokemonWithEnoughStamina), (i)->{
                    CardGroup validPokemon = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
                        if (card instanceof AbstractAllyPokemonCard) {
                            if (!card.hasTag(Tags.STARTER_POKEMON) && ((AbstractAllyPokemonCard) card).currentStamina >= STAMINA_LOSS) {
                                validPokemon.addToTop(card);
                            }
                        }
                    }
                    AbstractDungeon.gridSelectScreen.open(validPokemon, 1, OPTIONS[12] + STAMINA_LOSS + OPTIONS[13], false, false, false, false);
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), relic);
                    transitionKey("PokemonEscape");
                }).
                addOption(OPTIONS[14] + FontHelper.colorString(OPTIONS[10] + hpLoss + OPTIONS[15], "r"), (i)->{
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    adp().damage(new DamageInfo(null, hpLoss));
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), relic);
                    transitionKey("SelfEscape");
                }));
        registerPhase("PotionEscape", new TextPhase(DESCRIPTIONS[3] + DESCRIPTIONS[6]).addOption(OPTIONS[8], (t)->this.openMap()));
        registerPhase("PokemonEscape", new TextPhase(DESCRIPTIONS[4] + DESCRIPTIONS[6]).addOption(OPTIONS[8], (t)->this.openMap()));
        registerPhase("SelfEscape", new TextPhase(DESCRIPTIONS[5] + DESCRIPTIONS[6]).addOption(OPTIONS[8], (t)->this.openMap()));
        transitionKey(0);
    }

    public TextPhase.OptionInfo createCardPreviewOption(String optionText, AbstractCard previewCard, Consumer<Integer> onClick) {
        return new TextPhase.OptionInfo(optionText, previewCard).setOptionResult(onClick);
    }

    private boolean hasPotion() {
        for (AbstractPotion potion : adp().potions) {
            if (!(potion instanceof PotionSlot)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasPokemonWithEnoughStamina() {
        for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
            if (card instanceof AbstractAllyPokemonCard) {
                if (!card.hasTag(Tags.STARTER_POKEMON) && ((AbstractAllyPokemonCard) card).currentStamina >= STAMINA_LOSS) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                if (c instanceof AbstractAllyPokemonCard) {
                    ((AbstractAllyPokemonCard) c).updateStamina(((AbstractAllyPokemonCard) c).currentStamina - STAMINA_LOSS);
                }
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }

    private void upgradeCards() {
        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
        ArrayList<AbstractCard> upgradableCards = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.canUpgrade()) {
                upgradableCards.add(c);
            }
        }
        Collections.shuffle(upgradableCards, new Random(AbstractDungeon.miscRng.randomLong()));
        if (!upgradableCards.isEmpty()) {
            for (int i = 0; i < UPGRADE_AMT; i++) {
                if (i < upgradableCards.size()) {
                    upgradableCards.get(i).upgrade();
                    AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(i));
                    AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(upgradableCards.get(i).makeStatEquivalentCopy()));
                }
            }
        }
    }
}