package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Scizor;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.Fortitude;
import pokeregions.powers.MonsterNextTurnBlockPower;
import pokeregions.powers.Taunt;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;
import pokeregions.util.Wiz;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class TyranitarEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(TyranitarEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte IRON_DEFENSE = 0;
    private static final byte STONE_EDGE = 1;
    private static final byte CRUNCH = 2;

    public final int SELF_BLOCK = 16;
    public final int GROUP_BLOCK = 9;
    public final int BUFF = calcAscensionSpecial(2);
    public final int HP_THRESHOLD = 50;
    public boolean isTaunting = false;

    public static final String POWER_ID = makeID("ProtectiveInstincts");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public TyranitarEnemy() {
        this(0.0f, 0.0f);
    }

    public TyranitarEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 190.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Tyranitar/Tyranitar.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.3f);
        setHp(calcAscensionTankiness(102), calcAscensionTankiness(114));
        addMove(IRON_DEFENSE, Intent.DEFEND_BUFF);
        addMove(STONE_EDGE, Intent.ATTACK_DEFEND, calcAscensionDamage(12));
        addMove(CRUNCH, Intent.ATTACK, calcAscensionDamage(20));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, 0, "talk_to_hand") {
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + HP_THRESHOLD + POWER_DESCRIPTIONS[1];
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
            case IRON_DEFENSE: {
                if (isTaunting) {
                    block(this, SELF_BLOCK);
                } else {
                    for (AbstractMonster mo : Wiz.getEnemies()) {
                        applyToTarget(mo, this, new MonsterNextTurnBlockPower(mo, GROUP_BLOCK));
                    }
                }
                applyToTarget(this, this, new Fortitude(this, BUFF, true));
                break;
            }
            case STONE_EDGE: {
                useFastAttackAnimation();
                if (isTaunting) {
                    block(this, SELF_BLOCK);
                } else {
                    for (AbstractMonster mo : Wiz.getEnemies()) {
                        applyToTarget(mo, this, new MonsterNextTurnBlockPower(mo, GROUP_BLOCK));
                    }
                }
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
            case CRUNCH: {
                useFastAttackAnimation();
                atb(new VFXAction(new BiteEffect(AbstractDungeon.player.hb.cX + MathUtils.random(-50.0F, 50.0F) * Settings.scale, AbstractDungeon.player.hb.cY + MathUtils.random(-50.0F, 50.0F) * Settings.scale, Color.CHARTREUSE.cpy()), 0.2F));
                dmg(adp(), info, AbstractGameAction.AttackEffect.NONE);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(IRON_DEFENSE)) {
            setMoveShortcut(STONE_EDGE, MOVES[STONE_EDGE]);
        } else if (lastMove(STONE_EDGE)) {
            setMoveShortcut(CRUNCH, MOVES[CRUNCH]);
        } else {
            boolean foundAlly = false;
            for (AbstractMonster mo : Wiz.getEnemies()) {
                if (mo instanceof PupitarEnemy && !mo.isDeadOrEscaped()) {
                    foundAlly = true;
                }
            }
            if (foundAlly) {
                setMoveShortcut(IRON_DEFENSE, MOVES[IRON_DEFENSE]);
            } else {
                setMoveShortcut(STONE_EDGE, MOVES[STONE_EDGE]);
            }
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        String textureString = makePowerPath("Fortitude32.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case IRON_DEFENSE: {
                Details powerDetail = new Details(this, BUFF, texture);
                details.add(powerDetail);
                Details blockDetail;
                if (isTaunting) {
                    blockDetail = new Details(this, SELF_BLOCK, BLOCK_TEXTURE);
                } else {
                    blockDetail = new Details(this, GROUP_BLOCK, BLOCK_TEXTURE, Details.TargetType.ALL_ENEMIES);
                }
                details.add(blockDetail);
                break;
            }
            case STONE_EDGE: {
                Details blockDetail;
                if (isTaunting) {
                    blockDetail = new Details(this, SELF_BLOCK, BLOCK_TEXTURE);
                } else {
                    blockDetail = new Details(this, GROUP_BLOCK, BLOCK_TEXTURE, Details.TargetType.ALL_ENEMIES);
                }
                details.add(blockDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    public void startTaunting() {
        if (!isTaunting) {
            applyToTarget(this, this, new Taunt(this, true, false));
            isTaunting = true;
            setDetailedIntents();
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Scizor();
    }

}