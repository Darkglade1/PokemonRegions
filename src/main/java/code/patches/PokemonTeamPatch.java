package code.patches;

import code.actions.UsePreBattleActionAction;
import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import code.util.Tags;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import static code.util.Wiz.adp;
import static code.util.Wiz.atb;


public class PokemonTeamPatch {

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "preBattlePrep"
    )
    public static class SummonStarterPokemonPatch {
        public static void Postfix(AbstractPlayer __instance) {
            CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
            AbstractPokemonAlly pokemon = null;
            for (AbstractCard card : pokemonTeam.group) {
                AbstractAllyPokemonCard pokemonCard;
                if (card instanceof AbstractAllyPokemonCard) {
                    pokemonCard = (AbstractAllyPokemonCard)card;
                    if (pokemonCard.hasTag(Tags.STARTER_POKEMON)) {
                        pokemon = pokemonCard.getAssociatedPokemon(AbstractPokemonAlly.X_POSITION, AbstractPokemonAlly.Y_POSITION);
                        break;
                    }
                }
            }
            if (pokemon != null) {
                PlayerSpireFields.activePokemon.set(adp(), pokemon);
                atb(new SpawnMonsterAction(pokemon, false));
                atb(new UsePreBattleActionAction(pokemon));
            }
        }
    }

}