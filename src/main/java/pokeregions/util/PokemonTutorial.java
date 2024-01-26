package pokeregions.util;

import basemod.abstracts.CustomMultiPageFtue;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.PokemonRegions;
import pokeregions.monsters.AbstractPokemonAlly;

import java.io.IOException;

import static pokeregions.PokemonRegions.makeUIPath;

public class PokemonTutorial extends CustomMultiPageFtue {

    public PokemonTutorial(Texture[] images, String[] messages) {
        super(images, messages);
    }

    @SpirePatch(
            clz = DrawCardAction.class,
            method = "update"
    )
    public static class ShowAllyTutorialPatch {
        public static final String ID = PokemonRegions.makeID("PokemonTutorial");
        public static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(ID);
        public static final String[] TEXT = tutorialStrings.TEXT;

        public static void Postfix(DrawCardAction __instance) {
            if (__instance.isDone && !PokemonRegions.pokemonRegionConfig.getBool("Pokemon Combat Tutorial Seen")) {
                for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                    if (mo instanceof AbstractPokemonAlly) {
                        Texture tip1 = TexLoader.getTexture(makeUIPath("PokemonAllyTutorial.png"));
                        Texture tip2 = TexLoader.getTexture(makeUIPath("PokemonAllyTutorial2.png"));
                        Texture[] images = new Texture[2];
                        images[0] = tip1;
                        images[1] = tip2;
                        AbstractDungeon.ftue = new PokemonTutorial(images, TEXT);
                        PokemonRegions.pokemonRegionConfig.setBool("Pokemon Combat Tutorial Seen", true);
                        try { PokemonRegions.pokemonRegionConfig.save(); } catch (IOException e) { e.printStackTrace(); }
                        break;
                    }
                }
            }
        }
    }
}