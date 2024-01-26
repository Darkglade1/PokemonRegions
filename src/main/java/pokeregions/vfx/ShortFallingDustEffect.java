package pokeregions.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.vfx.FallingDustEffect;

public class ShortFallingDustEffect extends FallingDustEffect {

    public ShortFallingDustEffect(float x, float y) {
        super(x, y);
        this.duration = MathUtils.random(2.0F, 3.0F);
    }

    public ShortFallingDustEffect(float x, float y, Color c) {
        this(x, y);
        color.set(c);
    }
}
