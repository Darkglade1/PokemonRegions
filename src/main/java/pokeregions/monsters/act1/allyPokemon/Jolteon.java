package pokeregions.monsters.act1.allyPokemon;

import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.EnergizedPower;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.applyToTarget;

public class Jolteon extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Jolteon.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Jolteon(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 130.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Jolteon/Jolteon.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = Intent.MAGIC;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(adp(), this, new DexterityPower(adp(),  pokeregions.cards.pokemonAllyCards.act1.Jolteon.MOVE_1_DEX));
                break;
            }
            case MOVE_2: {
                applyToTarget(adp(), this, new EnergizedPower(adp(),  pokeregions.cards.pokemonAllyCards.act1.Jolteon.MOVE_2_ENERGY));
                break;
            }
        }
        postTurn();
    }

}