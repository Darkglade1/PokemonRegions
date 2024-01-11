package code.monsters.act1.allyPokemon;

import code.BetterSpriterAnimation;
import code.PokemonRegions;
import code.actions.MagikarpDamageAction;
import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

import static code.PokemonRegions.makeMonsterPath;
import static code.util.Wiz.atb;

public class Magikarp extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Magikarp.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public boolean evolving = false;

    public Magikarp(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 130.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Magikarp/Magikarp.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.UNKNOWN;
        move2Intent = Intent.ATTACK;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, code.cards.pokemonAllyCards.Magikarp.MOVE_2_DAMAGE);
        defaultMove = MOVE_1;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                break;
            }
            case MOVE_2: {
                atb(new MagikarpDamageAction(target, info, allyCard, this));
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (!evolving) {
                    postTurn();
                }
                this.isDone = true;
            }
        });
    }

}