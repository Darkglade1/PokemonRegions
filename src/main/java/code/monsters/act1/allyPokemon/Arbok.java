package code.monsters.act1.allyPokemon;

import code.BetterSpriterAnimation;
import code.PokemonRegions;
import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import code.powers.ToxicPower;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.WeakPower;

import static code.PokemonRegions.makeMonsterPath;
import static code.util.Wiz.applyToTarget;
import static code.util.Wiz.dmg;

public class Arbok extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Arbok.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Arbok(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 150.0f, 160.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Arbok/Arbok.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.DEBUFF;
        move2Intent = Intent.ATTACK_DEBUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, code.cards.pokemonAllyCards.Arbok.MOVE_2_DAMAGE);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(target, this, new ToxicPower(target,  code.cards.pokemonAllyCards.Arbok.MOVE_1_TOXIC));
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                dmg(target, info, AbstractGameAction.AttackEffect.POISON);
                applyToTarget(target, this, new WeakPower(target, code.cards.pokemonAllyCards.Arbok.MOVE_2_WEAK, false));
                break;
            }
        }
        postTurn();
    }

}