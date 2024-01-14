package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.monsters.AbstractPokemonAlly;

@SpirePatch(cls = "com.evacipated.cardcrawl.mod.hubris.relics.ScarierMask", method = "atPreBattle", optional = true)
public class HubrisScarierMaskPatch {
	@SpirePrefixPatch()
	public static SpireReturn<Void> DontTriggerInAllyFights() {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (m instanceof AbstractPokemonAlly) {
				return SpireReturn.Return(null);
			}
		}
		return SpireReturn.Continue();
	}
}