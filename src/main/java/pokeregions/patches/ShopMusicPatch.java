package pokeregions.patches;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CtBehavior;
import pokeregions.dungeons.AbstractPokemonRegionDungeon;

@SpirePatch(
        clz = ShopRoom.class,
        method = "onPlayerEntry"
)

public class ShopMusicPatch {
    @SpireInsertPatch(locator = ShopMusicPatch.Locator.class)
    public static void ChangeShopMusic(ShopRoom instance) {
        if (CardCrawlGame.dungeon instanceof AbstractPokemonRegionDungeon) {
            CustomDungeon.playTempMusicInstantly("PokeMart");
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ProceedButton.class, "setLabel");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}