package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Slaking;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;
import pokeregions.vfx.SporeDustEffect;
import pokeregions.vfx.ThrowEffect;
import pokeregions.vfx.WaitEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class BreloomEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(BreloomEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SPORE = 0;
    private static final byte SWORDS_DANCE = 1;
    private static final byte BULLET_SEED = 2;

    public final int STR = calcAscensionSpecialSmall(3);
    public final int DEBUFF = 3;

    public static final String POWER_ID = makeID("Sleepy");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BreloomEnemy() {
        this(0.0f, 0.0f);
    }

    public BreloomEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 150.0f, 170.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Breloom/Breloom.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.2f);
        setHp(calcAscensionTankiness(220));
        addMove(SPORE, Intent.STRONG_DEBUFF);
        addMove(SWORDS_DANCE, Intent.BUFF);
        addMove(BULLET_SEED, Intent.ATTACK, calcAscensionDamage(5), 3);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SPORE:
                useFastAttackAnimation();
                atb(new SFXAction("ATTACK_MAGIC_FAST_3", MathUtils.random(0.88F, 0.92F), true));
                float x = adp().hb.cX;
                float y= adp().hb.cY + (adp().hb.height * 0.5f * Settings.scale);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        for (int i = 0; i < 5; i++) { AbstractDungeon.effectsQueue.add(new SporeDustEffect(x, y)); }
                        this.isDone = true;
                    }
                });
                atb(new VFXAction(new WaitEffect(), 1.0f));
                applyToTarget(adp(), this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.DEBUFF, false, adp(), DEBUFF) {

                    @Override
                    public void onEnergyRecharge() {
                        this.flash();
                        AbstractDungeon.player.loseEnergy(this.amount);
                        this.amount--;
                        if (this.amount <= 0) {
                            atb(new RemoveSpecificPowerAction(owner, owner, this));
                        }
                        this.updateDescription();
                    }

                    @Override
                    public void updateDescription() {
                        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
                    }
                });
                break;
            case SWORDS_DANCE:
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
            case BULLET_SEED: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    float duration = 0.5f;
                    atb(new VFXAction(ThrowEffect.throwEffect("BulletSeed.png", 1.0f, this.hb, adp().hb, Color.GREEN.cpy(), duration), duration));
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(SPORE)) {
            setMoveShortcut(SWORDS_DANCE, MOVES[SWORDS_DANCE]);
        } else if (this.lastMove(SWORDS_DANCE) || this.lastMoveBefore(SWORDS_DANCE)) {
            setMoveShortcut(BULLET_SEED, MOVES[BULLET_SEED]);
        } else {
            setMoveShortcut(SPORE, MOVES[SPORE]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        String textureString = makePowerPath("Sleepy32.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case SPORE: {
                Details powerDetail = new Details(this, DEBUFF, texture);
                details.add(powerDetail);
                break;
            }
            case SWORDS_DANCE: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Slaking();
    }

}