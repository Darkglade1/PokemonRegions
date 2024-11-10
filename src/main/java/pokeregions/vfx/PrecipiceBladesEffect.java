package pokeregions.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.AnimatedSlashEffect;

public class PrecipiceBladesEffect extends AbstractGameEffect {
    private float x;
    private float y;
    private float dx;
    private float angle;

    public PrecipiceBladesEffect(float x, float y, float dx, float angle) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.angle = angle;
        this.startingDuration = 0.1F;
        this.duration = this.startingDuration;
    }

    public void update() {
        CardCrawlGame.sound.playA("ATTACK_IRON_2", -0.4F);
        AbstractDungeon.effectsQueue.add(new AnimatedSlashEffect(this.x, this.y - 30.0F * Settings.scale, this.dx, 500.0F, angle, 4.0F, Color.BROWN, Color.BROWN));

        this.isDone = true;
    }

    public void render(SpriteBatch sb) {
    }

    public void dispose() {
    }
}
