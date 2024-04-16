package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act2.allyPokemon.Scizor;
import pokeregions.monsters.act2.allyPokemon.Tyranitar;

import static pokeregions.util.Wiz.adp;

@SpirePatch(
        clz = UseCardAction.class,
        method = "update"
)

public class UseCardActionPatch {
    @SpirePostfixPatch()
    public static void applyPowersForPokemon(UseCardAction instance) {
        AbstractPokemonAlly activePokemon = PlayerSpireFields.activePokemon.get(adp());
        if (activePokemon instanceof Scizor || activePokemon instanceof Tyranitar) {
            AbstractDungeon.onModifyPower();
        }
    }
}