package pokeregions.monsters.act3.allyPokemon;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.atb;
import static pokeregions.util.Wiz.dmg;

public class Metagross extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Metagross.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public Metagross(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 180.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Metagross/Metagross.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.BUFF;
        addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act3.Metagross.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_2;
        move1RequiresTarget = true;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        updateMoveHits();
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                }
                break;
            }
            case MOVE_2: {
                atb(new DrawCardAction(pokeregions.cards.pokemonAllyCards.act3.Metagross.MOVE_2_DRAW));
                break;
            }
        }
        postTurn();
    }

    @Override
    public void applyPowers() {
        updateMoveHits();
        super.applyPowers();
    }

    public void updateMoveHits() {
        if (this.nextMove == MOVE_1 && allyCard.currentStamina >= move1StaminaCost) {
            int numHits = 1;
            if (target != null) {
                for (AbstractPower power : target.powers) {
                    if (!(power instanceof InvisiblePower) && power.type == AbstractPower.PowerType.DEBUFF) {
                        numHits++;
                    }
                }
            }
            if (numHits == 1) {
                addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act3.Metagross.MOVE_1_DAMAGE);
                setMoveShortcut(MOVE_1);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentMultiAmt", -1);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "isMultiDmg", false);
            } else {
                addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act3.Metagross.MOVE_1_DAMAGE, numHits);
                setMoveShortcut(MOVE_1);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentMultiAmt", numHits);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "isMultiDmg", true);
            }
        }
    }
}