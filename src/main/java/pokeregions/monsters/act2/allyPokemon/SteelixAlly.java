package pokeregions.monsters.act2.allyPokemon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.DrawPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Steelix;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class SteelixAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Steelix.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public SteelixAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Steelix/Steelix.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 0.7f);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.BUFF;
        addMove(MOVE_1, move1Intent, Steelix.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        addMove(MOVE_1, move1Intent, Steelix.MOVE_1_DAMAGE + (int)(adp().currentBlock * ((float)Steelix.MOVE_1_EFFECT / 100)));
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
                applyToTarget(adp(), this, new DrawPower(adp(), Steelix.MOVE_2_EFFECT));
                break;
            }
        }
        postTurn();
    }

    @Override
    public void applyPowers(AbstractCreature target) {
        addMove(MOVE_1, move1Intent, Steelix.MOVE_1_DAMAGE + (int)(adp().currentBlock * ((float)Steelix.MOVE_1_EFFECT / 100)));
        applyPowers(target, -1);
    }

}