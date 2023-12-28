package code.patches;


import code.monsters.AbstractPokemonAlly;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class AreMonstersDeadPatch {

    @SpirePatch(clz = MonsterGroup.class, method = "areMonstersBasicallyDead")
    public static class areMonstersBasicallyDead {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getFieldName().equals("isDying")) {
                        f.replace("{" +
                                "if(m instanceof " + AbstractPokemonAlly.class.getName() + ") {" +
                                "$_ = true;" +
                                "} else { " +
                                "$_ = $proceed($$);" +
                                "}" +
                                "}");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = MonsterGroup.class, method = "areMonstersDead")
    public static class areMonstersDead {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getFieldName().equals("isDead")) {
                        f.replace("{" +
                                "if(m instanceof " + AbstractPokemonAlly.class.getName() + ") {" +
                                "$_ = true;" +
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