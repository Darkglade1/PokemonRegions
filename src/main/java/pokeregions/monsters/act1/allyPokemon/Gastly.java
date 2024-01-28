package pokeregions.monsters.act1.allyPokemon;

import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.actions.UpdateStaminaOnCardAction;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Gastly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Gastly.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Gastly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Gastly/Gastly.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.MAGIC;
        move2Intent = Intent.BUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                atb(new LoseHPAction(adp(), this, pokeregions.cards.pokemonAllyCards.act1.Gastly.MOVE_1_HP_COST));
                atb(new UpdateStaminaOnCardAction(allyCard, pokeregions.cards.pokemonAllyCards.act1.Gastly.MOVE_1_STAMINA));
                atb(new HealAction(this, this, pokeregions.cards.pokemonAllyCards.act1.Gastly.MOVE_1_STAMINA));
                break;
            }
            case MOVE_2: {
                atb(new LoseHPAction(adp(), this, pokeregions.cards.pokemonAllyCards.act1.Gastly.MOVE_2_HP_COST));
                applyToTarget(adp(), this, new IntangiblePlayerPower(adp(), pokeregions.cards.pokemonAllyCards.act1.Gastly.MOVE_2_INTANGIBLE));
                break;
            }
        }
        postTurn();
    }

}