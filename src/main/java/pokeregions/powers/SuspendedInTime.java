package pokeregions.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.BobEffect;
import pokeregions.PokemonRegions;
import pokeregions.patches.SuspendedInTimePatch;
import pokeregions.util.Wiz;

import java.util.ArrayList;

public class SuspendedInTime extends AbstractUnremovablePower {

    public static final String POWER_ID = PokemonRegions.makeID(SuspendedInTime.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private ArrayList<CardInfo> cardsInfo = new ArrayList<>();
    public ArrayList<CardInfo> playingCards = new ArrayList<>(); //The cards to not delay

    public static final float Y_OFFSET = 70f * Settings.scale;
    public static final float X_OFFSET = 100f * Settings.scale;
    public final CardGroup cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    public final CardGroup renderQueue = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    private static final BobEffect bob = new BobEffect(3.0f * Settings.scale, 3.0f);
    public static AbstractCard hovered;

    public SuspendedInTime(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        this.loadRegion("time");
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void renderIcons(SpriteBatch sb, float x, float y, Color c) {
        super.renderIcons(sb, x, y, c);
        for (AbstractCard card : cards.group) {
            if (card != hovered) {
                card.render(sb);
            }
        }
        if (hovered != null) {
            hovered.render(sb);
            TipHelper.renderTipForCard(hovered, sb, hovered.keywords);
        }
        renderQueue.render(sb);
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        bob.update();
        int i = 0;
        hovered = null;
        for (AbstractCard card : cards.group) {
            card.target_y = Wiz.adp().hb.cY + Wiz.adp().hb.height/2f + Y_OFFSET + bob.y;
            card.target_x = Wiz.adp().hb.cX + X_OFFSET * (cards.size()-1) / 2f - X_OFFSET * i;
            card.targetAngle = 0f;
            card.update();
            card.hb.update();
            if (card.hb.hovered && hovered == null) {
                card.targetDrawScale = 0.75f;
                hovered = card;
            } else {
                card.targetDrawScale = 0.2f;
            }
            card.applyPowers();
            i++;
        }
        renderQueue.update();
    }

    public void playCards() {
        playingCards.addAll(cardsInfo);
        cardsInfo.clear();
        for (AbstractCard card : cards.group) {
            card.targetDrawScale = 0.75F;
            card.applyPowers();
        }
        renderQueue.group.addAll(cards.group);
        cards.clear();
        playNextCard();
    }

    public void playNextCard() {
        if (!renderQueue.isEmpty()) {
            AbstractCard card = renderQueue.group.get(0);
            card.dontTriggerOnUseCard = false;
            SuspendedInTime.CardInfo info = SuspendedInTimePatch.GetCard(playingCards, card);
            if (info != null && info.target != null) {
                Wiz.atb(new NewQueueCardAction(card, info.target, false, true));
            } else {
                Wiz.atb(new NewQueueCardAction(card, true, false, true));
            }
        }
    }

    public void addCard(AbstractCard card, AbstractMonster monster) {
        this.flash();
        int energy = card.energyOnUse;
        if (card.cost == -1) {
            energy = EnergyPanel.totalCount;
            AbstractDungeon.player.energy.use(EnergyPanel.totalCount);
        }
        CardInfo cardInfo = new CardInfo(card, monster, energy);
        cardsInfo.add(cardInfo);
        playingCards.clear();

        SuspendedCardFields.suspendedField.set(card, true);
        card.targetAngle = 0f;
        card.beginGlowing();
        cards.addToTop(card);
        CardCrawlGame.sound.play("ORB_SLOT_GAIN", 0.1F);
    }

    public static class CardInfo {
        public AbstractCard card;
        public AbstractMonster target;
        public int energyOnUse;

        public CardInfo(AbstractCard card, AbstractMonster target, int energyOnUse) {
            this.card = card;
            this.target = target;
            this.energyOnUse = energyOnUse;
        }

        @Override
        public String toString() {
            return card.toString() + energyOnUse;
        }
    }

    public static SuspendedInTime getSuspendPower() {
        SuspendedInTime power = null;
        for (AbstractMonster mo : Wiz.getEnemies()) {
            power = (SuspendedInTime) mo.getPower(SuspendedInTime.POWER_ID);
            if (power != null) {
                break;
            }
        }
        return power;
    }

    @SpirePatch2(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class SuspendedCardFields {
        public static SpireField<Boolean> suspendedField = new SpireField<>(() -> false);
    }

    @SpirePatch2(clz = UseCardAction.class, method = SpirePatch.CLASS)
    public static class SuspendedActionField {
        public static SpireField<Boolean> suspendedField = new SpireField<>(() -> false);
    }

    @SpirePatch2(clz = UseCardAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, AbstractCreature.class})
    public static class InheritSuspendedField {
        @SpirePrefixPatch
        public static void pushSuspend(UseCardAction __instance, AbstractCard card) {
            if (SuspendedCardFields.suspendedField.get(card)) {
                SuspendedActionField.suspendedField.set(__instance, true);
                SuspendedCardFields.suspendedField.set(card, false);
            }
        }
    }

    @SpirePatch2(clz = UseCardAction.class, method = "update")
    public static class DontDoAnythingIfSuspended {
        @SpirePrefixPatch
        public static SpireReturn<Void> pushSuspend(UseCardAction __instance) {
            if (SuspendedActionField.suspendedField.get(__instance)) {
                __instance.isDone = true;
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = UseCardAction.class, method = "update")
    public static class DoNextCard {
        @SpirePostfixPatch
        public static void doNextSuspendedCard(AbstractCard ___targetCard) {
            SuspendedInTime power = getSuspendPower();
            if (power != null) {
                power.renderQueue.removeCard(___targetCard);
                power.playNextCard();
            }
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfTurnCards")
    public static class PlayCards {
        @SpirePrefixPatch
        public static void playCards() {
            SuspendedInTime power = getSuspendPower();
            if (power != null) {
                power.playCards();
            }
        }
    }
}