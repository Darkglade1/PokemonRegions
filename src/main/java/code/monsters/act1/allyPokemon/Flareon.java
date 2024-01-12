package code.monsters.act1.allyPokemon;

import code.BetterSpriterAnimation;
import code.PokemonRegions;
import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

import static code.PokemonRegions.makeMonsterPath;
import static code.util.Wiz.*;

public class Flareon extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Flareon.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Flareon(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 130.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Flareon/Flareon.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.BUFF;
        addMove(MOVE_1, move1Intent, code.cards.pokemonAllyCards.Flareon.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_2;
        move1RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                dmg(target, info, AbstractGameAction.AttackEffect.FIRE);
                makeInHand(new Burn(), code.cards.pokemonAllyCards.Flareon.MOVE_1_BURN);
                break;
            }
            case MOVE_2: {
                atb(new DrawCardAction(code.cards.pokemonAllyCards.Flareon.MOVE_2_DRAW));
                atb(new ExhaustAction(code.cards.pokemonAllyCards.Flareon.MOVE_2_EXHAUST, false));
                break;
            }
        }
        postTurn();
    }

}