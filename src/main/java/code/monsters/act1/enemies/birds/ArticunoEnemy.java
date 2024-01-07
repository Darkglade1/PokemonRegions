package code.monsters.act1.enemies.birds;

import basemod.ReflectionHacks;
import code.BetterSpriterAnimation;
import code.PokemonRegions;
import code.cards.Frozen;
import code.cards.pokemonAllyCards.Articuno;
import code.monsters.AbstractPokemonMonster;
import code.powers.AbstractLambdaPower;
import code.powers.MonsterNextTurnBlockPower;
import code.util.Details;
import code.util.Wiz;
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
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

import java.util.ArrayList;

import static code.PokemonRegions.*;
import static code.util.Wiz.*;

public class ArticunoEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(ArticunoEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte POWDER_SNOW = 0;
    private static final byte SNOWSCAPE = 1;
    private static final byte ICE_SHARD = 2;

    public final int BLOCK = 6;
    public final int SOLO_BLOCK = 12;
    public final int DEBUFF = 1;
    public final int STATUS = calcAscensionSpecial(2);
    public final int POWER_STR = 2;
    private MoltresEnemy moltres;
    private ZapdosEnemy zapdos;
    private AbstractPower retributionPower;

    public static final String POWER_ID = makeID("RetributionArticuno");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ArticunoEnemy() {
        this(0.0f, 0.0f);
    }

    public ArticunoEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0.0f, 250.0f, 260.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Articuno/Articuno.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.5f);
        setHp(calcAscensionTankiness(100));
        addMove(POWDER_SNOW, Intent.DEBUFF);
        addMove(SNOWSCAPE, Intent.DEFEND_DEBUFF);
        addMove(ICE_SHARD, Intent.ATTACK, 2, 3);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        for (AbstractMonster mo :Wiz.getEnemies()) {
            if (mo instanceof MoltresEnemy) {
                moltres = (MoltresEnemy) mo;
            }
            if (mo instanceof ZapdosEnemy) {
                zapdos = (ZapdosEnemy) mo;
            }
        }
        retributionPower = new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, POWER_STR) {

            @Override
            public void onSpecificTrigger() {
                flash();
                applyToTargetNextTurn(owner, owner, new StrengthPower(owner, amount));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        };
        applyToTarget(this, this, retributionPower);
    }

    public void birdsDead() {
        retributionPower.onSpecificTrigger();
        setDetailedIntents();
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case POWDER_SNOW: {
                intoDiscardMo(new Frozen(), STATUS);
                break;
            }
            case SNOWSCAPE: {
                if (moltres.isDeadOrEscaped() && zapdos.isDeadOrEscaped()) {
                    block(this, SOLO_BLOCK);
                } else {
                    for (AbstractMonster mo : Wiz.getEnemies()) {
                        applyToTarget(mo, this, new MonsterNextTurnBlockPower(mo, BLOCK));
                    }
                }
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                break;
            }
            case ICE_SHARD: {
                useFastAttackAnimation();
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
        if (!firstMove && moltres.isDeadOrEscaped() && zapdos.isDeadOrEscaped()) {
            if (lastMove(ICE_SHARD)) {
                setMoveShortcut(SNOWSCAPE, MOVES[SNOWSCAPE]);
            } else {
                setMoveShortcut(ICE_SHARD, MOVES[ICE_SHARD]);
            }
        } else {
            if (lastMove(POWDER_SNOW)) {
                setMoveShortcut(SNOWSCAPE, MOVES[SNOWSCAPE]);
            } else if (lastMove(SNOWSCAPE)) {
                setMoveShortcut(ICE_SHARD, MOVES[ICE_SHARD]);
            } else {
                setMoveShortcut(POWDER_SNOW, MOVES[POWDER_SNOW]);
            }
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case POWDER_SNOW: {
                Details powerDetail = new Details(this, STATUS, FROZEN_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(powerDetail);
                break;
            }
            case SNOWSCAPE: {
                if (moltres.isDeadOrEscaped() && zapdos.isDeadOrEscaped()) {
                    Details blockDetail = new Details(this, SOLO_BLOCK, BLOCK_TEXTURE);
                    details.add(blockDetail);
                } else {
                    Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE, Details.TargetType.ALL_ENEMIES);
                    details.add(blockDetail);
                }
                Details powerDetail = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!moltres.isDeadOrEscaped()) {
            moltres.birdsDead();
        }
        if (!zapdos.isDeadOrEscaped()) {
            zapdos.birdsDead();
        }
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            onBossVictoryLogic();
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Articuno();
    }

}