package pokeregions.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.powers.ReactivePower;
import pokeregions.monsters.AbstractPokemonAlly;

@SpirePatch(clz = ReactivePower.class, method = "onAttacked")
public class WrithingMassPatch {
	public static SpireReturn Prefix(ReactivePower __instance, DamageInfo info, int damageAmount) {
		if (info.owner instanceof AbstractPokemonAlly) {
			return SpireReturn.Return(damageAmount);
		}
		return SpireReturn.Continue();
	}
}