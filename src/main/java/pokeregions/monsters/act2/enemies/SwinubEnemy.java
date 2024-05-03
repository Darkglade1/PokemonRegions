package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.Frozen;
import pokeregions.cards.pokemonAllyCards.act2.Azumarill;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class SwinubEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(SwinubEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte ICE_SHARD = 0;
    private static final byte ICY_WIND = 1;

    public final int STATUS_DRAW = 1;
    public final int STATUS_DISCARD = calcAscensionSpecial(1);
    public final int STR = 1;
    private boolean attackFirst;

    public SwinubEnemy() {
        this(0.0f, 0.0f, false);
    }
    public SwinubEnemy(final float x, final float y, boolean attackFirst) {
        super(NAME, ID, 140, 0.0F, 0, 140.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Swinub/Swinub.scml"));
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime((int)(time * Math.random()));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(45), calcAscensionTankiness(52));
        addMove(ICE_SHARD, Intent.ATTACK, calcAscensionDamage(8));
        addMove(ICY_WIND, Intent.DEBUFF);
        this.attackFirst = attackFirst;

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
            case ICE_SHARD: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
                break;
            }
            case ICY_WIND: {
                AbstractCard status = new Frozen();
                intoDrawMo(status.makeStatEquivalentCopy(), STATUS_DRAW);
                intoDiscardMo(status.makeStatEquivalentCopy(), STATUS_DISCARD);
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (attackFirst) {
            if (this.lastMove(ICE_SHARD)) {
                setMoveShortcut(ICY_WIND, MOVES[ICY_WIND]);
            } else {
                setMoveShortcut(ICE_SHARD, MOVES[ICE_SHARD]);
            }
        } else {
            if (this.lastMove(ICY_WIND)) {
                setMoveShortcut(ICE_SHARD, MOVES[ICE_SHARD]);
            } else {
                setMoveShortcut(ICY_WIND, MOVES[ICY_WIND]);
            }
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case ICY_WIND: {
                Details statusDetail = new Details(this, STATUS_DRAW, FROZEN_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                Details statusDetail2 = new Details(this, STATUS_DISCARD, FROZEN_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail2);
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Azumarill();
    }

}