package pokeregions.util;

import basemod.ClickableUIElement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import pokeregions.actions.PokeballMoveAction;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.relics.PokeballBelt;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeUIPath;
import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;

public class PokeballMove extends ClickableUIElement {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("PokeballMove"));
    private static final String[] TEXT = uiStrings.TEXT;

    public static final float ELITE_ZERO_CHANCE_THRESHOLD = 0.3f;
    public static final float ELITE_FIFTY_CHANCE_THRESHOLD = 0.2f;
    public static final float ELITE_ONE_HUNDRED_CHANCE_THRESHOLD = 0.1f;

    public static final float BOSS_ZERO_CHANCE_THRESHOLD = 0.15f;
    public static final float BOSS_FIFTY_CHANCE_THRESHOLD = 0.10f;
    public static final float BOSS_ONE_HUNDRED_CHANCE_THRESHOLD = 0.05f;

    public static final float NORMAL_ZERO_CHANCE_THRESHOLD = 0.5f;
    public static final float NORMAL_FIFTY_CHANCE_THRESHOLD = 0.35f;
    public static final float NORMAL_ONE_HUNDRED_CHANCE_THRESHOLD = 0.2f;

    private final String ID;
    private final String moveDescription;
    private final AbstractPokemonMonster owner;
    public String captureChanceMessage = "";
    public Color textColor;
    private final int hpThreshold;

    public PokeballMove(AbstractPokemonMonster owner) {
        super(TexLoader.getTexture(makeUIPath("CatchPokemonButton.png")), 0, 0, 76.0f, 64.0f);
        this.ID = TEXT[0] + owner.name;
        this.owner = owner;
        switch (owner.type) {
            case ELITE:
                hpThreshold = (int)(ELITE_ONE_HUNDRED_CHANCE_THRESHOLD * owner.maxHealth);
                break;
            case BOSS:
                hpThreshold = (int)(BOSS_ONE_HUNDRED_CHANCE_THRESHOLD * owner.maxHealth);
                break;
            default:
                hpThreshold = (int)(NORMAL_ONE_HUNDRED_CHANCE_THRESHOLD * owner.maxHealth);
                break;
        }
        this.moveDescription = TEXT[1] + hpThreshold + TEXT[9];
    }

    private void doMove() {
        for (AbstractGameAction action : AbstractDungeon.actionManager.actions) {
            if (action instanceof PokeballMoveAction) {
                return;
            }
        }
        atb(new PokeballMoveAction(owner, this));
    }

    public String getID(){
        return this.ID;
    }

    @Override
    protected void onHover() {
        TipHelper.renderGenericTip(this.x - 30f * Settings.scale, this.y - 15f * Settings.scale, this.ID, this.moveDescription);
        if (this.hitbox.justHovered && canUseMoveString().equals("") && !AbstractDungeon.actionManager.turnHasEnded && !adp().inSingleTargetMode && !adp().isDraggingCard) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
            int chance = calculateCaptureChance(owner);
            if (chance >= 100) {
                textColor = new Color(0, 255, 0, 1);
            } else if (chance <= 0) {
                textColor = new Color(255, 0, 0, 1);
            } else {
                textColor = new Color(255, 255, 0, 1); //downfall fucks up Color.Yellow :upside_down:
            }
            captureChanceMessage = TEXT[7] + chance + TEXT[8];
        }
    }

    @Override
    protected void onUnhover() {
        captureChanceMessage = "";
    }

    @Override
    protected void onClick() {
        String canUse = canUseMoveString();
        if (!canUse.equals("")) {
            atb(new TalkAction(true, canUse, 1.0F, 1.0F));
        } else if(!AbstractDungeon.actionManager.turnHasEnded && !adp().inSingleTargetMode && !adp().isDraggingCard){
            CardCrawlGame.sound.play("UI_CLICK_1");
            doMove();
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
        return "";
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!AbstractDungeon.isScreenUp) {
            if(AbstractDungeon.actionManager.turnHasEnded || !canUseMoveString().equals("")){
                super.render(sb, Color.GRAY);
            } else if (this.hitbox.hovered && !adp().inSingleTargetMode && !adp().isDraggingCard) {
                super.render(sb, Color.GOLD);
                if (!captureChanceMessage.equals("")) {
                    FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, captureChanceMessage, owner.intentHb.cX, owner.intentHb.cY - ((32.0f - 80.0f) * Settings.scale), textColor);
                }
            } else {
                super.render(sb);
            }
        }
    }

    public int calculateCaptureChance(AbstractMonster mo) {
        if (mo.currentHealth <= hpThreshold) {
            return 100;
        }
        float ZERO_CHANCE_THRESHOLD;
        float FIFTY_CHANCE_THRESHOLD;
        float ONE_HUNDRED_CHANCE_THRESHOLD;

        switch (mo.type) {
            case ELITE:
                ZERO_CHANCE_THRESHOLD = ELITE_ZERO_CHANCE_THRESHOLD;
                FIFTY_CHANCE_THRESHOLD = ELITE_FIFTY_CHANCE_THRESHOLD;
                ONE_HUNDRED_CHANCE_THRESHOLD = ELITE_ONE_HUNDRED_CHANCE_THRESHOLD;
                break;
            case BOSS:
                ZERO_CHANCE_THRESHOLD = BOSS_ZERO_CHANCE_THRESHOLD;
                FIFTY_CHANCE_THRESHOLD = BOSS_FIFTY_CHANCE_THRESHOLD;
                ONE_HUNDRED_CHANCE_THRESHOLD = BOSS_ONE_HUNDRED_CHANCE_THRESHOLD;
                break;
            default:
                ZERO_CHANCE_THRESHOLD = NORMAL_ZERO_CHANCE_THRESHOLD;
                FIFTY_CHANCE_THRESHOLD = NORMAL_FIFTY_CHANCE_THRESHOLD;
                ONE_HUNDRED_CHANCE_THRESHOLD = NORMAL_ONE_HUNDRED_CHANCE_THRESHOLD;
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
}