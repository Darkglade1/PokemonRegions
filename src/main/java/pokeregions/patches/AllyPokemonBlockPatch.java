package pokeregions.patches;

import pokeregions.monsters.AbstractPokemonAlly;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;

@SpirePatch(
        clz = GainBlockAction.class,
        method = "update"
)
// Ally pokemon can't gain Block
public class AllyPokemonBlockPatch {
    @SpirePrefixPatch()
    public static SpireReturn<Void> StopPokemonBlock(GainBlockAction instance) {
        if (instance.target instanceof AbstractPokemonAlly) {
            instance.isDone = true;
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}