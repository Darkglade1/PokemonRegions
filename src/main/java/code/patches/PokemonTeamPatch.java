package code.patches;

import code.actions.UsePreBattleActionAction;
import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import code.ui.PokemonTeamButton;
import code.util.Tags;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

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
            AbstractAllyPokemonCard mostRecent = PlayerSpireFields.mostRecentlyUsedPokemonCard.get(adp());
            if (mostRecent != null && mostRecent.currentStamina > 0) {
                pokemon = mostRecent.getAssociatedPokemon(AbstractPokemonAlly.X_POSITION, AbstractPokemonAlly.Y_POSITION);
            } else {
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
            }
            if (pokemon != null) {
                PlayerSpireFields.activePokemon.set(adp(), pokemon);
                atb(new SpawnMonsterAction(pokemon, false));
                atb(new UsePreBattleActionAction(pokemon));
            }
        }
    }

    @SpirePatch(
            clz= AbstractPlayer.class,
            method="onVictory"
    )
    public static class OnVictoryPatch {
        public static void Prefix(AbstractPlayer __instance) {
            CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
            for (AbstractCard card : pokemonTeam.group) {
                if (card instanceof AbstractAllyPokemonCard) {
                    AbstractAllyPokemonCard pokemonCard = (AbstractAllyPokemonCard)card;
                    if (pokemonCard.hasTag(Tags.STARTER_POKEMON) && pokemonCard.currentStamina <= 0) {
                        pokemonCard.updateStamina(1);
                        pokemonCard.initializeDescriptionFromMoves();
                        break;
                    }
                }
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "dungeonTransitionSetup")
    public static class DungeonTransitionHeal {
        @SpirePostfixPatch
        public static void Postfix() {
            PokemonTeamButton.teamWideHeal(0.5f);
        }
    }

}