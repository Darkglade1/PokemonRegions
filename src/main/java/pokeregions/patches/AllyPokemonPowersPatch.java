package pokeregions.patches;

import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.AbstractEasyPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

@SpirePatch(
        clz = AbstractCreature.class,
        method = "addPower"
)
// Stop ally pokemon from getting philosopher stoned
public class AllyPokemonPowersPatch {
    @SpirePrefixPatch()
    public static SpireReturn<Void> StopPokemonBlock(AbstractCreature instance, AbstractPower power) {
        if (instance instanceof AbstractPokemonAlly && !(power instanceof AbstractEasyPower)) {
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}