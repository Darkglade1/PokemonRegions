package pokeregions.monsters.act2.enemies;

import actlikeit.dungeons.CustomDungeon;
import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.cardMods.ShadowCurseMod;
import pokeregions.cards.pokemonAllyCards.act2.Lugia;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;
import pokeregions.vfx.FlexibleDivinityParticleEffect;
import pokeregions.vfx.FlexibleStanceAuraEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class LugiaEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(LugiaEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SHADOW_RUSH = 0;
    private static final byte SHADOW_BLAST = 1;
    private static final byte SHADOW_SKY = 2;

    public final int DEBUFF = 1;
    public final int STRONG_DEBUFF = calcAscensionSpecial(2);
    public final int POWER_NUM_TRIGGERS = 2;

    private float particleTimer;
    private float particleTimer2;

    public static final String POWER_ID = makeID("Shadow");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public LugiaEnemy() {
        this(100.0f, 0.0f);
    }

    public LugiaEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 250.0f, 290.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Lugia/Lugia.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.2f);
        setHp(calcAscensionTankiness(430));
        addMove(SHADOW_RUSH, Intent.ATTACK, calcAscensionDamage(26));
        addMove(SHADOW_BLAST, Intent.ATTACK_DEBUFF, calcAscensionDamage(14));
        addMove(SHADOW_SKY, Intent.STRONG_DEBUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, POWER_NUM_TRIGGERS, "corruption") {
            @Override
            public void onUseCard(AbstractCard card, UseCardAction action) {
                if (this.amount > 0) {
                    this.flash();
                    this.amount--;
                    ShadowCurseMod mod = new ShadowCurseMod();
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            if (!CardModifierManager.hasModifier(card, ShadowCurseMod.ID)) {
                                CardModifierManager.addModifier(card, mod.makeCopy());
                            }
                            this.isDone = true;
                        }
                    });
                }
            }

            @Override
            public void atEndOfRound() {
                this.amount = POWER_NUM_TRIGGERS;
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + POWER_NUM_TRIGGERS + POWER_DESCRIPTIONS[1];
            }
        });
        CustomDungeon.playTempMusicInstantly("Zinnia");
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SHADOW_RUSH: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                break;
            }
            case SHADOW_BLAST: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.FIRE);
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new VulnerablePower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                break;
            }
            case SHADOW_SKY: {
                applyToTarget(adp(), this, new StrengthPower(adp(), -STRONG_DEBUFF));
                applyToTarget(adp(), this, new DexterityPower(adp(), -STRONG_DEBUFF));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(SHADOW_BLAST)) {
            setMoveShortcut(SHADOW_RUSH, MOVES[SHADOW_RUSH]);
        } else if (lastMove(SHADOW_RUSH)) {
            if (lastMoveBefore(SHADOW_RUSH)) {
                setMoveShortcut(SHADOW_SKY, MOVES[SHADOW_SKY]);
            } else {
                setMoveShortcut(SHADOW_RUSH, MOVES[SHADOW_RUSH]);
            }
        } else {
            setMoveShortcut(SHADOW_BLAST, MOVES[SHADOW_BLAST]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case SHADOW_BLAST: {
                Details powerDetail = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, DEBUFF, VULNERABLE_TEXTURE);
                details.add(powerDetail2);
                Details powerDetail3 = new Details(this, DEBUFF, FRAIL_TEXTURE);
                details.add(powerDetail3);
                break;
            }
            case SHADOW_SKY: {
                Details powerDetail = new Details(this, -STRONG_DEBUFF, STRENGTH_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, -STRONG_DEBUFF, DEXTERITY_TEXTURE);
                details.add(powerDetail2);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        onBossVictoryLogic();
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(POWER_ID) && this.getPower(POWER_ID).amount > 0) {
            this.particleTimer -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer < 0.0F) {
                this.particleTimer = 0.04F;
                AbstractDungeon.effectsQueue.add(new FlexibleDivinityParticleEffect(this, Color.DARK_GRAY.cpy()));
            }
            this.particleTimer2 -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer2 < 0.0F) {
                this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(Color.DARK_GRAY.cpy(), this));
            }
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        tips.add(new PowerTip(BaseMod.getKeywordProper("pokeregions:shadow-cursed"), BaseMod.getKeywordDescription("pokeregions:shadow-cursed")));
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Lugia();
    }

}