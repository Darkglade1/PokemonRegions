package pokeregions.util;

import basemod.abstracts.CustomMultiPageFtue;
import pokeregions.PokemonRegions;
import pokeregions.monsters.AbstractPokemonMonster;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.io.IOException;

import static pokeregions.PokemonRegions.makeUIPath;

public class PokemonCatchTutorial extends CustomMultiPageFtue {
    public PokemonCatchTutorial(Texture[] images, String[] messages) {
        super(images, messages);
    }

    @SpirePatch(
            clz = AbstractMonster.class,
            method = "damage"
    )
    public static class ShowCatchTutorialPatch {
        public static final String ID = PokemonRegions.makeID("PokemonCatchTutorial");
        public static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(ID);
        public static final String[] TEXT = tutorialStrings.TEXT;

        public static void Postfix(AbstractMonster __instance, DamageInfo info) {
            if (__instance instanceof AbstractPokemonMonster && __instance.currentHealth < (__instance.maxHealth * 0.3f) && !PokemonRegions.pokemonRegionConfig.getBool("Pokemon Catch Tutorial Seen")) {
                Texture tip1 = TexLoader.getTexture(makeUIPath("PokemonCatchTutorial.png"));
                Texture tip2 = TexLoader.getTexture(makeUIPath("PokemonCatchTutorial2.png"));
                Texture[] images = new Texture[2];
                images[0] = tip1;
                images[1] = tip2;
                AbstractDungeon.ftue = new PokemonCatchTutorial(images, TEXT);
                PokemonRegions.pokemonRegionConfig.setBool("Pokemon Catch Tutorial Seen", true);
                try { PokemonRegions.pokemonRegionConfig.save(); } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }
}