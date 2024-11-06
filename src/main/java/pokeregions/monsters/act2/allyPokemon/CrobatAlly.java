package pokeregions.monsters.act2.allyPokemon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Crobat;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.ToxicPower;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class CrobatAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Crobat.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public static final String POWER_ID = makeID("PoisonFang");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public CrobatAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 170.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Crobat/Crobat.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = Intent.ATTACK_BUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, Crobat.MOVE_2_DAMAGE);
        defaultMove = MOVE_2;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(adp(), this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, adp(), 0, "envenom") {

                    @Override
                    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                        if (info.owner != target && info.type == DamageInfo.DamageType.NORMAL && damageAmount > 0) {
                            applyToTarget(target, owner, new ToxicPower(target, damageAmount));
                        }
                    }

                    @Override
                    public void onUseCard(AbstractCard card, UseCardAction action) {
                        if (card.type == AbstractCard.CardType.ATTACK) {
                            atb(new RemoveSpecificPowerAction(this.owner, this.owner, this));
                        }
                    }

                    @Override
                    public void updateDescription() {
                        description = POWER_DESCRIPTIONS[0];
                    }
                });
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                dmg(target, info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
                atb(new DrawCardAction(Crobat.MOVE_2_DRAW));
                break;
            }
        }
        postTurn();
    }
}