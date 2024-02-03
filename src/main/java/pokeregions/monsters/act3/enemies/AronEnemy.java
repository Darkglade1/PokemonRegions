package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Aron;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class AronEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(AronEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte NIBBLE = 0;
    private static final byte METAL_BURST = 1;

    public final int BLOCK = 10;
    public final int MOVE_THORNS = 1;
    public final int POWER_THORNS = calcAscensionSpecial(2);
    public final int INITIAL_ARTIFACT = calcAscensionSpecial(1);

    public static final String POWER_ID = makeID("IronEater");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public AronEnemy() {
        this(0.0f, 0.0f);
    }

    public AronEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 100.0f, 70.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Aron/Aron.scml"));
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime((int)(time * Math.random()));
        setHp(calcAscensionTankiness(44), calcAscensionTankiness(50));
        addMove(NIBBLE, Intent.ATTACK, calcAscensionDamage(9));
        addMove(METAL_BURST, Intent.DEFEND_BUFF);
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

        AbstractPower power = this.getPower(POWER_ID);
        if (power != null && this.currentBlock > 0) {
            power.flash();
            applyToTarget(this, this, new ThornsPower(this, POWER_THORNS));
        }

        switch (this.nextMove) {
            case NIBBLE: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                break;
            }
            case METAL_BURST: {
                block(this, BLOCK);
                applyToTarget(this, this, new ThornsPower(this, MOVE_THORNS));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(NIBBLE)) {
            possibilities.add(NIBBLE);
        }
        if (!this.lastMove(METAL_BURST)) {
            possibilities.add(METAL_BURST);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
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
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Aron();
    }

}