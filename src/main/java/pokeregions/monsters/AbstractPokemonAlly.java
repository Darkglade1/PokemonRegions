package pokeregions.monsters;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.RunicDome;
import pokeregions.CustomIntent.IntentEnums;
import pokeregions.PokemonRegions;
import pokeregions.actions.SwitchPokemonAction;
import pokeregions.actions.UpdateStaminaOnCardAction;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.SicEm;
import pokeregions.cards.pokemonAllyCards.act1.Mew;
import pokeregions.util.*;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeUIPath;
import static pokeregions.util.Wiz.*;

public abstract class AbstractPokemonAlly extends AbstractPokemonMonster {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("AllyStrings"));
    private static final String[] TEXT = uiStrings.TEXT;

    public ArrayList<AllyMove> allyMoves = new ArrayList<>();
    public SwitchPokemonMove switchMove;
    public AbstractAllyPokemonCard allyCard;
    public AbstractMonster target;
    public static final byte MOVE_1 = 0;
    public static final byte MOVE_2 = 1;
    public static final byte NO_MOVE = 2;
    public byte defaultMove;
    public Intent move1Intent;
    public Intent move2Intent;
    public boolean move1RequiresTarget = false;
    public boolean move2RequiresTarget = false;
    public int move1StaminaCost;
    public int move2StaminaCost;
    public boolean noStaminaCostForTurn = false;
    public static final float X_POSITION = -700.0f;
    public static final float Y_POSITION = 0.0f;
    private float arrowTime = 0.0f;
    private float alpha = 0.0f;
    private float alphaSpeed = 3.0f;

    public AbstractPokemonAlly(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
        addMove(NO_MOVE, Intent.NONE);
    }

    public AbstractPokemonAlly(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
        addMove(NO_MOVE, Intent.NONE);
    }

    public AbstractPokemonAlly(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
        addMove(NO_MOVE, Intent.NONE);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                halfDead = true;
                this.isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                setSmartTarget();
                this.isDone = true;
            }
        });
        populateAllyMoves();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                setDefaultMove();
                createIntent();
                this.isDone = true;
            }
        });
    }

    public void populateAllyMoves() {
        String move1Name = allyCard.move1Name;
        String move1Description = replaceModIDPrefix(allyCard.move1Description);
        move1Description = replaceAsterick(move1Description);
        Texture move1Texture = getTextureForIntent(move1Intent);
        AllyMove move1 = new AllyMove(move1Name, this, move1Texture, move1Description, () -> {
            setMoveShortcut(MOVE_1);
            createIntent();
            AbstractDungeon.onModifyPower();
        }, move1RequiresTarget, MOVE_1);
        move1.setX(this.intentHb.x);
        move1.setY(this.intentHb.cY - ((32.0f - 80.0f) * Settings.scale));
        allyMoves.add(move1);

        String move2Name = allyCard.move2Name;
        String move2Description = replaceModIDPrefix(allyCard.move2Description);
        move2Description = replaceAsterick(move2Description);
        Texture move2Texture = getTextureForIntent(move2Intent);
        AllyMove move2 = new AllyMove(move2Name, this, move2Texture, move2Description, () -> {
            setMoveShortcut(MOVE_2);
            createIntent();
            AbstractDungeon.onModifyPower();
        }, move2RequiresTarget, MOVE_2);
        move2.setX(this.intentHb.x);
        move2.setY(this.intentHb.cY - ((32.0f - 160.0f) * Settings.scale));
        allyMoves.add(move2);

        switchMove = new SwitchPokemonMove();
        switchMove.setX(this.intentHb.x);
        switchMove.setY(this.intentHb.cY - ((32.0f - 240.0f) * Settings.scale));
    }

    private String replaceModIDPrefix(String input) {
        return input.replaceAll(PokemonRegions.modID.toLowerCase() + ":", "");
    }

    private String replaceAsterick(String input) {
        return input.replaceAll("\\*", "");
    }

    public Texture getTextureForIntent(Intent intent) {
        if (intent == IntentEnums.MASS_ATTACK) {
            return TexLoader.getTexture(makeUIPath("areaAttackIcon.png"));
        }
        switch (intent) {
            case ATTACK:
                return TexLoader.getTexture(makeUIPath("attackIcon.png"));
            case ATTACK_BUFF:
                return TexLoader.getTexture(makeUIPath("attackBuffIcon.png"));
            case ATTACK_DEBUFF:
                return TexLoader.getTexture(makeUIPath("attackDebuffIcon.png"));
            case ATTACK_DEFEND:
                return TexLoader.getTexture(makeUIPath("attackBlockIcon.png"));
            case BUFF:
                return TexLoader.getTexture(makeUIPath("BuffIcon.png"));
            case DEBUFF:
                return TexLoader.getTexture(makeUIPath("DebuffIcon.png"));
            case DEFEND:
                return TexLoader.getTexture(makeUIPath("BlockIcon.png"));
            case DEFEND_BUFF:
                return TexLoader.getTexture(makeUIPath("blockBuffIcon.png"));
            case MAGIC:
                return TexLoader.getTexture(makeUIPath("magicIcon.png"));
            case UNKNOWN:
                return TexLoader.getTexture(makeUIPath("UnknownIcon.png"));
            default:
                return TexLoader.getTexture(makeUIPath("missing.png"));
        }
    }

    public void setDefaultMove() {
        boolean move1Available = true;
        boolean move2Available = true;
        if (allyCard.move1isLimited && allyCard.hasUsedMove1) {
            move1Available = false;
        }
        if (move1StaminaCost > allyCard.currentStamina) {
            move1Available = false;
        }
        if (allyCard.move2isLimited && allyCard.hasUsedMove2) {
            move2Available = false;
        }
        if (move2StaminaCost > allyCard.currentStamina) {
            move2Available = false;
        }
        if (move1Available && move2Available) {
            setMoveShortcut(defaultMove);
        } else if (move1Available) {
            setMoveShortcut(MOVE_1);
        } else if (move2Available) {
            setMoveShortcut(MOVE_2);
        } else {
            setMoveShortcut(NO_MOVE);
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                halfDead = false;
                this.isDone = true;
            }
        });
        if (info != null && target != null && info.base > -1) {
            info.applyPowers(this, target);
            if (target.hasPower(SicEm.POWER_ID)) {
                int amount = target.getPower(SicEm.POWER_ID).amount;
                info.output = (int) (info.output * (1 + ((float)amount / 100)));
            }
        }
    }

    public void postTurn() {
        int staminaChange = 0;
        if (this.nextMove == MOVE_1) {
            allyCard.hasUsedMove1 = true;
            staminaChange = -move1StaminaCost;
        }
        if (this.nextMove == MOVE_2) {
            allyCard.hasUsedMove2 = true;
            staminaChange = -move2StaminaCost;
        }
        if (allyCard instanceof Mew) {
            ((Mew) allyCard).updateUsedLimitedMoves(this.nextMove);
        }
        atb(new UpdateStaminaOnCardAction(this, staminaChange));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (AbstractPokemonAlly.this.nextMove == MOVE_1 && (allyCard.currentStamina < move1StaminaCost || allyCard.move1isLimited)) {
                    setDefaultMove();
                }
                if (AbstractPokemonAlly.this.nextMove == MOVE_2 && (allyCard.currentStamina < move2StaminaCost || allyCard.move2isLimited)) {
                    setDefaultMove();
                }
                this.isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                halfDead = true;
                this.isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (allyCard.currentStamina <= 0) {
                    att(new SwitchPokemonAction(false));
                }
                this.isDone = true;
            }
        });
    }

    public void setStaminaInfo(AbstractAllyPokemonCard allyCard) {
        this.currentHealth = allyCard.currentStamina;
        this.maxHealth = allyCard.maxStamina;
        this.healthBarUpdatedEvent();
        this.move1StaminaCost = allyCard.staminaCost1;
        this.move2StaminaCost = allyCard.staminaCost2;
    }

    @Override
    public void createIntent() {
        super.createIntent();
        applyPowers();
    }

    public void applyPowers(AbstractCreature target) {
        applyPowers(target, -1);
    }

    public void applyPowers(AbstractCreature target, float additionalMultiplier) {
        if (this.nextMove >= 0) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            if (target != adp()) {
                if (info.base > -1) {
                    Color color = new Color(0.0F, 1.0F, 0.0F, 0.5F);
                    ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentColor", color);
                    if (this.intent == IntentEnums.MASS_ATTACK) {
                        // Apply powers against self since that should be good enough to calc massattack damage with only self damage mods
                        info.applyPowers(this, this);
                        if (additionalMultiplier >= 0) {
                            info.output = (int) (info.output * additionalMultiplier);
                        }
                        ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                        PowerTip intentTip = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                        if (moves.get(this.nextMove).multiplier > 0) {
                            intentTip.body = TEXT[9] + info.output + TEXT[10] + " " + FontHelper.colorString(String.valueOf(moves.get(this.nextMove).multiplier), "b") + TEXT[4];
                        } else {
                            intentTip.body = TEXT[9] + info.output + TEXT[10] + TEXT[6];
                        }
                    } else {
                        info.applyPowers(this, target);
                        if (additionalMultiplier >= 0) {
                            info.output = (int) (info.output * additionalMultiplier);
                        }
                        if (target.hasPower(SicEm.POWER_ID)) {
                            int amount = target.getPower(SicEm.POWER_ID).amount;
                            info.output = (int) (info.output * (1 + ((float)amount / 100)));
                        }
                        ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                        PowerTip intentTip = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                        Texture attackImg;
                        if (moves.get(this.nextMove).multiplier > 0) {
                            intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[3] + moves.get(this.nextMove).multiplier + TEXT[4];
                            attackImg = getAttackIntent(info.output * moves.get(this.nextMove).multiplier);
                        } else {
                            intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[2];
                            attackImg = getAttackIntent(info.output);
                        }
                        ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentImg", attackImg);
                    }
                } else {
                    Color color = new Color(1.0F, 1.0F, 1.0F, 0.5F);
                    ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentColor", color);
                    PowerTip intentTip = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                    if (this.intent == AbstractMonster.Intent.DEBUFF || this.intent == AbstractMonster.Intent.STRONG_DEBUFF) {
                        intentTip.body = TEXT[5] + FontHelper.colorString(target.name, "y") + TEXT[6];
                    }
                    if (this.intent == AbstractMonster.Intent.BUFF || this.intent == AbstractMonster.Intent.DEFEND_BUFF) {
                        intentTip.body = TEXT[7];
                    }
                    if (this.intent == AbstractMonster.Intent.DEFEND || this.intent == AbstractMonster.Intent.DEFEND_DEBUFF) {
                        intentTip.body = TEXT[8];
                    }
                    if (this.intent == AbstractMonster.Intent.MAGIC) {
                        intentTip.body = TEXT[11];
                    }
                    if (this.intent == Intent.UNKNOWN || this.intent == Intent.NONE) {
                        intentTip.body = TEXT[12];
                    }
                }
            } else {
                Color color = new Color(1.0F, 1.0F, 1.0F, 0.5F);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentColor", color);
                super.applyPowers();
            }
        }
    }

    @Override
    public void damage(DamageInfo info) {
        // Pokemon allies are immune to damage
    }

    @Override
    public void renderReticle(SpriteBatch sb) {
        // removes targeting reticle from player cards
    }

    @Override
    public void render(SpriteBatch sb) {
        for (AllyMove allyMove : allyMoves) {
            allyMove.render(sb);
        }
        if (switchMove != null) {
            switchMove.render(sb);
        }
        super.render(sb);
        // hack so allies don't get domed
        if (adp().hasRelic(RunicDome.ID)) {
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderIntentVfxBehind", sb.getClass()).invoke(this, sb);
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderIntent", sb.getClass()).invoke(this, sb);
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderIntentVfxAfter", sb.getClass()).invoke(this, sb);
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderDamageRange", sb.getClass()).invoke(this, sb);
        }

        alpha += Gdx.graphics.getDeltaTime() * alphaSpeed / 4;
        if (alpha > 0.7f) {
            alpha = 0.7f;
        }
        if (target != null && !target.isDead && !target.isEscaping && !AbstractDungeon.actionManager.turnHasEnded && ((this.nextMove == MOVE_1 && move1RequiresTarget) || (this.nextMove == MOVE_2 && move2RequiresTarget))) {
            TargetArrow.drawTargetArrow(sb, this.intentHb, target.hb, TargetArrow.CONTROL_HEIGHT * Settings.scale, arrowTime, alpha, null);
        }
        arrowTime += Gdx.graphics.getDeltaTime();
    }

    public void update() {
        super.update();
        for (AllyMove allyMove : allyMoves) {
            allyMove.update();
        }
        if (switchMove != null) {
            switchMove.update();
        }
        if (this.currentHealth != allyCard.currentStamina || this.maxHealth != allyCard.maxStamina) {
            this.currentHealth = allyCard.currentStamina;
            this.maxHealth = allyCard.maxStamina;
            this.healthBarUpdatedEvent();
        }
    }

    @Override
    public void getMove(int num) {
        setMoveShortcut(defaultMove);
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1) {
            return;
        }
        if (target != null) {
            applyPowers(target);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        // Pokemon allies cannot die
    }

    public void setAnimationFlip(boolean horizontal, boolean vertical) {
        animation.setFlip(horizontal, vertical);
    }

    public int[] calcMassAttack(DamageInfo info) {
        int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size()];
        for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
            AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
            info.applyPowers(this, mo);
            if (mo.hasPower(SicEm.POWER_ID)) {
                int amount = mo.getPower(SicEm.POWER_ID).amount;
                info.output = (int) (info.output * (1 + ((float)amount / 100)));
            }
            damageArray[i] = info.output;
        }
        return damageArray;
    }

    public boolean canUseMove1() {
        if (allyCard.move1isLimited && allyCard.hasUsedMove1) {
            return false;
        }
        return allyCard.currentStamina >= this.move1StaminaCost;
    }

    public boolean canUseMove2() {
        if (allyCard.move2isLimited && allyCard.hasUsedMove2) {
            return false;
        }
        return allyCard.currentStamina >= this.move2StaminaCost;
    }

    public void onSwitchIn() {

    }
    public void setSmartTarget() {
        ArrayList<AbstractMonster> moList = Wiz.getEnemies();
        moList.removeIf(m -> m.currentBlock > 0);
        if (moList.size() > 0) {
            target = findLowestHPTarget(moList);
        } else {
            target = findLowestHPTarget(Wiz.getEnemies());
        }
        if (target == null) {
            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                if (!mo.isDead && mo != this) {
                    target = mo;
                    break;
                }
            }
        }
    }

    public AbstractMonster findLowestHPTarget(ArrayList<AbstractMonster> moList) {
        if (moList.size() == 0) {
            return null;
        }
        AbstractMonster lowestHP = moList.get(0);
        for (AbstractMonster mo : moList) {
            if (mo.currentHealth < lowestHP.currentHealth) {
                lowestHP = mo;
            }
        }
        return lowestHP;
    }
}