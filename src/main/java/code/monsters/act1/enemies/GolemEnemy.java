package code.monsters.act1.enemies;

import basemod.ReflectionHacks;
import code.BetterSpriterAnimation;
import code.PokemonRegions;
import code.cards.pokemonAllyCards.Golem;
import code.monsters.AbstractPokemonAlly;
import code.monsters.AbstractPokemonMonster;
import code.powers.AbstractLambdaPower;
import code.powers.Sturdy;
import code.powers.SuperEffective;
import code.util.Details;
import code.util.ProAudio;
import code.util.Wiz;
import code.vfx.WaitEffect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import java.util.ArrayList;

import static code.PokemonRegions.*;
import static code.util.Wiz.*;

public class GolemEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(GolemEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte ROCK_POLISH = 0;
    private static final byte SMACK_DOWM = 1;
    private static final byte EARTHQUAKE = 2;

    public final int STR = 3;
    public final int BLOCK = 6;
    public final int DEBUFF = calcAscensionSpecial(1);
    public final int DAMAGE_REDUCTION = 50;
    public final int DAMAGE_INCREASE = 100;

    public static final String POWER_ID = makeID("TypeAdvantage");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public GolemEnemy() {
        this(0.0f, 0.0f);
    }

    public GolemEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 160.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Golem/Golem.scml"));
        setHp(calcAscensionTankiness(120), calcAscensionTankiness(126));
        addMove(ROCK_POLISH, Intent.DEFEND_BUFF);
        addMove(SMACK_DOWM, Intent.ATTACK_DEBUFF, calcAscensionDamage(7));
        addMove(EARTHQUAKE, Intent.ATTACK, calcAscensionDamage(15));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new Sturdy(this, DAMAGE_REDUCTION));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, DAMAGE_INCREASE) {
            private boolean triggered = false;

            @Override
            public int onAttacked(DamageInfo info, int damageAmount) {
                if (info.type == DamageInfo.DamageType.NORMAL && info.owner instanceof AbstractPokemonAlly && !triggered && !owner.hasPower(SuperEffective.POWER_ID)) {
                    this.flash();
                    triggered = true;
                    applyToTarget(owner, owner, new SuperEffective(owner, amount));
                }
                return damageAmount;
            }

            @Override
            public void atEndOfRound() {
                triggered = false;
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + DAMAGE_INCREASE + POWER_DESCRIPTIONS[1];
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
            case ROCK_POLISH: {
                applyToTarget(this, this, new StrengthPower(this, STR));
                block(this, BLOCK);
                break;
            }
            case SMACK_DOWM: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                applyToTarget(adp(), this, new VulnerablePower(adp(), DEBUFF, true));
                break;
            }
            case EARTHQUAKE: {
                Wiz.playAudio(ProAudio.EARTHQUAKE);
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.XLONG, false);
                atb(new VFXAction(new WaitEffect(), 0.3f));
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(SMACK_DOWM)) {
            setMoveShortcut(EARTHQUAKE, MOVES[EARTHQUAKE]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(SMACK_DOWM)) {
                possibilities.add(SMACK_DOWM);
            }
            if (!this.lastMove(EARTHQUAKE)) {
                possibilities.add(EARTHQUAKE);
            }
            if (!this.lastMove(ROCK_POLISH)) {
                possibilities.add(ROCK_POLISH);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case ROCK_POLISH: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                break;
            }
            case SMACK_DOWM: {
                Details powerDetail = new Details(this, DEBUFF, VULNERABLE_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Golem();
    }

}