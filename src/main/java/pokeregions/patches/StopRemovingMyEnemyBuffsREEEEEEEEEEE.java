package pokeregions.patches;

import basemod.ReflectionHacks;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractUnremovablePower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.InvinciblePower;

@SpirePatch(clz = RemoveSpecificPowerAction.class, method = "update")
public class StopRemovingMyEnemyBuffsREEEEEEEEEEE {
	@SpirePrefixPatch()
	public static SpireReturn<Void> EveryTimeAnEnemyBuffsGetsRemovedIDieALittleInside(RemoveSpecificPowerAction instance) {
		AbstractPower powerBeingRemoved = ReflectionHacks.getPrivate(instance, RemoveSpecificPowerAction.class, "powerInstance");
		String powerToRemove = ReflectionHacks.getPrivate(instance, RemoveSpecificPowerAction.class, "powerToRemove");
		if (powerBeingRemoved == null) {
			powerBeingRemoved = instance.target.getPower(powerToRemove);
		}
		if (powerBeingRemoved instanceof AbstractUnremovablePower && instance.target instanceof AbstractMonster) {
			if (((AbstractUnremovablePower)powerBeingRemoved).isUnremovable && powerBeingRemoved.type == AbstractPower.PowerType.BUFF) {
				instance.isDone = true;
				return SpireReturn.Return(null);
			}
		}

		if (powerBeingRemoved instanceof InvinciblePower && instance.target instanceof AbstractPokemonMonster) {
			instance.isDone = true;
			return SpireReturn.Return(null);
		}
		return SpireReturn.Continue();
	}
}