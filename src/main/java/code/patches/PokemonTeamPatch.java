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

            String mostRecentID = PlayerSpireFields.mostRecentlyUsedPokemonCardID.get(adp());
            AbstractAllyPokemonCard mostRecent = null;
            AbstractAllyPokemonCard starter = null;
            for (AbstractCard card : pokemonTeam.group) {
                AbstractAllyPokemonCard pokemonCard;
                if (card instanceof AbstractAllyPokemonCard) {
                    pokemonCard = (AbstractAllyPokemonCard)card;
                    if (pokemonCard.hasTag(Tags.STARTER_POKEMON)) {
                        starter = pokemonCard;
                    }
                    if (pokemonCard.cardID.equals(mostRecentID)) {
                        mostRecent = pokemonCard;
                    }
                }
            }
            if (mostRecent != null && mostRecent.currentStamina > 0) {
                pokemon = mostRecent.getAssociatedPokemon(AbstractPokemonAlly.X_POSITION, AbstractPokemonAlly.Y_POSITION);
            } else if (starter != null && starter.currentStamina > 0){
                pokemon = starter.getAssociatedPokemon(AbstractPokemonAlly.X_POSITION, AbstractPokemonAlly.Y_POSITION);
            }
            if (pokemon != null) {
                PlayerSpireFields.activePokemon.set(adp(), pokemon);
                atb(new SpawnMonsterAction(pokemon, false));
                atb(new UsePreBattleActionAction(pokemon));
                pokemon.onSwitchIn();
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