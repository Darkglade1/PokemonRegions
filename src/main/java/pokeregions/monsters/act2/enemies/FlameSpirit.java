package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class FlameSpirit extends AbstractPokemonMonster
{
    public static final String ID = makeID(FlameSpirit.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    public static final byte BUFF = 0;

    public static final byte DEBUFF = 1;
    public static final byte EXTINGUISH = 2;
    public final int HEAL = 15;
    public final int DEBUFF_STATUS = 1;
    public final int ATK_STATUS = calcAscensionSpecial(1);
    private final AbstractMonster summoner;

    public FlameSpirit() {
        this(0.0f, 0.0f, null);
    }

    public FlameSpirit(final float x, final float y, AbstractMonster summoner) {
        super(NAME, ID, 140, 0.0F, 0, 80.0f, 160.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("HoOh/FlameSpirit/Spriter/FlameSpirit.scml"));
        this.summoner = summoner;
        setHp(calcAscensionTankiness(50), calcAscensionTankiness(58));
        addMove(BUFF, Intent.BUFF);
        addMove(DEBUFF, Intent.DEBUFF);
        addMove(EXTINGUISH, Intent.ATTACK_DEBUFF, calcAscensionDamage(20));
        isCatchable = false;
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
            case BUFF: {
                if (summoner != null) {
                    atb(new HealAction(summoner, this, HEAL));
                }
                break;
            }
            case DEBUFF: {
                if (AbstractDungeon.ascensionLevel >= 19) {
                    intoDrawMo(new Burn(), DEBUFF_STATUS);
                } else {
                    intoDiscardMo(new Burn(), DEBUFF_STATUS);
                }
                break;
            }
            case EXTINGUISH: {
                atb(new VFXAction(new ExplosionSmallEffect(this.hb.cX, this.hb.cY), 0.1F));
                dmg(adp(), info);
                intoDiscardMo(new Burn(), ATK_STATUS);
                atb(new SuicideAction(this));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(BUFF)) {
            setMoveShortcut(DEBUFF, MOVES[DEBUFF]);
        } else if(lastMove(DEBUFF)) {
            setMoveShortcut(EXTINGUISH, MOVES[EXTINGUISH]);
        } else {
            setMoveShortcut(BUFF, MOVES[BUFF]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case BUFF: {
                Details healDetail = new Details(this, HEAL, HEAL_TEXTURE);
                details.add(healDetail);
                break;
            }
            case DEBUFF: {
                Details statusDetail;
                if (AbstractDungeon.ascensionLevel >= 19) {
                    statusDetail = new Details(this, DEBUFF_STATUS, BURN_TEXTURE, Details.TargetType.DRAW_PILE);
                } else {
                    statusDetail = new Details(this, DEBUFF_STATUS, BURN_TEXTURE, Details.TargetType.DISCARD_PILE);
                }
                details.add(statusDetail);
                break;
            }
            case EXTINGUISH: {
                Details statusDetail = new Details(this, ATK_STATUS, BURN_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                Details detail = new Details(this, Details.DIES);
                details.add(detail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

}