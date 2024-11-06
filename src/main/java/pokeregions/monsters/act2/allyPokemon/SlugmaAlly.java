package pokeregions.monsters.act2.allyPokemon;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Slugma;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Burn;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class SlugmaAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Slugma.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public SlugmaAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 130.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Slugma/Slugma.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.DEBUFF;
        move2Intent = Intent.DEFEND;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(target, this, new Burn(target, Slugma.MOVE_1_EFFECT));
                break;
            }
            case MOVE_2: {
                block(adp(), Slugma.MOVE_2_BLOCK);
                makeInHand(new com.megacrit.cardcrawl.cards.status.Burn(), Slugma.MOVE_2_STATUS);
                break;
            }
        }
        postTurn();
    }

}