package code.monsters.act1.enemies.birds;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import code.BetterSpriterAnimation;
import code.PokemonRegions;
import code.cards.pokemonAllyCards.Dragonite;
import code.monsters.AbstractPokemonMonster;
import code.powers.AbstractLambdaPower;
import code.util.Details;
import code.util.Wiz;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
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

import java.util.ArrayList;

import static code.PokemonRegions.*;
import static code.util.Wiz.*;

public class MoltresEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(MoltresEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte INCINERATE = 0;
    private static final byte HEAT_WAVE = 1;
    public final int STATUS = 1;
    public final int POWER_STATUS = 3;

    private final AbstractCard status;
    private ZapdosEnemy zapdos;
    private ArticunoEnemy articuno;
    private AbstractPower retributionPower;

    public static final String POWER_ID = makeID("RetributionMoltres");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public MoltresEnemy() {
        this(0.0f, 0.0f);
    }

    public MoltresEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0.0f, 250.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Moltres/Moltres.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.5f);
        setHp(calcAscensionTankiness(100));
        addMove(INCINERATE, Intent.ATTACK_DEBUFF, calcAscensionDamage(8));
        addMove(HEAT_WAVE, Intent.ATTACK, calcAscensionDamage(5), 2);
        status = new Burn();
        if (AbstractDungeon.ascensionLevel >= 19) {
            status.upgrade();
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        CustomDungeon.playTempMusicInstantly("Zinnia");
        for (AbstractMonster mo :Wiz.getEnemies()) {
            if (mo instanceof ZapdosEnemy) {
                zapdos = (ZapdosEnemy) mo;
            }
            if (mo instanceof ArticunoEnemy) {
                articuno = (ArticunoEnemy) mo;
            }
        }
        retributionPower = new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, POWER_STATUS) {
            private boolean triggered = false;

            @Override
            public void onSpecificTrigger() {
                if (!triggered) {
                    triggered = true;
                    flash();
                    intoDiscardMo(status.makeStatEquivalentCopy(), amount);
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        };
        applyToTarget(this, this, retributionPower);
    }

    public void checkBirdsDead() {
        if (zapdos.isDeadOrEscaped() && articuno.isDeadOrEscaped()) {
            retributionPower.onSpecificTrigger();
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case INCINERATE: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                intoDiscardMo(status.makeStatEquivalentCopy(), STATUS);
                break;
            }
            case HEAT_WAVE: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(INCINERATE)) {
            setMoveShortcut(HEAT_WAVE, MOVES[HEAT_WAVE]);
        } else {
            setMoveShortcut(INCINERATE, MOVES[INCINERATE]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case INCINERATE: {
                Details statusDetail = new Details(this, STATUS, BURN_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!zapdos.isDeadOrEscaped()) {
            zapdos.checkBirdsDead();
        }
        if (!articuno.isDeadOrEscaped()) {
            articuno.checkBirdsDead();
        }
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            onBossVictoryLogic();
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Dragonite();
    }

}