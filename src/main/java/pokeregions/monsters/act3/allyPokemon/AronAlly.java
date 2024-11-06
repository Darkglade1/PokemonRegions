package pokeregions.monsters.act3.allyPokemon;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Aron;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class AronAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Aron.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public AronAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 100.0f, 70.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Aron/Aron.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = Intent.DEFEND_BUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_2;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(adp(), this, new ArtifactPower(adp(), Aron.MOVE_1_EFFECT));
                break;
            }
            case MOVE_2: {
                block(adp(), Aron.MOVE_2_BLOCK);
                applyToTarget(adp(), this, new ThornsPower(adp(), Aron.MOVE_2_BUFF));
                break;
            }
        }
        postTurn();
    }

}