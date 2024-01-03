package code.monsters.act1.enemies;

import code.BetterSpriterAnimation;
import code.cards.pokemonAllyCards.Gengar;
import code.monsters.AbstractPokemonMonster;
import code.powers.NastyPlot;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;

import static code.PokemonRegions.makeID;
import static code.PokemonRegions.makeMonsterPath;
import static code.util.Wiz.*;

public class GengarEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(GengarEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte NASTY_PLOT = 0;
    private static final byte SHADOW_BALL = 1;

    public final int BUFF_AMT = 5;

    public GengarEnemy() {
        this(0.0f, 0.0f);
    }

    public GengarEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0.0f, 160.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Gengar/Gengar.scml"));
        setHp(calcAscensionTankiness(54), calcAscensionTankiness(58));
        addMove(NASTY_PLOT, Intent.BUFF);
        addMove(SHADOW_BALL, Intent.ATTACK, calcAscensionDamage(10));
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
            case NASTY_PLOT: {
                applyToTarget(this, this, new NastyPlot(this, BUFF_AMT));
                break;
            }
            case SHADOW_BALL: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.POISON);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(NASTY_PLOT, MOVES[NASTY_PLOT]);
        } else if (lastTwoMoves(SHADOW_BALL)) {
            setMoveShortcut(NASTY_PLOT, MOVES[NASTY_PLOT]);
        } else {
            setMoveShortcut(SHADOW_BALL, MOVES[SHADOW_BALL]);
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Gengar();
    }

}