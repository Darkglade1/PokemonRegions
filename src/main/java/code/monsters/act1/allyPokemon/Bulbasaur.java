package code.monsters.act1.allyPokemon;

import code.BetterSpriterAnimation;
import code.PokemonRegions;
import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import code.powers.ToxicPower;
import code.vfx.WaitEffect;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.WeakPower;

import static code.PokemonRegions.makeMonsterPath;
import static code.util.Wiz.*;

public class Bulbasaur extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Bulbasaur.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Bulbasaur(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 150.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Bulbasaur/Bulbasaur.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.DEBUFF;
        addMove(MOVE_1, move1Intent, code.cards.pokemonAllyCards.Bulbasaur.MOVE_1_DAMAGE);
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
                runAnim("Whip");
                atb(new VFXAction(new WaitEffect(), 0.2f));
                dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                break;
            }
            case MOVE_2: {
                runAnim("Debuff");
                applyToTarget(target, this, new ToxicPower(target,  code.cards.pokemonAllyCards.Bulbasaur.MOVE_2_TOXIC));
                applyToTarget(target, this, new WeakPower(target, code.cards.pokemonAllyCards.Bulbasaur.MOVE_2_WEAK, false));
                break;
            }
        }
        postTurn();
    }

}