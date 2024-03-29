package pokeregions.patches;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.dungeons.Exordium;
import javassist.CtBehavior;
import pokeregions.dungeons.AbstractPokemonRegionDungeon;

import java.util.ArrayList;

@SpirePatch(clz = CustomDungeon.class, method = "setupMisc")
public class CustomDungeonMusicPatch {
    @SpireInsertPatch(locator = CustomDungeonMusicPatch.Locator.class, localvars = {"tracks"})
    public static SpireReturn<Void> play(CustomDungeon instance, ArrayList<MainMusic> tracks) {
        if (instance instanceof AbstractPokemonRegionDungeon) {
            ((AbstractPokemonRegionDungeon) instance).setMusic();
            Music music = MainMusic.newMusic(instance.mainmusic);
            MainMusic main = new MainMusic(Exordium.id);
            ReflectionHacks.setPrivate(main, MainMusic.class, "music", music);
            tracks.add(main);
            music.setLooping(true);
            music.play();
            music.setVolume(0.0F);
            return SpireReturn.Return(null);
        } else {
            return SpireReturn.Continue();
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(CustomDungeon.class, "mainmusic");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}