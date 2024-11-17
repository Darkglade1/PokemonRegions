package pokeregions.monsters.act2.allyPokemon;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.WeakPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Marshtomp;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class MarshtompAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Marshtomp.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public MarshtompAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 150.0f, 130.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Marshtomp/Marshtomp.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.DEFEND_DEBUFF;
        addMove(MOVE_1, move1Intent, Marshtomp.MOVE_1_DAMAGE);
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
                break;
            }
            case MOVE_2: {
                block(adp(), Marshtomp.MOVE_2_BLOCK);
                applyToTarget(target, this, new WeakPower(target, Marshtomp.MOVE_2_EFFECT, false));
                break;
            }
        }
        postTurn();
    }

}