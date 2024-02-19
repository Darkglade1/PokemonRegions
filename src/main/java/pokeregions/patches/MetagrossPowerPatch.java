package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.monsters.act3.enemies.MetagrossEnemy;
import pokeregions.util.Wiz;

import static pokeregions.util.Wiz.adp;

@SpirePatch(
        clz = GainBlockAction.class,
        method = "update"
)
// Make Metagross work.
public class MetagrossPowerPatch {
    @SpirePostfixPatch()
    public static void AnalyticPatch(GainBlockAction instance) {
        if (instance.target == adp()) {
            for (AbstractMonster mo : Wiz.getEnemies()) {
                if (mo instanceof MetagrossEnemy) {
                    mo.rollMove();
                }
            }
        }
    }
}