package pokeregions.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.NextTurnBlockPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class MonsterNextTurnBlockPower extends NextTurnBlockPower {

    public MonsterNextTurnBlockPower(AbstractCreature owner, int amount) {
        super(owner, amount);
    }

    @Override
    public void atStartOfTurn() {
    }

    @Override
    public void atEndOfRound() {
        this.flash();
        AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.owner.hb.cX, this.owner.hb.cY, AbstractGameAction.AttackEffect.SHIELD));
        this.addToBot(new GainBlockAction(this.owner, this.owner, this.amount));
        this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }
}
