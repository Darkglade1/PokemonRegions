package pokeregions.vfx;

import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pokeregions.util.TexLoader;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeVfxPath;

public class ThrowEffect {
    public static AbstractGameEffect throwEffect(String texturePath, float scale, Hitbox source, Hitbox target, Color color, float duration) {
        Texture tex = TexLoader.getTexture(makeVfxPath(texturePath));
        VfxBuilder builder = new VfxBuilder(tex, source.cX, source.cY, duration)
                .moveX(source.cX, target.cX, VfxBuilder.Interpolations.POW2OUT)
                .moveY(source.cY, target.cY, VfxBuilder.Interpolations.POW2OUT)
                .rotate(MathUtils.random(100f, 300f) * (MathUtils.randomBoolean() ? -1 : 1))
                .setScale(scale)
                .emitEvery((x,y) -> new ParticleEffect(color.cpy(), x, y), 0.01f);
        return builder.build();
    }
}