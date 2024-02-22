package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Aggron;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class AggronEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(AggronEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte IRON_TAIL = 0;
    private static final byte METAL_BURST = 1;
    private static final byte METAL_SOUND = 2;

    public final int BLOCK = 14;
    public final int MOVE_THORNS = 1;
    public final int POWER_THORNS = calcAscensionSpecial(2);
    public final int INITIAL_ARTIFACT = 2;
    public final int STATUS = 2;
    public final int DEBUFF = calcAscensionSpecial(1);

    public static final String POWER_ID = makeID("IronEater");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public AggronEnemy() {
        this(0.0f, 0.0f);
    }

    public AggronEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 220.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Aggron/Aggron.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.3f);
        setHp(calcAscensionTankiness(76), calcAscensionTankiness(85));
        addMove(IRON_TAIL, Intent.ATTACK, calcAscensionDamage(13));
        addMove(METAL_BURST, Intent.DEFEND_BUFF);
        addMove(METAL_SOUND, Intent.DEBUFF);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new ArtifactPower(this, INITIAL_ARTIFACT));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, POWER_THORNS) {
            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (info.owner != target && info.type == DamageInfo.DamageType.NORMAL && damageAmount < info.output) {
                    this.flash();
                    block(owner, info.output - damageAmount);
                }
            }

            @Override
            public void onSpecificTrigger() {
                if (owner.currentBlock > 0) {
                    this.flash();
                    applyToTarget(owner, owner, new ThornsPower(owner, amount));
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case IRON_TAIL: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
            case METAL_BURST: {
                block(this, BLOCK);
                applyToTarget(this, this, new ThornsPower(this, MOVE_THORNS));
                break;
            }
            case METAL_SOUND: {
                intoDrawMo(new Wound(), STATUS);
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (!this.lastMove(IRON_TAIL)) {
            setMoveShortcut(IRON_TAIL, MOVES[IRON_TAIL]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            possibilities.add(METAL_BURST);
            possibilities.add(METAL_SOUND);
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }

        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case METAL_BURST: {
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                Details powerDetail = new Details(this, MOVE_THORNS, THORNS_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case METAL_SOUND: {
                Details statusDetail = new Details(this, STATUS, WOUND_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                Details powerDetail = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, DEBUFF, FRAIL_TEXTURE);
                details.add(powerDetail2);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Aggron();
    }

}