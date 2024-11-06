package pokeregions.monsters.act2.allyPokemon;

import basemod.cardmods.RetainMod;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Phanpy;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.atb;

public class PhanpyAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Phanpy.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public PhanpyAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 130.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Phanpy/Phanpy.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.UNKNOWN;
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
                atb(new ObtainPotionAction(AbstractDungeon.returnRandomPotion(true)));
                break;
            }
            case MOVE_2: {
                atb(new DrawCardAction(Phanpy.MOVE_2_EFFECT, new AbstractGameAction() {
                    @Override
                    public void update() {
                        for (AbstractCard c : DrawCardAction.drawnCards) {
                            CardModifierManager.addModifier(c, new RetainMod());
                        }
                        this.isDone = true;
                    }
                }));
                break;
            }
        }
        postTurn();
    }
}