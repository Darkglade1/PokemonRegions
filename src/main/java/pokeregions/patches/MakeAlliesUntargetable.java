package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import javassist.CtBehavior;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.enemies.VictreebelEnemy;
import pokeregions.util.Wiz;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "updateSingleTargetInput"

)
// A patch to make allies untargetable by the player
public class MakeAlliesUntargetable {
    @SpireInsertPatch(locator = Locator.class, localvars = {"hoveredMonster"})
    public static void MakeHoveredMonsterNull(AbstractPlayer instance, @ByRef AbstractMonster[] hoveredMonster) {
        if (hoveredMonster[0] instanceof AbstractPokemonAlly) {
            hoveredMonster[0] = null;
        }
        AbstractMonster tauntingMonster = null;
        for (AbstractMonster mo : Wiz.getEnemies()) {
            if (mo.hasPower(VictreebelEnemy.POWER_ID)) {
                tauntingMonster = mo;
                break;
            }
        }
        if (tauntingMonster != null && hoveredMonster[0] != tauntingMonster) {
            hoveredMonster[0] = null;
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(MonsterGroup.class, "areMonstersBasicallyDead");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}