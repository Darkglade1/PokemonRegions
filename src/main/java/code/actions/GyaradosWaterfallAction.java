package code.actions;

import code.cards.AbstractAllyPokemonCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import static code.util.Wiz.att;

public class GyaradosWaterfallAction extends AbstractGameAction {
    private final int staminaAmt;
    private final DamageInfo info;
    private final AbstractAllyPokemonCard pokemonCard;

    public GyaradosWaterfallAction(AbstractCreature target, DamageInfo info, int staminaAmt, AbstractAllyPokemonCard pokemonCard) {
        this.info = info;
        this.setValues(target, info);
        this.staminaAmt = staminaAmt;
        this.pokemonCard = pokemonCard;
        this.actionType = ActionType.DAMAGE;
        this.duration = Settings.ACTION_DUR_FASTER;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FASTER && this.target != null) {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.BLUNT_HEAVY));
            this.target.damage(this.info);
            if (this.target.isDying || this.target.currentHealth <= 0) {
                att(new UpdateStaminaOnCardAction(pokemonCard, staminaAmt));
                att(new HealAction(info.owner, info.owner, staminaAmt));
            }
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        this.tickDuration();
    }
}
