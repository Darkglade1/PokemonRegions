package pokeregions.monsters.act1.allyPokemon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.AnimateOrbAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.actions.defect.EvokeOrbAction;
import com.megacrit.cardcrawl.actions.defect.EvokeWithoutRemovingOrbAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.orbs.MewtwoDark;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.atb;

public class Mewtwo extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Mewtwo.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Mewtwo(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 150.0f, 130.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Mewtwo/Mewtwo.scml"));
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
                atb(new ChannelAction(new MewtwoDark(pokeregions.cards.pokemonAllyCards.act1.Mewtwo.MOVE_1_INCREASE)));
                break;
            }
            case MOVE_2: {
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        halfDead = true;
                        this.isDone = true;
                    }
                });
                for (int i = 0; i < pokeregions.cards.pokemonAllyCards.act1.Mewtwo.MOVE_2_EFFECT - 1; i++) {
                    atb(new AnimateOrbAction(1));
                    atb(new EvokeWithoutRemovingOrbAction(1));
                }
                atb(new AnimateOrbAction(1));
                atb(new EvokeOrbAction(1));
                break;
            }
        }
        postTurn();
    }

}