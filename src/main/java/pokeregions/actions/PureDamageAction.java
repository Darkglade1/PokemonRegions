package pokeregions.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class PureDamageAction extends DamageAction
{
    private DamageInfo infoBackup;

    public PureDamageAction(AbstractCreature target, DamageInfo info, AbstractGameAction.AttackEffect effect)
    {
        super(target, info, effect);
        infoBackup = info;
    }

    @Override
    public void update()
    {
        if (infoBackup.owner == null && duration == 0.1f) {
            duration = 0.09f;
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(target.hb.cX, target.hb.cY, attackEffect));
        } else {
            super.update();
        }
    }
}