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
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.Frozen;
import pokeregions.cards.pokemonAllyCards.act3.Salamence;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.BinaryBody;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class RegiceEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(RegiceEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SLAM = 0;
    private static final byte ICY_WIND = 1;

    public final int BASE_DEBUFF = 1;
    public final int BASE_STATUS = calcAscensionSpecial(2);
    public final int DEBUFF_INCREASE = 1;
    public final int STATUS_INCREASE = 1;

    private int debuff = BASE_DEBUFF;
    private int status = BASE_STATUS;

    public RegiceEnemy() {
        this(0.0f, 0.0f);
    }

    public RegiceEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 230.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Regice/Regice.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.5f);
        setHp(calcAscensionTankiness(120));
        addMove(SLAM, Intent.ATTACK, calcAscensionDamage(18));
        addMove(ICY_WIND, Intent.DEBUFF);
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
                debuff = BASE_DEBUFF;
                status = BASE_STATUS;
                break;
            }
            case ICY_WIND: {
                intoDrawMo(new Frozen(), status);
                applyToTarget(adp(), this, new WeakPower(adp(), debuff, true));
                applyToTarget(adp(), this, new FrailPower(adp(), debuff, true));
                debuff += DEBUFF_INCREASE;
                status += STATUS_INCREASE;
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(ICY_WIND)) {
            setMoveShortcut(SLAM, MOVES[SLAM]);
        } else {
            setMoveShortcut(ICY_WIND, MOVES[ICY_WIND]);
        }
        super.postGetMove();
        createIntent();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case ICY_WIND: {
                Details statusDetail = new Details(this, status, FROZEN_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                Details powerDetail = new Details(this, debuff, WEAK_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, debuff, FRAIL_TEXTURE);
                details.add(powerDetail2);
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