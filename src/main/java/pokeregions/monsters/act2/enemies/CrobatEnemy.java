package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.actions.PureDamageAction;
import pokeregions.cards.pokemonAllyCards.act2.Crobat;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class CrobatEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(CrobatEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte QUICK_GUARD = 0;
    private static final byte AIR_SLASH = 1;
    private static final byte CROSS_POISON = 2;

    public final int BLOCK = 14;
    public final int BUFF = calcAscensionSpecial(2);
    private boolean buffFirst;

    public static final String POWER_ID = makeID("Infiltrator");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public CrobatEnemy() {
        this(0.0f, 0.0f, false);
    }

    public CrobatEnemy(final float x, final float y, boolean buffFirst) {
        super(NAME, ID, 140, 0.0F, 0, 170.0f, 110.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Crobat/Crobat.scml"));
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime((int)(time * Math.random()));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(65), calcAscensionTankiness(74));
        addMove(QUICK_GUARD, Intent.DEFEND_BUFF);
        addMove(AIR_SLASH, Intent.ATTACK, calcAscensionDamage(5), 2);
        addMove(CROSS_POISON, Intent.ATTACK_BUFF, calcAscensionDamage(8));
        this.buffFirst = buffFirst;

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
            case QUICK_GUARD: {
                block(this, BLOCK);
                applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, 0, "accuracy") {

                    boolean justApplied = true;

                    @Override
                    public int onAttacked(DamageInfo info, int damageAmount) {
                        if (info.owner != owner && info.type == DamageInfo.DamageType.NORMAL && damageAmount > 0) {
                            this.flash();
                            makePowerRemovable(this);
                            atb(new RemoveSpecificPowerAction(owner, owner, this));
                        }
                        return damageAmount;
                    }

                    @Override
                    public void atEndOfRound() {
                        if (justApplied) {
                            justApplied = false;
                        } else {
                            makePowerRemovable(this);
                            atb(new RemoveSpecificPowerAction(owner, owner, this));
                        }
                    }

                    @Override
                    public void updateDescription() {
                        description = POWER_DESCRIPTIONS[0];
                    }
                });
                break;
            }
            case AIR_SLASH: {
                useFastAttackAnimation();
                if (this.hasPower(POWER_ID)) {
                    atb(new PureDamageAction(adp(), new DamageInfo(null, info.output, DamageInfo.DamageType.HP_LOSS), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                    atb(new PureDamageAction(adp(), new DamageInfo(null, info.output, DamageInfo.DamageType.HP_LOSS), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                } else {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL);
                    dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_VERTICAL);
                }
                break;
            }
            case CROSS_POISON: {
                useFastAttackAnimation();
                if (this.hasPower(POWER_ID)) {
                    atb(new PureDamageAction(adp(), new DamageInfo(null, info.output, DamageInfo.DamageType.HP_LOSS), AbstractGameAction.AttackEffect.POISON));
                } else {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.POISON);
                }
                applyToTarget(this, this, new StrengthPower(this, BUFF));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (buffFirst) {
            if (this.lastMove(CROSS_POISON)) {
                setMoveShortcut(QUICK_GUARD, MOVES[QUICK_GUARD]);
            } else if (this.lastMove(QUICK_GUARD)) {
                setMoveShortcut(AIR_SLASH, MOVES[AIR_SLASH]);
            } else {
                setMoveShortcut(CROSS_POISON, MOVES[CROSS_POISON]);
            }
        } else {
            if (this.lastMove(QUICK_GUARD)) {
                setMoveShortcut(AIR_SLASH, MOVES[AIR_SLASH]);
            } else if (this.lastMove(AIR_SLASH)) {
                setMoveShortcut(CROSS_POISON, MOVES[CROSS_POISON]);
            } else {
                setMoveShortcut(QUICK_GUARD, MOVES[QUICK_GUARD]);
            }
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        String textureString = makePowerPath("Infiltrator32.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case QUICK_GUARD: {
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                Details powerDetail = new Details(this, 1, texture);
                details.add(powerDetail);
                break;
            }
            case CROSS_POISON: {
                Details powerDetail = new Details(this, BUFF, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Crobat();
    }

}