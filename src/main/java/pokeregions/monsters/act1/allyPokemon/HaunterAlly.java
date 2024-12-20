package pokeregions.monsters.act1.allyPokemon;

import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act1.Haunter;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Burn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class HaunterAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Haunter.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public HaunterAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Haunter/Haunter.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.DEBUFF;
        move2Intent = Intent.ATTACK;
        addMove(MOVE_1, move1Intent);
        updateMoveFromStamina();
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        updateMoveFromStamina();
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(target, this, new VulnerablePower(target, Haunter.MOVE_1_DEBUFF, AbstractDungeon.actionManager.turnHasEnded));
                applyToTarget(target, this, new Burn(target, Haunter.MOVE_1_DEBUFF));
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info, AbstractGameAction.AttackEffect.POISON);
                }
                break;
            }
        }
        postTurn();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                updateMoveFromStamina();
                if (HaunterAlly.this.nextMove == MOVE_2) {
                    setMoveShortcut(MOVE_2);
                    createIntent();
                }
                this.isDone = true;
            }
        });
    }

    public void updateMoveFromStamina() {
        if (allyCard.currentStamina == 1) {
            addMove(MOVE_2, move2Intent, Haunter.MOVE_2_DAMAGE);
        } else {
            addMove(MOVE_2, move2Intent, Haunter.MOVE_2_DAMAGE, allyCard.currentStamina);
        }
    }

}