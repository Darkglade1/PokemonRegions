package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.stances.DivinityStance;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.MagicGuard;
import pokeregions.util.Details;
import pokeregions.util.Wiz;
import pokeregions.vfx.FlexibleDivinityParticleEffect;
import pokeregions.vfx.FlexibleStanceAuraEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class EspeonAnnie extends AbstractPokemonMonster
{
    public static final String ID = makeID(EspeonAnnie.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte CONFUSION = 0;
    private static final byte HELPING_HAND = 1;
    private static final byte PSYCHIC = 2;

    public final int STR = calcAscensionSpecialSmall(3);
    public final int STATUS = 1;

    private float particleTimer;
    private float particleTimer2;

    public EspeonAnnie() {
        this(0.0f, 0.0f, false);
    }

    public EspeonAnnie(final float x, final float y) {
        this(x, y, false);
    }

    public EspeonAnnie(final float x, final float y, boolean isCatchable) {
        super(NAME, ID, 140, 0.0F, 0, 150.0f, 110.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Espeon/Espeon.scml"));
        this.type = EnemyType.ELITE;
        this.isCatchable = isCatchable;
        setHp(calcAscensionTankiness(100), calcAscensionTankiness(108));
        addMove(CONFUSION, Intent.ATTACK_DEBUFF, calcAscensionDamage(9));
        addMove(HELPING_HAND, Intent.BUFF);
        addMove(PSYCHIC, Intent.ATTACK, calcAscensionDamage(23));
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new MagicGuard(this));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case CONFUSION: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.POISON);
                intoDrawMo(new VoidCard(), STATUS);
                if (AbstractDungeon.ascensionLevel >= 18) {
                    intoDiscardMo(new VoidCard(), STATUS);
                }
                break;
            }
            case HELPING_HAND: {
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new StrengthPower(mo, STR));
                }
                break;
            }
            case PSYCHIC: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.POISON);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(CONFUSION)) {
            setMoveShortcut(HELPING_HAND, MOVES[HELPING_HAND]);
        } else if (lastMove(HELPING_HAND)){
            setMoveShortcut(PSYCHIC, MOVES[PSYCHIC]);
        } else {
            setMoveShortcut(CONFUSION, MOVES[CONFUSION]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case CONFUSION: {
                Details statusDetail = new Details(this, STATUS, VOID_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                if (AbstractDungeon.ascensionLevel >= 18) {
                    Details statusDetail2 = new Details(this, STATUS, VOID_TEXTURE, Details.TargetType.DISCARD_PILE);
                    details.add(statusDetail2);
                }
                break;
            }
            case HELPING_HAND: {
                Details powerDetails = new Details(this, STR, STRENGTH_TEXTURE, Details.TargetType.ALL_ENEMIES);
                details.add(powerDetails);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(MagicGuard.POWER_ID) && this.getPower(MagicGuard.POWER_ID).amount >= 1) {
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