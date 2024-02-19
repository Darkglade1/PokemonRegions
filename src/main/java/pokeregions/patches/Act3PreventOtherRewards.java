package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.Instanceof;
import pokeregions.dungeons.Hoenn;

public class Act3PreventOtherRewards {

	@SpirePatch(
			clz = AbstractRoom.class,
			method = "addGoldToRewards"
	)
	public static class stopGold {
		public static SpireReturn<Void> Prefix(AbstractRoom __instance, int gold) {
			if (CardCrawlGame.dungeon instanceof Hoenn && __instance instanceof MonsterRoomBoss) {
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatch(
			clz = AbstractRoom.class,
			method = "addPotionToRewards",
			paramtypez = {}
	)
	public static class stopPotion {
		public static SpireReturn<Void> Prefix(AbstractRoom __instance) {
			if (CardCrawlGame.dungeon instanceof Hoenn && __instance instanceof MonsterRoomBoss) {
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatch(
			clz = CombatRewardScreen.class,
			method = "setupItemReward"
	)
	public static class StopCard {
		public static ExprEditor Instrument() {
			return new ExprEditor() {
				@Override
				public void edit(Instanceof i) throws CannotCompileException {
					try {
						if (i.getType().getName().equals(RestRoom.class.getName())) {
							i.replace("$_ = $proceed($$) || " + Act3PreventOtherRewards.class.getName() + ".isHoennBoss();");
						}
					} catch (NotFoundException e) { }
				}
			};
		}
	}

	public static boolean isHoennBoss() {
		return CardCrawlGame.dungeon instanceof Hoenn && AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss;
	}
}