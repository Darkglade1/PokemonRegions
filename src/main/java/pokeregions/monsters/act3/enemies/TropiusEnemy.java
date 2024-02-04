package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Aggron;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.NastyPlot;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;
import pokeregions.vfx.ColoredLaserBeamEffect;
import pokeregions.vfx.SunBeamEffect;
import pokeregions.vfx.SunEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class TropiusEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(TropiusEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SUNNY_DAY = 0;
    private static final byte SOLAR_BEAM = 1;
    private static final byte SEED_BOMB = 2;

    public final int BUFF = calcAscensionSpecial(2);
    public final int HP_LOSS_PERCENT = 5;
    public final int STATUS = calcAscensionSpecial(2);

    public static final String POWER_ID = makeID("SolarPower");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public TropiusEnemy() {
        this(0.0f, 0.0f);
    }

    public TropiusEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 220.0f, 230.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Tropius/Tropius.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.3f);
        setHp(calcAscensionTankiness(130), calcAscensionTankiness(138));
        addMove(SUNNY_DAY, Intent.BUFF);
        addMove(SOLAR_BEAM, Intent.ATTACK, calcAscensionDamage(20));
        addMove(SEED_BOMB, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SUNNY_DAY: {
                applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, BUFF) {

                    private boolean justApplied = true;

                    @Override
                    public void atEndOfRound() {
                        if (justApplied) {
                            justApplied = false;
                        } else {
                            this.flash();
                            applyToTarget(owner, owner, new NastyPlot(owner, amount));
                            atb(new LoseHPAction(owner, owner, (int)(owner.maxHealth * ((float)HP_LOSS_PERCENT / 100))));
                        }
                    }

                    @Override
                    public void updateDescription() {
                        description = POWER_DESCRIPTIONS[0] + (amount * 10) + POWER_DESCRIPTIONS[1] + HP_LOSS_PERCENT + POWER_DESCRIPTIONS[2];
                    }
                });
                break;
            }
            case SOLAR_BEAM: {
                atb(new VFXAction(new ColoredLaserBeamEffect(this.hb.cX, this.hb.cY + 60.0F * Settings.scale, Color.GREEN.cpy()), 1.5F));
                dmg(adp(), info);
                break;
            }
            case SEED_BOMB: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.POISON);
                intoDrawMo(new Slimed(), STATUS);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(SUNNY_DAY, MOVES[SUNNY_DAY]);
        } else if (!this.lastMove(SOLAR_BEAM)){
            setMoveShortcut(SOLAR_BEAM, MOVES[SOLAR_BEAM]);
        } else {
            setMoveShortcut(SEED_BOMB, MOVES[SEED_BOMB]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        String textureString = makePowerPath("SolarPower32.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case SUNNY_DAY: {
                Details powerDetail = new Details(this, BUFF, texture);
                details.add(powerDetail);
                break;
            }
            case SEED_BOMB: {
                Details statusDetail = new Details(this, STATUS, SLIMED_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Aggron();
    }

    boolean sunRayCycle = false;
    float particleTimer = 0.0f;
    float secondParticleTimer = 0.0F;
    AbstractGameEffect sun;
    @Override
    public void update() {
        super.update();
        if (this.hasPower(POWER_ID)) {
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
    }

}