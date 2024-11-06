package pokeregions.monsters.act3.allyPokemon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.actions.GroudonExhaustDrawPileAction;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Groudon;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.atb;
import static pokeregions.util.Wiz.dmg;

public class GroudonAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Groudon.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public GroudonAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Groudon/Groudon.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.DEBUFF;
        move2Intent = Intent.ATTACK;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, Groudon.MOVE_2_DAMAGE, Groudon.MOVE_2_HITS);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                atb(new GroudonExhaustDrawPileAction(Groudon.MOVE_1_EXHAUST, true, target, this));
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                }
                break;
            }
        }
        postTurn();
    }

}