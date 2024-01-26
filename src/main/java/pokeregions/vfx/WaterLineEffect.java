package pokeregions.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pokeregions.util.TexLoader;

import static pokeregions.PokemonRegions.makeVfxPath;

public class WaterLineEffect extends AbstractGameEffect {
    private static Texture waterLine;
    float x;
    float y;

    public WaterLineEffect(float x, float y, boolean facingLeft) {
        waterLine = TexLoader.getTexture(makeVfxPath("WaterLine.png"));
        this.x = x;
        this.y = y;
        this.startingDuration = 0.5F;
        this.duration = this.startingDuration;
        this.rotation = 180.0f;
        if (!facingLeft) {
            this.rotation += 180.0F;
        }

        this.scale = MathUtils.random(0.8F, 1.0F) * Settings.scale;
        this.color = Color.WHITE.cpy();
        this.renderBehind = false;
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration > this.startingDuration / 2.0F) {
            this.color.a = Interpolation.fade.apply(0.8F, 0.01F, this.duration - this.startingDuration / 2.0F) * Settings.scale;
        } else {
            this.color.a = Interpolation.pow5Out.apply(0.01F, 0.8F, this.duration / (this.startingDuration / 2.0F)) * Settings.scale;
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
        }

    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.draw(waterLine, this.x, this.y, 0.0F, 128.0F, 256.0F, 256.0F, this.scale * 2.0F * (MathUtils.cos(this.duration * 16.0F) / 4.0F + 1.5F), this.scale, this.rotation, 0, 0, 256, 256, false, false);
    }

    public void dispose() {
    }
}
