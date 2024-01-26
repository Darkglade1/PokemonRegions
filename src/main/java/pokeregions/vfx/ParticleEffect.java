package pokeregions.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class ParticleEffect extends AbstractGameEffect {
    protected float x;
    protected float y;
    protected float oX;
    protected float oY;
    protected float vX;
    protected float vY;
    protected final float dur_div2;
    protected final Hitbox hb;
    protected final TextureAtlas.AtlasRegion img;

    public ParticleEffect(Color c, float x, float y) {
        this(c, (Hitbox)null);
        this.x = x;
        this.y = y;
    }

    public ParticleEffect(Color c, Hitbox hb) {
        this.hb = hb;
        this.img = ImageMaster.GLOW_SPARK_2;
        this.duration = MathUtils.random(1.8F, 2.0F);
        this.scale = MathUtils.random(1.0F, 1.2F) * Settings.scale;
        this.dur_div2 = this.duration / 2.0F;
        this.color = c;
        this.oX = MathUtils.random(-25.0F, 25.0F) * Settings.scale;
        this.oY = MathUtils.random(-25.0F, 25.0F) * Settings.scale;
        this.oX -= (float)this.img.packedWidth / 2.0F;
        this.oY -= (float)this.img.packedHeight / 2.0F;
        this.vX = MathUtils.random(-15.0F, 15.0F) * Settings.scale;
        this.vY = MathUtils.random(-17.0F, 17.0F) * Settings.scale;
        this.renderBehind = MathUtils.randomBoolean(0.2F + (this.scale - 0.5F));
        this.rotation = MathUtils.random(-8.0F, 8.0F);
    }

    public void update() {
        if (this.duration > this.dur_div2) {
            this.color.a = Interpolation.pow3In.apply(0.5F, 0.0F, (this.duration - this.dur_div2) / this.dur_div2);
        } else {
            this.color.a = Interpolation.pow3In.apply(0.0F, 0.5F, this.duration / this.dur_div2);
        }

        this.oX += this.vX * Gdx.graphics.getDeltaTime();
        this.oY += this.vY * Gdx.graphics.getDeltaTime();
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.setBlendFunction(770, 1);
        if (this.hb != null) {
            sb.draw(this.img, this.hb.cX + this.oX, this.hb.cY + this.oY, (float)this.img.packedWidth / 2.0F, (float)this.img.packedHeight / 2.0F, (float)this.img.packedWidth, (float)this.img.packedHeight, this.scale * MathUtils.random(0.8F, 1.2F), this.scale * MathUtils.random(0.8F, 1.2F), this.rotation);
        } else {
            sb.draw(this.img, this.x + this.oX, this.y + this.oY, (float)this.img.packedWidth / 2.0F, (float)this.img.packedHeight / 2.0F, (float)this.img.packedWidth, (float)this.img.packedHeight, this.scale * MathUtils.random(0.8F, 1.2F), this.scale * MathUtils.random(0.8F, 1.2F), this.rotation);
        }
        sb.setBlendFunction(770, 771);
    }

    public void dispose() {
    }
}