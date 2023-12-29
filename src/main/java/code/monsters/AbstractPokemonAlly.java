package code.monsters;

import basemod.ReflectionHacks;
import code.CustomIntent.IntentEnums;
import code.actions.UpdateStaminaOnCardAction;
import code.cards.AbstractAllyPokemonCard;
import code.util.AllyMove;
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

import java.util.ArrayList;

import static code.PokemonRegions.makeID;
import static code.PokemonRegions.makeUIPath;
import static code.util.Wiz.*;

public abstract class AbstractPokemonAlly extends AbstractPokemonMonster {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("AllyStrings"));
    private static final String[] TEXT = uiStrings.TEXT;
    public boolean massAttackHitsPlayer = false;

    public ArrayList<AllyMove> allyMoves = new ArrayList<>();
    public AbstractAllyPokemonCard allyCard;
    public AbstractMonster target;
    public static final byte MOVE_1 = 0;
    public static final byte MOVE_2 = 1;
    public byte defaultMove;
    public Intent move1Intent;
    public Intent move2Intent;
    public boolean move1RequiresTarget = false;
    public boolean move2RequiresTarget = false;
    public int move1StaminaCost;
    public int move2StaminaCost;
    public static final float X_POSITION = -700.0f;
    public static final float Y_POSITION = 0.0f;

    public AbstractPokemonAlly(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    public AbstractPokemonAlly(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
    }

    public AbstractPokemonAlly(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
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
                AbstractPokemonAlly.this.target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.aiRng);
                this.isDone = true;
            }
        });
        populateAllyMoves();
        setDefaultMove();
    }

    public void populateAllyMoves() {
        Texture move1Texture = getTextureForIntent(move1Intent);
        AllyMove move1 = new AllyMove(allyCard.move1Name, this, move1Texture, allyCard.move1Description, () -> {
            setMoveShortcut(MOVE_1);
            createIntent();
            AbstractDungeon.onModifyPower();
        }, move1RequiresTarget);
        move1.setX(this.intentHb.x);
        move1.setY(this.intentHb.cY - ((32.0f - 80.0f) * Settings.scale));
        allyMoves.add(move1);

        Texture move2Texture = getTextureForIntent(move2Intent);
        AllyMove move2 = new AllyMove(allyCard.move2Name, this, move2Texture, allyCard.move2Description, () -> {
            setMoveShortcut(MOVE_2);
            createIntent();
            AbstractDungeon.onModifyPower();
        }, move2RequiresTarget);
        move2.setX(this.intentHb.x);
        move2.setY(this.intentHb.cY - ((32.0f - 160.0f) * Settings.scale));
        allyMoves.add(move2);

        //changeToGuard.setX(this.intentHb.x - ((50.0F + 32.0f) * Settings.scale));
        //changeToGuard.setY(this.intentHb.cY - ((32.0f - 240.0f) * Settings.scale));
    }

    public Texture getTextureForIntent(Intent intent) {
        if (intent == IntentEnums.MASS_ATTACK) {
            return new Texture(makeUIPath("areaIntent.png"));
        }
        switch (intent) {
            case ATTACK:
                return new Texture(makeUIPath("attackIcon.png"));
            case ATTACK_BUFF:
                return new Texture(makeUIPath("attackBuffIcon.png"));
            case ATTACK_DEBUFF:
                return new Texture(makeUIPath("attackDebuffIcon.png"));
            case ATTACK_DEFEND:
                return new Texture(makeUIPath("attackBlockIcon.png"));
            case BUFF:
                return new Texture(makeUIPath("BuffIcon.png"));
            case DEBUFF:
                return new Texture(makeUIPath("DebuffIcon.png"));
            case DEFEND:
                return new Texture(makeUIPath("BlockIcon.png"));
            default:
                return new Texture(makeUIPath("missing.png"));
        }
    }

    public void setDefaultMove() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                setMoveShortcut(defaultMove);
                createIntent();
                this.isDone = true;
            }
        });
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
    }

    public void postTurn() {
        int newCurrentStamina = allyCard.currentStamina;
        System.out.println("CURRENT STAMINA");
        System.out.println(newCurrentStamina);
        switch (this.nextMove) {
            case MOVE_1: {
                newCurrentStamina -= move1StaminaCost;
                System.out.println("LOWERING STAMINA");
                System.out.println(move1StaminaCost);
                break;
            }
            case MOVE_2: {
                newCurrentStamina -= move2StaminaCost;
                System.out.println("LOWERING STAMINA");
                System.out.println(move2StaminaCost);
                System.out.println(newCurrentStamina);
                break;
            }
        }
        atb(new UpdateStaminaOnCardAction(allyCard, newCurrentStamina));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                halfDead = true;
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
                if(info.base > -1) {
                    Color color = new Color(0.0F, 1.0F, 0.0F, 0.5F);
                    ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentColor", color);
                    if (this.intent == IntentEnums.MASS_ATTACK) {
                        if (massAttackHitsPlayer) {
                            info.applyPowers(this, adp());
                            if (additionalMultiplier > 0) {
                                info.output = (int)(info.output * additionalMultiplier);
                            }
                            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                            PowerTip intentTip = (PowerTip)ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                            if (moves.get(this.nextMove).multiplier > 0) {
                                intentTip.body = TEXT[13] + info.output + TEXT[14] + " " + FontHelper.colorString(String.valueOf(moves.get(this.nextMove).multiplier), "b") + TEXT[16];
                            } else {
                                intentTip.body = TEXT[13] + info.output + TEXT[14] + TEXT[15];
                            }
                        } else {
                            info.applyPowers(this, target);
                            if (additionalMultiplier > 0) {
                                info.output = (int)(info.output * additionalMultiplier);
                            }
                            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                            PowerTip intentTip = (PowerTip)ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                            if (moves.get(this.nextMove).multiplier > 0) {
                                intentTip.body = TEXT[13] + info.output + TEXT[17] + " " + FontHelper.colorString(String.valueOf(moves.get(this.nextMove).multiplier), "b") + TEXT[16];
                            } else {
                                intentTip.body = TEXT[13] + info.output + TEXT[17] + TEXT[15];
                            }
                        }
                    } else {
                        info.applyPowers(this, target);
                        if (additionalMultiplier > 0) {
                            info.output = (int)(info.output * additionalMultiplier);
                        }
                        ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                        PowerTip intentTip = (PowerTip)ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
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
                    PowerTip intentTip = (PowerTip)ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                    if (this.intent == AbstractMonster.Intent.DEBUFF || this.intent == AbstractMonster.Intent.STRONG_DEBUFF) {
                        intentTip.body = TEXT[5] + FontHelper.colorString(target.name, "y") + TEXT[6];
                    }
                    if (this.intent == AbstractMonster.Intent.BUFF || this.intent == AbstractMonster.Intent.DEFEND_BUFF) {
                        intentTip.body = TEXT[7];
                    }
                    if (this.intent == AbstractMonster.Intent.DEFEND || this.intent == AbstractMonster.Intent.DEFEND_DEBUFF) {
                        intentTip.body = TEXT[8];
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
        super.render(sb);
    }

    public void update() {
        super.update();
        for (AllyMove allyMove : allyMoves) {
            allyMove.update();
        }
        if (this.currentHealth != allyCard.currentStamina || this.maxHealth != allyCard.maxStamina) {
            System.out.println("HEALTH DIFFERS FROM SOURCE OF TRUTH");
            this.currentHealth = allyCard.currentStamina;
            this.maxHealth = allyCard.maxStamina;
            this.healthBarUpdatedEvent();
        }
    }

    @Override
    public void getMove(int num) {}

    @Override
    public void applyPowers() {
        if (this.nextMove == -1) {
            return;
        }
        applyPowers(target);
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
            damageArray[i] = info.output;
        }
        return damageArray;
    }
}