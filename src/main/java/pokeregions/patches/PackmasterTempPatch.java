package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.events.AbstractEvent;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import thePackmaster.patches.OpeningRunScreenPatch;

import java.util.ArrayList;

public class PackmasterTempPatch {

    //Fixes the event not spawning with ActLikeIt's custom event on run start feature
    @SpirePatch2(cls="actlikeit.dungeons.CustomDungeon", method = SpirePatch.CONSTRUCTOR, paramtypes = {"actlikeit.dungeons.CustomDungeon", "com.megacrit.cardcrawl.characters.AbstractPlayer", "java.util.ArrayList"})
    public static class CustomNeowReplacementEventFix {
        @SpireInsertPatch(locator = Locator.class, localvars = {"ae"})
        public static void bigThanksGkTapSmile(AbstractEvent ae) {
            if (Loader.isModLoaded("anniv5")) {
                OpeningRunScreenPatch.SetTheThing(ae);
            }
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractEvent.class, "onEnterRoom");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }
}