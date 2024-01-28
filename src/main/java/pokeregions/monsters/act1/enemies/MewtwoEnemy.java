package pokeregions.monsters.act1.enemies;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.stances.DivinityStance;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act1.Mewtwo;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.MindOverMatter;
import pokeregions.util.Details;
import pokeregions.vfx.FlexibleDivinityParticleEffect;
import pokeregions.vfx.FlexibleStanceAuraEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class MewtwoEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(MewtwoEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte CALM_MIND = 0;
    private static final byte RECOVER = 1;
    private static final byte CONFUSION = 2;

    public final int FUTURE_SIGHT_BASE_DAMAGE = 10;
    public final int FUTURE_SIGH_DAMAGE_INCREASE = 5;
    public final int STR = 3;
    public final int METALLICIZE = 5;
    public final int BLOCK = 8;
    public final int HEAL = 15;
    public final int STATUS = 1;

    private MindOverMatter power;

    private float particleTimer;
    private float particleTimer2;

    public MewtwoEnemy() {
        this(0.0f, 0.0f);
    }

    public MewtwoEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 220.0f, 210.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Mewtwo/Mewtwo.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.5f);
        setHp(calcAscensionTankiness(260));
        addMove(CALM_MIND, Intent.DEFEND_BUFF);
        addMove(RECOVER, Intent.BUFF);
        addMove(CONFUSION, Intent.ATTACK_DEBUFF, calcAscensionDamage(8));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        power = new MindOverMatter(this, FUTURE_SIGHT_BASE_DAMAGE, FUTURE_SIGH_DAMAGE_INCREASE);
        applyToTarget(this, this, power);
        CustomDungeon.playTempMusicInstantly("HauntedHouse");
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case CALM_MIND: {
                block(this, BLOCK);
                applyToTarget(this, this, new MetallicizePower(this, METALLICIZE));
                break;
            }
            case RECOVER: {
                atb(new HealAction(this, this, HEAL));
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
            }
            case CONFUSION: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.POISON);
                intoDiscardMo(new VoidCard(), STATUS);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (AbstractDungeon.ascensionLevel >= 19) {
            if (lastMove(CALM_MIND)) {
                setMoveShortcut(RECOVER, MOVES[RECOVER]);
            } else if (lastMove(RECOVER)) {
                setMoveShortcut(CONFUSION, MOVES[CONFUSION]);
            } else {
                setMoveShortcut(CALM_MIND, MOVES[CALM_MIND]);
            }
        } else {
            if (lastMove(CONFUSION)) {
                setMoveShortcut(RECOVER, MOVES[RECOVER]);
            } else if (lastMove(RECOVER)) {
                setMoveShortcut(CALM_MIND, MOVES[CALM_MIND]);
            } else {
                setMoveShortcut(CONFUSION, MOVES[CONFUSION]);
            }
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case CALM_MIND: {
                Details powerDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, METALLICIZE, METALLICIZE_TEXTURE);
                details.add(powerDetail2);
                break;
            }
            case RECOVER: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, HEAL, HEAL_TEXTURE);
                details.add(powerDetail2);
                break;
            }
            case CONFUSION: {
                Details statusDetail = new Details(this, STATUS, VOID_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
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
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (power != null) {
            if (power.aboutToTrigger) {
                this.particleTimer -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer < 0.0F) {
                    this.particleTimer = 0.04F;
                    AbstractDungeon.effectsQueue.add(new FlexibleDivinityParticleEffect(this));
                }

                this.particleTimer2 -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer2 < 0.0F) {
                    this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                    AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(DivinityStance.STANCE_ID, this));
                }
            }
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Mewtwo();
    }

}