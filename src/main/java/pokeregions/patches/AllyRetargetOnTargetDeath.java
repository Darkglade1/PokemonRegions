package pokeregions.patches;

import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.ZapdosAlly;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "die",
        paramtypez = boolean.class
)

// A patch that allows the active pokemon to automatically retarget on its target's death
public class AllyRetargetOnTargetDeath {
    @SpirePostfixPatch
    public static void allyRetarget(AbstractMonster instance, boolean triggerRelics) {
        AbstractPokemonAlly activePokemon = PlayerSpireFields.activePokemon.get(adp());
        if (activePokemon != null && activePokemon.target == instance) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    activePokemon.setSmartTarget();
                    if (activePokemon.target != null) {
                        AbstractDungeon.onModifyPower();
                    }
                    this.isDone = true;
                }
            });
        } else if (activePokemon instanceof ZapdosAlly) {
            AbstractDungeon.onModifyPower();
        }
    }
}
