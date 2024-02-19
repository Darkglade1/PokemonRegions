package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import pokeregions.scenes.PokemonScene;
import pokeregions.util.ProAudio;

import static pokeregions.PokemonRegions.makeID;

public class RainSoundPatch {

    @SpirePatch(clz = AbstractScene.class, method = "updateAmbienceVolume")
    public static class SetRainAmbiance {

        @SpirePrefixPatch
        public static void Prefix(AbstractScene __instance) {
            if(PokemonScene.isKyogre()) {
                if (Settings.AMBIANCE_ON) {
                    CardCrawlGame.sound.adjustVolume(makeID(ProAudio.RAIN.name()), PokemonScene.rainSoundId);
                } else {
                    CardCrawlGame.sound.adjustVolume(makeID(ProAudio.RAIN.name()), PokemonScene.rainSoundId, 0.0f);
                }
            }
        }
    }

    @SpirePatch(clz = AbstractScene.class, method = "fadeOutAmbiance")
    public static class FadeOutAmbiencePatch {

        @SpirePostfixPatch
        public static void Postfix() {
            if (PokemonScene.isKyogre()) {
                CardCrawlGame.sound.adjustVolume(makeID(ProAudio.RAIN.name()), PokemonScene.rainSoundId, 0.0f);
                PokemonScene.rainSoundId = 0L;
            }
        }
    }

    @SpirePatch(clz = AbstractScene.class, method = "muteAmbienceVolume")
    public static class MuteAmbiencePatch {

        @SpirePostfixPatch
        public static void Postfix() {
            if (PokemonScene.isKyogre()) {
                CardCrawlGame.sound.adjustVolume(makeID(ProAudio.RAIN.name()), PokemonScene.rainSoundId, 0.0f);
            }
        }
    }
}