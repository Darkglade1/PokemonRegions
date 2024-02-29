package pokeregions.events.act3;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.relics.QuickClaw;
import pokeregions.relics.RainbowBadge;
import pokeregions.util.Tags;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;

public class PokemonTrainerSchool extends PhasedEvent {
    public static final String ID = makeID("PokemonTrainerSchool");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private static final int CARDS = 25;
    private static final int STAMINA_LOSS = 4;
    private boolean pickCard = false;
    private boolean pickPokemon = false;
    private final AbstractRelic relic;

    public PokemonTrainerSchool() {
        super(ID, title, PokemonRegions.makeEventPath("PokemonTrainerSchool.png"));
        if (adp().hasRelic(QuickClaw.ID)) {
            relic = RelicLibrary.getRelic(Circlet.ID).makeCopy();
        } else {
            relic = RelicLibrary.getRelic(QuickClaw.ID).makeCopy();
        }
        TextPhase.OptionInfo mockBattle = new TextPhase.OptionInfo(hasPokemonWithEnoughStamina() ? (OPTIONS[1] + FontHelper.colorString(OPTIONS[4] + STAMINA_LOSS + OPTIONS[5], "r") + " " + FontHelper.colorString(OPTIONS[6], "g")) : OPTIONS[8], relic).enabledCondition(this::hasPokemonWithEnoughStamina).setOptionResult((i)->{
            this.pickPokemon = true;
            CardGroup validPokemon = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
                if (card instanceof AbstractAllyPokemonCard) {
                    if (!card.hasTag(Tags.STARTER_POKEMON) && ((AbstractAllyPokemonCard) card).currentStamina >= STAMINA_LOSS) {
                        validPokemon.addToTop(card);
                    }
                }
            }
            AbstractDungeon.gridSelectScreen.open(validPokemon, 1, OPTIONS[10], false, false, false, false);
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), relic);
            transitionKey("Battle");
        });
        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
                addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + CARDS + OPTIONS[3], "g"), (i)->{
                    this.pickCard = true;
                    CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    for (int count = 0; count < CARDS; count++) {
                        AbstractCard card = AbstractDungeon.getCard(AbstractDungeon.rollRarity()).makeCopy();
                        boolean containsDupe = true;
                        while (containsDupe) {
                            containsDupe = false;
                            for (AbstractCard c : group.group) {
                                if (c.cardID.equals(card.cardID)) {
                                    containsDupe = true;
                                    card = AbstractDungeon.getCard(AbstractDungeon.rollRarity()).makeCopy();
                                }
                            }
                        }
                        if (!group.contains(card)) {
                            for (AbstractRelic r : AbstractDungeon.player.relics) {
                                r.onPreviewObtainCard(card);
                            }
                            group.addToBottom(card);
                        } else {
                            count--;
                        }
                    }
                    for (AbstractCard c : group.group) {
                        UnlockTracker.markCardAsSeen(c.cardID);
                    }
                    AbstractDungeon.gridSelectScreen.open(group, 1, OPTIONS[9], false);
                    transitionKey("Lesson");
                }).
                addOption(mockBattle));

        registerPhase("Lesson", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[7], (t)->this.openMap()));
        registerPhase("Battle", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[7], (t)->this.openMap()));
        transitionKey(0);
    }

    public void update() {
        super.update();
        if (this.pickCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = (AbstractDungeon.gridSelectScreen.selectedCards.get(0)).makeStatEquivalentCopy();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
        if (this.pickPokemon && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                if (c instanceof AbstractAllyPokemonCard) {
                    ((AbstractAllyPokemonCard) c).updateStamina(((AbstractAllyPokemonCard) c).currentStamina - STAMINA_LOSS);
                }
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
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
}