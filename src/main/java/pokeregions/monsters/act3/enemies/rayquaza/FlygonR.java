package pokeregions.monsters.act3.enemies.rayquaza;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
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
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Flygon;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.monsters.act3.enemies.FlygonEnemy;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.MonsterNextTurnBlockPower;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;
import pokeregions.util.Wiz;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class FlygonR extends AbstractPokemonMonster
{
    public static final String ID = makeID(FlygonEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte GUARDIAN_SPIRIT = 0;
    private static final byte FRUSTRATION = 2;

    public final int BLOCK = 11;

    public static final String POWER_ID = makeID("Taunt");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FlygonR() {
        this(0.0f, 0.0f);
    }

    public FlygonR(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 220.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Flygon/Flygon.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.3f);
        setHp(calcAscensionTankiness(250));
        addMove(GUARDIAN_SPIRIT, Intent.DEFEND_BUFF);
        addMove(FRUSTRATION, Intent.ATTACK, calcAscensionDamage(6), 2);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case GUARDIAN_SPIRIT: {
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new MonsterNextTurnBlockPower(mo, BLOCK));
                }
                applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, 0) {
                    private boolean justApplied = true;
                    @Override
                    public void atEndOfRound() {
                        if (justApplied) {
                            justApplied = false;
                        } else {
                            makePowerRemovable(this);
                            atb(new RemoveSpecificPowerAction(owner, owner, this));
                        }
                    }

                    @Override
                    public void updateDescription() {
                        description = POWER_DESCRIPTIONS[0];
                    }
                });
                break;
            }
            case FRUSTRATION: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_VERTICAL);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        boolean isAlone = true;
        for (AbstractMonster mo : Wiz.getEnemies()) {
            if (mo != this) {
                isAlone = false;
                break;
            }
        }
        if (isAlone) {
            setMoveShortcut(FRUSTRATION, MOVES[FRUSTRATION]);
        } else {
            if (this.lastMove(GUARDIAN_SPIRIT)) {
                setMoveShortcut(FRUSTRATION, MOVES[FRUSTRATION]);
            } else {
                setMoveShortcut(GUARDIAN_SPIRIT, MOVES[GUARDIAN_SPIRIT]);
            }
        }
        super.postGetMove();
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        AbstractPower str = this.getPower(StrengthPower.POWER_ID);
        if (str != null && str.amount > 0) {
            for (AbstractMonster mo : Wiz.getEnemies()) {
                if (mo instanceof RayquazaEnemy) {
                    applyToTarget(mo, this, new StrengthPower(mo, str.amount));
                }
            }
        }
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            onBossVictoryLogic();
            onFinalBossVictoryLogic();
        }
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        String textureString = makePowerPath("Taunt32.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case GUARDIAN_SPIRIT: {
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE, Details.TargetType.ALL_ENEMIES);
                details.add(blockDetail);
                Details powerDetails = new Details(this, 1, texture);
                details.add(powerDetails);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Flygon();
    }

}