package pokeregions.monsters.act3.enemies;

import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.cards.pokemonAllyCards.act3.Slaking;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class SlakingEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(SlakingEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SLEEPING = 0;
    private static final byte SLACK_OFF = 1;
    private static final byte GIGA_IMPACT = 2;
    private static final byte HAMMER_ARM = 3;
    private static final byte GROGGY = 4;

    public final int STR = 3;
    public final int TEMP_HP = calcAscensionSpecial(30);
    public final int TEMP_HP_GAIN = 10;
    public int GIGA_IMPACT_COOLDOWN = 4;
    public final int GIGA_IMPACT_DAMAGE_INCREASE = calcAscensionSpecial(5);
    public final int MAX_SLEEP = 3;

    public static final String POWER_ID = makeID("Laziness");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int sleepTurns = 0;
    private int cooldown = GIGA_IMPACT_COOLDOWN;

    public SlakingEnemy() {
        this(0.0f, 0.0f);
    }

    public SlakingEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 230.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Slaking/Slaking.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.5f);
        setHp(calcAscensionTankiness(400));
        addMove(SLEEPING, Intent.SLEEP);
        addMove(SLACK_OFF, Intent.UNKNOWN);
        addMove(HAMMER_ARM, Intent.ATTACK, calcAscensionDamage(19));
        addMove(GIGA_IMPACT, Intent.ATTACK, calcAscensionDamage(30));
        addMove(GROGGY, Intent.STUN);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, MAX_SLEEP) {

            @Override
            public int onAttacked(DamageInfo info, int damageAmount) {
                if (damageAmount > 0) {
                    makePowerRemovable(this);
                    atb(new RemoveSpecificPowerAction(owner, owner, this));
                    if (owner instanceof SlakingEnemy) {
                        ((SlakingEnemy) owner).setMoveShortcut(GROGGY, MOVES[GROGGY]);
                        ((SlakingEnemy) owner).createIntent();
                        if (sleepTurns > 0) {
                            applyToTarget(owner, owner, new StrengthPower(owner, STR * sleepTurns));
                        }
                    }
                }
                return damageAmount;
            }

            @Override
            public void duringTurn() {
                amount--;
                if (amount <= 0) {
                    makePowerRemovable(this);
                    atb(new RemoveSpecificPowerAction(owner, owner, this));
                    if (owner instanceof AbstractMonster) {
                        atb(new RollMoveAction((AbstractMonster) owner));
                        applyToTarget(owner, owner, new StrengthPower(owner, STR * sleepTurns));
                    }
                }
                updateDescription();
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
        atb(new AddTemporaryHPAction(this, this, TEMP_HP));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SLEEPING:
                atb(new AddTemporaryHPAction(this, this, TEMP_HP_GAIN));
                sleepTurns++;
                break;
            case SLACK_OFF:
            case GROGGY:
                break;
            case GIGA_IMPACT: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                cooldown = GIGA_IMPACT_COOLDOWN - sleepTurns + 1;
                int newDamage = moves.get(GIGA_IMPACT).baseDamage += GIGA_IMPACT_DAMAGE_INCREASE;
                addMove(GIGA_IMPACT, Intent.ATTACK, newDamage);
                break;
            }
            case HAMMER_ARM: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                break;
            }
        }
        cooldown--;
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.hasPower(POWER_ID) || firstMove) {
            setMoveShortcut(SLEEPING, MOVES[SLEEPING]);
        } else if (!this.lastMove(GIGA_IMPACT) && !this.lastMove(HAMMER_ARM)) {
            if (cooldown <= 0) {
                setMoveShortcut(GIGA_IMPACT, MOVES[GIGA_IMPACT]);
            } else {
                setMoveShortcut(HAMMER_ARM, MOVES[HAMMER_ARM]);
            }
        } else {
            setMoveShortcut(SLACK_OFF, MOVES[SLACK_OFF]);
        }
        super.postGetMove();
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Slaking();
    }

}