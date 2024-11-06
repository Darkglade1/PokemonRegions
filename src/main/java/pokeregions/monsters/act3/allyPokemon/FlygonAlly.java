package pokeregions.monsters.act3.allyPokemon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Flygon;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Slow;
import pokeregions.util.ProAudio;
import pokeregions.util.Wiz;
import pokeregions.vfx.WaitEffect;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class FlygonAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Flygon.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public FlygonAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Flygon/Flygon.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK_DEBUFF;
        move2Intent = Intent.DEBUFF;
        addMove(MOVE_1, move1Intent, Flygon.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                dmg(target, info, AbstractGameAction.AttackEffect.POISON);
                applyToTarget(target, this, new Slow(target, Flygon.MOVE_1_DEBUFF, AbstractDungeon.actionManager.turnHasEnded));
                break;
            }
            case MOVE_2: {
                AbstractMonster m = target;
                if (target.currentHealth < adp().currentHealth) {
                    useFastAttackAnimation();
                    Wiz.playAudio(ProAudio.EARTHQUAKE);
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.XLONG, false);
                    atb(new VFXAction(new WaitEffect(), 0.8f));
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            m.currentHealth = 0;
                            m.healthBarUpdatedEvent();
                            m.useStaggerAnimation();
                            AbstractDungeon.effectList.add(new StrikeEffect(m, m.hb.cX, m.hb.cY, 999));
                            m.damage(new DamageInfo(null, 0, DamageInfo.DamageType.HP_LOSS));
                            this.isDone = true;
                        }
                    });
                }
                break;
            }
        }
        postTurn();
    }

}