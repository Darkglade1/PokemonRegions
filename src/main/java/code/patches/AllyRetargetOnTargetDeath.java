package code.patches;

import code.monsters.AbstractPokemonAlly;
import code.monsters.act1.allyPokemon.Zapdos;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static code.util.Wiz.adp;
import static code.util.Wiz.atb;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "die",
        paramtypez = boolean.class
)

// A patch that allows the active pokemon to automatically retarget on its target's death
public class AllyRetargetOnTargetDeath {
    @SpirePostfixPatch
    public static void triggerOnKillPowers(AbstractMonster instance, boolean triggerRelics) {
        AbstractPokemonAlly activePokemon = PlayerSpireFields.activePokemon.get(adp());
        if (activePokemon != null && activePokemon.target == instance) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    activePokemon.target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.aiRng);
                    if (activePokemon.target != null) {
                        AbstractDungeon.onModifyPower();
                    }
                    this.isDone = true;
                }
            });
        } else if (activePokemon instanceof Zapdos) {
            AbstractDungeon.onModifyPower();
        }
    }
}
