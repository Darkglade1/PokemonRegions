package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;
import pokeregions.powers.Sapphire;
import pokeregions.util.Wiz;

@SpirePatch(
        clz = GameActionManager.class,
        method = "callEndOfTurnActions"
)
// A patch to make Sapphire of the Drowned work
public class SapphirePowerPatch {
    @SpireInsertPatch(locator = SapphirePowerPatch.Locator.class)
    public static void TriggerMonsterPower(GameActionManager instance) {
        for (AbstractMonster mo : Wiz.getEnemies()) {
            for (AbstractPower p : mo.powers) {
                //hardcode this so we don't accidentally break other mods
                if (p instanceof Sapphire) {
                    p.onSpecificTrigger();
                }
            }
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "hand");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}