package pokeregions.monsters.act3.enemies.rayquaza;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
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
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Rayquaza;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;
import pokeregions.util.Wiz;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class RayquazaEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(RayquazaEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte HURRICANE = 0;
    private static final byte DRAGON_PULSE = 1;
    private static final byte DRAGON_BREATH = 2;

    public final int STR = calcAscensionSpecial(1);
    public final int STATUS = calcAscensionSpecial(2);
    public final int BLOCK = 12;

    private AbstractMonster minion1;
    private AbstractMonster minion2;

    public static final String POWER_ID = makeID("DragonLord");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public RayquazaEnemy() {
        this(0.0f, 0.0f);
    }

    public RayquazaEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 300.0f, 280.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Rayquaza/Rayquaza.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 2.0f);
        setHp(calcAscensionTankiness(350));
        addMove(HURRICANE, Intent.ATTACK, calcAscensionDamage(5), 3);
        addMove(DRAGON_PULSE, Intent.ATTACK_DEFEND, calcAscensionDamage(13));
        addMove(DRAGON_BREATH, Intent.DEBUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        for (AbstractMonster mo : Wiz.getEnemies()) {
            if (mo instanceof SalamenceR) {
                minion1 = mo;
            }
            if (mo instanceof FlygonR) {
                minion2 = mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, STR) {

            @Override
            public void atEndOfRound() {
                if (!minion1.isDeadOrEscaped() || !minion2.isDeadOrEscaped()) {
                    this.flash();
                    applyToTarget(minion1, owner, new StrengthPower(minion1, amount));
                    applyToTarget(minion2, owner, new StrengthPower(minion2, amount));
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
        CustomDungeon.playTempMusicInstantly("Zinnia");
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case HURRICANE: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                }
                break;
            }
            case DRAGON_PULSE: {
                useFastAttackAnimation();
                block(this, BLOCK);
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                break;
            }
            case DRAGON_BREATH: {
                intoDiscardMo(new Burn(), STATUS);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove || (minion1.isDeadOrEscaped() && minion2.isDeadOrEscaped())) {
            setMoveShortcut(HURRICANE, MOVES[HURRICANE]);
        } else if(minion1.isDeadOrEscaped() || minion2.isDeadOrEscaped()) {
            if (this.lastMove(DRAGON_BREATH)) {
                setMoveShortcut(HURRICANE, MOVES[HURRICANE]);
            } else {
                setMoveShortcut(DRAGON_BREATH, MOVES[DRAGON_BREATH]);
            }
        } else {
            if (this.lastMove(HURRICANE)) {
                setMoveShortcut(DRAGON_BREATH, MOVES[DRAGON_BREATH]);
            } else if (this.lastMove(DRAGON_BREATH)) {
                if (this.lastMoveBefore(HURRICANE)) {
                    setMoveShortcut(DRAGON_PULSE, MOVES[DRAGON_PULSE]);
                } else {
                    setMoveShortcut(HURRICANE, MOVES[HURRICANE]);
                }
            } else if (this.lastMove(DRAGON_PULSE)) {
                setMoveShortcut(DRAGON_BREATH, MOVES[DRAGON_BREATH]);
            } else {
                setMoveShortcut(HURRICANE, MOVES[HURRICANE]);
            }
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case DRAGON_PULSE: {
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                break;
            }
            case DRAGON_BREATH: {
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
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            onBossVictoryLogic();
            onFinalBossVictoryLogic();
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Rayquaza();
    }

}