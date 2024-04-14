package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
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
import com.megacrit.cardcrawl.powers.*;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Scizor;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class SteelixEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(SteelixEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte DOUBLE_EDGE = 0;
    private static final byte HEAVY_SLAM = 1;
    private static final byte IRON_TAIL = 2;
    private static final byte AUTOTOMIZE = 3;

    public final int STR = calcAscensionSpecialSmall(5);
    public final int BLOCK = calcAscensionSpecial(10);
    public final int DEBUFF = 2;
    public final int SELF_DEBUFF = 1;
    public final int POWER_BUFF = 34;
    public final int METALLICIZE = 15;
    private boolean powerTriggered = false;
    public static final int AUTOTOMIZE_COOLDOWN = 3;
    private int cooldown = AUTOTOMIZE_COOLDOWN;

    public static final String POWER_ID = makeID("SheerForce");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SteelixEnemy() {
        this(0.0f, 0.0f);
    }

    public SteelixEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 300.0f, 240.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Steelix/Steelix.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        setHp(calcAscensionTankiness(160));
        addMove(DOUBLE_EDGE, Intent.ATTACK, calcAscensionDamage(24));
        addMove(HEAVY_SLAM, Intent.ATTACK_DEBUFF, calcAscensionDamage(18));
        addMove(IRON_TAIL, Intent.ATTACK_BUFF, calcAscensionDamage(12));
        addMove(AUTOTOMIZE, Intent.DEFEND_BUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        block(this, METALLICIZE);
        applyToTarget(this, this, new MetallicizePower(this, METALLICIZE));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, POWER_BUFF, "doubleDamage", 99) {

            @Override
            public float atDamageGive(float damage, DamageInfo.DamageType type) {
                if (type == DamageInfo.DamageType.NORMAL && powerTriggered) {
                    return damage * (1 + ((float)amount / 100));
                } else {
                    return damage;
                }
            }

            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null) {
                    powerTriggered = false;
                    AbstractDungeon.onModifyPower();
                }
            }

            @Override
            public int onAttacked(DamageInfo info, int damageAmount) {
                if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null) {
                    if (!powerTriggered && damageAmount > 0) {
                        if (owner instanceof AbstractMonster) {
                            if (((AbstractMonster) owner).getIntentBaseDmg() >= 0) {
                                this.flash();
                                powerTriggered = true;
                                clearAttackingDetailedIntent();
                                AbstractDungeon.onModifyPower();
                            }
                        }
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
            case DOUBLE_EDGE: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                if (!powerTriggered) {
                    applyToTarget(this, this, new VulnerablePower(this, SELF_DEBUFF, true));
                }
                break;
            }
            case HEAVY_SLAM: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                if (!powerTriggered) {
                    applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                    applyToTarget(adp(), this, new VulnerablePower(adp(), DEBUFF, true));
                }
                break;
            }
            case IRON_TAIL: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                if (!powerTriggered) {
                    applyToTarget(this, this, new StrengthPower(this, STR));
                }
                break;
            }
            case AUTOTOMIZE: {
                block(this, BLOCK);
                applyToTarget(this, this, new StrengthPower(this, STR));
                cooldown = AUTOTOMIZE_COOLDOWN + 1;
                break;
            }
        }
        cooldown--;
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (AbstractDungeon.ascensionLevel >= 18) {
            if (lastMove(HEAVY_SLAM)) {
                setMoveShortcut(IRON_TAIL, MOVES[IRON_TAIL]);
            } else if (lastMove(IRON_TAIL)) {
                setMoveShortcut(DOUBLE_EDGE, MOVES[DOUBLE_EDGE]);
            } else if (lastMove(DOUBLE_EDGE)) {
                setMoveShortcut(AUTOTOMIZE, MOVES[AUTOTOMIZE]);
            } else {
                setMoveShortcut(HEAVY_SLAM, MOVES[HEAVY_SLAM]);
            }
        } else {
            if (cooldown <= 0) {
                setMoveShortcut(AUTOTOMIZE, MOVES[AUTOTOMIZE]);
            } else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastMove(HEAVY_SLAM) && !this.lastMoveBefore(HEAVY_SLAM)) {
                    possibilities.add(HEAVY_SLAM);
                }
                if (!this.lastMove(IRON_TAIL) && !this.lastMoveBefore(IRON_TAIL)) {
                    possibilities.add(IRON_TAIL);
                }
                if (!this.lastMove(DOUBLE_EDGE) && !this.lastMoveBefore(DOUBLE_EDGE)) {
                    possibilities.add(DOUBLE_EDGE);
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
            case DOUBLE_EDGE: {
                Details powerDetail = new Details(this, SELF_DEBUFF, VULNERABLE_TEXTURE, Details.TargetType.SELF);
                details.add(powerDetail);
                break;
            }
            case HEAVY_SLAM: {
                Details powerDetail = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, DEBUFF, VULNERABLE_TEXTURE);
                details.add(powerDetail2);
                break;
            }
            case IRON_TAIL: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case AUTOTOMIZE: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    private void clearAttackingDetailedIntent() {
        ArrayList<Details> details = new ArrayList<>();
        if (this.getIntentBaseDmg() >= 0) {
            PokemonRegions.intents.put(this, details);
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Scizor();
    }

}