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

//    @SpirePatch(clz = GridCardSelectScreen.class, method = "update")
//    public static class MakeViewOnlyPlease {
//        public static ExprEditor Instrument() {
//            return new ExprEditor() {
//                @Override
//                public void edit(MethodCall m) throws CannotCompileException {
//                    if (m.getMethodName().equals("contains")) {
//                        m.replace("$_ = $proceed($$) && !" + PokemonTeamPatch.class.getName() + ".isPokemonGridViewOnly();" );
//                    }
//                }
//            };
//        }
//    }
//    public static boolean isPokemonGridViewOnly() {
//        return PokemonTeamButton.gridViewOnly;
//    }
//
//    @SpirePatch(
//            clz = AbstractDungeon.class,
//            method = "closeCurrentScreen"
//    )
//    public static class SetStaticVariable {
//        public static void Prefix() {
//            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
//                if (PokemonTeamButton.gridViewOnly) {
//                    PokemonTeamButton.gridViewOnly = false;
//                }
//            }
//        }
//    }

}