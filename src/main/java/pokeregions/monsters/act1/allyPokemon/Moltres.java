package pokeregions.monsters.act1.allyPokemon;

import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.actions.ExhaustDrawPileAction;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Burn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Moltres extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Moltres.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Moltres(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 170.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Moltres/Moltres.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK_DEBUFF;
        move2Intent = Intent.BUFF;
        addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.Moltres.MOVE_1_DAMAGE, pokeregions.cards.pokemonAllyCards.Moltres.MOVE_1_HITS);
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
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info, AbstractGameAction.AttackEffect.FIRE);
                }
                for (int i = 0; i < multiplier; i++) {
                    applyToTarget(target, this, new Burn(target,  pokeregions.cards.pokemonAllyCards.Moltres.MOVE_1_BURN));
                }
                break;
            }
            case MOVE_2: {
                atb(new ExhaustDrawPileAction(pokeregions.cards.pokemonAllyCards.Moltres.MOVE_2_EXHAUST, true));
                break;
            }
        }
        postTurn();
    }

}