package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Solrock;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.NastyPlot;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class SolrockEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(SolrockEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte CALM_MIND = 0;
    private static final byte POWER_GEM = 1;
    private static final byte HEAT_WAVE = 2;
    private static final byte MORNING_SUN_FURY = 3;

    public final int STR = calcAscensionSpecial(2);
    public final int BUFF = calcAscensionSpecialSmall(3);
    public final int STATUS = calcAscensionSpecial(3);
    public final int BLOCK = 8;

    public boolean advent = false;
    private LunatoneEnemy lunatone;

    public static final String POWER_ID = makeID("SunAdvent");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SolrockEnemy() {
        this(0.0f, 0.0f);
    }

    public SolrockEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 150.0f, 170.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Solrock/Solrock.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.2f);
        setHp(calcAscensionTankiness(106), calcAscensionTankiness(118));
        addMove(CALM_MIND, Intent.DEFEND_BUFF);
        addMove(POWER_GEM, Intent.ATTACK, calcAscensionDamage(16));
        addMove(HEAT_WAVE, Intent.STRONG_DEBUFF);
        addMove(MORNING_SUN_FURY, Intent.ATTACK_BUFF, calcAscensionDamage(26));
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof LunatoneEnemy) {
                lunatone = (LunatoneEnemy)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, STR) {

            @Override
            public void atEndOfRound() {
                if (advent) {
                    this.flash();
                    applyToTarget(owner, owner, new StrengthPower(owner, amount));
                }
            }

            @Override
            public void updateDescription() {
                if (advent) {
                    description = POWER_DESCRIPTIONS[1] + amount + POWER_DESCRIPTIONS[2];
                } else {
                    description = POWER_DESCRIPTIONS[0];
                }
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
            case CALM_MIND:
                block(this, BLOCK);
                applyToTarget(this, this, new NastyPlot(this, BUFF));
                break;
            case POWER_GEM:
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                break;
            case HEAT_WAVE:
                intoDrawMo(new Burn(), STATUS);
                break;
            case MORNING_SUN_FURY: {
                useFastAttackAnimation();
                atb(new VampireDamageAction(adp(), info, AbstractGameAction.AttackEffect.FIRE));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.advent) {
            if (this.lastMove(MORNING_SUN_FURY)) {
                setMoveShortcut(HEAT_WAVE, MOVES[HEAT_WAVE]);
            } else {
                setMoveShortcut(MORNING_SUN_FURY, MOVES[MORNING_SUN_FURY]);
            }
        } else {
            if (this.lastMove(POWER_GEM)) {
                setMoveShortcut(CALM_MIND, MOVES[CALM_MIND]);
            } else {
                setMoveShortcut(POWER_GEM, MOVES[POWER_GEM]);
            }
        }
        super.postGetMove();
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (lunatone != null && !lunatone.isDeadOrEscaped()) {
            lunatone.advent = true;
            AbstractPower power = lunatone.getPower(LunatoneEnemy.POWER_ID);
            if (power != null) {
                power.flash();
                power.updateDescription();
            }
        }
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case CALM_MIND: {
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                Details powerDetail = new Details(this, BUFF, NASTY_PLOT_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case HEAT_WAVE: {
                Details statusDetail = new Details(this, STATUS, BURN_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                break;
            }
            case MORNING_SUN_FURY: {
                Details moveDetail = new Details(this, Details.LIFESTEAL);
                details.add(moveDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Solrock();
    }

}