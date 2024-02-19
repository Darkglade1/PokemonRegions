package pokeregions.monsters.act3.allyPokemon;

import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Burn;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Solrock extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Solrock.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Solrock(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 130.0f, 145.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Solrock/Solrock.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.DEBUFF;
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
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new Burn(mo, pokeregions.cards.pokemonAllyCards.act3.Solrock.MOVE_1_EFFECT));
                }
                break;
            }
            case MOVE_2: {
                atb(new AddTemporaryHPAction(adp(), this, pokeregions.cards.pokemonAllyCards.act3.Solrock.MOVE_2_EFFECT));
                break;
            }
        }
        postTurn();
    }

}