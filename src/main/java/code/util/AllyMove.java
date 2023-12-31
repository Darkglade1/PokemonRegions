package code.util;

import basemod.BaseMod;
import basemod.ClickableUIElement;
import code.monsters.AbstractPokemonAlly;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.utility.HandCheckAction;
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
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import static code.PokemonRegions.makeID;
import static code.monsters.AbstractPokemonAlly.MOVE_1;
import static code.monsters.AbstractPokemonAlly.MOVE_2;
import static code.util.Wiz.adp;


public class AllyMove extends ClickableUIElement {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("StaminaCost"));
    private static final String[] TEXT = uiStrings.TEXT;

    private String ID;
    private String moveDescription;
    private Texture moveImage;
    private Runnable moveActions;
    private AbstractPokemonAlly owner;
    private boolean requiresTarget;
    private byte moveNum;

    public boolean targetMode = false;
    private Vector2[] points = new Vector2[20];
    private Vector2 controlPoint;
    private float arrowScale;
    private float arrowScaleTimer = 0.0F;
    private AbstractMonster hoveredMonster = null;

    public AllyMove(String ID, AbstractPokemonAlly owner, Texture moveImage, String moveDescription, Runnable moveActions, boolean requiresTarget, byte moveNum) {
        super(moveImage, 0, 0, 64.0f, 64.0f);
        this.moveImage = moveImage;
        this.moveActions = moveActions;
        this.ID = ID;
        this.moveDescription = moveDescription;
        this.owner = owner;
        this.requiresTarget = requiresTarget;
        this.moveNum = moveNum;
        for(int i = 0; i < this.points.length; ++i) {
            this.points[i] = new Vector2();
        }
    }

    private void doMove() {
        if(moveActions != null) {
            if (requiresTarget && Wiz.getEnemies().size() > 1) {
                targetMode = true;
            } else {
                moveActions.run();
            }
        } else {
            BaseMod.logger.info("Pokemon Move: " + this.ID + " had no actions!");
        }
    }

    public String getID(){
        return this.ID;
    }

    @Override
    protected void onHover() {
        String descrption = this.moveDescription;
        int staminaCost = 0;
        if (this.moveNum == MOVE_1) {
            staminaCost = owner.move1StaminaCost;
        }
        if (this.moveNum == MOVE_2) {
            staminaCost = owner.move2StaminaCost;
        }
        if (staminaCost != 0) {
            descrption += " " + TEXT[0] + staminaCost + TEXT[1];
        }
        TipHelper.renderGenericTip(this.x, this.y - 15f * Settings.scale, this.ID, descrption);

        if (this.hitbox.justHovered) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
        }
    }

    @Override
    protected void onUnhover() {

    }

    @Override
    protected void onClick() {
        if(!AbstractDungeon.actionManager.turnHasEnded && !adp().inSingleTargetMode && !adp().isDraggingCard){
            CardCrawlGame.sound.play("UI_CLICK_1");
            this.doMove();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if(AbstractDungeon.actionManager.turnHasEnded){
            super.render(sb, Color.GRAY);
        } else if (this.hitbox.hovered) {
            super.render(sb, Color.GOLD);
        } else {
            super.render(sb);
        }
        if (this.targetMode) {
            if (this.hoveredMonster != null) {
                this.hoveredMonster.renderReticle(sb);
            }
            this.renderTargetingUi(sb);
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.targetMode) {
            this.updateTargetMode();
        }
    }

    private void updateTargetMode() {
        if (InputHelper.justClickedRight || AbstractDungeon.isScreenUp || (float)InputHelper.mY > (float)Settings.HEIGHT - 80.0F * Settings.scale || AbstractDungeon.player.hoveredCard != null || (float)InputHelper.mY < 140.0F * Settings.scale || CInputActionSet.cancel.isJustPressed()) {
            CInputActionSet.cancel.unpress();
            this.targetMode = false;
            GameCursor.hidden = false;
        }

        this.hoveredMonster = null;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m.hb.hovered && !m.isDying && !(m instanceof AbstractPokemonAlly)) {
                this.hoveredMonster = m;
                break;
            }
        }

        if (InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed()) {
            InputHelper.justClickedLeft = false;
            CInputActionSet.select.unpress();
            if (this.hoveredMonster != null) {
                owner.target = this.hoveredMonster;
                moveActions.run();
                if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
                    AbstractDungeon.actionManager.addToBottom(new HandCheckAction());
                }
                this.targetMode = false;
                GameCursor.hidden = false;
            }
        }
    }

    private void renderTargetingUi(SpriteBatch sb) {
        float x = (float)InputHelper.mX;
        float y = (float)InputHelper.mY;
        this.controlPoint = new Vector2(this.x - (x - this.x) / 4.0F, y + (y - this.y - 40.0F * Settings.scale) / 2.0F);
        if (this.hoveredMonster == null) {
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