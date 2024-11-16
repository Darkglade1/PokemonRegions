package pokeregions.monsters.act3.allyPokemon;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;
import com.megacrit.cardcrawl.powers.EnergizedPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Sceptile;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class SceptileAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Sceptile.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public SceptileAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Sceptile/Sceptile.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = Intent.ATTACK_BUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, Sceptile.MOVE_2_DAMAGE);
        defaultMove = MOVE_1;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                AbstractCard shiv = Sceptile.getShiv().makeStatEquivalentCopy();
                makeInHand(shiv, Sceptile.MOVE_1_EFFECT);
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                dmg(target, info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                applyToTarget(adp(), this, new EnergizedPower(adp(), Sceptile.MOVE_2_ENERGY));
                applyToTarget(adp(), this, new DrawCardNextTurnPower(adp(), Sceptile.MOVE_2_DRAW));
                break;
            }
        }
        postTurn();
    }

}