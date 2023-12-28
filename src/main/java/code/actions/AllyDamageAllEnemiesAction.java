package code.actions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class AllyDamageAllEnemiesAction extends AbstractGameAction {
    public int[] damage;
    private boolean firstFrame;

    public AllyDamageAllEnemiesAction(AbstractCreature source, int[] amount, DamageType type, AttackEffect effect, boolean isFast) {
        this.firstFrame = true;
        this.source = source;
        this.damage = amount;
        this.actionType = ActionType.DAMAGE;
        this.damageType = type;
        this.attackEffect = effect;
        if (isFast) {
            this.duration = Settings.ACTION_DUR_XFAST;
        } else {
            this.duration = Settings.ACTION_DUR_FAST;
        }

    }

    public AllyDamageAllEnemiesAction(AbstractCreature source, int[] amount, DamageType type, AttackEffect effect) {
        this(source, amount, type, effect, false);
    }

    public void update() {
        if (this.firstFrame) {
            boolean playedMusic = false;
            for(int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); ++i) {
                AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                if (!mo.isDeadOrEscaped() && mo != source) {
                    if (playedMusic) {
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(mo.hb.cX, mo.hb.cY, this.attackEffect, true));
                    } else {
                        playedMusic = true;
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(mo.hb.cX, mo.hb.cY, this.attackEffect));
                    }
                }
            }

            this.firstFrame = false;
        }

        this.tickDuration();
        if (this.isDone && !source.isDeadOrEscaped()) {
            for(int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); ++i) {
                AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                if (!mo.isDeadOrEscaped() && mo != source) {
                    if (this.attackEffect == AttackEffect.POISON) {
                        mo.tint.color.set(Color.CHARTREUSE.cpy());
                        mo.tint.changeColor(Color.WHITE.cpy());
                    } else if (this.attackEffect == AttackEffect.FIRE) {
                        mo.tint.color.set(Color.RED);
                        mo.tint.changeColor(Color.WHITE.cpy());
                    }
                    mo.damage(new DamageInfo(this.source, this.damage[i], this.damageType));
                }
            }

            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }

            if (!Settings.FAST_MODE) {
                this.addToTop(new WaitAction(0.1F));
            }
        }

    }
}
