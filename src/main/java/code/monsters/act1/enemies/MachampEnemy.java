package code.monsters.act1.enemies;

import basemod.ReflectionHacks;
import code.BetterSpriterAnimation;
import code.PokemonRegions;
import code.cards.pokemonAllyCards.Machamp;
import code.monsters.AbstractPokemonMonster;
import code.powers.NoGuard;
import code.util.Details;
import code.vfx.FlexibleStanceAuraEffect;
import code.vfx.FlexibleWrathParticleEffect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.stances.WrathStance;

import java.util.ArrayList;

import static code.PokemonRegions.*;
import static code.util.Wiz.*;

public class MachampEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(MachampEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte BULK_UP = 0;
    private static final byte CHOP = 1;
    private static final byte SCARY = 2;

    public final int STR = 2;
    public final int DEBUFF = 2;
    public final int STATUS = calcAscensionSpecial(1);
    public final int HP_THRESHOLD = 50;
    public final int DAMAGE_INCREASE = 50;

    private boolean justBuffed = false;
    private boolean playedSfx = false;
    private int hpThreshold;

    private float particleTimer;
    private float particleTimer2;

    public MachampEnemy() {
        this(0.0f, 0.0f);
    }

    public MachampEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 180.0f, 180.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Machamp/Machamp.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.25f);
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(90), calcAscensionTankiness(94));
        addMove(BULK_UP, Intent.BUFF);
        addMove(CHOP, Intent.ATTACK, calcAscensionDamage(6), 2);
        addMove(SCARY, Intent.DEBUFF);
        this.hpThreshold = calculateHPThreshold();
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new NoGuard(this, DAMAGE_INCREASE, hpThreshold));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case BULK_UP: {
                applyToTarget(this, this, new StrengthPower(this, STR));
                justBuffed = true;
                break;
            }
            case CHOP: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                }
                break;
            }
            case SCARY: {
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                intoDiscardMo(new Wound(), STATUS);
                justBuffed = false;
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (underHPThreshold()) {
            if (!playedSfx) {
                CardCrawlGame.sound.play("STANCE_ENTER_WRATH");
                playedSfx = true;
            }
            AbstractDungeon.onModifyPower();
        }
    }

    private boolean underHPThreshold() {
        return this.currentHealth < hpThreshold;
    }

    private int calculateHPThreshold() {
        return Math.round(this.maxHealth * ((float)HP_THRESHOLD / 100));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(BULK_UP, MOVES[BULK_UP]);
        } else if (underHPThreshold()) {
            setMoveShortcut(CHOP, MOVES[CHOP]);
        } else if (lastMove(CHOP)) {
            if (justBuffed) {
                setMoveShortcut(SCARY, MOVES[SCARY]);
            } else {
                setMoveShortcut(BULK_UP, MOVES[BULK_UP]);
            }
        } else {
            setMoveShortcut(CHOP, MOVES[CHOP]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case BULK_UP: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case SCARY: {
                Details statusDetails = new Details(this, STATUS, WOUND_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetails);
                Details powerDetail = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (underHPThreshold()) {
            this.particleTimer -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer < 0.0F) {
                this.particleTimer = 0.04F;
                AbstractDungeon.effectsQueue.add(new FlexibleWrathParticleEffect(this));
            }

            this.particleTimer2 -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer2 < 0.0F) {
                this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(WrathStance.STANCE_ID, this));
            }
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Machamp();
    }

}