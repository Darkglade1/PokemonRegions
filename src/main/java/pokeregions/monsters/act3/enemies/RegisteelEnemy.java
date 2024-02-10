package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Registeel;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.BinaryBody;
import pokeregions.powers.MonsterNextTurnBlockPower;
import pokeregions.util.Details;
import pokeregions.util.Wiz;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class RegisteelEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(RegisteelEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SLAM = 0;
    private static final byte WIDE_GUARD = 1;

    public final int BASE_BLOCK = 12;
    public final int BASE_METALLICIZE = calcAscensionSpecialSmall(3);
    public final int BLOCK_INCREASE = 6;
    public final int METALLCIZE_INCREASE = 1;

    private int block = BASE_BLOCK;
    private int metallicize = BASE_METALLICIZE;

    public RegisteelEnemy() {
        this(0.0f, 0.0f);
    }

    public RegisteelEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 230.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Registeel/Registeel.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.5f);
        setHp(calcAscensionTankiness(120));
        addMove(SLAM, Intent.ATTACK, calcAscensionDamage(18));
        addMove(WIDE_GUARD, Intent.DEFEND_BUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new BinaryBody(this));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SLAM: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                block = BASE_BLOCK;
                metallicize = BASE_METALLICIZE;
                break;
            }
            case WIDE_GUARD: {
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new MonsterNextTurnBlockPower(mo, block));
                    applyToTarget(mo, this, new MetallicizePower(mo, metallicize));
                }
                block += BLOCK_INCREASE;
                metallicize += METALLCIZE_INCREASE;
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(WIDE_GUARD)) {
            setMoveShortcut(SLAM, MOVES[SLAM]);
        } else {
            setMoveShortcut(WIDE_GUARD, MOVES[WIDE_GUARD]);
        }
        super.postGetMove();
        createIntent();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case WIDE_GUARD: {
                Details blockDetail = new Details(this, block, BLOCK_TEXTURE, Details.TargetType.ALL_ENEMIES);
                details.add(blockDetail);
                Details powerDetail = new Details(this, metallicize, METALLICIZE_TEXTURE, Details.TargetType.ALL_ENEMIES);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Registeel();
    }

}