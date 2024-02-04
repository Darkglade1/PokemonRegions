package pokeregions.patches;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pokeregions.monsters.AbstractPokemonAlly;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import pokeregions.monsters.act3.allyPokemon.Aggron;

import static pokeregions.util.Wiz.adp;

@SpirePatch(
        clz = GainBlockAction.class,
        method = "update"
)
// Ally pokemon can't gain Block. Also piggy back for Aggron.
public class AllyPokemonBlockPatch {
    @SpirePrefixPatch()
    public static SpireReturn<Void> StopPokemonBlock(GainBlockAction instance) {
        AbstractPokemonAlly activePokemon = PlayerSpireFields.activePokemon.get(adp());
        if (activePokemon instanceof Aggron) {
            AbstractDungeon.onModifyPower();
        }
        if (instance.target instanceof AbstractPokemonAlly) {
            instance.isDone = true;
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}