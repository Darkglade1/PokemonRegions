package code.monsters.act1.allyPokemon;

import basemod.ReflectionHacks;
import code.BetterSpriterAnimation;
import code.CustomIntent.IntentEnums;
import code.PokemonRegions;
import code.actions.AllyDamageAllEnemiesAction;
import code.actions.ZapdosMassAttackAction;
import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import code.util.Wiz;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.DiscardPileToTopOfDeckAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static code.PokemonRegions.makeMonsterPath;
import static code.util.Wiz.adp;
import static code.util.Wiz.atb;

public class Zapdos extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Zapdos.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public Zapdos(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 170.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Zapdos/Zapdos.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = IntentEnums.MASS_ATTACK;
        move2Intent = Intent.BUFF;
        int numEnemies = Wiz.getEnemies().size();
        if (numEnemies > 1) {
            addMove(MOVE_1, move1Intent, code.cards.pokemonAllyCards.Zapdos.MOVE_1_DAMAGE, numEnemies);
        } else {
            addMove(MOVE_1, move1Intent, code.cards.pokemonAllyCards.Zapdos.MOVE_1_DAMAGE);
        }
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_2;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                if (multiplier > 0) {
                    for(int i = 0; i < multiplier; ++i) {
                        AllyDamageAllEnemiesAction massAttack = new AllyDamageAllEnemiesAction(this, calcMassAttack(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE);
                        atb(new ZapdosMassAttackAction(massAttack));
                    }
                } else {
                    AllyDamageAllEnemiesAction massAttack = new AllyDamageAllEnemiesAction(this, calcMassAttack(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE);
                    atb(new ZapdosMassAttackAction(massAttack));
                }
                break;
            }
            case MOVE_2: {
                atb(new DiscardPileToTopOfDeckAction(adp()));
                break;
            }
        }
        postTurn();
    }

    @Override
    public void applyPowers() {
        if (allyCard.currentStamina >= move1StaminaCost) {
            int numEnemies = Wiz.getEnemies().size();
            if (numEnemies == 1) {
                addMove(MOVE_1, move1Intent, code.cards.pokemonAllyCards.Zapdos.MOVE_1_DAMAGE);
                if (this.nextMove == MOVE_1) {
                    setMoveShortcut(MOVE_1);
                    ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentMultiAmt", -1);
                    ReflectionHacks.setPrivate(this, AbstractMonster.class, "isMultiDmg", false);
                }
            } else {
                addMove(MOVE_1, move1Intent, code.cards.pokemonAllyCards.Zapdos.MOVE_1_DAMAGE, numEnemies);
                if (this.nextMove == MOVE_1) {
                    setMoveShortcut(MOVE_1);
                    ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentMultiAmt", numEnemies);
                    ReflectionHacks.setPrivate(this, AbstractMonster.class, "isMultiDmg", true);
                }
            }
        }
        super.applyPowers();
    }

}