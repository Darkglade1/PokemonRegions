package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.unique.GainBlockRandomMonsterAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import pokeregions.monsters.AbstractPokemonAlly;

import java.util.ArrayList;

@SpirePatch(
        clz = GainBlockRandomMonsterAction.class,
        method = "update"

)
// Stop trolling centurion and shield gremlin
public class GainBlockRandomMonsterPatch {
    @SpireInsertPatch(locator = Locator.class, localvars = {"validMonsters"})
    public static void doNotBlockPokemon(GainBlockRandomMonsterAction instance, @ByRef ArrayList<AbstractMonster>[] validMonsters) {
        ArrayList<AbstractMonster> monsterList = validMonsters[0];
        ArrayList<AbstractMonster> monstersToRemove = new ArrayList<>();
        for (AbstractMonster mo : monsterList) {
            if (mo instanceof AbstractPokemonAlly) {
                monstersToRemove.add(mo);
            }
        }
        for (AbstractMonster mo : monstersToRemove) {
            monsterList.remove(mo);
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}