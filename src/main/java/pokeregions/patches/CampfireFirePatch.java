package pokeregions.patches;

import pokeregions.dungeons.AbstractPokemonRegionDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.rooms.CampfireUI;

@SpirePatch(clz = CampfireUI.class, method = "updateFire")
public class CampfireFirePatch {
	public static SpireReturn Prefix(CampfireUI __instance) {
		if (CardCrawlGame.dungeon instanceof AbstractPokemonRegionDungeon) {
			return SpireReturn.Return(null);
		}
		return SpireReturn.Continue();
	}
}