package code.patches;

import code.monsters.AbstractPokemonAlly;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.city.GremlinLeader;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

//Stop your Pokemon from getting yeeted by Gremlin Leader's death
public class GremlinLeaderJankPatch {
    @SpirePatch(clz = GremlinLeader.class, method = "die")
    public static class GremlinLeaderPls {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("addToBottom")) {
                        m.replace("{" +
                                "if(!(m instanceof " + AbstractPokemonAlly.class.getName() + ")) {" +
                                "$proceed($$);" +
                                "}" +
                                "}");
                    }
                }
            };
        }
    }
}