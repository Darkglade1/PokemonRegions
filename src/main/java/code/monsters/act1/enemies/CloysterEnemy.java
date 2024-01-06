package code.monsters.act1.enemies;

import basemod.ReflectionHacks;
import code.BetterSpriterAnimation;
import code.PokemonRegions;
import code.cards.pokemonAllyCards.Cloyster;
import code.monsters.AbstractPokemonAlly;
import code.monsters.AbstractPokemonMonster;
import code.powers.AbstractLambdaPower;
import code.util.Details;
import code.vfx.WaitEffect;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;

import java.util.ArrayList;

import static code.PokemonRegions.*;
import static code.PokemonRegions.DRAW_DOWN_TEXTURE;
import static code.util.Wiz.*;

public class CloysterEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(CloysterEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SHELL_SMASH = 0;
    private static final byte RAZOR_SHELL = 1;
    private static final byte ICICLE_SPEAR = 2;

    public final int METALLICIZE = 20;
    public final int METALLICIZE_LOSS = 5;
    public final int STR = 3;
    public final int DEBUFF = 1;
    public final int POWER_INITAL_HP_LOSS = 10;
    public final int HP_LOSS_INCREASE = 5;

    public static final String POWER_ID = makeID("CrackedShell");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public CloysterEnemy() {
        this(0.0f, 0.0f);
    }

    public CloysterEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 160.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Cloyster/Cloyster.scml"));
        setHp(calcAscensionTankiness(80));
        addMove(SHELL_SMASH, Intent.BUFF);
        addMove(RAZOR_SHELL, Intent.ATTACK_DEBUFF, calcAscensionDamage(8));
        addMove(ICICLE_SPEAR, Intent.ATTACK, calcAscensionDamage(5), 3);

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new MetallicizePower(this, METALLICIZE));
        block(this, METALLICIZE);
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, POWER_INITAL_HP_LOSS) {
            private boolean triggered;
            @Override
            public void wasHPLost(DamageInfo info, int damageAmount) {
                if (info.type == DamageInfo.DamageType.NORMAL && info.owner instanceof AbstractPokemonAlly && damageAmount > 0 && !triggered) {
                    this.flash();
                    triggered = true;
                    atb(new LoseHPAction(CloysterEnemy.this, CloysterEnemy.this, amount, AbstractGameAction.AttackEffect.POISON));
                    amount += HP_LOSS_INCREASE;
                    updateDescription();
                }
            }

            @Override
            public void atEndOfRound() {
                triggered = false;
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + HP_LOSS_INCREASE + POWER_DESCRIPTIONS[2];
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
            case SHELL_SMASH: {
                applyToTarget(this, this, new StrengthPower(this, STR));
                if (AbstractDungeon.ascensionLevel < 18) {
                    applyToTarget(this, this, new VulnerablePower(this, 1, true));
                }
                atb(new ReducePowerAction(this, this, MetallicizePower.POWER_ID, METALLICIZE_LOSS));
                break;
            }
            case RAZOR_SHELL: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                break;
            }
            case ICICLE_SPEAR: {
                runAnim("Spear");
                atb(new VFXAction(new WaitEffect(), 0.2f));
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(ICICLE_SPEAR)) {
            setMoveShortcut(SHELL_SMASH, MOVES[SHELL_SMASH]);
        } else if (lastMove(RAZOR_SHELL)) {
            setMoveShortcut(ICICLE_SPEAR, MOVES[ICICLE_SPEAR]);
        } else {
            setMoveShortcut(RAZOR_SHELL, MOVES[RAZOR_SHELL]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case SHELL_SMASH: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                if (AbstractDungeon.ascensionLevel < 18) {
                    Details vulnerableDetail = new Details(this, 1, VULNERABLE_TEXTURE, Details.TargetType.SELF);
                    details.add(vulnerableDetail);
                }
                Details metalDetail = new Details(this, -METALLICIZE_LOSS, METALLICIZE_TEXTURE, Details.TargetType.SELF);
                details.add(metalDetail);
                break;
            }
            case RAZOR_SHELL: {
                Details powerDetail = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, DEBUFF, FRAIL_TEXTURE);
                details.add(powerDetail2);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Cloyster();
    }

}