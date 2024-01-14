package pokeregions.monsters.act1.enemies;

import basemod.ReflectionHacks;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.Haunter;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.util.Details;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Pain;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class HaunterEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(HaunterEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte CURSE = 0;
    private static final byte SPITE = 1;
    private static final byte NIGHT_SHADE = 2;

    public final int CURSE_AMT = calcAscensionSpecial(1);
    public final int DEBUFF = 1;

    public HaunterEnemy() {
        this(0.0f, 0.0f);
    }

    public HaunterEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0.0f, 160.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Haunter/Haunter.scml"));
        setHp(calcAscensionTankiness(46), calcAscensionTankiness(50));
        addMove(CURSE, Intent.STRONG_DEBUFF);
        addMove(SPITE, Intent.ATTACK_DEBUFF, calcAscensionDamage(8));
        addMove(NIGHT_SHADE, Intent.ATTACK, calcAscensionDamage(12));
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
            case CURSE: {
                intoDiscardMo(new Pain(), CURSE_AMT);
                break;
            }
            case SPITE: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                break;
            }
            case NIGHT_SHADE: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.POISON);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(CURSE)) {
            setMoveShortcut(SPITE, MOVES[SPITE]);
        } else if (lastMove(SPITE)) {
            setMoveShortcut(NIGHT_SHADE, MOVES[NIGHT_SHADE]);
        } else {
            setMoveShortcut(CURSE, MOVES[CURSE]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case CURSE: {
                Details statusDetails = new Details(this, CURSE_AMT, PAIN_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetails);
                break;
            }
            case SPITE: {
                Details powerDetail = new Details(this, DEBUFF, FRAIL_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Haunter();
    }

}