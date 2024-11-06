package pokeregions.monsters.act3.allyPokemon;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Kyogre;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Drowning;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class KyogreAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Kyogre.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public KyogreAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Kyogre/Kyogre.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = Intent.DEBUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_2;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                atb(new HealAction(adp(), adp(), Kyogre.MOVE_1_HEAL));
                break;
            }
            case MOVE_2: {
                applyToTarget(target, this, new Drowning(target, Kyogre.MOVE_2_DEBUFF));
                break;
            }
        }
        postTurn();
    }

}