package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

@SpirePatch(cls = "Goldenglow.helper.Hpr", method = "isInBattle", optional = true)
public class GoldenGlowPlease {
	@SpirePrefixPatch()
	public static SpireReturn<Boolean> StopBreaking() {
		boolean result = (CardCrawlGame.dungeon != null && AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() != null &&
				(AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT);
		return SpireReturn.Return(result);
	}
}