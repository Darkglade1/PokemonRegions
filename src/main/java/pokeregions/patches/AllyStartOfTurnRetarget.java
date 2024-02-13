package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.enemies.DiglettEnemy;
import pokeregions.util.Wiz;

import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;

@SpirePatch(
        clz = GameActionManager.class,
        method = "getNextAction"

)
// A patch to let allies target again at start of turn
public class AllyStartOfTurnRetarget {
    @SpireInsertPatch(locator = Locator.class)
    public static void Retarget(GameActionManager instance) {
        if (AbstractDungeon.getCurrRoom() != null) {
            AbstractPokemonAlly activePokemon = PlayerSpireFields.activePokemon.get(adp());
            if (activePokemon != null) {
                boolean isDiglett = false;
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    if (mo instanceof DiglettEnemy) {
                        isDiglett = true;
                        break;
                    }
                }
                if (isDiglett) {
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            activePokemon.setSmartTarget();
                            if (activePokemon.target != null) {
                                AbstractDungeon.onModifyPower();
                            }
                            this.isDone = true;
                        }
                    });
                }
            }
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "applyStartOfTurnRelics");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}