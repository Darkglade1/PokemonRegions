package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import pokeregions.monsters.AbstractPokemonAlly;

public class AllyDomePatch {

    @SpirePatch(clz = AbstractMonster.class, method = "renderTip")
    public static class renderTip {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("hasRelic")) {
                        m.replace("{" +
                                "if(this instanceof " + AbstractPokemonAlly.class.getName() + ") {" +
                                "$_ = false;" +
                                "} else { " +
                                "$_ = $proceed($$);" +
                                "}" +
                                "}");
                    }
                }
            };
        }
    }

}