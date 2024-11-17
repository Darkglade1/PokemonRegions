package pokeregions.actions;

import pokeregions.ui.PokemonTeamButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.DreamCatcher;
import com.megacrit.cardcrawl.relics.RegalPillow;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;

public class PokemonCampfireSleepEffect extends AbstractGameEffect {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private boolean hasHealed = false;
    private int healAmount;
    private Color screenColor;

    public PokemonCampfireSleepEffect() {
        this.screenColor = AbstractDungeon.fadeColor.cpy();
        if (Settings.FAST_MODE) {
            this.startingDuration = 1.5F;
        } else {
            this.startingDuration = 3.0F;
        }

        this.duration = this.startingDuration;
        this.screenColor.a = 0.0F;
        ((RestRoom)AbstractDungeon.getCurrRoom()).cutFireSound();
        AbstractDungeon.overlayMenu.proceedButton.hide();

        this.healAmount = (int)((float)AbstractDungeon.player.maxHealth * HealPokemonCampfireOption.HEAL_PERCENT);
        if (AbstractDungeon.player.hasRelic(RegalPillow.ID)) {
            this.healAmount += 15;
        }

    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.updateBlackScreenColor();
        if (this.duration < this.startingDuration - 0.5F && !this.hasHealed) {
            this.playSleepJingle();
            this.hasHealed = true;
            if (AbstractDungeon.player.hasRelic(RegalPillow.ID)) {
                AbstractDungeon.player.getRelic(RegalPillow.ID).flash();
            }

            AbstractDungeon.player.heal(this.healAmount, false);
            PokemonTeamButton.teamWideHeal(HealPokemonCampfireOption.STAMINA_HEAL);
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onRest();
            }
        }

        if (this.duration < this.startingDuration / 2.0F) {
            if (AbstractDungeon.player.hasRelic(DreamCatcher.ID)) {
                AbstractDungeon.player.getRelic(DreamCatcher.ID).flash();
                ArrayList<AbstractCard> rewardCards = AbstractDungeon.getRewardCards();
                if (rewardCards != null && !rewardCards.isEmpty()) {
                    AbstractDungeon.cardRewardScreen.open(rewardCards, null, TEXT[0]);
                }
            }

            this.isDone = true;
            ((RestRoom)AbstractDungeon.getCurrRoom()).fadeIn();
            AbstractRoom.waitTimer = 0.0F;
            AbstractDungeon.getCurrRoom().phase = RoomPhase.COMPLETE;
        }

    }

    private void playSleepJingle() {
        int roll = MathUtils.random(0, 2);
        switch (AbstractDungeon.actNum) {
            case 1:
                if (roll == 0) {
                    CardCrawlGame.sound.play("SLEEP_1-1");
                } else if (roll == 1) {
                    CardCrawlGame.sound.play("SLEEP_1-2");
                } else {
                    CardCrawlGame.sound.play("SLEEP_1-3");
                }
                break;
            case 2:
                if (roll == 0) {
                    CardCrawlGame.sound.play("SLEEP_2-1");
                } else if (roll == 1) {
                    CardCrawlGame.sound.play("SLEEP_2-2");
                } else {
                    CardCrawlGame.sound.play("SLEEP_2-3");
                }
                break;
            case 3:
                if (roll == 0) {
                    CardCrawlGame.sound.play("SLEEP_3-1");
                } else if (roll == 1) {
                    CardCrawlGame.sound.play("SLEEP_3-2");
                } else {
                    CardCrawlGame.sound.play("SLEEP_3-3");
                }
        }

    }

    private void updateBlackScreenColor() {
        if (this.duration > this.startingDuration - 0.5F) {
            this.screenColor.a = Interpolation.fade.apply(1.0F, 0.0F, (this.duration - (this.startingDuration - 0.5F)) * 2.0F);
        } else if (this.duration < 1.0F) {
            this.screenColor.a = Interpolation.fade.apply(0.0F, 1.0F, this.duration);
        } else {
            this.screenColor.a = 1.0F;
        }

    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.screenColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)Settings.WIDTH, (float)Settings.HEIGHT);
    }

    public void dispose() {
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString("CampfireSleepEffect");
        TEXT = uiStrings.TEXT;
    }
}
