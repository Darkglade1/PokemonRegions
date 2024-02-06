package pokeregions.monsters.act3.enemies;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.cardMods.ScorchedMod;
import pokeregions.cards.pokemonAllyCards.act3.Groudon;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;
import pokeregions.vfx.SunBeamEffect;
import pokeregions.vfx.SunEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class GroudonEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(GroudonEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SWORDS = 0;
    private static final byte BLADES = 1;
    private static final byte SCORCH = 2;

    public final int STR = 6;
    public final int DEBUFF = 2;
    public final int SUN = 1;

    public static final String POWER_ID = makeID("HarshSunlight");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public GroudonEnemy() {
        this(0.0f, 0.0f);
    }

    public GroudonEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 300.0f, 280.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Groudon/Groudon.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 2.0f);
        setHp(calcAscensionTankiness(500));
        addMove(SWORDS, Intent.BUFF);
        addMove(BLADES, Intent.ATTACK, calcAscensionDamage(13), 2);
        addMove(SCORCH, Intent.STRONG_DEBUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, SUN) {
            private int cardsAffected = 0;

            @Override
            public void onCardDraw(AbstractCard card) {
                if (cardsAffected < amount) {
                    CardModifierManager.addModifier(card, new ScorchedMod());
                    card.flash();
                    cardsAffected++;
                }
            }

            @Override
            public void atEndOfRound() {
                cardsAffected = 0;
            }

            @Override
            public void updateDescription() {
                if (amount == 1) {
                    description = POWER_DESCRIPTIONS[0];
                } else {
                    description = POWER_DESCRIPTIONS[1] + amount + POWER_DESCRIPTIONS[2];
                }
            }
        });
        CustomDungeon.playTempMusicInstantly("Lysandre");
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SWORDS: {
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
            }
            case BLADES: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                }
                break;
            }
            case SCORCH: {
                applyToTarget(adp(), this, new VulnerablePower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                if (AbstractDungeon.ascensionLevel >= 19) {
                    applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(SWORDS)) {
            setMoveShortcut(BLADES, MOVES[BLADES]);
        } else if (this.lastMove(BLADES)) {
            if (this.lastMoveBefore(BLADES)) {
                setMoveShortcut(SWORDS, MOVES[SWORDS]);
            } else if (this.lastMoveBefore(SCORCH)) {
                setMoveShortcut(BLADES, MOVES[BLADES]);
            } else {
                setMoveShortcut(SCORCH, MOVES[SCORCH]);
            }
        } else if (this.lastMove(SCORCH)) {
            setMoveShortcut(BLADES, MOVES[BLADES]);
        } else {
            setMoveShortcut(SWORDS, MOVES[SWORDS]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case SWORDS: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case SCORCH: {
                Details powerDetail = new Details(this, DEBUFF, VULNERABLE_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail2);
                if (AbstractDungeon.ascensionLevel >= 19) {
                    Details powerDetail3 = new Details(this, DEBUFF, FRAIL_TEXTURE);
                    details.add(powerDetail3);
                }
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        onBossVictoryLogic();
        onFinalBossVictoryLogic();
    }

    boolean sunRayCycle = false;
    float particleTimer = 0.0f;
    float secondParticleTimer = 0.0F;
    AbstractGameEffect sun;
    @Override
    public void update() {
        super.update();
        this.particleTimer -= Gdx.graphics.getDeltaTime();
        if (this.sunRayCycle) {
            this.secondParticleTimer -= Gdx.graphics.getDeltaTime();
            if (this.secondParticleTimer < 0.0F) {
                AbstractDungeon.effectsQueue.add(new SunBeamEffect());
                this.secondParticleTimer = MathUtils.random(2.0F, 3.5F);
            }
        }

        if (this.particleTimer < 0.0F) {
            this.particleTimer = 0.3F;
            if (!this.sunRayCycle) {
                this.sun = new SunEffect();
                AbstractDungeon.effectsQueue.add(this.sun);
                this.secondParticleTimer = MathUtils.random(1.0F, 1.5F);
                this.sunRayCycle = true;
            }
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Groudon();
    }

}