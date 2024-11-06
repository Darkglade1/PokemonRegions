package pokeregions.monsters.act2.allyPokemon;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.watcher.SkipEnemiesTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.WhirlwindEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act2.Celebi;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.AbstractLambdaPower;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class CelebiAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Celebi.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public static final String POWER_ID = makeID("TimeWalk");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public CelebiAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 140.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Celebi/Celebi.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.MAGIC;
        move2Intent = Intent.BUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_2;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                applyToTarget(adp(), this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, adp(), 0, "time") {

                    @Override
                    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
                        this.flash();
                        atb(new VFXAction(new WhirlwindEffect(new Color(1.0F, 0.9F, 0.4F, 1.0F), true)));
                        atb(new SkipEnemiesTurnAction());
                        atb(new RemoveSpecificPowerAction(owner, owner, this));
                    }

                    @Override
                    public void updateDescription() {
                        this.description = POWER_DESCRIPTIONS[0];
                    }
                });

                break;
            }
            case MOVE_2: {
                atb(new DrawCardAction(Celebi.MOVE_2_EFFECT, new AbstractGameAction() {
                    @Override
                    public void update() {
                        int totalCost = 0;
                        for (AbstractCard c : DrawCardAction.drawnCards) {
                            if (c.costForTurn > 0) {
                                totalCost += c.costForTurn;
                            }
                        }
                        if (totalCost > 0) {
                            atb(new AddTemporaryHPAction(adp(), CelebiAlly.this, totalCost));
                        }
                        this.isDone = true;
                    }
                }));
                break;
            }
        }
        postTurn();
    }

}