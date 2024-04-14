package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.stances.CalmStance;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Quagsire;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;
import pokeregions.vfx.FlexibleCalmParticleEffect;
import pokeregions.vfx.FlexibleStanceAuraEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class QuagsireEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(QuagsireEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SCALD = 0;
    private static final byte QUICKSAND = 1;

    public final int STATUS = calcAscensionSpecial(1);
    public final int DEBUFF = calcAscensionSpecialSmall(3);
    public final int POWER_THRESHOLD = 3;

    public static final String POWER_ID = makeID("Unaware");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private float particleTimer;
    private float particleTimer2;

    public QuagsireEnemy() {
        this(0.0f, 0.0f);
    }

    public QuagsireEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Quagsire/Quagsire.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.1f);
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(108), calcAscensionTankiness(120));
        addMove(SCALD, Intent.ATTACK_DEBUFF, calcAscensionDamage(18));
        addMove(QUICKSAND, Intent.ATTACK_DEBUFF, calcAscensionDamage(10));

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, 1, "confusion") {
            @Override
            public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
                if (info.type == DamageInfo.DamageType.NORMAL) {
                    if (damageAmount > 0) {
                        if (this.amount >= POWER_THRESHOLD) {
                            this.amount = 1;
                            return 0;
                        } else {
                            this.amount++;
                        }
                    }
                }
                return damageAmount;
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + POWER_THRESHOLD + POWER_DESCRIPTIONS[1];
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
            case SCALD: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                intoDrawMo(new Burn(), STATUS);
                break;
            }
            case QUICKSAND: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.POISON);
                applyToTarget(adp(), this, new ConstrictedPower(adp(), this, DEBUFF));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(QUICKSAND, MOVES[QUICKSAND]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(SCALD)) {
                possibilities.add(SCALD);
            }
            if (!this.lastTwoMoves(QUICKSAND)) {
                possibilities.add(QUICKSAND);
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
            case SCALD: {
                Details statusDetail = new Details(this, STATUS, BURN_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                break;
            }
            case QUICKSAND: {
                Details powerDetail = new Details(this, DEBUFF, CONSTRICTED_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(POWER_ID) && this.getPower(POWER_ID).amount >= POWER_THRESHOLD) {
            this.particleTimer -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer < 0.0F) {
                this.particleTimer = 0.04F;
                AbstractDungeon.effectsQueue.add(new FlexibleCalmParticleEffect(this));
            }
            this.particleTimer2 -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer2 < 0.0F) {
                this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(CalmStance.STANCE_ID, this));
            }
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Quagsire();
    }

}