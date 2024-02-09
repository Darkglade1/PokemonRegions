package pokeregions.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import pokeregions.monsters.AbstractPokemonAlly;

public class GyaradosWaterfallAction extends AbstractGameAction {
    private final DamageInfo info;
    private final AbstractPokemonAlly pokemon;

    public GyaradosWaterfallAction(AbstractCreature target, DamageInfo info, AbstractPokemonAlly pokemon) {
        this.info = info;
        this.setValues(target, info);
        this.pokemon = pokemon;
        this.actionType = ActionType.DAMAGE;
        this.duration = Settings.ACTION_DUR_FASTER;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FASTER && this.target != null) {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.BLUNT_HEAVY));
            this.target.damage(this.info);
            if (this.target.isDying || this.target.currentHealth <= 0) {
                pokemon.noStaminaCostForTurn = true;
            }
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        this.tickDuration();
    }
}
