package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;
import pokeregions.powers.AbstractEasyPower;
import pokeregions.util.Wiz;

@SpirePatch(
        clz = CardGroup.class,
        method = "moveToExhaustPile"
)
// A patch to make onExhaust hook trigger on monster powers
public class MonsterOnExhaustPatch {
    @SpireInsertPatch(locator = MonsterOnExhaustPatch.Locator.class, localvars = {"c"})
    public static void TriggerMonsterPower(CardGroup instance, AbstractCard c) {
        for (AbstractMonster mo : Wiz.getEnemies()) {
            for (AbstractPower p : mo.powers) {
                //hardcode this so we don't accidentally break other mods
                if (p instanceof AbstractEasyPower) {
                    p.onExhaust(c);
                }
            }
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "triggerOnExhaust");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}