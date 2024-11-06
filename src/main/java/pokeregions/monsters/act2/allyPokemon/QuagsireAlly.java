package pokeregions.monsters.act2.allyPokemon;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.BufferPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.CustomIntent.IntentEnums;
import pokeregions.PokemonRegions;
import pokeregions.actions.AllyDamageAllEnemiesAction;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Quagsire;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Burn;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class QuagsireAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Quagsire.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public QuagsireAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 140.0f, 130.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Quagsire/Quagsire.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.DEFEND_BUFF;
        move2Intent = IntentEnums.MASS_ATTACK;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, Quagsire.MOVE_2_DAMAGE);
        defaultMove = MOVE_2;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(adp(), this, new BufferPower(adp(), 1));
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                atb(new AllyDamageAllEnemiesAction(this, calcMassAttack(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.FIRE));
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new WeakPower(mo, Quagsire.MOVE_2_EFFECT, false));
                    applyToTarget(mo, this, new Burn(mo, Quagsire.MOVE_2_EFFECT));
                }
                break;
            }
        }
        postTurn();
    }

}