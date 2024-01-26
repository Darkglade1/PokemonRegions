package pokeregions.vfx;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.vfx.scene.CeilingDustCloudEffect;

public class ColoredDustCloudEffect extends CeilingDustCloudEffect {
    public ColoredDustCloudEffect(float x, float y, Color c) {
        super(x, y);
        color.set(c);
    }
}
