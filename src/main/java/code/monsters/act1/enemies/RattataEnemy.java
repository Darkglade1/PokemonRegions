package code.monsters.act1.enemies;

import basemod.ReflectionHacks;
import code.BetterSpriterAnimation;
import code.PokemonRegions;
import code.cards.pokemonAllyCards.Rattata;
import code.monsters.AbstractPokemonMonster;
import code.util.Details;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;

import java.util.ArrayList;

import static code.PokemonRegions.*;
import static code.PokemonRegions.STRENGTH_TEXTURE;
import static code.util.Wiz.*;

public class RattataEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(RattataEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte BITE = 0;
    private static final byte WORK = 1;

    public final int BUFF = 1;

    public RattataEnemy() {
        this(0.0f, 0.0f);
    }

    public RattataEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 130.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Rattata/Rattata.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(34), calcAscensionTankiness(38));
        addMove(BITE, Intent.ATTACK, calcAscensionDamage(6));
        addMove(WORK, Intent.ATTACK_BUFF, 3);

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
            case BITE: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
                break;
            }
            case WORK: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
                applyToTarget(this, this, new StrengthPower(this, BUFF));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(BITE)) {
            possibilities.add(BITE);
        }
        if (!this.lastTwoMoves(WORK)) {
            possibilities.add(WORK);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);

        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case WORK: {
                Details powerDetail = new Details(this, BUFF, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Rattata();
    }

}