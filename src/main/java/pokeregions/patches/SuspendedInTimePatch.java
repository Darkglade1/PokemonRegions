package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.VelvetChoker;
import javassist.CannotCompileException;
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
                            power.addCard(c, m);
                            if (AbstractDungeon.player.hasRelic(VelvetChoker.ID)) {
                                // Hack to make suspended cards not count towards velvet choker
                                AbstractDungeon.player.getRelic(VelvetChoker.ID).counter--;
                            }
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
}