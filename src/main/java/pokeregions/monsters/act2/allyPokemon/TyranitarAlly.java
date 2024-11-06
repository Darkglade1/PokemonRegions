package pokeregions.monsters.act2.allyPokemon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Tyranitar;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class TyranitarAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Tyranitar.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public TyranitarAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Tyranitar/Tyranitar.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.ATTACK_DEFEND;
        addMove(MOVE_1, move1Intent, Tyranitar.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent, Tyranitar.MOVE_2_DAMAGE);
        defaultMove = MOVE_2;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                if (AbstractDungeon.actionManager.cardsPlayedThisTurn.size() <= Tyranitar.MOVE_1_EFFECT) {
                    info.output *= 2;
                }
                dmg(target, info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                block(adp(), Tyranitar.MOVE_2_BLOCK);
                dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
        }
        postTurn();
    }

    @Override
    public void applyPowers(AbstractCreature target) {
        int multiplier = -1;
        if (this.nextMove == MOVE_1 && AbstractDungeon.actionManager.cardsPlayedThisTurn.size() <= Tyranitar.MOVE_1_EFFECT) {
            multiplier = 2;
        }
        applyPowers(target, multiplier);
    }
}