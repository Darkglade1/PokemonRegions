package code.monsters.act1.allyPokemon;

import code.BetterSpriterAnimation;
import code.CustomIntent.IntentEnums;
import code.PokemonRegions;
import code.actions.AllyDamageAllEnemiesAction;
import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

import static code.PokemonRegions.makeMonsterPath;
import static code.util.Wiz.atb;
import static code.util.Wiz.dmg;

public class Charmander extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Charmander.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Charmander(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 150.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Charmander/Charmander.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = IntentEnums.MASS_ATTACK;
        addMove(MOVE_1, Intent.ATTACK, allyCard.damage);
        addMove(MOVE_2, IntentEnums.MASS_ATTACK, allyCard.secondDamage);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (this.nextMove) {
            case MOVE_1: {
                dmg(target, info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
                break;
            }
            case MOVE_2: {
                // add vfx of shooting fire bolts at each enemy
                atb(new AllyDamageAllEnemiesAction(this, calcMassAttack(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.FIRE));
                break;
            }
        }
        postTurn();
    }

}