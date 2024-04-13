package pokeregions.monsters.act2.allyPokemon;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.Courage;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class Scizor extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Scizor.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public static final String POWER_ID = makeID("UTurn");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public Scizor(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Scizor/Scizor.scml"));
        this.animation.setFlip(true, false);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK_BUFF;
        move2Intent = Intent.ATTACK;
        addMove(MOVE_1, move1Intent, pokeregions.cards.pokemonAllyCards.act2.Scizor.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent, pokeregions.cards.pokemonAllyCards.act2.Scizor.MOVE_2_DAMAGE, 2);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        updateMoveFromCardsPlayed();
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                dmg(target, info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
                applyToTarget(adp(), this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, adp(), pokeregions.cards.pokemonAllyCards.act2.Scizor.MOVE_1_EFFECT, "retain", 99) {
                    @Override
                    public void onPokemonSwitch(AbstractMonster pokemon) {
                        pokemon.addPower(new Courage(pokemon, amount));
                        AbstractDungeon.onModifyPower();
                        atb(new RemoveSpecificPowerAction(owner, owner, this));
                    }

                    @Override
                    public void updateDescription() {
                        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
                    }
                });
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                }
                addMove(MOVE_2, move2Intent, pokeregions.cards.pokemonAllyCards.act2.Scizor.MOVE_2_DAMAGE, 2);
                break;
            }
        }
        postTurn();
    }

    @Override
    public void applyPowers() {
        updateMoveFromCardsPlayed();
        super.applyPowers();
    }

    public void updateMoveFromCardsPlayed() {
        if (this.nextMove == MOVE_2 && allyCard.currentStamina >= move1StaminaCost) {
            int totalAttacks = 2;
            for (AbstractCard card : AbstractDungeon.actionManager.cardsPlayedThisTurn) {
                if (card.type == AbstractCard.CardType.ATTACK) {
                    totalAttacks++;
                }
            }
            addMove(MOVE_2, move2Intent, pokeregions.cards.pokemonAllyCards.act2.Scizor.MOVE_2_DAMAGE, totalAttacks);
            setMoveShortcut(MOVE_2);
            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentMultiAmt", totalAttacks);
            ReflectionHacks.setPrivate(this, AbstractMonster.class, "isMultiDmg", true);
        }
    }
}