package code.util;

import basemod.ClickableUIElement;
import code.monsters.AbstractPokemonAlly;
import code.monsters.AbstractPokemonMonster;
import code.relics.AbstractEasyRelic;
import code.relics.PokeballBelt;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;

import static code.PokemonRegions.makeID;
import static code.PokemonRegions.makeUIPath;
import static code.util.Wiz.adp;
import static code.util.Wiz.atb;


public class PokeballMove extends ClickableUIElement {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("PokeballMove"));
    private static final String[] TEXT = uiStrings.TEXT;

    private final String ID;
    private final String moveDescription;

    public boolean targetMode = false;
    private final Vector2[] points = new Vector2[20];
    private Vector2 controlPoint;
    private float arrowScale;
    private float arrowScaleTimer = 0.0F;
    public static AbstractMonster hoveredMonster = null;
    public static String captureChanceMessage = "";
    public static Color textColor;

    public PokeballMove() {
        super(TexLoader.getTexture(makeUIPath("CatchPokemonButton.png")), 0, 0, 76.0f, 64.0f);
        this.ID = TEXT[0];
        this.moveDescription = TEXT[1];
        for(int i = 0; i < this.points.length; ++i) {
            this.points[i] = new Vector2();
        }
    }

    private void doMove() {
        atb(new LoseEnergyAction(1));
        AbstractRelic pokeBallBelt = adp().getRelic(PokeballBelt.ID);
        if (pokeBallBelt != null) {
            pokeBallBelt.counter--;
            if (pokeBallBelt instanceof AbstractEasyRelic) {
                ((AbstractEasyRelic) pokeBallBelt).fixDescription();
            }
        }
        int roll = AbstractDungeon.miscRng.random(1, 100);
        int chance = calculateCaptureChance(hoveredMonster);
        if ((roll <= chance || Settings.isDebug) && hoveredMonster instanceof AbstractPokemonMonster) {
            hoveredMonster.currentBlock = 0;
            atb(new SuicideAction(hoveredMonster));
            AbstractCard pokemonCard = ((AbstractPokemonMonster) hoveredMonster).getAssociatedPokemonCard();
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(pokemonCard.cardID));
            AbstractDungeon.effectList.add(new BlockedWordEffect(hoveredMonster, hoveredMonster.hb.cX, hoveredMonster.hb.cY, TEXT[5]));
        } else {
            AbstractDungeon.effectList.add(new BlockedWordEffect(hoveredMonster, hoveredMonster.hb.cX, hoveredMonster.hb.cY, TEXT[6]));
        }
    }

    public String getID(){
        return this.ID;
    }

    @Override
    protected void onHover() {
        TipHelper.renderGenericTip(this.x - (175.0f * Settings.scale), this.y + (150.0f * Settings.scale), this.ID, this.moveDescription);
        if (this.hitbox.justHovered && canUseMoveString().equals("")) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
        }
    }

    @Override
    protected void onUnhover() {

    }

    @Override
    protected void onClick() {
        String canUse = canUseMoveString();
        if (!canUse.equals("")) {
            atb(new TalkAction(true, canUse, 1.0F, 1.0F));
        } else if(!AbstractDungeon.actionManager.turnHasEnded && !adp().inSingleTargetMode && !adp().isDraggingCard){
            CardCrawlGame.sound.play("UI_CLICK_1");
            targetMode = true;
        }
    }

    private String canUseMoveString() {
        int numPokeballs;
        AbstractRelic pokeBallBelt = adp().getRelic(PokeballBelt.ID);
        if (pokeBallBelt == null) {
            return TEXT[3];
        }
        numPokeballs = pokeBallBelt.counter;
        if (numPokeballs < 1) {
            return TEXT[3];
        }
        if (EnergyPanel.totalCount <= 0) {
            return TEXT[4];
        }
        for (AbstractMonster mo : Wiz.getEnemies()) {
            if (mo instanceof AbstractPokemonMonster) {
                return "";
            }
        }
        return TEXT[2];
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!AbstractDungeon.isScreenUp) {
            if(AbstractDungeon.actionManager.turnHasEnded || !canUseMoveString().equals("")){
                super.render(sb, Color.GRAY);
            } else if (this.hitbox.hovered) {
                super.render(sb, Color.GOLD);
            } else {
                super.render(sb);
            }
            if (this.targetMode) {
                if (hoveredMonster != null) {
                    hoveredMonster.renderReticle(sb);
                }
                this.renderTargetingUi(sb);
            }
        }
    }

    @Override
    public void update() {
        if (!AbstractDungeon.isScreenUp) {
            super.update();
            if (this.targetMode) {
                this.updateTargetMode();
            }
        }
    }

    private void updateTargetMode() {
        if (InputHelper.justClickedRight || AbstractDungeon.isScreenUp || (float)InputHelper.mY > (float)Settings.HEIGHT - 80.0F * Settings.scale || AbstractDungeon.player.hoveredCard != null || (float)InputHelper.mY < 140.0F * Settings.scale || CInputActionSet.cancel.isJustPressed()) {
            CInputActionSet.cancel.unpress();
            this.targetMode = false;
            GameCursor.hidden = false;
            hoveredMonster = null;
            captureChanceMessage = "";
        }

        hoveredMonster = null;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m.hb.hovered && !m.isDying && !(m instanceof AbstractPokemonAlly) && m instanceof AbstractPokemonMonster) {
                hoveredMonster = m;
                break;
            }
        }

        if (InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed()) {
            InputHelper.justClickedLeft = false;
            CInputActionSet.select.unpress();
            if (hoveredMonster != null) {
                this.doMove();
                this.targetMode = false;
                GameCursor.hidden = false;
                hoveredMonster = null;
                captureChanceMessage = "";
            }
        }
    }

    private int calculateCaptureChance(AbstractMonster mo) {
        float ZERO_CHANCE_THRESHOLD;
        float FIFTY_CHANCE_THRESHOLD;
        float ONE_HUNDRED_CHANCE_THRESHOLD;

        switch (mo.type) {
            case ELITE:
                ZERO_CHANCE_THRESHOLD = 0.3f;
                FIFTY_CHANCE_THRESHOLD = 0.2f;
                ONE_HUNDRED_CHANCE_THRESHOLD = 0.1f;
                break;
            case BOSS:
                ZERO_CHANCE_THRESHOLD = 0.1f;
                FIFTY_CHANCE_THRESHOLD = 0.07f;
                ONE_HUNDRED_CHANCE_THRESHOLD = 0.05f;
                break;
            default:
                ZERO_CHANCE_THRESHOLD = 0.5f;
                FIFTY_CHANCE_THRESHOLD = 0.3f;
                ONE_HUNDRED_CHANCE_THRESHOLD = 0.2f;
                break;

        }
        float captureChance = 0.0f;
        float monsterThreshold = ((float)mo.currentHealth / mo.maxHealth);
        if (monsterThreshold > ZERO_CHANCE_THRESHOLD) {
            captureChance = 0.0f;
        } else if (monsterThreshold <= ZERO_CHANCE_THRESHOLD && monsterThreshold >= FIFTY_CHANCE_THRESHOLD) {
            float thresholdLength = ZERO_CHANCE_THRESHOLD - FIFTY_CHANCE_THRESHOLD;
            float progressToThreshold = 1 - (monsterThreshold - FIFTY_CHANCE_THRESHOLD) / thresholdLength;
            captureChance = Interpolation.linear.apply(0, 50, progressToThreshold);
        } else if (monsterThreshold < FIFTY_CHANCE_THRESHOLD && monsterThreshold > ONE_HUNDRED_CHANCE_THRESHOLD) {
            float thresholdLength = FIFTY_CHANCE_THRESHOLD - ONE_HUNDRED_CHANCE_THRESHOLD;
            float progressToThreshold = 1 - (monsterThreshold - ONE_HUNDRED_CHANCE_THRESHOLD) / thresholdLength;
            captureChance = Interpolation.linear.apply(50, 100, progressToThreshold);
        } else if (monsterThreshold <= ONE_HUNDRED_CHANCE_THRESHOLD) {
            captureChance = 100.0f;
        }
        return (int)(captureChance);
    }

    public void renderTargetingUi(SpriteBatch sb) {
        float x = (float)InputHelper.mX;
        float y = (float)InputHelper.mY;
        this.controlPoint = new Vector2(this.x - (x - this.x) / 4.0F, y + (y - this.y - 40.0F * Settings.scale) / 2.0F);
        if (hoveredMonster == null) {
            this.arrowScale = Settings.scale;
            this.arrowScaleTimer = 0.0F;
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, 1.0F));
        } else {
            this.arrowScaleTimer += Gdx.graphics.getDeltaTime();
            if (this.arrowScaleTimer > 1.0F) {
                this.arrowScaleTimer = 1.0F;
            }

            this.arrowScale = Interpolation.elasticOut.apply(Settings.scale, Settings.scale * 1.2F, this.arrowScaleTimer);
            sb.setColor(new Color(1.0F, 0.2F, 0.3F, 1.0F));
        }
        Vector2 tmp = new Vector2(this.controlPoint.x - x, this.controlPoint.y - y);
        tmp.nor();
        this.drawCurvedLine(sb, new Vector2(this.x, this.y - 40.0F * Settings.scale), new Vector2(x, y), this.controlPoint);
        sb.draw(ImageMaster.TARGET_UI_ARROW, x - 128.0F, y - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, this.arrowScale, this.arrowScale, tmp.angle() + 90.0F, 0, 0, 256, 256, false, false);

        if (hoveredMonster != null) {
            int chance = calculateCaptureChance(hoveredMonster);
            if (chance >= 100) {
                textColor = Color.GREEN.cpy();
            } else if (chance <= 0) {
                textColor = Color.RED.cpy();
            } else {
                textColor = Color.YELLOW.cpy();
            }
            captureChanceMessage = TEXT[7] + chance + TEXT[8];
        } else {
            captureChanceMessage = "";
        }
    }

    private void drawCurvedLine(SpriteBatch sb, Vector2 start, Vector2 end, Vector2 control) {
        float radius = 7.0F * Settings.scale;
        for(int i = 0; i < this.points.length - 1; ++i) {
            this.points[i] = (Vector2) Bezier.quadratic(this.points[i], (float)i / 20.0F, start, control, end, new Vector2());
            radius += 0.4F * Settings.scale;
            Vector2 tmp;
            float angle;
            if (i != 0) {
                tmp = new Vector2(this.points[i - 1].x - this.points[i].x, this.points[i - 1].y - this.points[i].y);
                angle = tmp.nor().angle() + 90.0F;
            } else {
                tmp = new Vector2(this.controlPoint.x - this.points[i].x, this.controlPoint.y - this.points[i].y);
                angle = tmp.nor().angle() + 270.0F;
            }

            sb.draw(ImageMaster.TARGET_UI_CIRCLE, this.points[i].x - 64.0F, this.points[i].y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, radius / 18.0F, radius / 18.0F, angle, 0, 0, 128, 128, false, false);
        }
    }
}