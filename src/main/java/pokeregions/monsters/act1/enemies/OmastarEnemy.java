package pokeregions.monsters.act1.enemies;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.cards.pokemonAllyCards.act1.Omastar;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class OmastarEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(OmastarEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte ROLLOUT = 0;
    private static final byte STUNNED = 1;
    public final int ROLLOUT_BASE_DAMAGE = 4;
    public final int ROLLOUT_MULTIPLIER = 2;
    public final int ROLLOUT_DAMAGE_CAP = 32;

    public final int POWER_HITS = 4;

    public static final String POWER_ID = makeID("WeakArmor");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public OmastarEnemy() {
        this(0.0f, 0.0f);
    }

    public OmastarEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 130.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Omastar/Omastar.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(55), calcAscensionTankiness(60));
        addMove(ROLLOUT, Intent.ATTACK, ROLLOUT_BASE_DAMAGE);
        addMove(STUNNED, Intent.STUN);

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, POWER_HITS) {
            @Override
            public int onAttacked(DamageInfo info, int damageAmount) {
                if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != owner) {
                    amount--;
                    if (amount <= 0) {
                        this.flash();
                        makePowerRemovable(this);
                        atb(new RemoveSpecificPowerAction(owner, owner, this));
                        setMoveShortcut(STUNNED);
                        createIntent();
                        addMove(ROLLOUT, Intent.ATTACK, ROLLOUT_BASE_DAMAGE);
                    } else {
                        this.flashWithoutSound();
                        updateDescription();
                    }
                }
                return damageAmount;
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
            case ROLLOUT: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                int newDamage = moves.get(ROLLOUT).baseDamage * ROLLOUT_MULTIPLIER;
                if (newDamage <= ROLLOUT_DAMAGE_CAP) {
                    addMove(ROLLOUT, Intent.ATTACK, newDamage);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(ROLLOUT, MOVES[ROLLOUT]);
        super.postGetMove();
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Omastar();
    }

}