package pokeregions.monsters.act1.allyPokemon;

import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act1.Gastly;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class GastlyAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Gastly.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public GastlyAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
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
                atb(new DrawCardAction(Gastly.MOVE_1_DRAW, new AbstractGameAction() {
                    @Override
                    public void update() {
                        for (AbstractCard c : DrawCardAction.drawnCards) {
                            adp().hand.moveToExhaustPile(c);
                        }
                        this.isDone = true;
                    }
                }));
                atb(new AddTemporaryHPAction(adp(), this, Gastly.MOVE_1_EFFECT));
                break;
            }
            case MOVE_2: {
                atb(new LoseHPAction(adp(), this, Gastly.MOVE_2_HP_COST));
                applyToTarget(adp(), this, new IntangiblePlayerPower(adp(), Gastly.MOVE_2_INTANGIBLE));
                break;
            }
        }
        postTurn();
    }

}