package pokeregions.monsters.act2.enemies;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Lugia;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class LugiaEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(LugiaEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SHADOW_RUSH = 0;
    private static final byte SHADOW_BLAST = 1;
    private static final byte SHADOW_SKY = 2;

    public final int DEBUFF = 1;
    public final int STRONG_DEBUFF = calcAscensionSpecial(2);
    public final int DAMAGE_REDUCTION = calcAscensionSpecial(50);
    public final int TEMP_HP = 40;
    public final int TEMP_HP_TURNS = 4;

    public static final String POWER_ID = makeID("ClosedHeart");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public LugiaEnemy() {
        this(100.0f, 0.0f);
    }

    public LugiaEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 250.0f, 290.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Lugia/Lugia.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.2f);
        setHp(calcAscensionTankiness(240));
        addMove(SHADOW_RUSH, Intent.ATTACK, calcAscensionDamage(26));
        addMove(SHADOW_BLAST, Intent.ATTACK_DEBUFF, calcAscensionDamage(15));
        addMove(SHADOW_SKY, Intent.STRONG_DEBUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        atb(new AddTemporaryHPAction(this, this, TEMP_HP));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, DAMAGE_REDUCTION, "corruption", 99) {

            private int count = 0;
            private int calculateDamageTakenAmount(int damage) {
                if (TempHPField.tempHp.get(this) > 0) {
                    return (int)(damage * (1 - ((float)amount / 100)));
                } else {
                    return damage;
                }
            }

            @Override
            public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
                if (info.type == DamageInfo.DamageType.NORMAL && !(info.owner instanceof AbstractPokemonAlly)) {
                    return calculateDamageTakenAmount(damageAmount);
                } else {
                    return damageAmount;
                }
            }

            @Override
            public void atEndOfRound() {
                count++;
                if (count >= TEMP_HP_TURNS) {
                    flash();
                    atb(new AddTemporaryHPAction(owner, owner, TEMP_HP));
                    count = 0;
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + TEMP_HP_TURNS + POWER_DESCRIPTIONS[2] + TEMP_HP + POWER_DESCRIPTIONS[3];
            }
        });
        CustomDungeon.playTempMusicInstantly("HauntedHouse");
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SHADOW_RUSH: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                break;
            }
            case SHADOW_BLAST: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new VulnerablePower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                break;
            }
            case SHADOW_SKY: {
                applyToTarget(adp(), this, new StrengthPower(adp(), -STRONG_DEBUFF));
                applyToTarget(adp(), this, new DexterityPower(adp(), -STRONG_DEBUFF));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(SHADOW_BLAST)) {
            setMoveShortcut(SHADOW_RUSH, MOVES[SHADOW_RUSH]);
        } else if (lastMove(SHADOW_RUSH)) {
            if (lastMoveBefore(SHADOW_RUSH)) {
                setMoveShortcut(SHADOW_SKY, MOVES[SHADOW_SKY]);
            } else {
                setMoveShortcut(SHADOW_RUSH, MOVES[SHADOW_RUSH]);
            }
        } else {
            setMoveShortcut(SHADOW_BLAST, MOVES[SHADOW_BLAST]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case SHADOW_BLAST: {
                Details powerDetail = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, DEBUFF, VULNERABLE_TEXTURE);
                details.add(powerDetail2);
                Details powerDetail3 = new Details(this, DEBUFF, FRAIL_TEXTURE);
                details.add(powerDetail3);
                break;
            }
            case SHADOW_SKY: {
                Details powerDetail = new Details(this, -STRONG_DEBUFF, STRENGTH_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, -STRONG_DEBUFF, DEXTERITY_TEXTURE);
                details.add(powerDetail2);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        onBossVictoryLogic();
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Lugia();
    }

}