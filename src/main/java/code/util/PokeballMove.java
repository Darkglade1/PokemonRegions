package code.util;

import basemod.ClickableUIElement;
import code.monsters.AbstractPokemonMonster;
import code.relics.AbstractEasyRelic;
import code.relics.PokeballBelt;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
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
    private final AbstractPokemonMonster owner;
    public String captureChanceMessage = "";
    public Color textColor;

    public PokeballMove(AbstractPokemonMonster owner) {
        super(TexLoader.getTexture(makeUIPath("CatchPokemonButton.png")), 0, 0, 76.0f, 64.0f);
        this.ID = TEXT[0] + owner.name;
        this.moveDescription = TEXT[1];
        this.owner = owner;
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
        int chance = calculateCaptureChance(owner);
        if (roll <= chance || Settings.isDebug) {
            owner.currentBlock = 0;
            atb(new SuicideAction(owner));
            AbstractCard pokemonCard = owner.getAssociatedPokemonCard();
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(pokemonCard.cardID));
            AbstractDungeon.effectList.add(new BlockedWordEffect(owner, owner.hb.cX, owner.hb.cY, TEXT[5]));
        } else {
            AbstractDungeon.effectList.add(new BlockedWordEffect(owner, owner.hb.cX, owner.hb.cY, TEXT[6]));
        }
    }

    public String getID(){
        return this.ID;
    }

    @Override
    protected void onHover() {
        TipHelper.renderGenericTip(this.x, this.y - 15f * Settings.scale, this.ID, this.moveDescription);
        if (this.hitbox.justHovered && canUseMoveString().equals("") && !AbstractDungeon.actionManager.turnHasEnded && !adp().inSingleTargetMode && !adp().isDraggingCard) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
            int chance = calculateCaptureChance(owner);
            if (chance >= 100) {
                textColor = Color.GREEN.cpy();
            } else if (chance <= 0) {
                textColor = Color.RED.cpy();
            } else {
                textColor = Color.YELLOW.cpy();
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
                ZERO_CHANCE_THRESHOLD = 0.15f;
                FIFTY_CHANCE_THRESHOLD = 0.10f;
                ONE_HUNDRED_CHANCE_THRESHOLD = 0.05f;
                break;
            default:
                ZERO_CHANCE_THRESHOLD = 0.5f;
                FIFTY_CHANCE_THRESHOLD = 0.35f;
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
}