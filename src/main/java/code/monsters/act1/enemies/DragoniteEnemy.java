package code.monsters.act1.enemies;

import code.BetterSpriterAnimation;
import code.cards.pokemonAllyCards.Dragonite;
import code.monsters.AbstractPokemonMonster;
import code.powers.Outrage;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static code.PokemonRegions.makeID;
import static code.PokemonRegions.makeMonsterPath;
import static code.util.Wiz.*;

public class DragoniteEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(DragoniteEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte DRAGON_DANCE = 0;
    private static final byte OUTRAGE = 1;
    private static final byte STUNNED = 2;

    public final int OUTRAGE_BASE_DAMAGE = calcAscensionDamage(15);
    public final int OUTRAGE_DAMAGE_INCREASE = calcAscensionSpecial(5);
    public final int STR = 5;
    public final int OUTRAGE_BASE_TURNS = 2;
    public final int OUTRAGE_DAMAGE_THRESHOLD = 25;

    public DragoniteEnemy() {
        this(0.0f, 0.0f);
    }

    public DragoniteEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 250.0f, 270.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Dragonite/Dragonite.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 2.0f);
        setHp(calcAscensionTankiness(300));
        addMove(DRAGON_DANCE, Intent.BUFF);
        addMove(OUTRAGE, Intent.ATTACK, OUTRAGE_BASE_DAMAGE);
        addMove(STUNNED, Intent.STUN);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case DRAGON_DANCE: {
                applyToTarget(this, this, new StrengthPower(this, STR));
                applyToTarget(this, this, new Outrage(this, OUTRAGE_BASE_TURNS, OUTRAGE_DAMAGE_THRESHOLD));
                break;
            }
            case OUTRAGE: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                atb(new ReducePowerAction(this, this, Outrage.POWER_ID, 1));
                int newDamage = moves.get(OUTRAGE).baseDamage += OUTRAGE_DAMAGE_INCREASE;
                addMove(OUTRAGE, Intent.ATTACK, newDamage);
                break;
            }
            case STUNNED: {
                addMove(OUTRAGE, Intent.ATTACK, OUTRAGE_BASE_DAMAGE);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(DRAGON_DANCE)) {
            setMoveShortcut(OUTRAGE, MOVES[OUTRAGE]);
        } else if (lastMove(OUTRAGE)) {
            if (this.hasPower(Outrage.POWER_ID)) {
                setMoveShortcut(OUTRAGE, MOVES[OUTRAGE]);
            } else {
                setMoveShortcut(STUNNED, MOVES[STUNNED]);
            }
        } else {
            setMoveShortcut(DRAGON_DANCE, MOVES[DRAGON_DANCE]);
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Dragonite();
    }

}