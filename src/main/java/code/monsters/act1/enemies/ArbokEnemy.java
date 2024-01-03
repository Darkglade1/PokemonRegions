package code.monsters.act1.enemies;

import code.BetterSpriterAnimation;
import code.cards.pokemonAllyCards.Arbok;
import code.monsters.AbstractPokemonMonster;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.powers.DrawReductionPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;

import java.util.ArrayList;

import static code.PokemonRegions.makeID;
import static code.PokemonRegions.makeMonsterPath;
import static code.util.Wiz.*;

public class ArbokEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(ArbokEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte WRAP = 0;
    private static final byte CRUNCH = 1;
    private static final byte GLARE = 2;

    public final int BLOCK = 8;
    public final int DEBUFF = calcAscensionSpecial(1);
    public final int DRAW_DOWN = 1;
    public final int CONSTRICTED = calcAscensionDamage(5);

    public ArbokEnemy() {
        this(0.0f, 0.0f);
    }

    public ArbokEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 150.0f, 160.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Arbok/Arbok.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(64), calcAscensionTankiness(68));
        addMove(WRAP, Intent.STRONG_DEBUFF);
        addMove(CRUNCH, Intent.ATTACK_DEBUFF, calcAscensionDamage(9));
        addMove(GLARE, Intent.DEFEND_DEBUFF);

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case WRAP: {
                applyToTarget(adp(), this, new ConstrictedPower(adp(), this, CONSTRICTED));
                break;
            }
            case CRUNCH: {
                useFastAttackAnimation();
                atb(new VFXAction(new BiteEffect(adp().hb.cX, adp().hb.cY), 0.3F));
                dmg(adp(), info, AbstractGameAction.AttackEffect.NONE);
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                break;
            }
            case GLARE: {
                block(this, BLOCK);
                applyToTarget(adp(), this, new DrawReductionPower(adp(), DRAW_DOWN));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(WRAP, MOVES[WRAP]);
        } else {
            if (!lastMove(WRAP) && !lastMoveBefore(WRAP)) {
                setMoveShortcut(WRAP, MOVES[WRAP]);
            } else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastMove(CRUNCH)) {
                    possibilities.add(CRUNCH);
                }
                if (!this.lastMove(GLARE)) {
                    possibilities.add(GLARE);
                }
                byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
                setMoveShortcut(move, MOVES[move]);
            }
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Arbok();
    }

}