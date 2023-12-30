package code.patches;


import code.cards.AbstractAllyPokemonCard;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.Soul;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import static code.util.Wiz.adp;

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

}