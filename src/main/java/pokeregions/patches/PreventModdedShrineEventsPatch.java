package pokeregions.patches;

import basemod.eventUtil.EventUtils;
import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.AddEvents;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import pokeregions.dungeons.AbstractPokemonRegionDungeon;

import java.util.HashMap;

public class PreventModdedShrineEventsPatch {
    @SpirePatch(clz = AddEvents.NormalAndShrineEvents.class, method = "insert")
    @SpirePatch(clz = AddEvents.SaveAndLoadShrineEvents.class, method = "insert")
    public static class ShrineEvents {
        public static class ShrineEventsExprEditor extends ExprEditor {
            @Override
            public void edit(FieldAccess fieldAccess) throws CannotCompileException {
                if (fieldAccess.getClassName().equals(EventUtils.class.getName()) && fieldAccess.getFieldName().equals("shrineEvents")) {
                    fieldAccess.replace(String.format("{ $_ = __instance instanceof %1$s ? new %2$s() : $proceed($$); }", AbstractPokemonRegionDungeon.class.getName(), HashMap.class.getName()));
                }
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor shrineEvents() {
            return new ShrineEventsExprEditor();
        }
    }
}