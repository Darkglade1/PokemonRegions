package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;
import pokeregions.monsters.act2.enemies.PiloswineEnemy;

@SpirePatch(
        clz = CardGroup.class,
        method = "moveToExhaustPile",
        paramtypez={
                AbstractCard.class,
        }

)
// A patch to make enemy exhaust hook work
public class EnemyPowerOnExhaustPatch {
    @SpireInsertPatch(locator = Locator.class)
    public static void TriggerOnGainedBlock(CardGroup instance, AbstractCard c) {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            for (AbstractPower p : mo.powers) {
                if (p.ID.equals(PiloswineEnemy.POWER_ID)) {
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