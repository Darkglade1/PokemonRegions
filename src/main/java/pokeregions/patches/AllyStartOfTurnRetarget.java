package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;
import pokeregions.monsters.AbstractPokemonAlly;

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
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
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
                        this.isDone = true;
                    }
                });
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