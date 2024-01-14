package pokeregions.patches;

import pokeregions.monsters.AbstractPokemonAlly;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import javassist.CtBehavior;

@SpirePatch(
        clz = AbstractCreature.class,
        method = "updateFastAttackAnimation"
)
// A patch to make ally pokemon move in the correct direction during fastAttackAnimation
public class AllyPokemonFastAttackAnimationPatch {
    @SpireInsertPatch(locator = AllyPokemonFastAttackAnimationPatch.Locator.class, localvars = {"targetPos"})
    public static void FixAnimationDirection(AbstractCreature instance, @ByRef float[] targetPos) {
        if (instance instanceof AbstractPokemonAlly) {
            float pos = targetPos[0];
            float flippedPos = -pos;
            targetPos[0] = flippedPos;
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCreature.class, "isPlayer");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}