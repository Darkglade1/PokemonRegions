package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Lanturn;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class LanturnEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(LanturnEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte EERIE_IMPULSE = 0;
    private static final byte ILLUMINATE = 1;
    private static final byte DISCHARGE = 2;

    public final int STATUS = calcAscensionSpecial(2);
    public final int STR_DOWN = 2;
    public final int DEBUFF = calcAscensionSpecial(1);
    public final int VULNERABLE = 2;

    public LanturnEnemy() {
        this(0.0f, 0.0f);
    }

    public LanturnEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 160.0f, 120.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Lanturn/Lanturn.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(75), calcAscensionTankiness(84));
        addMove(EERIE_IMPULSE, Intent.STRONG_DEBUFF);
        addMove(ILLUMINATE, Intent.ATTACK_DEBUFF, calcAscensionDamage(9));
        addMove(DISCHARGE, Intent.DEBUFF);

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
            case EERIE_IMPULSE: {
                applyToTarget(adp(), this, new StrengthPower(adp(), -STR_DOWN));
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                break;
            }
            case ILLUMINATE: {
                useFastAttackAnimation();
                atb(new VFXAction(new LightningEffect(adp().drawX, adp().drawY), 0.0f));
                atb(new SFXAction("ORB_LIGHTNING_EVOKE"));
                dmg(adp(), info, AbstractGameAction.AttackEffect.NONE);
                intoDiscardMo(new Dazed(), STATUS);
                break;
            }
            case DISCHARGE: {
                applyToTarget(adp(), this, new VulnerablePower(adp(), VULNERABLE, true));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(ILLUMINATE)) {
            setMoveShortcut(EERIE_IMPULSE, MOVES[EERIE_IMPULSE]);
        } else if (this.lastMove(EERIE_IMPULSE)) {
            setMoveShortcut(DISCHARGE, MOVES[DISCHARGE]);
        } else {
            setMoveShortcut(ILLUMINATE, MOVES[ILLUMINATE]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case EERIE_IMPULSE: {
                Details powerDetail = new Details(this, -STR_DOWN, STRENGTH_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail2);
                break;
            }
            case ILLUMINATE: {
                Details statusDetail = new Details(this, STATUS, DAZED_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                break;
            }
            case DISCHARGE: {
                Details powerDetail = new Details(this, VULNERABLE, VULNERABLE_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Lanturn();
    }

}