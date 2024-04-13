package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Slugma;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.Burn;
import pokeregions.powers.VisibleBarricadePower;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class SlugmaEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(SlugmaEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte HARDEN = 0;
    private static final byte ROCK_THROW = 1;
    private static final byte LAVA_PLUME = 2;

    public final int STATUS = 1;
    public final int BIG_BLOCK = 11;
    public final int SMALL_BLOCK = 6;
    public final int POWER_BURN = 1;

    public static final String POWER_ID = makeID("FlameBody");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SlugmaEnemy() {
        this(0.0f, 0.0f);
    }

    public SlugmaEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 130.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Slugma/Slugma.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime((int)(time * Math.random()));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(64), calcAscensionTankiness(72));
        addMove(HARDEN, Intent.DEFEND_DEBUFF);
        addMove(ROCK_THROW, Intent.ATTACK_DEFEND, calcAscensionDamage(9));
        addMove(LAVA_PLUME, Intent.ATTACK, calcAscensionDamage(5), 2);

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new VisibleBarricadePower(this));
        block(this, SMALL_BLOCK);
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, POWER_BURN, "flameBarrier") {
            @Override
            public int onAttacked(DamageInfo info, int damageAmount) {
                if (info.type == DamageInfo.DamageType.NORMAL && info.owner != this.owner) {
                    if (damageAmount < info.output) {
                        flash();
                        applyToTarget(info.owner, owner, new Burn(info.owner, amount));
                    }
                }
                return damageAmount;
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
            case HARDEN: {
                block(this, BIG_BLOCK);
                intoDiscardMo(new com.megacrit.cardcrawl.cards.status.Burn(), STATUS);
                if (AbstractDungeon.ascensionLevel >= 17) {
                    intoDrawMo(new com.megacrit.cardcrawl.cards.status.Burn(), STATUS);
                }
                break;
            }
            case ROCK_THROW: {
                useFastAttackAnimation();
                block(this, SMALL_BLOCK);
                dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                break;
            }
            case LAVA_PLUME: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(HARDEN) && !this.lastMoveBefore(HARDEN)) {
            possibilities.add(HARDEN);
        }
        if (!this.lastMove(ROCK_THROW) && !this.lastMoveBefore(ROCK_THROW)) {
            possibilities.add(ROCK_THROW);
        }
        if (!this.lastMove(LAVA_PLUME) && !this.lastMoveBefore(LAVA_PLUME)) {
            possibilities.add(LAVA_PLUME);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);

        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case HARDEN: {
                Details blockDetail = new Details(this, BIG_BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                Details statusDetail = new Details(this, STATUS, BURN_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                if (AbstractDungeon.ascensionLevel >= 17) {
                    Details statusDetail2 = new Details(this, STATUS, BURN_TEXTURE, Details.TargetType.DRAW_PILE);
                    details.add(statusDetail2);
                }
                break;
            }
            case ROCK_THROW: {
                Details blockDetail = new Details(this, SMALL_BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Slugma();
    }

}