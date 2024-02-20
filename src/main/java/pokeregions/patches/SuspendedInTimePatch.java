package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import pokeregions.powers.SuspendedInTime;

import java.util.ArrayList;

public class SuspendedInTimePatch {
    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class DelayUseCard {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("use")) {
                        m.replace("{" +
                                "if(!(" + SuspendedInTimePatch.class.getName() + ".DelayCardPlay(c, monster))) {" +
                                "$proceed($$);" +
                                "}" +
                                "}");
                    }
                }
            };
        }
    }

    public static boolean DelayCardPlay(AbstractCard c, AbstractMonster m) {
        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().monsters != null) {
            for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (monster.hasPower(SuspendedInTime.POWER_ID)) {
                    SuspendedInTime power = (SuspendedInTime)monster.getPower(SuspendedInTime.POWER_ID);
                    if (!ContainsCard(power.playingCards, c)) {
                        if (power.amount == 1) {
                            power.amount = 0;
                            c.dontTriggerOnUseCard = true;
                            power.increment(c, m);
                            return true;
                        } else {
                            power.amount = 1;
                            return false;
                        }
                    } else {
                        SuspendedInTime.CardInfo info = GetCard(power.playingCards, c);
                        if (info != null && c.cost == -1) {
                            c.energyOnUse = info.energyOnUse; //Make X costs work properly
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public static boolean ContainsCard(ArrayList<SuspendedInTime.CardInfo> arr, AbstractCard card) {
        for (SuspendedInTime.CardInfo info : arr) {
            if (info.card == card) {
                return true;
            }
        }
        return false;
    }

    public static SuspendedInTime.CardInfo GetCard(ArrayList<SuspendedInTime.CardInfo> arr, AbstractCard card) {
        for (SuspendedInTime.CardInfo info : arr) {
            if (info.card == card) {
                return info;
            }
        }
        return null;
    }


    @SpirePatch(
            clz = GameActionManager.class,
            method = "getNextAction"
    )
    public static class TriggerAtStartOfPlayerTurn {
        @SpireInsertPatch(locator = TriggerAtStartOfPlayerTurn.Locator.class)
        public static void Trigger(GameActionManager instance) {
            if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().monsters != null) {
                for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (monster.hasPower(SuspendedInTime.POWER_ID)) {
                        SuspendedInTime power = (SuspendedInTime)monster.getPower(SuspendedInTime.POWER_ID);
                        power.playCards();
                    }
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "gameHandSize");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}