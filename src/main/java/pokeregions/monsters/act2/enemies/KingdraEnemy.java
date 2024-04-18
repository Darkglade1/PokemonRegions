package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Kingdra;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.NastyPlot;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class KingdraEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(KingdraEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte WATER_PULSE = 0;
    private static final byte WAVE_CRASH = 1;
    private static final byte FOCUS_ENERGY = 2;

    public final int STATUS_DRAW = calcAscensionSpecial(1);
    public final int STATUS_DISCARD = 1;
    public final int BUFF = calcAscensionSpecialSmall(3);
    public final int DEBUFF = 1;
    private boolean triggeredPower = false;

    public static final String POWER_ID = makeID("Sniper");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public KingdraEnemy() {
        this(0.0f, 0.0f);
    }

    public KingdraEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 140.0f, 140.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Kingdra/Kingdra.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(95), calcAscensionTankiness(104));
        addMove(WATER_PULSE, Intent.ATTACK_DEBUFF, calcAscensionDamage(14));
        addMove(WAVE_CRASH, Intent.ATTACK, calcAscensionDamage(23));
        addMove(FOCUS_ENERGY, Intent.BUFF);

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, DEBUFF, "accuracy") {
            @Override
            public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
                    applyToTarget(target, owner, new FrailPower(target, amount, true));
                    triggeredPower = true;
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
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
            case WATER_PULSE: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.POISON);
                intoDrawMo(new Slimed(), STATUS_DRAW);
                intoDiscardMo(new Slimed(), STATUS_DISCARD);
                break;
            }
            case WAVE_CRASH: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                break;
            }
            case FOCUS_ENERGY: {
                applyToTarget(this, this, new NastyPlot(this, BUFF));
                triggeredPower = false;
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(WATER_PULSE)) {
            setMoveShortcut(WAVE_CRASH, MOVES[WAVE_CRASH]);
        } else if (this.lastMove(WAVE_CRASH)) {
            if (triggeredPower) {
                if (lastMoveBefore(WAVE_CRASH)) {
                    setMoveShortcut(FOCUS_ENERGY, MOVES[FOCUS_ENERGY]);
                } else {
                    setMoveShortcut(WAVE_CRASH, MOVES[WAVE_CRASH]);
                }
            } else {
                setMoveShortcut(FOCUS_ENERGY, MOVES[FOCUS_ENERGY]);
            }
        } else {
            setMoveShortcut(WATER_PULSE, MOVES[WATER_PULSE]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case WATER_PULSE: {
                Details statusDetail = new Details(this, STATUS_DISCARD, SLIMED_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                Details statusDetail2 = new Details(this, STATUS_DRAW, SLIMED_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail2);
                break;
            }
            case FOCUS_ENERGY: {
                Details powerDetail = new Details(this, BUFF, NASTY_PLOT_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Kingdra();
    }

}