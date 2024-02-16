package pokeregions.monsters.act3.allyPokemon;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.CustomIntent.IntentEnums;
import pokeregions.PokemonRegions;
import pokeregions.actions.AllyDamageAllEnemiesAction;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.LusterPurge;
import pokeregions.util.ProAudio;
import pokeregions.util.Wiz;
import pokeregions.vfx.ThrowEffect;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Latios extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Latios.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public Latios(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Latios/Latios.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK_DEBUFF;
        move2Intent = IntentEnums.MASS_ATTACK;
        addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act3.Latios.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent, pokeregions.cards.pokemonAllyCards.act3.Latios.MOVE_2_DAMAGE);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                float duration = 0.5f;
                atb(new VFXAction(ThrowEffect.throwEffect("PurpleSpike.png", 1.0f, this.hb, target.hb, Color.PURPLE.cpy(), duration, false, true), duration));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        Wiz.playAudio(ProAudio.MAGIC_BLAST, 1.0f);
                        this.isDone = true;
                    }
                });
                dmg(target, info, AbstractGameAction.AttackEffect.NONE);
                applyToTarget(target, this, new LusterPurge(target, pokeregions.cards.pokemonAllyCards.act3.Latios.MOVE_1_EFFECT, 1, AbstractDungeon.actionManager.turnHasEnded));
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                atb(new AllyDamageAllEnemiesAction(this, calcMassAttack(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.FIRE));
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new WeakPower(mo, pokeregions.cards.pokemonAllyCards.act3.Latios.MOVE_2_EFFECT, false));
                    applyToTarget(mo, this, new VulnerablePower(mo, pokeregions.cards.pokemonAllyCards.act3.Latios.MOVE_2_EFFECT, AbstractDungeon.actionManager.turnHasEnded));
                }
                break;
            }
        }
        postTurn();
    }

}