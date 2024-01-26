package pokeregions.vfx;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.combat.ThrowDaggerEffect;

public class ColoredThrowDaggerEffect extends ThrowDaggerEffect {
    public ColoredThrowDaggerEffect(float x, float y, Color c) {
        super(x, y);
        color.set(c);
    }

    public ColoredThrowDaggerEffect(float x, float y, Color c, boolean isEnemy) {
        super(x, y);
        color.set(c);
        if (isEnemy) {
            TextureAtlas.AtlasRegion img = ImageMaster.DAGGER_STREAK;
            float targetX = x + MathUtils.random(320.0F, 360.0F) - (float)img.packedWidth / 2.0F;
            ReflectionHacks.setPrivate(this, ThrowDaggerEffect.class, "x", targetX);
        }
    }
}