package pokeregions.util;

import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.AbstractUnremovablePower;
import pokeregions.powers.NextTurnPowerPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pokeregions.actions.TimedVFXAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static pokeregions.PokemonRegions.makeID;

public class Wiz {
    //The wonderful Wizard of Oz allows access to most easy compilations of data, or functions.

    public static AbstractPlayer adp() {
        return AbstractDungeon.player;
    }

    public static void forAllCardsInList(Consumer<AbstractCard> consumer, ArrayList<AbstractCard> cardsList) {
        cardsList.forEach(c -> consumer.accept(c));
    }

    public static ArrayList<AbstractCard> getAllCardsInCardGroups(boolean includeHand, boolean includeExhaust) {
        ArrayList<AbstractCard> masterCardsList = new ArrayList<>();
        masterCardsList.addAll(AbstractDungeon.player.drawPile.group);
        masterCardsList.addAll(AbstractDungeon.player.discardPile.group);
        if (includeHand)
            masterCardsList.addAll(AbstractDungeon.player.hand.group);
        if (includeExhaust)
            masterCardsList.addAll(AbstractDungeon.player.exhaustPile.group);
        return masterCardsList;
    }

    public static void forAllMonstersLiving(Consumer<AbstractMonster> consumer) {
        getEnemies().forEach(mo -> consumer.accept(mo));
    }

    public static void forAllMonstersLivingTop(Consumer<AbstractMonster> consumer) {
        ArrayList<AbstractMonster> enemies = getEnemies();
        Collections.reverse(enemies);
        enemies.forEach(mo -> consumer.accept(mo));
    }

