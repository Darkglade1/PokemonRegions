package pokeregions.monsters.act2.allyPokemon;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.BlurPower;
import com.megacrit.cardcrawl.powers.NextTurnBlockPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Piloswine;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class PiloswineAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Piloswine.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public PiloswineAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 140.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Piloswine/Piloswine.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

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
                applyToTarget(adp(), this, new NextTurnBlockPower(adp(), Piloswine.MOVE_1_BLOCK));
                break;
            }
            case MOVE_2: {
                block(adp(), Piloswine.MOVE_2_BLOCK);
                applyToTarget(adp(), this, new BlurPower(adp(), Piloswine.MOVE_2_EFFECT));
                break;
            }
        }
        postTurn();
    }
}