package pokeregions.patches;


import basemod.ReflectionHacks;
import basemod.TopPanelGroup;
import basemod.TopPanelItem;
import basemod.patches.com.megacrit.cardcrawl.helpers.TopPanel.TopPanelHelper;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.ui.PokemonTeamButton;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.Soul;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import java.util.ArrayList;

import static pokeregions.util.Wiz.adp;

public class PokemonObtainPatch {
    @SpirePatch(clz = Soul.class, method = "obtain")
    public static class addPokemonToTeam {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getFieldName().equals("masterDeck")) {
                        f.replace("{" +
                                "if(card instanceof " + AbstractAllyPokemonCard.class.getName() + ") {" +
                                "$_ = " + PokemonObtainPatch.class.getName() + ".getPokemonTeamGroup();" +
                                "} else { " +
                                "$_ = $proceed($$);" +
                                "}" +
                                "}");
                    }
                }
            };
        }
    }
    public static CardGroup getPokemonTeamGroup() {
        return PlayerSpireFields.pokemonTeam.get(adp());
    }


    @SpirePatch(clz = Soul.class, method = "obtain")
    public static class noHoarding {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("isModEnabled")) {
                        m.replace("$_ = $proceed($$) && !(card instanceof " + AbstractAllyPokemonCard.class.getName() + ");");
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz = Soul.class,
            method = "obtain"
    )
    public static class ReleaseExcessPokemon {
        public static void Postfix(Soul __instance, AbstractCard card) {
            CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
            if (pokemonTeam.size() > PokemonTeamButton.MAX_TEAM_SIZE) {
                ArrayList<TopPanelItem> topPanelItems = ReflectionHacks.getPrivate(TopPanelHelper.topPanelGroup, TopPanelGroup.class, "topPanelItems");
                for (TopPanelItem item : topPanelItems) {
                    if (item instanceof PokemonTeamButton) {
                        ((PokemonTeamButton) item).releaseExcessPokemon();
                        return;
                    }
                }
            }
        }
    }

}