    public static ArrayList<AbstractMonster> getEnemies() {
        ArrayList<AbstractMonster> monsters = new ArrayList<>();
        if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom() == null || AbstractDungeon.getCurrRoom().monsters == null) {
            return monsters;
        }
        monsters = new ArrayList<>(AbstractDungeon.getMonsters().monsters);
        monsters.removeIf(AbstractCreature::isDeadOrEscaped);
        monsters.removeIf(mo -> mo instanceof AbstractPokemonAlly);
        return monsters;
    }

    public static ArrayList<AbstractCard> getCardsMatchingPredicate(Predicate<AbstractCard> pred) {
        return getCardsMatchingPredicate(pred, false);
    }

    public static ArrayList<AbstractCard> getCardsMatchingPredicate(Predicate<AbstractCard> pred, boolean allcards) {
        if (allcards)
            return (ArrayList<AbstractCard>)CardLibrary.getAllCards().stream().filter(pred).collect(Collectors.toList());
        else {
            ArrayList<AbstractCard> cardsList = new ArrayList<>();
            cardsList.addAll(AbstractDungeon.srcCommonCardPool.group);
            cardsList.addAll(AbstractDungeon.srcUncommonCardPool.group);
            cardsList.addAll(AbstractDungeon.srcRareCardPool.group);
            cardsList.removeIf(c -> !pred.test(c));
            return cardsList;
        }
    }

    public static AbstractCard returnTrulyRandomPrediCardInCombat(Predicate<AbstractCard> pred, boolean allCards) {
        return getRandomItem(getCardsMatchingPredicate(pred, allCards));
    }

    public static AbstractCard returnTrulyRandomPrediCardInCombat(Predicate<AbstractCard> pred) {
        return returnTrulyRandomPrediCardInCombat(pred, false);
    }

    public static <T> T getRandomItem(ArrayList<T> list, Random rng) {
        return list.isEmpty() ? null : list.get(rng.random(list.size() - 1));
    }

    public static <T> T getRandomItem(ArrayList<T> list) {
        return getRandomItem(list, AbstractDungeon.cardRandomRng);
    }

    public static boolean actuallyHovered(Hitbox hb) {
        return InputHelper.mX > hb.x && InputHelper.mX < hb.x + hb.width && InputHelper.mY > hb.y && InputHelper.mY < hb.y + hb.height;
    }

    public static boolean isInCombat() {
        return CardCrawlGame.isInARun() && AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }

    public static void atb(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    public static void att(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToTop(action);
    }

    public static void vfx(AbstractGameEffect gameEffect) {
        atb(new VFXAction(gameEffect));
    }

    public static void vfx(AbstractGameEffect gameEffect, float duration) {
        atb(new VFXAction(gameEffect, duration));
    }

    public static void tfx(AbstractGameEffect gameEffect) {
        atb(new TimedVFXAction(gameEffect));
    }

    public static void makeInHand(AbstractCard c, int i) {
        atb(new MakeTempCardInHandAction(c, i));
    }

    public static void makeInHand(AbstractCard c) {
        makeInHand(c, 1);
    }

    public static void shuffleIn(AbstractCard c, int i) {
        atb(new MakeTempCardInDrawPileAction(c, i, true, true));
    }

    public static void shuffleIn(AbstractCard c) {
        shuffleIn(c, 1);
    }

    public static void topDeck(AbstractCard c, int i) {
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(c, i, false, true));
    }

    public static void topDeck(AbstractCard c) {
        topDeck(c, 1);
    }

    public static void applyToEnemy(AbstractMonster m, AbstractPower po) {
        atb(new ApplyPowerAction(m, AbstractDungeon.player, po, po.amount));
    }

    public static void applyToEnemyTop(AbstractMonster m, AbstractPower po) {
        att(new ApplyPowerAction(m, AbstractDungeon.player, po, po.amount));
    }

    public static void applyToSelf(AbstractPower po) {
        atb(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, po, po.amount));
    }

    public static void applyToSelfTop(AbstractPower po) {
        att(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, po, po.amount));
    }

    public static void thornDmg(AbstractCreature m, int amount, AbstractGameAction.AttackEffect AtkFX) {
        atb(new DamageAction(m, new DamageInfo(AbstractDungeon.player, amount, DamageInfo.DamageType.THORNS), AtkFX));
    }

    public static void thornDmg(AbstractCreature m, int amount) {
        thornDmg(m, amount, AbstractGameAction.AttackEffect.NONE);
    }

    public static void discard(int amount, boolean isRandom) {
        atb(new DiscardAction(adp(), adp(), amount, isRandom));
    }

    public static void discard(int amount) {
        discard(amount, false);
    }

    public static int pwrAmt(AbstractCreature check, String ID) {
        AbstractPower found = check.getPower(ID);
        if (found != null)
            return found.amount;
        return 0;
    }

    public static AbstractGameAction actionify(Runnable todo) {
        return new AbstractGameAction() {
            public void update() {
                isDone = true;
                todo.run();
            }
        };
    }

    public static void actB(Runnable todo) {
        atb(actionify(todo));
    }

    public static void actT(Runnable todo) {
        att(actionify(todo));
    }

    public static AbstractGameAction multiAction(AbstractGameAction... actions) {
        return actionify(() -> {
            ArrayList<AbstractGameAction> actionsList = (ArrayList<AbstractGameAction>)Arrays.asList(actions);
            Collections.reverse(actionsList);
            for (AbstractGameAction action : actions)
                att(action);
        });
    }

    public static void playAudio(ProAudio a) {
        CardCrawlGame.sound.play(makeID(a.name()));
    }

    public static void playAudio(ProAudio a, float volume) {
        CardCrawlGame.sound.playV(makeID(a.name()), volume);
    }

    public static void intoDrawMo(AbstractCard c, int i) {
        atb(new MakeTempCardInDrawPileAction(c, i, true, true));
    }

    public static void intoDiscardMo(AbstractCard c, int i) {
        //because for some reason the action is HARDCODED to only take up to FIVE
        if (i > 5) {
            int times = i / 5;
            int remainder = i % 5;
            for (int count = 0; count < times; count++) {
                atb(new MakeTempCardInDiscardAction(c, 5));
            }
            atb(new MakeTempCardInDiscardAction(c, remainder));
        } else {
            atb(new MakeTempCardInDiscardAction(c, i));
        }
    }

    public static void applyToTarget(AbstractCreature target, AbstractCreature source, AbstractPower po) {
        atb(new ApplyPowerAction(target, source, po, po.amount));
    }
    public static void applyToTargetTop(AbstractCreature target, AbstractCreature source, AbstractPower po) {
        att(new ApplyPowerAction(target, source, po, po.amount));
    }

    public static void applyToTargetNextTurn(AbstractCreature target, AbstractCreature source, AbstractPower po) {
        atb(new ApplyPowerAction(target, source, new NextTurnPowerPower(target, po)));
    }

    public static void applyToTargetNextTurnTop(AbstractCreature target, AbstractCreature source, AbstractPower po) {
        att(new ApplyPowerAction(target, source, new NextTurnPowerPower(target, po)));
    }

    public static void dmg(AbstractCreature target, DamageInfo info, AbstractGameAction.AttackEffect effect) {
        atb(new DamageAction(target, info, effect));
    }

    public static void dmg(AbstractCreature target, DamageInfo info) {
        dmg(target, info, AbstractGameAction.AttackEffect.NONE);
    }

    public static void block(AbstractCreature target, int amount) {
        atb(new GainBlockAction(target, amount));
    }

    public static void makePowerRemovable(AbstractCreature owner, String powerID) {
        AbstractPower power = owner.getPower(powerID);
        if (power instanceof AbstractUnremovablePower) {
            ((AbstractUnremovablePower) power).isUnremovable = false;
        }
    }

    public static void makePowerRemovable(AbstractPower power) {
        if (power instanceof AbstractUnremovablePower) {
            ((AbstractUnremovablePower) power).isUnremovable = false;
        }
    }
}
