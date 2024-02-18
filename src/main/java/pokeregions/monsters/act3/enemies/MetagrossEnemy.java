package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Salamence;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;
import pokeregions.vfx.ExplosionEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class MetagrossEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(MetagrossEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte METEOR_MASH = 0;
    private static final byte HONE_CLAWS = 1;
    private static final byte PREPARE = 2;
    private static final byte EXPLODE = 3;

    public final int STR = calcAscensionSpecial(2);
    public final int INITIAL_ARTIFACT = 2;

    public final int HP_THRESHOLD = 30;
    private final int hpThreshold;
    private boolean flashedPowerThisTurn = false;
    private boolean exploding = false;

    public static final String POWER_ID = makeID("Analytic");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public MetagrossEnemy() {
        this(0.0f, 0.0f);
    }

    public MetagrossEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 230.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Metagross/Metagross.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.3f);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        setHp(calcAscensionTankiness(260));
        addMove(METEOR_MASH, Intent.ATTACK, calcAscensionDamage(12), 2);
        addMove(HONE_CLAWS, Intent.BUFF);
        addMove(PREPARE, Intent.UNKNOWN);
        addMove(EXPLODE, Intent.ATTACK, calcAscensionDamage(50));
        this.hpThreshold = calculateHPThreshold();
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new ArtifactPower(this, INITIAL_ARTIFACT));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, 0, "amplify") {

            @Override
            public void atEndOfRound() {
                flashedPowerThisTurn = false;
                if (owner instanceof AbstractMonster) {
                    atb(new RollMoveAction((AbstractMonster) owner));
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case METEOR_MASH: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                }
                break;
            }
            case HONE_CLAWS: {
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
            }
            case PREPARE: {
                makePowerRemovable(this, POWER_ID);
                atb(new RemoveSpecificPowerAction(this, this, POWER_ID));
                exploding = true;
                break;
            }
            case EXPLODE: {
                atb(new VFXAction(new ExplosionEffect(this.hb.cX, this.hb.cY), 0.1F));
                dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                atb(new SuicideAction(this));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    private boolean underHPThreshold() {
        return this.currentHealth < hpThreshold;
    }

    private int calculateHPThreshold() {
        return Math.round(this.maxHealth * ((float)HP_THRESHOLD / 100));
    }

    @Override
    protected void getMove(final int num) {
        if (exploding) {
            setMoveShortcut(EXPLODE, MOVES[EXPLODE]);
        } else if (this.getIntentBaseDmg() >= 0) {
            this.info = getInfoFromMove(METEOR_MASH);
            this.multiplier = getMultiplierFromMove(METEOR_MASH);
            if(info.base > -1) {
                info.applyPowers(this, adp());
            }
            int moDamage = info.output * multiplier;
            if (moDamage <= adp().currentBlock) {
                AbstractPower power = this.getPower(POWER_ID);
                if (power != null && !flashedPowerThisTurn) {
                    flashedPowerThisTurn = true;
                    power.flash();
                }
                if (underHPThreshold()) {
                    setMoveShortcut(PREPARE, MOVES[PREPARE]);
                } else {
                    setMoveShortcut(HONE_CLAWS, MOVES[HONE_CLAWS]);
                }
                createIntent();
            }
        } else if (AbstractDungeon.actionManager.turnHasEnded || firstMove) {
            setMoveShortcut(METEOR_MASH, MOVES[METEOR_MASH]);
        }
        super.postGetMove();
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        atb(new RollMoveAction(this));
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case HONE_CLAWS: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case EXPLODE: {
                Details diesDetail = new Details(this, Details.DIES);
                details.add(diesDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Salamence();
    }

}