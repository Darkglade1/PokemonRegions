package pokeregions.monsters.act1.allyPokemon;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.BetterSpriterAnimation;
import pokeregions.CustomIntent.IntentEnums;
import pokeregions.PokemonRegions;
import pokeregions.actions.AllyDamageAllEnemiesAction;
import pokeregions.actions.ShuffleDiscardPileBackAction;
import pokeregions.actions.ZapdosMassAttackAction;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.atb;

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
            addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act1.Zapdos.MOVE_1_DAMAGE, numEnemies);
        } else {
            addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act1.Zapdos.MOVE_1_DAMAGE);
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
                atb(new ShuffleDiscardPileBackAction(pokeregions.cards.pokemonAllyCards.Zapdos.MOVE_2_EFFECT, true));
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
                addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act1.Zapdos.MOVE_1_DAMAGE);
                if (this.nextMove == MOVE_1) {
                    setMoveShortcut(MOVE_1);
                    ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentMultiAmt", -1);
                    ReflectionHacks.setPrivate(this, AbstractMonster.class, "isMultiDmg", false);
                }
            } else {
                addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act1.Zapdos.MOVE_1_DAMAGE, numEnemies);
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