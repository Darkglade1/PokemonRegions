package pokeregions.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pokeregions.cards.pokemonAllyCards.act2.Phanpy;
import pokeregions.relics.PokemonEgg;
import pokeregions.util.PokemonReward;

public class HatchEggEffect extends AbstractGameEffect {
    private boolean hasDug = false;
    private Color screenColor;

    public HatchEggEffect() {
        this.screenColor = AbstractDungeon.fadeColor.cpy();
        this.duration = 2.0F;
        this.screenColor.a = 0.0F;
        ((RestRoom)AbstractDungeon.getCurrRoom()).cutFireSound();
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.updateBlackScreenColor();
        if (this.duration < 1.0F && !this.hasDug) {
            this.hasDug = true;
            AbstractDungeon.player.loseRelic(PokemonEgg.ID);
            AbstractDungeon.getCurrRoom().rewards.clear();
            AbstractCard rewardCard = CardLibrary.getCard(Phanpy.ID).makeCopy();
            AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(rewardCard.cardID));
            AbstractDungeon.getCurrRoom().phase = RoomPhase.COMPLETE;
            AbstractDungeon.combatRewardScreen.open();
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
            ((RestRoom)AbstractDungeon.getCurrRoom()).fadeIn();
            AbstractDungeon.getCurrRoom().phase = RoomPhase.COMPLETE;
        }

    }

    private void updateBlackScreenColor() {
        if (this.duration > 1.5F) {
            this.screenColor.a = Interpolation.fade.apply(1.0F, 0.0F, (this.duration - 1.5F) * 2.0F);
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
}
