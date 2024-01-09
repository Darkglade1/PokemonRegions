package code.actions;

import code.util.Wiz;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;

public class ZapdosMassAttackAction extends AbstractGameAction {
    private final AllyDamageAllEnemiesAction massAttack;

    public ZapdosMassAttackAction(AllyDamageAllEnemiesAction massAttack) {
        this.actionType = ActionType.DAMAGE;
        this.attackEffect = AttackEffect.NONE;
        this.massAttack = massAttack;
    }

    public void update() {
        this.addToTop(massAttack);
        for (AbstractMonster mo : Wiz.getEnemies()) {
            if (!mo.isDeadOrEscaped()) {
                this.addToTop(new VFXAction(new LightningEffect(mo.drawX, mo.drawY), 0.0f));
            }
        }
        this.addToTop(new SFXAction("ORB_LIGHTNING_EVOKE"));
        this.isDone = true;
    }
}
