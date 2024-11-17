package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import pokeregions.PokemonRegions;
import pokeregions.actions.UsePreBattleActionAction;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.AbstractAllyStarterPokemonCard;
import pokeregions.cards.pokemonAllyCards.act1.Mew;
import pokeregions.dungeons.AbstractPokemonRegionDungeon;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.ui.PokemonTeamButton;
import pokeregions.util.Tags;

import java.util.ArrayList;

import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;

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
            if (pokemon != null && (CardCrawlGame.dungeon instanceof AbstractPokemonRegionDungeon || !PokemonRegions.disablePokemonOutsideConfig)) {
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
                    pokemonCard.hasUsedMove1 = false;
                    pokemonCard.hasUsedMove2 = false;
                    if (pokemonCard instanceof Mew) {
                        ((Mew) pokemonCard).resetLimitedTracker();
                    }
                    if (pokemonCard.hasTag(Tags.STARTER_POKEMON) && pokemonCard.currentStamina <= 0) {
                        pokemonCard.updateStamina(1);
                    }
                    pokemonCard.initializeDescriptionFromMoves();
                }
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "dungeonTransitionSetup")
    public static class DungeonTransitionLogic {
        @SpirePostfixPatch
        public static void Postfix() {
            PokemonTeamButton.teamWideHeal(3);
            ArrayList<AbstractAllyPokemonCard> cardsToRemove = new ArrayList<>();
            ArrayList<AbstractAllyPokemonCard> cardsToAdd = new ArrayList<>();
            for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
                if (card.hasTag(Tags.STARTER_POKEMON) && card instanceof AbstractAllyStarterPokemonCard) {
                    AbstractAllyStarterPokemonCard starterCard = (AbstractAllyStarterPokemonCard) card;
                    AbstractAllyStarterPokemonCard evolvedStarter = starterCard.getNextStage();
                    if (evolvedStarter != null) {
                        cardsToRemove.add(starterCard);
                        evolvedStarter.updateStamina(starterCard.currentStamina);
                        cardsToAdd.add(evolvedStarter);
                    }
                }
            }
            if (cardsToAdd.size() > 0 && cardsToAdd.size() == cardsToRemove.size()) {
                for (int i = 0; i < cardsToAdd.size(); i++) {
                    PlayerSpireFields.pokemonTeam.get(adp()).removeCard(cardsToRemove.get(i));
                    PlayerSpireFields.pokemonTeam.get(adp()).addToBottom(cardsToAdd.get(i));
                    UnlockTracker.markCardAsSeen(cardsToAdd.get(i).cardID);
                }
            }

        }
    }

}