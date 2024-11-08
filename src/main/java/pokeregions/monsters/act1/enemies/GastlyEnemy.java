package pokeregions.monsters.act1.enemies;

import basemod.ReflectionHacks;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act1.Gastly;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.MonsterIntangiblePower;
import pokeregions.util.Details;
import pokeregions.util.Wiz;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.WeakPower;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

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
    public final int DEBUFF = calcAscensionSpecial(1);
    public final int INTANGIBLE = 1;

    public GastlyEnemy() {
        this(0.0f, 0.0f);
    }

    public GastlyEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0.0f, 160.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Gastly/Gastly.scml"));
        setHp(calcAscensionTankiness(36), calcAscensionTankiness(40));
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
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case LICK: {
                Details statusDetails = new Details(this, DAZED_AMT, DAZED_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetails);
                break;
            }
            case SUCKER_PUNCH: {
                Details powerDetail = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case GHOST_PARTY: {
                Details powerDetail = new Details(this, INTANGIBLE, INTANGIBLE_TEXTURE, Details.TargetType.ALL_ENEMIES);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Gastly();
    }

}