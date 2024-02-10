package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.NextTurnBlockPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Salamence;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.BinaryBody;
import pokeregions.util.Details;
import pokeregions.util.Wiz;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class RegirockEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(RegirockEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SLAM = 0;
    private static final byte ANCIENT_POWER = 1;

    public final int BASE_STR = 3;
    public final int BASE_STATUS = calcAscensionSpecial(1);
    public final int STR_INCREASE = 2;
    public final int STATUS_INCREASE = 1;

    private int str = BASE_STR;
    private int status = BASE_STATUS;

    public RegirockEnemy() {
        this(0.0f, 0.0f);
    }

    public RegirockEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 230.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Regirock/Regirock.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.5f);
        setHp(calcAscensionTankiness(120));
        addMove(SLAM, Intent.ATTACK, calcAscensionDamage(18));
        addMove(ANCIENT_POWER, Intent.BUFF);
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
                str = BASE_STR;
                status = BASE_STATUS;
                break;
            }
            case ANCIENT_POWER: {
                intoDiscardMo(new Wound(), status);
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new StrengthPower(mo, str));
                }
                str += STR_INCREASE;
                status += STATUS_INCREASE;
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(ANCIENT_POWER)) {
            setMoveShortcut(SLAM, MOVES[SLAM]);
        } else {
            setMoveShortcut(ANCIENT_POWER, MOVES[ANCIENT_POWER]);
        }
        super.postGetMove();
        createIntent();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case ANCIENT_POWER: {
                Details statusDetail = new Details(this, status, WOUND_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                Details powerDetail = new Details(this, str, STRENGTH_TEXTURE, Details.TargetType.ALL_ENEMIES);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Salamence();
    }

}