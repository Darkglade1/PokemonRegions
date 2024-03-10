package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import javassist.CtBehavior;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Taunt;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.atb;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "updateSingleTargetInput"

)
// A patch to make allies untargetable by the player
public class MakeAlliesUntargetable {

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("TauntNotice"));
    private static final String[] TEXT = uiStrings.TEXT;
    @SpireInsertPatch(locator = Locator.class, localvars = {"hoveredMonster"})
    public static void MakeHoveredMonsterNull(AbstractPlayer instance, @ByRef AbstractMonster[] hoveredMonster) {
        if (hoveredMonster[0] instanceof AbstractPokemonAlly) {
            hoveredMonster[0] = null;
        }
        AbstractMonster tauntingMonster = null;
        for (AbstractMonster mo : Wiz.getEnemies()) {
            if (mo.hasPower(Taunt.POWER_ID)) {
                tauntingMonster = mo;
                break;
            }
        }
        if (tauntingMonster != null && hoveredMonster[0] != null && hoveredMonster[0] != tauntingMonster) {
            if (InputHelper.justClickedLeft) {
                atb(new TalkAction(true, TEXT[0], 1.0F, 1.0F));
            }
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