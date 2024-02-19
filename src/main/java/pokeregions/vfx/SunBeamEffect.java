package pokeregions.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class SunBeamEffect extends AbstractGameEffect {
    private float x;
    private float y;
    private static TextureAtlas.AtlasRegion img;
    float RotationMod;

    public SunBeamEffect() {
        if (img == null) {
            img = ImageMaster.vfxAtlas.findRegion("combat/laserThick");
        }

        this.RotationMod = (float)MathUtils.random(-115, -15);
        this.x = 100.0F * Settings.scale;
        this.y = AbstractDungeon.floorY + (375.0F * Settings.scale);
        this.x *= Settings.scale;
        this.y *= Settings.scale;
        this.color = (new Color(CardHelper.getColor(MathUtils.random(230, 255), MathUtils.random(187, 195), MathUtils.random(12, 20)))).cpy();
        this.color.a = 0.0F;
        this.duration = 5.0F;
        this.startingDuration = 5.0F;
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration > this.startingDuration / 2.0F) {
            this.color.a = Interpolation.pow2In.apply(0.3F, 0.0F, (this.duration - this.startingDuration / 2.0F) / (this.startingDuration / 2.0F));
        } else {
            this.color.a = Interpolation.pow2Out.apply(0.0F, 0.3F, this.duration / (this.startingDuration / 2.0F));
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
        }

    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.draw(img, this.x, this.y - (float)(img.packedHeight / 2), 0.0F, (float)img.packedHeight / 2.0F, (float)img.packedWidth, (float)img.packedHeight, this.scale * 2.0F + MathUtils.random(-0.05F, 0.05F), this.scale * 5.5F + MathUtils.random(-0.1F, 0.1F), this.RotationMod);
        sb.draw(img, this.x, this.y - (float)(img.packedHeight / 2), 0.0F, (float)img.packedHeight / 2.0F, (float)img.packedWidth, (float)img.packedHeight, this.scale * 2.0F + MathUtils.random(-0.05F, 0.05F), this.scale * 5.5F + MathUtils.random(-0.1F, 0.1F), this.RotationMod);
        sb.draw(img, this.x, this.y - (float)(img.packedHeight / 2), 0.0F, (float)img.packedHeight / 2.0F, (float)img.packedWidth, (float)img.packedHeight, this.scale * 2.0F, this.scale * 4.0F, this.RotationMod);
        sb.draw(img, this.x, this.y - (float)(img.packedHeight / 2), 0.0F, (float)img.packedHeight / 2.0F, (float)img.packedWidth, (float)img.packedHeight, this.scale * 2.0F, this.scale * 4.0F, this.RotationMod);
        this.RotationMod = (float)((double)this.RotationMod + 0.2);
        this.color.a = MathUtils.random(0.1F, 0.2F);
    }

    public void dispose() {
    }
}
