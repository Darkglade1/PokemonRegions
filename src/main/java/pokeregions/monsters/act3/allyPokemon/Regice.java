package pokeregions.monsters.act3.allyPokemon;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Regice extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Regice.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public Regice(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Regice/Regice.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.DEFEND;
        move2Intent = Intent.DEBUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                block(adp(), pokeregions.cards.pokemonAllyCards.act3.Regice.MOVE_1_BLOCK);
                break;
            }
            case MOVE_2: {
                applyToTarget(target, this, new WeakPower(target, pokeregions.cards.pokemonAllyCards.act3.Regice.MOVE_2_EFFECT, false));
                applyToTarget(target, this, new VulnerablePower(target, pokeregions.cards.pokemonAllyCards.act3.Regice.MOVE_2_EFFECT, AbstractDungeon.actionManager.turnHasEnded));
                break;
            }
        }
        postTurn();
    }

}