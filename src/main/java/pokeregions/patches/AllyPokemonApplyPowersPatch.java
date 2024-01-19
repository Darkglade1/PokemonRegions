package pokeregions.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.AbstractEasyPower;

@SpirePatch(
        clz = ApplyPowerAction.class,
        method = "update"
)
// Stop ally pokemon from getting random powers from other mods
public class AllyPokemonApplyPowersPatch {
    @SpirePrefixPatch()
    public static SpireReturn<Void> StopPokemonPowers(ApplyPowerAction instance) {
        if (instance.target instanceof AbstractPokemonAlly) {
            AbstractPower powerToApply = ReflectionHacks.getPrivate(instance, ApplyPowerAction.class, "powerToApply");
            if (!(powerToApply instanceof AbstractEasyPower)) {
                instance.isDone = true;
                return SpireReturn.Return(null);
            }
        }
        return SpireReturn.Continue();
    }
}