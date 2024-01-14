package pokeregions.actions;

import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.Gyarados;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.act1.allyPokemon.Magikarp;
import pokeregions.patches.PlayerSpireFields;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;
public class MagikarpDamageAction extends AbstractGameAction {
    private final DamageInfo info;
    private final AbstractCard pokemonCard;
    private Magikarp magikarp;

    public MagikarpDamageAction(AbstractCreature target, DamageInfo info, AbstractCard pokemonCard, Magikarp magikarp) {
        this.info = info;
        this.setValues(target, info);
        this.actionType = ActionType.DAMAGE;
        this.duration = 0.1F;
        this.pokemonCard = pokemonCard;
        this.magikarp = magikarp;
    }

    @Override
    public void update() {
        if (this.duration == 0.1F && this.target != null) {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.BLUNT_LIGHT));
            this.target.damage(this.info);
            if ((this.target.isDying || this.target.currentHealth <= 0) && !this.target.halfDead && !this.target.hasPower(MinionPower.POWER_ID)) {
                magikarp.evolving = true;
                PlayerSpireFields.pokemonTeam.get(adp()).removeCard(pokemonCard);
                AbstractAllyPokemonCard gyaradosCard = new Gyarados();
                PlayerSpireFields.pokemonTeam.get(adp()).addToTop(gyaradosCard);
                UnlockTracker.markCardAsSeen(gyaradosCard.cardID);

                AbstractPokemonAlly gyaradosPokemon = gyaradosCard.getAssociatedPokemon(AbstractPokemonAlly.X_POSITION, AbstractPokemonAlly.Y_POSITION);
                atb(new RemoveMonsterAction(PlayerSpireFields.activePokemon.get(adp())));
                PlayerSpireFields.activePokemon.set(adp(), gyaradosPokemon);
                PlayerSpireFields.mostRecentlyUsedPokemonCardID.set(adp(), gyaradosCard.cardID);
                atb(new SpawnMonsterAction(gyaradosPokemon, false));
                atb(new UsePreBattleActionAction(gyaradosPokemon));
            }
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        this.tickDuration();
    }
}
