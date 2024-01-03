package code.monsters.act1.allyPokemon;

import code.BetterSpriterAnimation;
import code.PokemonRegions;
import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import code.powers.Burn;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

import static code.PokemonRegions.makeMonsterPath;
import static code.util.Wiz.*;

public class Vulpix extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Vulpix.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Vulpix(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 150.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Vulpix/Vulpix.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK_DEBUFF;
        move2Intent = Intent.ATTACK;
        addMove(MOVE_1, move1Intent, code.cards.pokemonAllyCards.Vulpix.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent, code.cards.pokemonAllyCards.Vulpix.MOVE_2_DAMAGE, code.cards.pokemonAllyCards.Vulpix.MOVE_2_HITS);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (this.nextMove) {
            case MOVE_1: {
                dmg(target, info, AbstractGameAction.AttackEffect.FIRE);
                applyToTarget(target, this, new Burn(target,  code.cards.pokemonAllyCards.Vulpix.MOVE_1_BURN));
                break;
            }
            case MOVE_2: {
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info, AbstractGameAction.AttackEffect.FIRE);
                }
                break;
            }
        }
        postTurn();
    }

}