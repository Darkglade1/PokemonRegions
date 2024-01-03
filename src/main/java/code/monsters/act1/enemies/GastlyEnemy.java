package code.monsters.act1.enemies;

import code.BetterSpriterAnimation;
import code.cards.pokemonAllyCards.Gastly;
import code.monsters.AbstractPokemonMonster;
import code.powers.MonsterIntangiblePower;
import code.util.Wiz;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;

import static code.PokemonRegions.makeID;
import static code.PokemonRegions.makeMonsterPath;
import static code.util.Wiz.*;

public class GastlyEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(GastlyEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte LICK = 0;
    private static final byte SUCKER_PUNCH = 1;
    private static final byte GHOST_PARTY = 2;

    public final int DAZED_AMT = calcAscensionSpecial(2);
    public final int DEBUFF = 1;
    public final int INTANGIBLE = 1;

    public GastlyEnemy() {
        this(0.0f, 0.0f);
    }

    public GastlyEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0.0f, 160.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Gastly/Gastly.scml"));
        setHp(calcAscensionTankiness(38), calcAscensionTankiness(42));
        addMove(LICK, Intent.DEBUFF);
        addMove(SUCKER_PUNCH, Intent.ATTACK_DEBUFF, calcAscensionDamage(6));
        addMove(GHOST_PARTY, Intent.BUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case LICK: {
                intoDiscardMo(new Dazed(), DAZED_AMT);
                break;
            }
            case SUCKER_PUNCH: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                break;
            }
            case GHOST_PARTY: {
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new MonsterIntangiblePower(mo, INTANGIBLE));
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(LICK)) {
            setMoveShortcut(GHOST_PARTY, MOVES[GHOST_PARTY]);
        } else if (lastMove(SUCKER_PUNCH)) {
            setMoveShortcut(LICK, MOVES[LICK]);
        } else {
            setMoveShortcut(SUCKER_PUNCH, MOVES[SUCKER_PUNCH]);
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Gastly();
    }

}