package pokeregions.monsters.act1.enemies.birds;

import basemod.ReflectionHacks;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act1.Zapdos;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.BetterPlatedArmor;
import pokeregions.util.Details;
import pokeregions.util.Wiz;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class ZapdosEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(ZapdosEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte DISCHARGE = 0;
    private static final byte DRILL_PECK = 1;
    private static final byte CHARGE = 2;

    public final int PLATED_ARMOR = 10;
    public final int BASE_STR = 1;
    public final int ALONE_STR = calcAscensionSpecial(3);
    public final int DEBUFF = 1;
    private MoltresEnemy moltres;
    private ArticunoEnemy articuno;
    private AbstractPower retributionPower;

    public static final String POWER_ID = makeID("RetributionZapdos");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ZapdosEnemy() {
        this(0.0f, 0.0f);
    }

    public ZapdosEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0.0f, 250.0f, 240.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Zapdos/Zapdos.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.5f);
        setHp(calcAscensionTankiness(100));
        addMove(DISCHARGE, Intent.ATTACK_DEBUFF, calcAscensionDamage(5));
        addMove(DRILL_PECK, Intent.ATTACK, calcAscensionDamage(9));
        addMove(CHARGE, Intent.BUFF);
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
            if (mo instanceof ArticunoEnemy) {
                articuno = (ArticunoEnemy) mo;
            }
        }
        retributionPower = new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, PLATED_ARMOR) {

            @Override
            public void onSpecificTrigger() {
                flash();
                applyToTarget(owner, owner, new BetterPlatedArmor(owner, amount));
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
            case DISCHARGE: {
                useFastAttackAnimation();
                atb(new VFXAction(new LightningEffect(adp().drawX, adp().drawY)));
                atb(new SFXAction("ORB_LIGHTNING_EVOKE", 0.1F));
                dmg(adp(), info, AbstractGameAction.AttackEffect.NONE);
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                break;
            }
            case DRILL_PECK: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                break;
            }
            case CHARGE: {
                if (moltres.isDeadOrEscaped() && articuno.isDeadOrEscaped()) {
                    applyToTarget(this, this, new StrengthPower(this, ALONE_STR));
                } else {
                    for (AbstractMonster mo : Wiz.getEnemies()) {
                        applyToTarget(mo, this, new StrengthPower(mo, BASE_STR));
                    }
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (!firstMove && moltres.isDeadOrEscaped() && articuno.isDeadOrEscaped()) {
            if (lastMove(DRILL_PECK)) {
                setMoveShortcut(CHARGE, MOVES[CHARGE]);
            } else {
                setMoveShortcut(DRILL_PECK, MOVES[DRILL_PECK]);
            }
        } else {
            if (lastMove(DISCHARGE)) {
                setMoveShortcut(DRILL_PECK, MOVES[DRILL_PECK]);
            } else if (lastMove(DRILL_PECK)) {
                setMoveShortcut(CHARGE, MOVES[CHARGE]);
            } else {
                setMoveShortcut(DISCHARGE, MOVES[DISCHARGE]);
            }
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case DISCHARGE: {
                Details powerDetail = new Details(this, DEBUFF, FRAIL_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case CHARGE: {
                if (moltres.isDeadOrEscaped() && articuno.isDeadOrEscaped()) {
                    Details powerDetail = new Details(this, ALONE_STR, STRENGTH_TEXTURE);
                    details.add(powerDetail);
                } else {
                    Details powerDetail = new Details(this, BASE_STR, STRENGTH_TEXTURE, Details.TargetType.ALL_ENEMIES);
                    details.add(powerDetail);
                }
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
        if (!articuno.isDeadOrEscaped()) {
            articuno.birdsDead();
        }
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            onBossVictoryLogic();
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Zapdos();
    }

}