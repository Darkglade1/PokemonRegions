package pokeregions.monsters.act3.allyPokemon;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Registeel;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class RegisteelAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Registeel.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public RegisteelAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Registeel/Registeel.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.DEFEND;
        move2Intent = Intent.DEFEND_BUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                block(adp(), Registeel.MOVE_1_BLOCK);
                break;
            }
            case MOVE_2: {
                applyToTarget(adp(), this, new PlatedArmorPower(adp(), Registeel.MOVE_2_EFFECT));
                break;
            }
        }
        postTurn();
    }

}