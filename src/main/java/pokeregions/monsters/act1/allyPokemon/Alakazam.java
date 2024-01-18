package pokeregions.monsters.act1.allyPokemon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Alakazam extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Alakazam.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Alakazam(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 135.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Alakazam/Alakazam.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK_DEBUFF;
        move2Intent = Intent.BUFF;
        addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.Alakazam.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                dmg(target, info, AbstractGameAction.AttackEffect.POISON);
                applyToTarget(target, this, new VulnerablePower(target, pokeregions.cards.pokemonAllyCards.Alakazam.MOVE_1_DEBUFF, AbstractDungeon.actionManager.turnHasEnded));
                break;
            }
            case MOVE_2: {
                atb(new ScryAction(pokeregions.cards.pokemonAllyCards.Alakazam.MOVE_2_SCRY));
                atb(new DrawCardAction(adp(), pokeregions.cards.pokemonAllyCards.Alakazam.MOVE_2_DRAW));
                break;
            }
        }
        postTurn();
    }

}