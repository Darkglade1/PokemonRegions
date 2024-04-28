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
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.cardMods.ChargedMod;
import pokeregions.cards.pokemonAllyCards.act2.Lugia;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.Voltage;
import pokeregions.util.Details;
import pokeregions.vfx.FlexibleDivinityParticleEffect;
import pokeregions.vfx.FlexibleStanceAuraEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class RaikouEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(RaikouEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte THUNDER = 0;
    private static final byte ZAP_CANNON = 1;
    private static final byte ELECTROWEB = 2;
    private static final byte CHARGE = 3;

    public final int STATUS = calcAscensionSpecial(3);
    public final int DEBUFF = 1;
    public final int BLOCK = 16;
    public final int POWER_NUM_TRIGGERS = 2;

    public final int CHARGE_COOLDOWN = 3;
    private int cooldown = CHARGE_COOLDOWN;

    private float particleTimer;
    private float particleTimer2;

    public static final String POWER_ID = makeID("ElectricTerrain");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public RaikouEnemy() {
        this(100.0f, 0.0f);
    }

    public RaikouEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 230.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Raikou/Raikou.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.2f);
        setHp(calcAscensionTankiness(430));
        addMove(THUNDER, Intent.ATTACK, calcAscensionDamage(12), 2);
        addMove(ZAP_CANNON, Intent.ATTACK_DEBUFF, calcAscensionDamage(26));
        addMove(ELECTROWEB, Intent.DEFEND_DEBUFF);
        addMove(CHARGE, Intent.BUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, POWER_NUM_TRIGGERS, "storm") {
            @Override
            public void onUseCard(AbstractCard card, UseCardAction action) {
                if (this.amount > 0) {
                    this.flash();
                    this.amount--;
                    ChargedMod mod = new ChargedMod();
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            CardModifierManager.addModifier(card, mod.makeCopy());
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
            case THUNDER: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    atb(new VFXAction(new LightningEffect(adp().drawX, adp().drawY), 0.0f));
                    atb(new SFXAction("ORB_LIGHTNING_EVOKE"));
                    dmg(adp(), info, AbstractGameAction.AttackEffect.NONE);
                }
                break;
            }
            case ZAP_CANNON: {
                useFastAttackAnimation();
                atb(new VFXAction(new LightningEffect(adp().drawX, adp().drawY), 0.0f));
                atb(new SFXAction("ORB_LIGHTNING_EVOKE"));
                dmg(adp(), info, AbstractGameAction.AttackEffect.NONE);
                applyToTarget(adp(), this, new DrawReductionPower(adp(), DEBUFF));
                break;
            }
            case ELECTROWEB: {
                block(this, BLOCK);
                intoDiscardMo(new Dazed(), STATUS);
                break;
            }
            case CHARGE: {
                int numChargedCards = 0;
                for (AbstractCard card : adp().hand.group) {
                    if (CardModifierManager.hasModifier(card, ChargedMod.ID)) {
                        numChargedCards++;
                    }
                }
                for (AbstractCard card : adp().drawPile.group) {
                    if (CardModifierManager.hasModifier(card, ChargedMod.ID)) {
                        numChargedCards++;
                    }
                }
                for (AbstractCard card : adp().discardPile.group) {
                    if (CardModifierManager.hasModifier(card, ChargedMod.ID)) {
                        numChargedCards++;
                    }
                }
                applyToTarget(this, this, new StrengthPower(this, numChargedCards));
                break;
            }
        }
        if (this.nextMove == CHARGE) {
            cooldown = CHARGE_COOLDOWN;
        } else {
            cooldown--;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (cooldown <= 0) {
            setMoveShortcut(CHARGE, MOVES[CHARGE]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(THUNDER) && !this.lastMoveBefore(THUNDER)) {
                possibilities.add(THUNDER);
            }
            if (!this.lastMove(ZAP_CANNON) && !this.lastMoveBefore(ZAP_CANNON)) {
                possibilities.add(ZAP_CANNON);
            }
            if (!this.lastMove(ELECTROWEB) && !this.lastMoveBefore(ELECTROWEB)) {
                possibilities.add(ELECTROWEB);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case ZAP_CANNON: {
                Details powerDetail = new Details(this, DEBUFF, DRAW_DOWN_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case ELECTROWEB: {
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                Details statusDetail = new Details(this, STATUS, DAZED_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                break;
            }
            case CHARGE: {
                Details detail = new Details(this, Details.CHARGE);
                details.add(detail);
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
                AbstractDungeon.effectsQueue.add(new FlexibleDivinityParticleEffect(this, Color.GOLD.cpy()));
            }
            this.particleTimer2 -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer2 < 0.0F) {
                this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(Color.GOLD.cpy(), this));
            }
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        tips.add(new PowerTip(BaseMod.getKeywordProper("pokeregions:charged"), BaseMod.getKeywordDescription("pokeregions:charged")));
        PowerStrings voltageStrings = CardCrawlGame.languagePack.getPowerStrings(Voltage.POWER_ID);
        String voltageName = voltageStrings.NAME;
        String[] voltageDescriptions = voltageStrings.DESCRIPTIONS;
        tips.add(new PowerTip(voltageName, voltageDescriptions[0]));
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Lugia();
    }

}