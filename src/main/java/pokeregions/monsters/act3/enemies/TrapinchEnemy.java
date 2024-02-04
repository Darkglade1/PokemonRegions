package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Aron;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class TrapinchEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(TrapinchEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte CRIPPLING_BITE = 0;
    private static final byte CRUNCH = 1;
    private static final byte TRAP_PREY = 2;

    public final int STR = 2;
    public final int STATUS = calcAscensionSpecial(1);
    public final int DEBUFF = 1;
    private boolean onlyAttack;

    public TrapinchEnemy() {
        this(0.0f, 0.0f, false);
    }

    public TrapinchEnemy(final float x, final float y, boolean onlyAttack) {
        super(NAME, ID, 140, 0.0F, 0, 100.0f, 90.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Trapinch/Trapinch.scml"));
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime((int)(time * Math.random()));
        setHp(calcAscensionTankiness(62), calcAscensionTankiness(70));
        addMove(CRIPPLING_BITE, Intent.ATTACK_DEBUFF, calcAscensionDamage(5));
        addMove(CRUNCH, Intent.ATTACK, calcAscensionDamage(5), 2);
        addMove(TRAP_PREY, Intent.DEBUFF);
        this.onlyAttack = onlyAttack;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case CRIPPLING_BITE: {
                useFastAttackAnimation();
                atb(new VFXAction(new BiteEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.3F));
                dmg(adp(), info, AbstractGameAction.AttackEffect.NONE);
                applyToTarget(adp(), this, new DexterityPower(adp(), -DEBUFF));
                break;
            }
            case CRUNCH: {
                useFastAttackAnimation();
                atb(new VFXAction(new BiteEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.3F));
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.NONE);
                }
                break;
            }
            case TRAP_PREY: {
                intoDiscardMo(new Wound(), STATUS);
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (onlyAttack) {
            if (this.lastMove(CRUNCH)) {
                setMoveShortcut(CRIPPLING_BITE, MOVES[CRIPPLING_BITE]);
            } else {
                setMoveShortcut(CRUNCH, MOVES[CRUNCH]);
            }
        } else {
            if (this.lastMove(TRAP_PREY)) {
                setMoveShortcut(CRUNCH, MOVES[CRUNCH]);
            } else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastTwoMoves(CRUNCH)) {
                    possibilities.add(CRUNCH);
                }
                if (!this.lastMove(CRIPPLING_BITE)) {
                    possibilities.add(CRIPPLING_BITE);
                }
                if (!this.lastMove(TRAP_PREY)) {
                    possibilities.add(TRAP_PREY);
                }
                byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
                setMoveShortcut(move, MOVES[move]);
            }
        }

        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case CRIPPLING_BITE: {
                Details powerDetail = new Details(this, -DEBUFF, DEXTERITY_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case TRAP_PREY: {
                Details statusDetail = new Details(this, STATUS, WOUND_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Aron();
    }

}