package pokeregions.monsters.act2.allyPokemon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Pupitar;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class PupitarAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Pupitar.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public PupitarAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Pupitar/Pupitar.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = Intent.ATTACK_DEFEND;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, Pupitar.MOVE_2_DAMAGE);
        defaultMove = MOVE_2;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                atb(new DrawCardAction(Pupitar.MOVE_1_DRAW));
                atb(new DiscardAction(adp(), this, Pupitar.MOVE_1_DISCARD, false));
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                block(adp(), Pupitar.MOVE_2_BLOCK);
                dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
        }
        postTurn();
    }
}