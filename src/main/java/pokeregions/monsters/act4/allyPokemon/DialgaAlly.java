package pokeregions.monsters.act4.allyPokemon;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.unique.ExpertiseAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act4.Dialga;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Rewind;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class DialgaAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Dialga.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public DialgaAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 180.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Dialga/Dialga.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.MAGIC;
        move2Intent = Intent.BUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_2;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(adp(), this, new Rewind(adp(), Dialga.MOVE_1_EFFECT));
                break;
            }
            case MOVE_2: {
                atb(new ExpertiseAction(adp(), BaseMod.MAX_HAND_SIZE));
                break;
            }
        }
        postTurn();
    }

}