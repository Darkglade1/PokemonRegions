package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act2.allyPokemon.SteelixAlly;
import pokeregions.monsters.act3.allyPokemon.AggronAlly;

import static pokeregions.util.Wiz.adp;

@SpirePatch(
        clz = GainBlockAction.class,
        method = "update"
)
// Ally pokemon can't gain Block. Also piggy back for Aggron and Steelix.
public class AllyPokemonBlockPatch {
    @SpirePrefixPatch()
    public static SpireReturn<Void> StopPokemonBlock(GainBlockAction instance) {
        AbstractPokemonAlly activePokemon = PlayerSpireFields.activePokemon.get(adp());
        if (activePokemon instanceof AggronAlly || activePokemon instanceof SteelixAlly) {
            AbstractDungeon.onModifyPower();
        }
        if (instance.target instanceof AbstractPokemonAlly) {
            instance.isDone = true;
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}