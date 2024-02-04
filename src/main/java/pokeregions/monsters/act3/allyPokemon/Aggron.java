package pokeregions.monsters.act3.allyPokemon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.ThornsPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Aggron extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Aggron.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public Aggron(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Aggron/Aggron.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.DEFEND_BUFF;
        addMove(MOVE_1, move1Intent, 0);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        addMove(MOVE_1, move1Intent, adp().currentBlock);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
            case MOVE_2: {
                block(adp(), pokeregions.cards.pokemonAllyCards.act3.Aggron.MOVE_2_BLOCK);
                applyToTarget(adp(), this, new ThornsPower(adp(), pokeregions.cards.pokemonAllyCards.act3.Aggron.MOVE_2_BUFF));
                break;
            }
        }
        postTurn();
    }

    @Override
    public void applyPowers(AbstractCreature target) {
        addMove(MOVE_1, move1Intent, adp().currentBlock);
        applyPowers(target, -1);
    }

}